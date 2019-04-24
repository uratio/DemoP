package com.uratio.demop.count;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.widget.TextView;

import com.uratio.demop.R;

@SuppressLint("AppCompatCustomView")
public class CircleProgressbar extends TextView {
 
    //外部轮廓的颜色
    private int outLineColor;
 
    //外部轮廓的宽度
    private float outLineWidth;
 
    //内部圆的颜色
//    private ColorStateList inCircleColors = ColorStateList.valueOf(Color.TRANSPARENT);
    private int inCircleColors;

    //中心圆的颜色
    private int circleColor;
 
    //进度条的颜色
    private int progressLineColor;
 
    //进度条的宽度
    private float progressLineWidth;

    //画笔
    private Paint mPaint = new Paint();
 
    //进度条的矩形区域
    private RectF mArcRect = new RectF();
 
    //进度
    private int progress;
 
    //进度条类型
    private ProgressType mProgressType = ProgressType.COUNT;
 
    //进度倒计时时间
    private long timeMillis;
 
    //View的显示区域。
    final Rect bounds = new Rect();
 
    //进度条通知。
    private OnCountdownProgressListener mCountdownProgressListener;
    private int listenerWhat = 0;
 
    public CircleProgressbar(Context context) {
        this(context, null);
    }
 
    public CircleProgressbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
 
    public CircleProgressbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }
 
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CircleProgressbar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context, attrs);
    }
 
 
    private void initialize(Context context, AttributeSet attributeSet) {
        mPaint.setAntiAlias(true);
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.CircleProgressbar);

        inCircleColors = typedArray.getColor(R.styleable.CircleProgressbar_inCircleColors, 0xEE000000);
        outLineColor = typedArray.getColor(R.styleable.CircleProgressbar_inCircleColors, Color.TRANSPARENT);
        outLineWidth = typedArray.getDimension(R.styleable.CircleProgressbar_outLineWidth, 0);
        progressLineColor = typedArray.getColor(R.styleable.CircleProgressbar_progressLineColor, Color.GREEN);
        progressLineWidth = typedArray.getDimension(R.styleable.CircleProgressbar_progressLineWidth, 8);
        progress = typedArray.getInteger(R.styleable.CircleProgressbar_mProgress, 0);
        timeMillis = typedArray.getInteger(R.styleable.CircleProgressbar_timeMillis, 3000);

        typedArray.recycle();
    }
 
    /**
     * 设置外部轮廓圆的颜色
     */
    public void setOutLineColor(@ColorInt int outLineColor) {
        this.outLineColor = outLineColor;
        invalidate();
    }
 
    /**
     * 设置外部轮廓圆的宽度
     */
    public void setOutLineWidth(@ColorInt int outLineWidth) {
        this.outLineWidth = outLineWidth;
        invalidate();
    }
 
    /**
     * 设置中心圆的颜色
     */
    public void setInCircleColor(@ColorInt int inCircleColor) {
        this.inCircleColors = inCircleColor;
        invalidate();
    }

    /**
     * 设置圆形进度条颜色
     */
    public void setProgressColor(@ColorInt int progressLineColor) {
        this.progressLineColor = progressLineColor;
        invalidate();
    }
 
    /**
     * 设置圆形进度条宽度
     */
    public void setProgressLineWidth(int progressLineWidth) {
        this.progressLineWidth = progressLineWidth;
        invalidate();
    }
 
    /**
     * 设置进度条值
     */
    public void setProgress(int progress) {
        this.progress = validateProgress(progress);
        invalidate();
    }
 
 
    private int validateProgress(int progress) {
        if (progress > 100)
            progress = 100;
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
 
    /**
     * 设置倒计时时间
     */
    public void setTimeMillis(long timeMillis) {
        this.timeMillis = timeMillis;
        invalidate();
    }
 
    /**
     * 获取倒计时时间
     */
    public long getTimeMillis() {
        return this.timeMillis;
    }
 
    /**
     * 设置进度条类型  是0-100 还是100_0
     */
    public void setProgressType(ProgressType progressType) {
        this.mProgressType = progressType;
        resetProgress();
        invalidate();
    }
 
 
    private void resetProgress() {
        switch (mProgressType) {
            case COUNT:
                progress = 0;
                break;
            case COUNT_BACK:
                progress = 100;
                break;
        }
    }
 
    /**
     * 获取进度条类型
     */
    public ProgressType getProgressType() {
        return mProgressType;
    }
 
    /**
     * 设置进度监听
     */
    public void setCountdownProgressListener(int what, OnCountdownProgressListener mCountdownProgressListener) {
        this.listenerWhat = what;
        this.mCountdownProgressListener = mCountdownProgressListener;
    }
 
 
    public void start() {
        stop();
        post(progressChangeTask);
    }
 
    /**
     * 开始旋转倒计时
     */
    public void reStart() {
        resetProgress();
        start();
    }
 
 
    public void stop() {
        removeCallbacks(progressChangeTask);
    }
 
    @Override
    protected void onDraw(Canvas canvas) {
        //获取view的边界
        getDrawingRect(bounds);
 
        int size = bounds.height() > bounds.width() ? bounds.width() : bounds.height();
        float outerRadius = size / 2;
 
        //画内部背景
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(inCircleColors);
        canvas.drawCircle(bounds.centerX(), bounds.centerY(), outerRadius - progressLineWidth, mPaint);
 
        //画边框圆
        if (outLineWidth != 0) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(outLineWidth);
            mPaint.setColor(outLineColor);
            canvas.drawCircle(bounds.centerX(), bounds.centerY(), outerRadius - outLineWidth / 2, mPaint);
        }
 
        //画字
        Paint paint = getPaint();
        paint.setColor(getCurrentTextColor());
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(getTextSize());
        float textY = bounds.centerY() - (paint.descent() + paint.ascent()) / 2;
        canvas.drawText(getText().toString(), bounds.centerX(), textY, paint);
 
        //画进度条
        mPaint.setColor(progressLineColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(progressLineWidth);
        // mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);
        float deleteWidth = progressLineWidth + outLineWidth;
        mArcRect.set(bounds.left + deleteWidth / 2, bounds.top + deleteWidth / 2, bounds.right - deleteWidth / 2, bounds.bottom - deleteWidth / 2);
 
        canvas.drawArc(mArcRect, -90, -360 * progress / 100, false, mPaint);
    }
 
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float lineWidth = 4 * (outLineWidth + progressLineWidth);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int size = (int) ((width > height ? width : height) + lineWidth);
        setMeasuredDimension(size, size);
    }
 
    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
    }
 
 
    private Runnable progressChangeTask = new Runnable() {
        @Override
        public void run() {
            removeCallbacks(this);
            switch (mProgressType) {
                //判断是顺数进度条还是倒数进度条
                case COUNT:
                    progress += 1;
                    break;
                case COUNT_BACK:
                    progress -= 1;
                    break;
            }
            if (progress >= 0 && progress <= 100) {
                if (mCountdownProgressListener != null)
                    mCountdownProgressListener.onProgress(listenerWhat, progress);
                invalidate();
                postDelayed(progressChangeTask, timeMillis / 100);
            } else
                progress = validateProgress(progress);
        }
    };
 
 
    public enum ProgressType {
        /**
         * 顺数进度条，从0-100；
         */
        COUNT,
 
        /**
         * 倒数进度条，从100-0；
         */
        COUNT_BACK;
    }
 
    public interface OnCountdownProgressListener {
        void onProgress(int what, int progress);
    }
}