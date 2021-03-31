package com.uratio.demop.count;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.uratio.demop.R;

import java.lang.reflect.Field;

public class CustomerGuide extends View {
    private Paint mPaint;
    private Path mPath;

    private int paintColor;

    public CustomerGuide(Context context) {
        super(context);
        init(context,null);
    }

    public CustomerGuide(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public CustomerGuide(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CustomerGuide);

        paintColor = array.getColor(R.styleable.CustomerGuide_paintColor, Color.BLUE);

        mPaint = new Paint();
        mPaint.setColor(paintColor);
        mPaint.setAntiAlias(true);

        mPath = new Path(); // 初始化 Path 对象

//        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStyle(Paint.Style.FILL);
        // 使用 path 对图形进行描述（这段描述代码不必看懂）
        mPath.addArc(50, 50, 100, 100, -225, 225);
        mPath.arcTo(100, 50, 150, 100, -180, 225, false);
        mPath.lineTo(100, 130);
        mPath.close();

        mPath.addCircle(300,100,60, Path.Direction.CW);
        mPath.addCircle(360,100,60, Path.Direction.CW);
//        mPaint.setStyle(Paint.Style.FILL);
//        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));

        mPath.addCircle(200,350,80, Path.Direction.CW);
        mPath.addRect(0,200,500,700, Path.Direction.CW);
        mPath.setFillType(Path.FillType.EVEN_ODD);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mPath, mPaint); // 绘制出 path 描述的图形（心形），大


        /*mPaint.setStyle(Paint.Style.FILL);
//        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
//        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
        mPaint.setColor(Color.BLUE);
        canvas.drawCircle(300,300,60, mPaint);
//        canvas.drawCircle(360,100,60, mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
        mPaint.setColor(0x33000000);
        canvas.drawRect(0,200,600, 700, mPaint);*/
    }

    /**
     * 通过反射的方式获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
