package com.uratio.demop.ripple;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.uratio.demop.R;
import com.uratio.demop.utils.DisplayUtils;

/**
 * @author lang
 * @data 2021/4/15
 */
public class RecordButton extends View {
    private static final String TAG = RecordButton.class.getSimpleName();
    private static final int DEF_START_COLOR = 0xFF13E4F4;
    private static final int DEF_END_COLOR = 0xFF266BDE;
    private static final long DEF_DURATION = 2000;
    private static final float DEF_SCOPE = 0.8f;
    private static final float DEF_LINE_WIDTH = 2;
    // 画笔
    private Paint mPaint;
    // 渐变颜色
    private int startColor;
    private int endColor;
    // 动画持续时间
    private long duration;
    // 变化范围：0 ~ 1
    private float scope;
    // 圆的线宽
    private float lineW;
    private LinearGradient gradient;
    private Rect rect;

    private float centerX;
    private float centerY;
    private float size;
    private float radius;
    private float interval;
    private float radiusOne;
    private float radiusTwo;
    private float lineWOne;
    private float lineWTwo;
    private float alphaOne;
    private float alphaTwo;
    private Bitmap bmButton;
    private Bitmap bmSpeaking;
    private boolean isButton = true;

    private RecordButtonListener listener;

    public void setRecordButtonListener(RecordButtonListener listener) {
        this.listener = listener;
    }

    public RecordButton(Context context) {
        this(context, null);
    }

    public RecordButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RecordButton);
        startColor = a.getColor(R.styleable.RecordButton_rb_startColor, DEF_START_COLOR);
        endColor = a.getColor(R.styleable.RecordButton_rb_endColor, DEF_END_COLOR);
        duration = (long) a.getDimension(R.styleable.RecordButton_rb_duration, DEF_DURATION);
        scope = a.getFloat(R.styleable.RecordButton_rb_scope, DEF_SCOPE);
        lineW = DisplayUtils.dp2px(context, a.getDimension(R.styleable.RecordButton_rb_line_width, DEF_LINE_WIDTH));

        init(context);
    }

    private void init(Context context) {
        // 创建画笔
        mPaint = new Paint();
        // 设置画笔颜色
        mPaint.setColor(0xFF000000);
        // 设置绘画风格为描边
        mPaint.setStyle(Paint.Style.STROKE);
        // 抗锯齿
        mPaint.setAntiAlias(true);

        bmButton = BitmapFactory.decodeResource(context.getResources(), R.drawable.vna_icon_voice_btn);
        bmSpeaking = BitmapFactory.decodeResource(context.getResources(), R.drawable.vna_icon_speaking);

        rect = new Rect();

        start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2f;
        centerY = h / 2f;
        size = Math.min(w, h);
        radius = size * 114 / 160 / 2f;

        interval = size / 2 - radius;
        radiusOne = radius;
        radiusTwo = radius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isButton) {
            float x = (float) (radiusOne * Math.sin(Math.toRadians(45))) + lineWOne;

            gradient = new LinearGradient(centerX + x, centerY - x, centerX - x, centerY + x,
                    startColor, endColor, Shader.TileMode.REPEAT);
            mPaint.setShader(gradient);

            mPaint.setAlpha((int) (alphaOne * 255 * scope));
            mPaint.setStrokeWidth(lineWOne);
            canvas.drawCircle(centerX, centerY, radiusOne, mPaint);


            x = (float) (radiusTwo * Math.sin(Math.toRadians(45))) + lineWTwo;

            gradient = new LinearGradient(centerX + x, centerY - x, centerX - x, centerY + x,
                    startColor, endColor, Shader.TileMode.REPEAT);
            mPaint.setShader(gradient);

            mPaint.setAlpha((int) (alphaTwo * 255 * scope));
            mPaint.setStrokeWidth(lineWTwo);
            canvas.drawCircle(centerX, centerY, radiusTwo, mPaint);

            mPaint.setAlpha(255);

            rect.left = (int) (centerX - radius);
            rect.top = (int) (centerY - radius);
            rect.right = (int) (centerX + radius);
            rect.bottom = (int) (centerY + radius);

            canvas.drawBitmap(bmButton, null, rect, mPaint);
        } else {
            float halfS = size / 2f;
            rect.left = (int) (centerX - halfS);
            rect.top = (int) (centerY - halfS);
            rect.right = (int) (centerX + halfS);
            rect.bottom = (int) (centerY + halfS);
            canvas.drawBitmap(bmSpeaking, null, rect, mPaint);
        }
    }

    public void start() {
        final ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(duration);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float progress = (float) animation.getAnimatedValue();
                        Log.e(TAG, "onAnimationUpdate: progress=" + progress);
                        alphaOne = 1 - progress * scope;
                        radiusOne = radius + interval * progress;
                        lineWOne = alphaOne * lineW;
                        float progress2;
                        if (progress >= 0.5f) {
                            progress2 = progress - 0.5f;
                        } else {
                            progress2 = progress + 0.5f;
                        }
                        alphaTwo = 1 - progress2 * scope;
                        radiusTwo = radius + interval * progress2;
                        lineWTwo = alphaTwo * lineW;
                        invalidate();
                    }
                });
        animator.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (x >= centerX - size && x <= centerX + size && y >= centerY - size && y <= centerY + size){
                    if (isButton) {
                        // 点击后切换图片
//                        setButton(false);
                        if (listener != null) {
                            listener.onClickButton();
                        }
                    }
                    setButton(!isButton);
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    public void setButton(boolean button) {
        isButton = button;
        invalidate();
    }

    public interface RecordButtonListener {
        void onClickButton();
    }
}
