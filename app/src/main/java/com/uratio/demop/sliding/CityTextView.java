package com.uratio.demop.sliding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class CityTextView extends TextView {
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public CityTextView(Context context) {
        super(context);
    }

    public CityTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CityTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int min = Math.min(width, height);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0xFF4F6FEE);
        paint.setStrokeWidth(2);
        canvas.drawCircle(width/2,height/2,(min-2)/2,paint);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }
}
