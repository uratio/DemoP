package com.uratio.demop.runnable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class DrawText extends View {
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mPaint3 = new Paint(Paint.ANTI_ALIAS_FLAG);

    public DrawText(Context context) {
        super(context);
        init(context, null);
    }

    public DrawText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DrawText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.BLACK);

        /*String str = "AFGJLYRXPTQafgjlyrxptq";
        Rect rectF = new Rect();
        mPaint.getTextBounds(str, 0, str.length(), rectF);
        float width = rectF.width();
        float height = rectF.height();*/
        mPaint.setTextSize(60);

        float baseX = 20;
        float baseY = 400;
//获取FontMetrics对象，可以获得top,bottom,ascent,descent
        Paint.FontMetrics metrics = mPaint.getFontMetrics();
        float top = baseY + metrics.top;
        float ascent = baseY + metrics.ascent;
        float descent = baseY + metrics.descent;
        float bottom = +baseY + metrics.bottom;

        String text = "Android*java";
        Rect rectF = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), rectF);
        float width = rectF.width();
        float height = rectF.height();
        canvas.drawText(text, baseX, baseY-height/2-(rectF.top+rectF.bottom)/2, mPaint);
        mPaint.setColor(Color.BLACK);
        //绘制top
        mPaint.setColor(Color.BLUE);
        canvas.drawLine(getWidth()/2-90, top, getWidth(), top, mPaint);
        //绘制ascent
        mPaint.setColor(Color.GREEN);
        canvas.drawLine(0, ascent, getWidth()/2-90, ascent, mPaint);
        //绘制base
        mPaint.setColor(Color.BLACK);
        canvas.drawLine(0, baseY, getWidth(), baseY, mPaint);
        //绘制descent
        mPaint.setColor(Color.RED);
        canvas.drawLine(0, descent, getWidth()/2-90, descent, mPaint);
        //绘制bottom
        mPaint.setColor(Color.BLUE);
        canvas.drawLine(getWidth()/2-90, bottom, getWidth(), bottom, mPaint);

//        canvas.drawText(str, 20,400,mPaint);
//
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(baseX, baseY-height, baseX+width, baseY, mPaint);


        mPaint2.setColor(Color.BLACK);
        String str2 = "AFGJLYRXPTQafgjlyrxptq1234567980";
        Rect rect2 = new Rect();
        mPaint2.getTextBounds(str2, 0, str2.length(), rect2);
        float width2 = rect2.width();
        float height2 = rect2.height();
        mPaint2.setTextSize(20);
//        canvas.drawText(str2, 20,200,mPaint2);


        float baseX2 = 20;
        float baseY2 = 200;
//获取FontMetrics对象，可以获得top,bottom,ascent,descent
        Paint.FontMetrics metrics2 = mPaint2.getFontMetrics();
        float top2 = baseY2 + metrics2.top;
        float ascent2 = baseY2 + metrics2.ascent;
        float descent2 = baseY2 + metrics2.descent;
        float bottom2 = +baseY2 + metrics2.bottom;

        canvas.drawText(str2, baseX2, baseY2, mPaint2);
        mPaint2.setColor(Color.BLACK);
        //绘制top
        mPaint2.setColor(Color.BLUE);
        canvas.drawLine(getWidth()/2-90, top2, getWidth(), top2, mPaint2);
        //绘制ascent
        mPaint2.setColor(Color.GREEN);
        canvas.drawLine(0, ascent2, getWidth()/2-90, ascent2, mPaint2);
        //绘制base
        mPaint2.setColor(Color.BLACK);
        canvas.drawLine(0, baseY2, getWidth(), baseY2, mPaint2);
        //绘制descent
        mPaint2.setColor(Color.RED);
        canvas.drawLine(0, descent2, getWidth()/2-90, descent2, mPaint2);
        //绘制bottom
        mPaint2.setColor(Color.BLUE);
        canvas.drawLine(getWidth()/2-90, bottom2, getWidth(), bottom2, mPaint2);

//        mPaint2.setStyle(Paint.Style.STROKE);
//        canvas.drawRect(20,200-height2,20+width2,200,mPaint2);
        mPaint2.setColor(Color.BLUE);
        mPaint2.setStyle(Paint.Style.STROKE);
        canvas.drawRect(baseX2, baseY2-height2, baseX2+width2, baseY2, mPaint2);

        drawText("TQ1a29f870gjly",40,Color.BLACK,50,300,mPaint3,canvas,true);
    }

    private void drawText(String text,int size,int color,float startX,float stopY,Paint paint,Canvas canvas,boolean showFrame){
        float ascent1 = paint.ascent();
        Paint.FontMetrics metrics = paint.getFontMetrics();
        float top = metrics.top;//在一个大小确定的字体中，被当做最高字形，基线(base)上方的最大距离。
        float ascent = metrics.ascent;//单行文本中，在基线(base)上方被推荐的距离。
        float descent = metrics.descent;//单行文本中，在基线(base)下方被推荐的距离。
        float bottom = metrics.bottom;//在一个大小确定的字体中，被当做最低字形,基线(base)下方的最大距离。

        Rect rectF = new Rect();
        paint.getTextBounds(text, 0, text.length(), rectF);
        float width = rectF.width();
        float height = rectF.height();

        paint.setColor(color);
        paint.setTextSize(size);
        canvas.drawText(text, startX, stopY, paint);

        if (showFrame) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1);
            canvas.drawRect(startX, stopY - 4 * bottom, startX + width, stopY + bottom, paint);
        }

        canvas.drawLine(startX, stopY, startX+width, stopY, paint);

        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(2);
        canvas.drawLine(startX+10, stopY-3*bottom, startX+10, stopY, paint);
    }
}
