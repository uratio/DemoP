package com.uratio.demop.count;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.uratio.demop.R;
import com.uratio.demop.utils.DisplayUtils;

public class CircleProgressBar extends View {
    public static final int DEF_PROGRESS_BG_COLOR = 0xFFEEEEEE;
    public static final int DEF_PROGRESS_COLOR = 0xFFFF6D26;
    public static final float DEF_PROGRESS_WIDTH = 6;
    public static final int DEF_TEXT_COLOR = 0xFFFF6D26;
    public static final int DEF_MAX = 100;

    //画笔-进度条
    private Paint mPaint;
    //画笔-文字
    private Paint mPaintText;

    //进度最大值
    private int max;
    //进度
    private int progress;
    //进度条背景颜色
    private int progressBgColor;
    //进度条颜色
    private int progressColor;
    //进度条宽度
    private float progressWidth;
    //文字
    private String text;
    //文字颜色
    private int textColor;
    //文字字体大小
    private int textSize;

    //进度条的矩形区域
    private RectF mArcRect = new RectF();

    public CircleProgressBar(Context context) {
        this(context, null);
    }

    public CircleProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context, attrs);
    }


    private void initialize(Context context, AttributeSet attributeSet) {
        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.CircleProgressBar);

        max = a.getInteger(R.styleable.CircleProgressBar_cpb_max, DEF_MAX);
        progress = a.getInteger(R.styleable.CircleProgressBar_cpb_progress, 0);
        progressBgColor = a.getColor(R.styleable.CircleProgressBar_cpb_progress_bg_color, DEF_PROGRESS_BG_COLOR);
        progressColor = a.getColor(R.styleable.CircleProgressBar_cpb_progress_color, DEF_PROGRESS_COLOR);
        progressWidth = DisplayUtils.dp2px(context, a.getDimension(R.styleable.CircleProgressBar_cpb_progress_width, DEF_PROGRESS_WIDTH));
        textColor = a.getColor(R.styleable.CircleProgressBar_cpb_text_color, DEF_TEXT_COLOR);
        textSize = a.getDimensionPixelSize(R.styleable.CircleProgressBar_cpb_text_size, 44);

        a.recycle();


        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(progressWidth);

        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setAntiAlias(true);
        mPaintText.setColor(textColor);
        mPaintText.setTextSize(textSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int size = Math.min(width, height);
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int radius = width / 2;

        canvas.drawColor(0xFF00C853);

        //画内部背景
//        mPaint.setStyle(Paint.Style.FILL);
//        mPaint.setColor(inCircleColors);
//        canvas.drawCircle(bounds.centerX(), bounds.centerY(), radius - progressWidth, mPaint);

        //进度条背景
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(progressBgColor);
        canvas.drawCircle(radius, radius, radius - progressWidth / 2, mPaint);

        //画进度条
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(progressColor);

        mArcRect.set(progressWidth / 2, progressWidth / 2, width - progressWidth / 2, width - progressWidth / 2);
        canvas.drawArc(mArcRect, -90, 360 * progress / max, false, mPaint);

        //文字
        Rect bounds = new Rect();
        String str = TextUtils.isEmpty(text) ? (progress + "%") : text;
        mPaintText.getTextBounds(str, 0, str.length(), bounds);
        float offSet = (bounds.top + bounds.bottom) / 2;
        canvas.drawText(str, radius - (bounds.left + bounds.right) / 2, radius - offSet, mPaintText);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
    }

    /**
     * 设置进度条值
     */
    public void setProgress(int progress) {
        this.progress = validateProgress(progress);
        invalidate();
    }

    /**
     * 验证 progress 有效性
     */
    private int validateProgress(int progress) {
        if (progress > max)
            progress = max;
        else if (progress < 0)
            progress = 0;
        return progress;
    }

    /**
     * 获取进度值
     */
    public int getProgress() {
        return progress;
    }
}
