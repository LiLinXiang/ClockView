package com.example.lincoln.clockview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Lincoln on 2016-11-5.
 */

public class MyView extends View {
    private Paint mPaint;
    // 圆心坐标
    private float x, y;
    // 圆半径
    private float r;
    // 时间角度
    private float secondAngle, minuteAngle, hourAngle;

    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        x = getMeasuredWidth() / 2;
        y = getMeasuredHeight() / 2;
        r = x - 5;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initClock();
        drawBigCircle(canvas);
        drawkedu(canvas);
        drawNumber(canvas);
        drawHour(canvas, hourAngle);
        drawMinute(canvas, minuteAngle);
        drawSecond(canvas, secondAngle);
        drawSmallCircle(canvas);
        postInvalidateDelayed(1000);
    }

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
                canvas.drawText("12", a, (float) (b + fontHeight / 3.5), mPaint); // 本以为font_height/2即可获得文字一半的高度，实测除以3.5才是，不明觉厉
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
}
