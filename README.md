# ClockView
# 简单的自定义View——表盘时钟

**先看看效果**
---------


![这里写图片描述](http://img.blog.csdn.net/20161106095410794)
*****
#先缕一下思路，看下绘制过程
- **先绘制外面的大圆**
- **再绘制圆心**
- **接着绘制时钟刻度**
- **然后绘制秒指针**
- **绘制分指针**
- **绘制时指针**
- **绘制好后初始化时间，获取时，分，秒**
- **接着用postInvalidateDelayed(1000)方法来定时1秒刷新View**

>关键点在于使用旋转画布，以及计算圆周长上各点坐标
>选择画布canvas.rotate(angle, x, y); // （x,y）旋转点  angle选择角度
>圆上点a，b计算公式：
>a = r * sin(angle) + x
>b = y - r * cos(angle)

#绘制过程onDraw方法如下
- **initClock()**
- **drawBigCircle(canvas)**
- **drawkedu(canvas)**
- **drawNumber(canvas)**
- **drawHour(canvas, hourAngle)**
- **drawMinute(canvas, minuteAngle)**
- **drawSecond(canvas, secondAngle)**
- **drawSmallCircle(canvas)**
- **postInvalidateDelayed(1000)**

#接着就是实现各种绘制方法了

``` java
	// 外面的大圆
    private void drawBigCircle(Canvas canvas) {
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(Dp2Px(getContext(),1));
        canvas.drawCircle(x, y, r, mPaint);
    }

    // 圆心
    private void drawSmallCircle(Canvas canvas) {
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(x, y, Dp2Px(getContext(),5), mPaint);
    }

    // 刻度
    private void drawkedu(Canvas canvas) {
        for (int i = 0; i < 60; i++) {
            // 如果i除于5余数为0即比较粗的时刻线
            if (i % 5 == 0) {
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setStrokeWidth(Dp2Px(getContext(),3));
                canvas.drawLine(x, y - r, x, y - r + Dp2Px(getContext(),14), mPaint);
            } else {
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setStrokeWidth(Dp2Px(getContext(),2));
                canvas.drawLine(x, y - r, x, y - r + Dp2Px(getContext(),9), mPaint);
            }
            canvas.rotate(6, x, y);
        }
    }

    // 刻度上的时间数字
    private void drawNumber(Canvas canvas) {
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(Dp2Px(getContext(),14));
        mPaint.setTextAlign(Paint.Align.CENTER);  // 设置文本水平居中
        float fontHeight = (mPaint.getFontMetrics().bottom - mPaint.getFontMetrics().top); // 获取文字高度用于设置文本垂直居中
        // 数字离圆心的距离
        float distance = r - Dp2Px(getContext(),25);
        // 数字的坐标(a,b)
        float a, b;
        // 每30度写一个数字
        for (int i = 0; i < 12; i++) {
            a = (float) (distance * Math.sin(i * 30 * (Math.PI / 180)) + x);
            b = (float) (y - distance * Math.cos(i * 30 * (Math.PI / 180)));
            if (i == 0) {
		        // 本以为font_height/2即可获得文字一半的高度，实测除以3.5才是，不明觉厉
                canvas.drawText("12", a, (float) (b + fontHeight / 3.5), mPaint);
            } else {
                canvas.drawText(String.valueOf(i), a, (float) (b + fontHeight / 3.5), mPaint);
            }
        }
    }

    // 秒指针
    private void drawSecond(Canvas canvas, float second) {
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(Dp2Px(getContext(),2));
        // 指针的长度
        float pointLenth = r - Dp2Px(getContext(),15);
        // 指针末尾的坐标(a,b)
        float a = (float) (pointLenth * Math.sin(second * (Math.PI / 180)) + x);
        float b = (float) (y - pointLenth * Math.cos(second * (Math.PI / 180)));
        canvas.drawLine(x, y, a, b, mPaint);
    }

    // 分钟指针
    private void drawMinute(Canvas canvas, float minute) {
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(Dp2Px(getContext(),3));
        // 指针的长度
        float pointLenth = r - Dp2Px(getContext(),35);
        // 指针末尾的坐标(a,b)
        float a = (float) (pointLenth * Math.sin(minute * (Math.PI / 180)) + x);
        float b = (float) (y - pointLenth * Math.cos(minute * (Math.PI / 180)));
        canvas.drawLine(x, y, a, b, mPaint);
    }

    // 小时指针
    private void drawHour(Canvas canvas, float hour) {
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(Dp2Px(getContext(),4));
        // 指针的长度
        float pointLenth = r - Dp2Px(getContext(),50);
        // 指针末尾的坐标(a,b)
        float a = (float) (pointLenth * Math.sin(hour * (Math.PI / 180)) + x);
        float b = (float) (y - pointLenth * Math.cos(hour * (Math.PI / 180)));
        canvas.drawLine(x, y, a, b, mPaint);
    }
```

#初始化时间

```java
	// 获取时间
    private void initClock() {
        SimpleDateFormat format = new SimpleDateFormat("HH-mm-ss");
        String time = format.format(new Date(System.currentTimeMillis()));
        String[] split = time.split("-");
        float hour = Integer.parseInt(split[0]);
        float minute = Integer.parseInt(split[1]);
        float second = Integer.parseInt(split[2]);
        //秒针走过的角度
        secondAngle = second * 6;
        //分针走过的角度
        minuteAngle = minute * 6 + second / 10;
        //时针走过的角度
        hourAngle = hour * 30 + minute / 10;
    }
```

#Dp像素转化



```java
	/**
     * 屏幕DP转PX
     **/
    public int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * 屏幕PX转DP
     **/
    public int Px2Dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }
```

# 简单的自定义View——矩阵、Camera变换
老规矩先看图

![这里写图片描述](http://img.blog.csdn.net/20161106122251007)
*****
#先了解下Matrix和Camera
#Matrix——顾名思义就是矩阵的意思
- **Matrix常用于2D变换，比如旋转、平移、缩放等，具体方法如下**
- **setTranslate(float dx,float dy)：控制Matrix进行位移。**
- **setSkew(float kx,float ky)：控制Matrix进行倾斜，kx、ky为X、Y方向上的比例。**
- **setSkew(float kx,float ky,float px,float py)：控制Matrix以px、py为轴心进行倾斜，kx、ky为X、Y方向上的倾斜比例。**
- **setRotate(float degrees)：控制Matrix进行depress角度的旋转，轴心为（0,0）。**
- **setRotate(float degrees,float px,float py)：控制Matrix进行depress角度的旋转，轴心为(px,py)。**
- **setScale(float sx,float sy)：设置Matrix进行缩放，sx、sy为X、Y方向上的缩放比例。**
- **setScale(float sx,float sy,float px,float py)：设置Matrix以(px,py)为轴心进行缩放，sx、sy为X、Y方向上的缩放比例。**


#Camera——常用于3D变换
- **1，camera位于坐标点（0,0），也就是视图的左上角；**
- **2，camera.translate(10, 20, 30)的意思是把观察物体右移10，上移20，向前移30（即让物体远离camera，这样物体将会变小）；**
- **3，camera.rotateX(45)的意思是绕x轴顺时针旋转45度。举例来说，如果物体中间线和x轴重合的话，绕x轴顺时针旋转45度就是指物体上半部分向里翻转，下半部分向外翻转；**
- **4，camera.rotateY(45)的意思是绕y轴顺时针旋转45度。举例来说，如果物体中间线和y轴重合的话，绕y轴顺时针旋转45度就是指物体右半部分向里翻转，左半部分向外翻转；**
- **5，camera.rotateZ(45)的意思是绕z轴顺时针旋转45度。举例来说，如果物体中间线和z轴重合的话，绕z轴顺时针旋转45度就是指物体上半部分向左翻转，下半部分向右翻转；**

**要了解更多，请百度，我就不复制过来了。**
****

#接着来看下实际应用
接着用上次做的时钟表盘：[http://blog.csdn.net/qq970259858/article/details/53053235](http://blog.csdn.net/qq970259858/article/details/53053235 "简单的自定义View——表盘时钟")

**增加如下方法即可：**

#3D变换

```java
    // 3D变换
    private void rotateCanvas(Canvas canvas) {
        mMatrix.reset();
        mCamera.save();
		// 沿x轴顺时针旋转
        mCamera.rotateX(mCanvasRotateX);
		// 沿y轴顺时针旋转
        mCamera.rotateY(mCanvasRotateY);
        mCamera.getMatrix(mMatrix);
        mCamera.restore();
		// 将变换参考点移置中心
        mMatrix.preTranslate(-x, -y);
        mMatrix.postTranslate(x, y);
        canvas.concat(mMatrix);
    }
```

#控制变换度

```java
	// 控制变换度
    private void rotateCanvasWhenMove(float xMove, float yMove) {
        float dx = xMove - x;
        float dy = yMove - y;
        float percentX = dx / x;
        float percentY = dy / y;
        if (percentX > 1f) {
            percentX = 1f;
        } else if (percentX < -1f) {
            percentX = -1f;
        }
        if (percentY > 1f) {
            percentY = 1f;
        } else if (percentY < -1f) {
            percentY = -1f;
        }
		//最终得到指定范围的最大变化度
        mCanvasRotateY = mCanvasMaxRotateDegree * percentX;
        mCanvasRotateX = -(mCanvasMaxRotateDegree * percentY);
    }
```

#重写onTouchEvent
```java
	@Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                rotateCanvasWhenMove(eventX, eventY);
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                rotateCanvasWhenMove(eventX, eventY);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mCanvasRotateX = 0;
                mCanvasRotateY = 0;
                invalidate();
                return true;

            default:
                return true;
        }

        return super.onTouchEvent(event);
    }
```
#最后看下onDraw绘制
**需要注意的一点：变换一定要在绘制之前。**

```java
	@Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initClock();
        rotateCanvas(canvas); //  必须在绘制之前变换
        drawBigCircle(canvas);
        drawkedu(canvas);
        drawNumber(canvas);
        drawHour(canvas, hourAngle);
        drawMinute(canvas, minuteAngle);
        drawSecond(canvas, secondAngle);
        drawSmallCircle(canvas);
        postInvalidateDelayed(1000);
    }
```

****
完整源码下载地址[https://github.com/LiLinXiang/ClockView](https://github.com/LiLinXiang/ClockView)