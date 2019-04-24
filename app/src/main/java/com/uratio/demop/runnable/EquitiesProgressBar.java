package com.uratio.demop.runnable;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.uratio.demop.R;

public class EquitiesProgressBar extends View {
    public static final int DEFAULT_COLOR = 0xFFD5AD70;
    public static final int DEFAULT_PROGRESS_COLOR = 0xFFFFFFFF;
    public static final int DEFAULT_TEXT_BG_COLOR = 0xFFFEF4D9;
    public static final int DEFAULT_TEXT_COLOR = 0xFFD5AD70;
    public static final float DEFAULT_TEXT_SIZE = 13;
    public static final float DEFAULT_WIDTH = 25f;
    public static final float DEFAULT_HEIGHT = 15;
    public static final float DEFAULT_TEXT_BG_WIDTH = 50;
    public static final float DEFAULT_TEXT_BG_HEIGHT = 18;
    public static final float DEFAULT_LINE_HEIGHT = 17;
    public static final int DEFAULT_MAX = 12;
    public static final int DEFAULT_PROGRESS = 0;
    public static final int DEFAULT_RADIAN = 70;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path mPathBase;
    private Path mPath;

    //标尺宽
    private float headerWidth;
    //标尺高
    private float headerHeight;
    //标尺圆半径
    private float radius;
    //进度条高
    private float progressHeight;
    //进度颜色
    private int barColor;
    //连接线高度
    private float lineHeight;
    //进度文字颜色
    private int progressTextColor;
    //进度文字大小
    private float progressTextSize;
    //进度文字背景颜色
    private int progressTextBgColor;
    //进度文字背景宽度
    private float textBgWidth;
    //进度文字背景高度
    private float textBgHeight;
    //圆弧度数
    private int radian;
    //圆弧度数
    private float cutWidth;
    //左右间距
    int space = 1;

    private int max;
    private int progress;

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
        barColor = a.getColor(R.styleable.EquitiesProgressBar_barBgColor, DEFAULT_COLOR);
        lineHeight = a.getDimension(R.styleable.EquitiesProgressBar_lineHeight, DEFAULT_LINE_HEIGHT);
        progressTextBgColor = a.getColor(R.styleable.EquitiesProgressBar_progressTextBgColor, DEFAULT_TEXT_BG_COLOR);
        progressTextColor = a.getColor(R.styleable.EquitiesProgressBar_progressTextColor, DEFAULT_TEXT_COLOR);
        progressTextSize = a.getDimension(R.styleable.EquitiesProgressBar_progressTextSize, DEFAULT_TEXT_SIZE);
        textBgWidth = a.getDimension(R.styleable.EquitiesProgressBar_progressTextBgWidth, DEFAULT_TEXT_BG_WIDTH);
        textBgHeight = a.getDimension(R.styleable.EquitiesProgressBar_progressTextBgHeight, DEFAULT_TEXT_BG_HEIGHT);
        radian = a.getInteger(R.styleable.EquitiesProgressBar_dRadian, DEFAULT_RADIAN);
        max = a.getInteger(R.styleable.EquitiesProgressBar_max, DEFAULT_MAX);
        progress = a.getInteger(R.styleable.EquitiesProgressBar_progress, DEFAULT_PROGRESS);
        space = a.getInteger(R.styleable.EquitiesProgressBar_eSpace, 1);

        headerWidth = dip2px(context,headerWidth);
        headerHeight = dip2px(context,headerHeight);
        lineHeight = dip2px(context,lineHeight);
        progressTextSize = sp2px(context,progressTextSize);
        textBgWidth = dip2px(context,textBgWidth);
        textBgHeight = dip2px(context,textBgHeight);
        progressHeight = dip2px(context,progressHeight);
        space = dip2px(context,space);

        radius = headerHeight / 2;
        cutWidth = (float) (radius-radius*Math.sin(radian*3.14/180));
        progressHeight = (float) (radius*Math.cos(radian*3.14/180) * 2);

        mPathBase = new Path();
        mPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = (int) (textBgHeight+lineHeight+ (headerHeight - progressHeight)/2)+2;
        setMeasuredDimension(widthMeasureSpec,height);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        float top = textBgHeight +lineHeight-progressHeight/2;

        mPaint.setColor(barColor);
        mPaint.setStyle(Paint.Style.STROKE);


        /**
         * 进度底层
         */
        mPathBase.addArc(space,top-radius,2*radius+space,top+radius,-270,180);
        mPathBase.lineTo(headerWidth-radius+space,top-radius);

        mPathBase.addArc(headerWidth-2*radius+space,top-radius,headerWidth+space,top+radius,-90,radian);

        mPathBase.arcTo(width-headerWidth-space,top-radius,width-headerWidth-space+2*radius,top+radius,-160,radian,false);
        mPathBase.lineTo(width-radius-space,top-radius);
        mPathBase.addArc(width-2*radius-space,top-radius,width-space,top+radius,-90,180);
        mPathBase.lineTo(width-headerWidth-space+radius,top+radius);
        mPathBase.addArc(width-headerWidth-space,top-radius,width-headerWidth-space+2*radius,top+radius, -270, radian);

        mPathBase.arcTo(headerWidth-2*radius+space,top-radius,headerWidth+space,top+radius, -340, radian,false);
        mPathBase.lineTo(radius+space,top+radius);
        canvas.drawPath(mPathBase,mPaint);

        /**
         * 左侧圆角矩形
         */
        mPaint.setColor(progressTextColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPath.addRoundRect(space,top-radius,headerWidth+space,top+radius, radius, radius, Path.Direction.CW);
        canvas.drawPath(mPath,mPaint);



        Rect rectText = new Rect();
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        //第一个文字绘制有位置问题，暂时用一个透明的字占位抵消
        mTextPaint.setTextSize(progressTextSize);
        mTextPaint.setColor(Color.TRANSPARENT);
        canvas.drawText("第一个", 0,50-(rectText.top+rectText.bottom)/2,mTextPaint);

        /**
         * 右侧文字
         */
        String endText = max+"";
        mTextPaint.getTextBounds(endText, 0, endText.length(), rectText);
        mTextPaint.setTextSize(progressTextSize);
        if (progress == max) {
            /**
             * 右侧圆角矩形
             */
            mPaint.setColor(progressTextColor);
            mPaint.setStyle(Paint.Style.FILL);
            mPath.addRoundRect(width-headerWidth-space,top-radius,width-space,top+radius, radius, radius, Path.Direction.CW);
            canvas.drawPath(mPath,mPaint);

            mTextPaint.setColor(DEFAULT_PROGRESS_COLOR);
        }else {
            mTextPaint.setColor(progressTextColor);
        }
        //绘制文字
        canvas.drawText(endText, width-headerWidth/ 2-space, top-(rectText.top+rectText.bottom)/2, mTextPaint);

        /**
         * 左侧文字
         */
        String startText = "0";
        mTextPaint.getTextBounds(startText, 0, startText.length(), rectText);
        mTextPaint.setColor(DEFAULT_PROGRESS_COLOR);
        mTextPaint.setTextSize(progressTextSize);
        canvas.drawText(startText, headerWidth/ 2+space, top-(rectText.top+rectText.bottom)/2, mTextPaint);


        float one = (width - (headerWidth - cutWidth)*2) / max;
        /**
         * 进度
         */
        mPaint.setColor(barColor);
        canvas.drawRect(headerWidth-cutWidth,top-progressHeight/2,headerWidth-cutWidth+progress*one,top+progressHeight/2,mPaint);
        /**
         * 进度连接线
         */
        mLinePaint.setColor(barColor);
        mLinePaint.setStrokeWidth(2);
        if (progress == 0){
            canvas.drawLine(headerWidth-cutWidth+progress*one+space,textBgHeight,headerWidth-cutWidth+progress*one+space, textBgHeight +lineHeight-1,mLinePaint);
        }else {
            canvas.drawLine(headerWidth-cutWidth+progress*one-1, textBgHeight,headerWidth-cutWidth+progress*one-1, textBgHeight +lineHeight-1,mLinePaint);
        }

        float v = headerWidth - cutWidth + progress * one;

        /**
         * 进度文字背景内圆
         */
        mPaint.setColor(progressTextBgColor);
        mPaint.setStrokeWidth(1);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(v-textBgWidth/2+space,1,v+textBgWidth/2-space,textBgHeight,textBgHeight/2,textBgHeight/2,mPaint);
        /**
         * 进度文字背景外圈
         */
        mPaint.setColor(barColor);
        mPaint.setStrokeWidth(1);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(v-textBgWidth/2+space,1,v+textBgWidth/2-space,textBgHeight,textBgHeight/2,textBgHeight/2,mPaint);

        /**
         * 进度文字
         */
        String pText = progress+"个月";
        mTextPaint.getTextBounds(pText, 0, pText.length(), rectText);
        mTextPaint.setColor(barColor);
        mTextPaint.setTextSize(progressTextSize);
        canvas.drawText(pText, v+space, textBgHeight/2-(rectText.top+rectText.bottom)/2, mTextPaint);
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

    private int dip2px(Context ctx, float dp) {
        float density = ctx.getResources().getDisplayMetrics().density;
        //dp = px/density
        int px = (int) (dp * density + 0.5f);
        return px;
    }

    private int sp2px(Context context, float spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics());
    }
}
