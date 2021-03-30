package com.uratio.demop.wave;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by YANG on 2019/2/23.
 */
public class BezierView2 extends View {

    private Paint paint;
    private Path mPath;
    private int mItemWidth = 800;

    private ValueAnimator mAnimator;
    private int mOffsetX;

    public BezierView2(Context context) {
        this(context, null);
    }

    public BezierView2(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BezierView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        mPath = new Path();

        mAnimator = ValueAnimator.ofInt(0, mItemWidth);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOffsetX = (int) animation.getAnimatedValue();
                invalidate();
            }
        });

        mAnimator.setInterpolator(new LinearInterpolator());

        mAnimator.setDuration(1000);
        mAnimator.setRepeatCount(-1);
//        mAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPath.reset();

        int halfItem = 150;

        LinearGradient gradient = new LinearGradient(-mItemWidth + mOffsetX, halfItem, -mItemWidth + mOffsetX, mItemWidth,
                0xFF7Bf0F9, 0xFF6E9CE9, Shader.TileMode.REPEAT);
        paint.setShader(gradient);

        //必须先减去一个浪的宽度，以便第一遍动画能够刚好位移出一个波浪，形成无限波浪的效果
        mPath.moveTo(-mItemWidth + mOffsetX, halfItem);
        for (int i = -mItemWidth; i < mItemWidth + getWidth(); i += mItemWidth) {
            mPath.rQuadTo(halfItem / 2, -100, halfItem, 0);
            mPath.rQuadTo(halfItem / 2, 100, halfItem, 0);
        }
        canvas.drawPath(mPath, paint);
    }
}