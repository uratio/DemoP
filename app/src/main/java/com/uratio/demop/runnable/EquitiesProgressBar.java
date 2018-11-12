package com.uratio.demop.runnable;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.uratio.demop.R;

public class EquitiesProgressBar extends View {
    public static final int DEFAULT_COLOR = 0xFFEC7C35;
    public static final int DEFAULT_PROFRESS_COLOR = 0xFFFFFFFF;
    public static final int DEFAULT_TEXT_BG_COLOR = 0x88FF532B;
    public static final int DEFAULT_TEXT_COLOR = 0xFFFF532B;
    public static final float DEFAULT_TEXT_SIZE = 12;
    public static final float DEFAULT_WIDTH = 60;
    public static final float DEFAULT_HEIGHT = 30;
    public static final float DEFAULT_TEXT_BG_WIDTH = 100;
    public static final float DEFAULT_TEXT_BG_HEIGHT = 30;
    public static final float DEFAULT_LINE_HEIGHT = 25;
    public static final int DEFAULT_MAX = 100;
    public static final int DEFAULT_PROGRESS = 0;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float headerWidth;
    private float headerHeight;
    private float radius;
    private int barColor;
    private float lineHeight;
    private float progressTextBgradius;
    private int progressTextBgColor;
    private int progressTextColor;
    private float progressTextSize;
    private float progressTextBgWidth;
    private float progressTextBgHeight;

    private int max;
    private int progress;

    /**
     * 要画的文字的宽度
     */
    private int textWidth;

    /**
     * 画文字时底部的坐标
     */
    private float textBottomY;

    public EquitiesProgressBar(Context context) {
        super(context);
        init(context, null);
    }

    public EquitiesProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EquitiesProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EquitiesProgressBar);

        headerWidth = a.getDimension(R.styleable.EquitiesProgressBar_headerWidth, DEFAULT_WIDTH);
        headerHeight = a.getDimension(R.styleable.EquitiesProgressBar_headerHeight, DEFAULT_HEIGHT);
        radius = headerHeight / 2;
        barColor = a.getColor(R.styleable.EquitiesProgressBar_barBgColor, DEFAULT_COLOR);
        lineHeight = a.getDimension(R.styleable.EquitiesProgressBar_lineHeight, DEFAULT_LINE_HEIGHT);
        progressTextBgColor = a.getColor(R.styleable.EquitiesProgressBar_progressTextBgColor, DEFAULT_TEXT_BG_COLOR);
        progressTextColor = a.getColor(R.styleable.EquitiesProgressBar_progressTextColor, DEFAULT_TEXT_COLOR);
        progressTextSize = a.getDimension(R.styleable.EquitiesProgressBar_progressTextSize, DEFAULT_TEXT_SIZE);
        progressTextBgWidth = a.getDimension(R.styleable.EquitiesProgressBar_progressTextBgWidth, DEFAULT_TEXT_BG_WIDTH);
        progressTextBgHeight = a.getDimension(R.styleable.EquitiesProgressBar_progressTextBgHeight, DEFAULT_TEXT_BG_HEIGHT);
        progressTextBgradius = progressTextBgHeight / 4;
        max = a.getInteger(R.styleable.EquitiesProgressBar_max, DEFAULT_MAX);
        progress = a.getInteger(R.styleable.EquitiesProgressBar_progress, DEFAULT_PROGRESS);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        // 起点
        mPaint.setColor(barColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(0, progressTextBgHeight + lineHeight, headerWidth, height, radius, radius, mPaint);
        // 终点
        if (progress == max) {
            mPaint.setStyle(Paint.Style.FILL);
        } else {
            mPaint.setStyle(Paint.Style.STROKE);
        }
        canvas.drawRoundRect(width - headerWidth, progressTextBgHeight + lineHeight, width, height, radius, radius, mPaint);
        //起点文字
        String pro = progress+"";
//        canvas.drawText(progress, progressX - textWidth / 2, progressTextBgHeight / 2 - textBottomY / 2, mPaint);//文字
        //终点文字
        //        canvas.drawText(progressText, progressX - textWidth / 2, progressTextBgHeight / 2 - textBottomY / 2, mPaint);//文字

        //进度条背景
        mPaint.setColor(barColor);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(headerWidth, progressTextBgHeight + lineHeight, width - headerWidth, height, mPaint);
        //当前进度
        mPaint.setColor(barColor);
        mPaint.setStyle(Paint.Style.FILL);
        float progressX = (width - headerWidth) * progress / max;
        canvas.drawRect(headerWidth, progressTextBgHeight + lineHeight, progressX, height, mPaint);
        //进度文字背景

        //进度文字背景
        mPaint.setColor(progressTextBgColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(headerWidth, progressTextBgHeight + lineHeight, progressX, height, progressTextBgradius, progressTextBgradius, mPaint);
        //进度文字
        String progressText = progress + "个月";
        Rect bounds = new Rect();
        mPaint.getTextBounds(progressText, 0, progressText.length(), bounds);
        textWidth = bounds.width();
        textBottomY = bounds.height();
        mPaint.setColor(progressTextColor);
        mPaint.setTextSize(progressTextSize);
        canvas.drawText(progressText, progressX - textWidth / 2, progressTextBgHeight / 2 - textBottomY / 2, mPaint);//文字
        //中间连接线
        mPaint.setColor(barColor);
        canvas.drawLine(progressX,progressTextBgHeight,progressX,progressX+lineHeight,mPaint);
    }

    /*设置进度条进度, 外部调用*/
    public void setProgress(int progress) {
        if (progress > max) {
            this.progress = max;
        } else if (progress < 0) {
            this.progress = 0;
        } else {
            this.progress = progress;
        }
        postInvalidate();
    }
}
