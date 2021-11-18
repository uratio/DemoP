package com.uratio.demop.wave.voice;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
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

import com.uratio.demop.R;
import com.uratio.demop.utils.DisplayUtils;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author lang
 * @data 2021/5/13
 */
public class WaveSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = WaveSurfaceView.class.getSimpleName();
    // 最小高度
    private static final float DEF_MIN_HEIGHT = 80;
    // 线条颜色
    private static final int DEF_COLOR = 0xFFFF6D26;
    // 线条宽度
    private static final float DEF_LINE_WIDTH = 2;
    // 线条间距
    private static final float DEF_LINE_SPACE = 4;
    // 最大音量
    private static final int MAX_VOLUME = 3000;
    // 整体动画持续时间
    private static final long DURATION_SUM = 1000;
    // 单个动画持续时间
    private static final long DURATION_ITEM = 400;

    private final Object mSurfaceLock = new Object();
    private DrawThread mThread;
    private SurfaceHolder mHolder;

    private Paint mPaint;
    private int color;

    private float minHeight;
    private int halfHeight;
    private int lineW;
    private int spaceW;
    private int startX = 0;
    private int[] waves;
    private int[] wavesTarget;

    private ValueAnimator animSum;
    private ValueAnimator[] animators;
    //是否可绘制
    private boolean canDraw;
    //动画结束可进行下一个动画
    private boolean nextPrepared;

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
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WaveSurfaceView);
        minHeight = a.getDimension(R.styleable.WaveSurfaceView_wsv_min_height, DEF_MIN_HEIGHT);
        color = a.getColor(R.styleable.WaveSurfaceView_wsv_color, DEF_COLOR);
        lineW = DisplayUtils.dp2px(a.getDimension(R.styleable.WaveSurfaceView_wsv_line_width, DEF_LINE_WIDTH));
        spaceW = DisplayUtils.dp2px(a.getDimension(R.styleable.WaveSurfaceView_wsv_space_width, DEF_LINE_SPACE));

        mHolder = getHolder();
        mHolder.addCallback(this);

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
        animSum.setDuration(DURATION_SUM);
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
                nextPrepared = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                nextPrepared = false;
                Arrays.fill(waves, 0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                nextPrepared = false;
                Arrays.fill(waves, 0);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                nextPrepared = true;
            }
        });
    }

    private void initAnimS(int maxLines) {
        animators = new ValueAnimator[maxLines];
        for (int i = 0; i < maxLines; i++) {
            final TagValueAnimator animator = new TagValueAnimator();
            animator.setDuration(DURATION_ITEM);
            animator.setRepeatMode(ValueAnimator.RESTART);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setTag(i);
            animator.addUpdateListener(new TagValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation, int tag) {
                    waves[tag] = (Integer) animation.getAnimatedValue();
                }
            });

            animators[i] = animator;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int maxHeight = (int) Math.max(minHeight, getMeasuredHeight());
        setMeasuredDimension(getMeasuredWidth(), maxHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        halfHeight = h / 2;
        int maxLines = w / (lineW + spaceW);

        if (waves == null) {
            waves = new int[maxLines];
            wavesTarget = new int[maxLines];
            initAnimS(maxLines);
        }

        // 绘画起始位置
        startX = (w + spaceW - maxLines * (lineW + spaceW) + lineW) / 2;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //设置画布  背景透明
        setZOrderOnTop(true);
        mHolder.setFormat(PixelFormat.TRANSLUCENT);

        canDraw = true;
        //动画准备完成
        nextPrepared = true;

        if (waves.length > 0) {
            Arrays.fill(waves, 0);
        }
        //绘制初始状态
        syncDraw();

        if (mThread == null) {
            mThread = new DrawThread();
            mThread.start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        synchronized (mSurfaceLock) {  //这里需要加锁，否则doDraw中有可能会crash
            onDestroy();
        }
    }

    private class DrawThread extends Thread {

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

        //转换为实际可变高度
        if (volume >= MAX_VOLUME) {
            volume = halfHeight - lineW / 2;
        } else {
            volume = halfHeight * volume / MAX_VOLUME;
        }

        //开始动画
        if (!animSum.isStarted()) {
            animSum.start();
        }

        for (int i = 0; i < wavesTarget.length; i++) {
            float nextFloat = ThreadLocalRandom.current().nextFloat();
            wavesTarget[i] = volume == 0 ? 0 : (int) (nextFloat * volume);

            animators[i].setIntValues(0, wavesTarget[i], 0);
            animators[i].setStartDelay((long) (nextFloat * DURATION_ITEM));
            if (!animators[i].isStarted()) {
                animators[i].start();
            }
        }
    }

    /**
     * 重置
     */
    public void resetView() {
        onDestroy();
        syncDraw();
    }

    public void onDestroy() {
        canDraw = false;
        for (int i = 0; i < animators.length; i++) {
            animCancel(animators[i]);
        }
        animCancel(animSum);
    }

    private void animCancel(ValueAnimator animator) {
        if (animator != null && animator.isStarted()) {
            animator.end();
        }
    }
}
