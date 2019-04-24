package com.uratio.demop.runnable;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.uratio.demop.R;

/**
 * Created by lankton on 16/1/8.
 */
public class CustomProgressBar extends View {

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int barColor;
    private int backColor;
    private int textColorProgress;
    private int textColorMax;
    private float textSizeRadio;
    private float radius;
    private String tvMax = "";
    private String tvProgress = "";

    int max = 100;
    int progress = 0;

    public CustomProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        /*获取自定义参数的颜色值*/
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomProgressBar, defStyle, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.CustomProgressBar_backColor:
                    backColor = a.getColor(attr, Color.GRAY);
                    break;
                case R.styleable.CustomProgressBar_barColor:
                    barColor = a.getColor(attr, Color.GREEN);
                    break;
                case R.styleable.CustomProgressBar_textColorProgress:
                    textColorProgress = a.getColor(attr, Color.WHITE);
                    break;
                case R.styleable.CustomProgressBar_textColorMax:
                    textColorMax = a.getColor(attr, Color.WHITE);
                    break;
                case R.styleable.CustomProgressBar_textSizeRadio:
                    textSizeRadio = 1.5f;
                    break;
                case R.styleable.CustomProgressBar_cProgress:
                    progress = a.getInteger(attr,0);
                    break;
            }

        }
        a.recycle();
    }

    public CustomProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomProgressBar(Context context) {
        this(context, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        radius = this.getMeasuredHeight() / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //背景
        mPaint.setColor(backColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(new RectF(0, 0, this.getMeasuredWidth(), this.getMeasuredHeight()), radius, radius, mPaint);
        //总进度
        mPaint.setColor(textColorMax);
        mPaint.setTextSize(this.getMeasuredHeight() / textSizeRadio);
        float xMax = this.getMeasuredWidth() - mPaint.measureText(tvMax) - 10;
        float yMax = this.getMeasuredHeight() / 2f - mPaint.getFontMetrics().ascent / 2f - mPaint.getFontMetrics().descent / 2f;
        canvas.drawText(tvMax, xMax, yMax, mPaint);
        //进度条
        mPaint.setColor(barColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(new RectF(0, 0, this.getMeasuredWidth() * progress / max, this.getMeasuredHeight()), radius, radius, mPaint);
        //进度
        mPaint.setColor(textColorProgress);
        mPaint.setTextSize(this.getMeasuredHeight() / textSizeRadio);
        float x = this.getMeasuredWidth() * progress / max - mPaint.measureText(tvProgress) - 10;
        float y = this.getMeasuredHeight() / 2f - mPaint.getFontMetrics().ascent / 2f - mPaint.getFontMetrics().descent / 2f;
        canvas.drawText(tvProgress, x, y, mPaint);
    }

    public void setBarColor(int barColor){
        this.barColor = barColor;
    }

    public void setMax(int max){
        if (max == 0){
            this.max = 1;
            return;
        }
        this.max = max;
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

    public void setTvProgress(String progress) {
        this.tvProgress = progress;
    }

    public void setTvMax(String progress) {
        this.tvMax = progress;
    }
}