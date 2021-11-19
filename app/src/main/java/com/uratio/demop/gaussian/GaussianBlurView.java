package com.uratio.demop.gaussian;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author lang
 * @data 2021/11/19
 */
public class GaussianBlurView extends View {
    private Paint paint;
    private Bitmap bitmap;

    public GaussianBlurView(Context context) {
        this(context, null);
    }

    public GaussianBlurView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GaussianBlurView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        paint = new Paint();
        // 设置绘画风格为实线
        paint.setStyle(Paint.Style.STROKE);
        // 抗锯齿
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (bitmap != null) {
            canvas.drawBitmap(bitmap, getWidth(), getHeight(), paint);
        }
    }
}
