package com.uratio.demop.wave;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class SineWave extends View implements Runnable {
    private Paint mPaint = null;
    private static float amplifier = 30.0f;     //振幅
    private static float frequency = 1.2f;    //频率
    private static float phase = 45.0f;       //相位
    private int height = 0;
    private int width = 0;
    private static float px = -1, py = -1;
    private boolean sp = false;

    private Path mPath;
    private int mItemWidth;
    private ValueAnimator mAnimator;
    private int mOffsetX;

    public SineWave(Context context) {
        this(context, null);
    }

    //如果不写下面的构造函数，则会报错：custom view SineWave is not using the 2- or 3-argument View constructors
    public SineWave(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPath = new Path();
        new Thread(this).start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        init();
    }

    private void init() {
        if (mAnimator != null) return;
        mItemWidth = (int) (getWidth() / frequency);
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

    public SineWave(Context context, float amplifier, float frequency, float phase) {
        super(context);
        this.frequency = frequency;
        this.amplifier = amplifier;
        this.phase = phase;
        mPaint = new Paint();
        new Thread(this).start();
    }

    public float GetAmplifier() {
        return amplifier;
    }

    public float GetFrequency() {
        return frequency;
    }

    public float GetPhase() {
        return phase;
    }

    public void Set(float amplifier, float frequency, float phase) {
        this.frequency = frequency;
        this.amplifier = amplifier;
        this.phase = phase;
    }

    public void SetXY(float px, float py) {
        this.px = px;
        this.py = py;
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        height = this.getHeight();
        width = this.getWidth();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.GREEN);
        amplifier = (amplifier * 2 > height) ? (height / 2) : amplifier;
        mPaint.setAlpha(200);
        mPaint.setStrokeWidth(5);
        float cy = height / 2;// centerY：高度中心 y 坐标


        float startY = cy - amplifier * (float) (Math.sin(phase * 2 * (float) Math.PI / 360.0f));
        float endY =
                cy - amplifier * (float) (Math.sin(phase * 2 * (float) Math.PI / 360.0f + 2 * Math.PI * frequency));
        LinearGradient gradient = new LinearGradient(0, startY, width,
                endY, 0xFF7Bf0F9, 0xFF6E9CE9, Shader.TileMode.REPEAT);
        mPaint.setShader(gradient);

        //必须先减去一个浪的宽度，以便第一遍动画能够刚好位移出一个波浪，形成无限波浪的效果
//        mPath.moveTo(-mItemWidth + mOffsetX, cy);
//        for (int i = -mItemWidth; i < mItemWidth + getWidth(); i += mItemWidth) {
//            mPath.rQuadTo(cy / 2, -100, cy, 0);
//            mPath.rQuadTo(cy / 2, 100, cy, 0);
//        }
//        canvas.drawPath(mPath, mPaint);

        //float py=this.py-this.getTop();

        mPath.moveTo(0, startY);
        for (int i = 0; i < width - 1; i++) {
            mPath.rQuadTo((float) i, cy - amplifier * (float) (Math.sin(phase * 2 * (float) Math.PI / 360.0f + 2 * Math.PI * frequency * i / width)), (float) (i + 1), cy - amplifier * (float) (Math.sin(phase * 2 * (float) Math.PI / 360.0f + 2 * Math.PI * frequency * (i + 1) / width)));
        }
        canvas.drawPath(mPath, mPaint);


        /*for (int i = 0; i < width - 1; i++) {
            canvas.drawLine((float) i,
                    cy - amplifier * (float) (Math.sin(phase * 2 * (float) Math.PI / 360.0f + 2 * Math.PI * frequency * i / width)), (float) (i + 1), cy - amplifier * (float) (Math.sin(phase * 2 * (float) Math.PI / 360.0f + 2 * Math.PI * frequency * (i + 1) / width)), mPaint);
            float point =
                    cy - amplifier * (float) (Math.sin(phase * 2 * (float) Math.PI / 360.0f + 2 * Math.PI * frequency * i / width));

            if ((py >= (point - 2.5f)) && (py <= (point + 2.5f)) && (px >= i - 2.5f) && (px <= i + 2.5f)) {
                sp = true;
            }
        }

        if (sp) {
            mPaint.setColor(Color.RED);
            mPaint.setTextSize(20);
            canvas.drawText("(x=" + Float.toString(px) + ",y=" + Float.toString(py) + ")", 20, 20
                    , mPaint);
            sp = false;
        }

        mPaint.setColor(Color.BLUE);
        mPaint.setTextSize(20);
        canvas.drawText("(x=" + Float.toString(px) + ",y=" + Float.toString(py) + ")", 20,
                this.getHeight() - 20, mPaint);*/

    }


    @Override

    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        float px = event.getX();
        float py = event.getY();
        this.SetXY(px, py);
        return super.onTouchEvent(event);
    }

    @Override

    public void run() {
        // TODO Auto-generated method stub
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            postInvalidate();
        }
    }
}  