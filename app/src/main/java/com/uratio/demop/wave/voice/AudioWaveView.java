package com.uratio.demop.wave.voice;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.uratio.demop.R;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author lang
 * @data 2021/5/13
 */
public class AudioWaveView extends View {
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
    //是否展示初始状态
    private boolean showStart = true;
    //动画结束可进行下一个动画
    private boolean nextPrepared = true;
    //是否为第一次启动总动画（第一次启动时可直接开始item动画）
    private boolean isFistStart = false;

    public AudioWaveView(Context context) {
        this(context, null);
    }

    public AudioWaveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioWaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    // 初始化
    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AudioWaveView);
        minHeight = a.getDimension(R.styleable.AudioWaveView_awv_min_height, DEF_MIN_HEIGHT);
        color = a.getColor(R.styleable.AudioWaveView_awv_color, DEF_COLOR);
        lineW = dp2px(context, a.getDimension(R.styleable.AudioWaveView_awv_line_width, DEF_LINE_WIDTH));
        spaceW = dp2px(context, a.getDimension(R.styleable.AudioWaveView_awv_space_width, DEF_LINE_SPACE));

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
                        if (progress == 0 && !isFistStart) {
                            nextPrepared = false;
                        }
                    }
                });
        animSum.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                showStart = false;
                nextPrepared = true;
                isFistStart = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                showStart = true;
                nextPrepared = false;
                isFistStart = false;
                invalidate();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                showStart = true;
                nextPrepared = false;
                isFistStart = false;
                invalidate();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                nextPrepared = true;
                isFistStart = false;
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
                    invalidate();
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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //移动画笔到中心高度
        canvas.translate(startX, halfHeight);
        for (int i = 0; i < waves.length; i++) {
            int x = i * (lineW + spaceW);
            if (showStart) {
                canvas.drawLine(x, 0, x, 0, mPaint);
            } else {
                canvas.drawLine(x, -waves[i], x, waves[i], mPaint);
            }
        }
    }

    public void setVolume(int volume) {
        //开始动画
        if (!animSum.isStarted()) {
            animSum.start();
        }

        if (!nextPrepared) return;

        //转换为实际可变高度
        if (volume >= MAX_VOLUME) {
            volume = halfHeight - lineW / 2;
        } else {
            volume = halfHeight * volume / MAX_VOLUME;
        }

        for (int i = 0; i < wavesTarget.length; i++) {
            float nextFloat = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                nextFloat = ThreadLocalRandom.current().nextFloat();
            } else {
                nextFloat = new Random().nextFloat();
            }
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
        if (animators != null) {
            for (int i = 0; i < animators.length; i++) {
                animCancel(animators[i]);
            }
        }
        animCancel(animSum);
    }

    private void animCancel(ValueAnimator animator) {
        if (animator != null && animator.isStarted()) {
            animator.end();
        }
    }

    private int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }
}
