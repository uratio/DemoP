package com.uratio.demop.wave.voice;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.LinearInterpolator;

import com.uratio.demop.lottery.LotteryView;
import com.uratio.demop.ripple.WavePointSurfaceView;
import com.uratio.demop.utils.DisplayUtils;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author lang
 * @data 2021/5/13
 */
public class WaveSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = WaveSurfaceView.class.getSimpleName();

    private final Object mSurfaceLock = new Object();
    private DrawThread mThread;
    private SurfaceHolder mHolder;

    private Paint mPaint;
    private int color = 0xFFFF6D26;

    private int minHeight = 40;
    private int halfHeight;
    private int lineW = 2;
    private int spaceW = 4;
    private int startX = 0;
    private int[] waves;
    private int[] wavesTarget;

    //最大音量
    private int maxVolume = 100;
    //一般高度上线
    private int halfMaxH;
    //当前音量（半个view的高度）
    private int currentVolume;

    //
    private ValueAnimator animSum;
    private ValueAnimator[] animators;
    // 动画持续时间
    private long durationSum = 1000;
    // 动画持续时间
    private long duration = 400;
    //是否可绘制
    private boolean canDraw;
    //动画结束可进行下一个动画
    private boolean nextPrepared;

    private boolean isFirst = true;

    public WaveSurfaceView(Context context) {
        this(context, null);
    }

    public WaveSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveSurfaceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    // 初始化
    private void init(Context context, AttributeSet attrs) {
//        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WavePointView);
//        startColor = a.getColor(R.styleable.WavePointView_wp_start_color, DEF_START_COLOR);
//        endColor = a.getColor(R.styleable.WavePointView_wp_end_color, DEF_END_COLOR);
//        lineW = DisplayUtils.dp2px(context, a.getDimension(R.styleable.WavePointView_wp_line_width, DEF_LINE_WIDTH));
//        speed = DisplayUtils.dp2px(context, a.getInteger(R.styleable.WavePointView_wp_speed, DEF_SPEED));
//        amplitude = a.getFloat(R.styleable.WavePointView_wp_amplitude, DEF_AMPLITUDE);
//        amplitudeP = a.getFloat(R.styleable.WavePointView_wp_amplitude_radio, DEF_AMPLITUDE_RADIO);

        mHolder = getHolder();
        mHolder.addCallback(this);

        minHeight = DisplayUtils.dp2px(context, minHeight);
        lineW = DisplayUtils.dp2px(context, lineW);
        spaceW = DisplayUtils.dp2px(context, spaceW);

        //初始化画笔
        initPaint();

        //初始化动画
        initAnim();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(color);
        mPaint.setStrokeWidth(lineW);
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Style.FILL);
    }

    private void initAnim() {
        //总动画
        animSum = ValueAnimator.ofFloat(0, 1);
        animSum.setDuration(durationSum);
        animSum.setRepeatMode(ValueAnimator.RESTART);
        animSum.setRepeatCount(ValueAnimator.INFINITE);
        animSum.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float progress = (float) animation.getAnimatedValue();
                        if (progress == 0) {
                            nextPrepared = false;
                        }
                    }
                });
        animSum.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
//                Log.e(TAG, "onAnimationStart: nextPrepared=" + nextPrepared);
                nextPrepared = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
//                Log.e(TAG, "onAnimationEnd: nextPrepared=" + nextPrepared);
                nextPrepared = false;
                Arrays.fill(waves, 0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
//                Log.e(TAG, "onAnimationCancel: nextPrepared=" + nextPrepared);
                nextPrepared = false;
                Arrays.fill(waves, 0);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
//                Log.e(TAG, "onAnimationRepeat: nextPrepared=" + nextPrepared);
                nextPrepared = true;

            }
        });
    }

    private void initAnimS(int maxLines) {
        animators = new ValueAnimator[maxLines];
        for (int i = 0; i < maxLines; i++) {
            final MyValueAnimator animator = new MyValueAnimator();
            animator.setDuration(duration);
            animator.setRepeatMode(ValueAnimator.RESTART);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setTag(i);
            animator.addUpdateListener(new MyValueAnimator.MyAnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation, int tag) {
                    Log.e(TAG, "onAnimationUpdate: tag=" +tag+ "getAnimatedValue=" + animation.getAnimatedValue());
                    waves[tag] = (int) (((float) animation.getAnimatedValue()) * wavesTarget[tag]);
//                    waves[tag] = (Integer) animation.getAnimatedValue();
                }
            });

            animators[i] = animator;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int maxHeight = Math.max(minHeight, getMeasuredHeight());
        setMeasuredDimension(getMeasuredWidth(), maxHeight);
    }

    // 大小改变
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        halfHeight = h / 2;
        Log.e(TAG, "onSizeChanged: w=" + w);
        Log.e(TAG, "onSizeChanged: lineW=" + lineW);
        Log.e(TAG, "onSizeChanged: spaceW=" + spaceW);
        int maxLines = w / (lineW + spaceW);
        Log.e(TAG, "onSizeChanged: maxLines=" + maxLines);

        if (waves == null) {
            waves = new int[maxLines];
            wavesTarget = new int[maxLines];
            initAnimS(maxLines);
        }

        halfMaxH = halfHeight - lineW / 2;
        // 绘画起始位置
        startX = (w + spaceW - maxLines * (lineW + spaceW) + lineW) / 2;
        Log.e(TAG, "onSizeChanged: startX=" + startX);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //设置画布  背景透明
        setZOrderOnTop(true);
        mHolder.setFormat(PixelFormat.TRANSLUCENT);

        syncDraw();
        if (isFirst) {
            isFirst = false;
            //动画准备完成
            nextPrepared = true;
        }

        mThread = new DrawThread(holder);
        if (!mThread.isAlive()) {
            mThread.start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        synchronized (mSurfaceLock) {  //这里需要加锁，否则doDraw中有可能会crash
            canDraw = false;
            onDestroy();
        }
    }

    private class DrawThread extends Thread {
        private SurfaceHolder mHolder;
//        private boolean mIsRun = false;

        public DrawThread(SurfaceHolder holder) {
            super(TAG);
            mHolder = holder;
        }

        @Override
        public void run() {
            Log.e(TAG, "run: ************");
            while (true) {
                syncDraw();
            }
//            while (canDraw) {
//                synchronized (mSurfaceLock) {
//                    if (!mIsRun) {
//                        return;
//                    }
//                    Canvas canvas = mHolder.lockCanvas();
//                    if (canvas != null) {
//                        //绘制内容
//                        doDraw(canvas);
//                        mHolder.unlockCanvasAndPost(canvas);
//                    }
//                }
//                try {
//                    Thread.sleep(SLEEP_TIME);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
        }
    }

    private class SurfaceRunnable implements Runnable {
        @Override
        public void run() {
            while (true) {
                syncDraw();
            }
        }
    }

    private void syncDraw() {
        synchronized (mSurfaceLock) {
            if (!canDraw) return;
            Canvas canvas = null;
            try {
                canvas = mHolder.lockCanvas();
                //绘制内容
                doDraw(canvas);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null)
                    mHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    /**
     * 绘制内容
     */
    private void doDraw(Canvas canvas) {
        //清除之前绘制内容
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
        //移动画笔到中心高度
        canvas.translate(startX, halfHeight);
        Log.e(TAG, "doDraw: waves=" + Arrays.toString(waves));
        Log.e(TAG, "doDraw: wavesTarget=" + Arrays.toString(wavesTarget));
        for (int i = 0; i < waves.length; i++) {
            int x = i * (lineW + spaceW);
            canvas.drawLine(x, -waves[i], x, waves[i], mPaint);
        }
    }

    public void setVolume(int volume) {
        canDraw = true;
        if (!nextPrepared) return;
        Log.e(TAG, "setVolume: ************************ volume=" + volume + "   canDraw=" + canDraw);

        if (volume >= 3000) {
            volume = maxVolume;
        } else {
            volume = (int) (volume / 30f);
        }

        if (volume >= halfMaxH) {
            currentVolume = halfMaxH;
        } else if (volume > lineW / 2) {
            currentVolume = halfHeight * volume / maxVolume;
        } else {
            currentVolume = 0;
        }

        for (int i = 0; i < wavesTarget.length; i++) {
            float nextFloat = ThreadLocalRandom.current().nextFloat();
//            wavesTarget[i] = currentVolume == 0 ? 0 : (int) (nextFloat * currentVolume);
            wavesTarget[i] = currentVolume == 0 ? 0 : ThreadLocalRandom.current().nextInt(currentVolume);

            animators[i].setFloatValues(0, 1, 0);
            animators[i].setStartDelay((long) (nextFloat * duration));
            animators[i].start();
        }

        //开始动画
        if (!animSum.isStarted()) {
            animSum.start();
        }
    }

    /**
     * 重置
     */
    public void resetView() {
        canDraw = false;
        syncDraw();
    }

    public void onDestroy() {
        if (animSum != null) {
            animSum.cancel();
        }
    }
}
