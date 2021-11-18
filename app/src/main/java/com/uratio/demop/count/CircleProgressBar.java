package com.uratio.demop.count;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.uratio.demop.R;
import com.uratio.demop.utils.DisplayUtils;
import com.uratio.demop.utils.LogUtils;

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
    private Paint mPaintSymbol;

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
        //符号文字字体大小
        private int symbolTextSize;

    //进度条的矩形区域
    private RectF mArcRect = new RectF();
    //View的显示区域。
    final Rect bounds = new Rect();

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

    private void initialize(Context context, AttributeSet attributeSet) {
        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.CircleProgressBar);

        max = a.getInteger(R.styleable.CircleProgressBar_cpb_max, DEF_MAX);
        progress = a.getInteger(R.styleable.CircleProgressBar_cpb_progress, 0);
        progressBgColor = a.getColor(R.styleable.CircleProgressBar_cpb_progress_bg_color, DEF_PROGRESS_BG_COLOR);
        progressColor = a.getColor(R.styleable.CircleProgressBar_cpb_progress_color, DEF_PROGRESS_COLOR);
        progressWidth = DisplayUtils.dp2px(a.getDimension(R.styleable.CircleProgressBar_cpb_progress_width, DEF_PROGRESS_WIDTH));
        textColor = a.getColor(R.styleable.CircleProgressBar_cpb_text_color, DEF_TEXT_COLOR);
        textSize = a.getDimensionPixelSize(R.styleable.CircleProgressBar_cpb_text_size,
                context.getResources().getDimensionPixelSize(R.dimen.font_16));
        symbolTextSize = a.getDimensionPixelSize(R.styleable.CircleProgressBar_cpb_symbol_text_size,
                context.getResources().getDimensionPixelSize(R.dimen.font_12));

        a.recycle();


        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(progressWidth);

        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setAntiAlias(true);
        mPaintText.setColor(textColor);
        mPaintText.setTextSize(textSize);

        mPaintSymbol = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintSymbol.setAntiAlias(true);
        mPaintSymbol.setColor(textColor);
        mPaintSymbol.setTextSize(symbolTextSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int size = Math.min(width, height);
        LogUtils.e("width=" + width);
        LogUtils.e("height=" + height);
        LogUtils.e("size=" + size);
//        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //获取view的边界
        getDrawingRect(bounds);

//        canvas.drawColor(0xFF00B8D4);
//        canvas.drawLine(0, bounds.centerX() - 0.5f, bounds.right, bounds.centerX() + 0.5f, mPaintText);
//        canvas.drawLine(bounds.centerY() - 0.5f, 0, bounds.centerY() + 0.5f, bounds.bottom, mPaintText);

        int size = Math.min(bounds.height(), bounds.width());
        float radius = size / 2f;


        //画内部背景
//        mPaint.setStyle(Paint.Style.FILL);
//        mPaint.setColor(inCircleColors);
//        canvas.drawCircle(bounds.centerX(), bounds.centerY(), radius - progressWidth, mPaint);

        //进度条背景
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(progressBgColor);
        canvas.drawCircle(bounds.centerX(), bounds.centerY(), radius - progressWidth / 2, mPaint);

        //画进度条
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(progressColor);
        mArcRect.set(bounds.centerX() - radius + progressWidth / 2, bounds.centerY() - radius + progressWidth / 2,
                bounds.centerX() + radius - progressWidth / 2, bounds.centerY() + radius - progressWidth / 2);
        canvas.drawArc(mArcRect, -90, 360f * progress / max, false, mPaint);

        //文字
        Rect boundText = new Rect();
        String str = progress > 0 ? (progress + "") : "0";
        mPaintText.getTextBounds(str, 0, str.length(), boundText);

        float textX = bounds.centerX() - (boundText.right - boundText.left) / 2f;
        float textY = bounds.centerY() - (boundText.bottom + boundText.top) / 2f;

        String symbol = "%";
        Rect boundSymbol = new Rect();
        mPaintSymbol.getTextBounds(symbol, 0, symbol.length(), boundSymbol);

        Paint.FontMetrics fontMetrics = new Paint.FontMetrics();
        mPaintText.getFontMetrics(fontMetrics);

//        float textX = bounds.centerX();
//        float textY = bounds.centerY() - (fontMetrics.descent + fontMetrics.ascent) / 2;

        //文字起始位置 x 坐标
        float symbolHalf = (boundSymbol.right - boundSymbol.left) / 2f;
//        float symbolHalf = 0;

        //绘制文字
        canvas.drawText(str, textX - symbolHalf, textY, mPaintText);
        //绘制符号
        canvas.drawText(symbol, textX - symbolHalf + (boundText.right - boundText.left), textY, mPaintSymbol);

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
