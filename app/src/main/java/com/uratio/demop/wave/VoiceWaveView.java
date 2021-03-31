package com.uratio.demop.wave;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.LinearInterpolator;

public class VoiceWaveView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = DrawThread.class.getSimpleName();
    private static final long SLEEP_TIME = 1000;
    private Context mContext;
    private SurfaceHolder mHolder;
    private Paint mPaint;
    private Path mPath;

    private final Object mSurfaceLock = new Object();
    private DrawThread mThread;

    private int line_1_start_color = 0xFF7Bf0F9;
    private int region_1_end_color = 0xFF6E9CE9;
    private int width;
    private int height;

    private static float amplifier = 30.0f;
    private static float frequency = 1.2f;    //2Hz
    private static float phase = 45.0f;         //相位

    private int mItemWidth;
    private ValueAnimator mAnimator;
    private int mOffsetX;

    public VoiceWaveView(Context context) {
        this(context, null);
    }

    public VoiceWaveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoiceWaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.mContext = context;
        mHolder = getHolder();
        mHolder.addCallback(this);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setStrokeWidth(5);

        mPath = new Path();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        setZOrderOnTop(true);//设置画布  背景透明
        mHolder.setFormat(PixelFormat.TRANSLUCENT);

        mThread = new DrawThread(surfaceHolder);
        mThread.setRun(true);
        mThread.start();

        width = getWidth();
        mItemWidth = (int) (width / frequency);
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
        mAnimator.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        //这里可以获取SurfaceView的宽高等信息
        this.width = width;
        this.height = height;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        synchronized (mSurfaceLock) {  //这里需要加锁，否则doDraw中有可能会crash
            mThread.setRun(false);
        }
    }

    private class DrawThread extends Thread {
        private SurfaceHolder mHolder;
        private boolean mIsRun = false;

        public DrawThread(SurfaceHolder holder) {
            super(TAG);
            mHolder = holder;
        }

        @Override
        public void run() {
            while (true) {
                synchronized (mSurfaceLock) {
                    if (!mIsRun) {
                        return;
                    }
                    Canvas canvas = mHolder.lockCanvas();
                    if (canvas != null) {
                        doDraw(canvas);  //这里做真正绘制的事情
                        mHolder.unlockCanvasAndPost(canvas);
                    }
                }
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void setRun(boolean isRun) {
            this.mIsRun = isRun;
        }
    }

    private void doDraw(Canvas canvas) {
        LinearGradient gradient = null;
        gradient = new LinearGradient(100, height - 200, 100, height + 200, line_1_start_color,
                region_1_end_color, Shader.TileMode.REPEAT);
        mPaint.setShader(gradient);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(100, height - 200, 300, height + 200, mPaint);
//
//        gradient = new LinearGradient(400, height - 200, 400, height,
//                line_1_start_color, region_1_end_color, Shader.TileMode.REPEAT);
//        mPaint.setShader(gradient);
//        mPaint.setStyle(Paint.Style.FILL);
//        canvas.drawRect(400, height - 200, 600, height + 200, mPaint);
//
//        gradient = new LinearGradient(700, height - 400, 700, height + 400,
//                line_1_start_color, region_1_end_color, Shader.TileMode.REPEAT);
//        mPaint.setShader(gradient);
//        mPaint.setStyle(Paint.Style.FILL);
//        canvas.drawRect(700, height - 200, 900, height + 200, mPaint);

//        this.mPhase = (float) ((this.mPhase + Math.PI * mSpeed) % (2 * Math.PI));
//        LinearGradient gradient = new LinearGradient(getXPos(startX), startY, getXPos(startX),
//        endY,
//                gradientStartColor, gradientEndColor, Shader.TileMode.REPEAT);
//

        float cy = height / 2;
        float startY = cy - amplifier * (float) (Math.sin(phase * 2 * (float) Math.PI / 360.0f));
        float endY =
                cy - amplifier * (float) (Math.sin(phase * 2 * (float) Math.PI / 360.0f + 2 * Math.PI * frequency));
//
        gradient = new LinearGradient(0, startY, width, endY,
                line_1_start_color, region_1_end_color, Shader.TileMode.REPEAT);
        mPaint.setShader(gradient);

        mPath.reset();
//        mPath.rewind();
//        mPath.addPath(mPathLine1);
//        mPath.lineTo(getXPos(mDensity - 1), -mLineCacheY[mDensity - 1] + height * 2);
//        mPath.addPath(mPathLine2);
//        mPath.lineTo(getXPos(0), mLineCacheY[0]);

        int halfItem = 150;

        mPath.moveTo(-width + mOffsetX, halfItem);
        for (int i = -mItemWidth; i < mItemWidth + getWidth(); i += mItemWidth) {
            mPath.rQuadTo(halfItem / 2, -100, halfItem, 0);
            mPath.rQuadTo(halfItem / 2, 100, halfItem, 0);
        }

//        mPath.setFillType(Path.FillType.INVERSE_WINDING);
//        mPaint.setStyle(Paint.Style.FILL);
//        mPaint.setShader(null);
//        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        mPaint.setColor(getResources().getColor(android.R.color.transparent));
        canvas.drawPath(mPath, mPaint);
//        mPaint.setXfermode(null);
    }
}
