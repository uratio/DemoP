
package com.uratio.demop.runnable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;


public class NumberProgressBar extends View {

    /**
     * 进度条画笔的宽度（dp）
     */
    private int paintProgressWidth = 3;

    /**
     * 文字百分比的字体大小（sp）
     */
    private int paintTextSize = 8;

    /**
     * 左侧已完成进度条的颜色
     */
    private int paintLeftColor = 0xff67aae4;

    /**
     * 进度小圆颜色
     */
    private int paintEndColor = 0xff538eeb;
    /**
     * 进度大圆颜色
     */
    private int paintEndBigColor = 0x44538eeb;//0xffbad2f7

    /**
     * 右侧未完成进度条的颜色
     */
    private int paintRightColor = 0xffe5e5e5;

    /**
     * 百分比文字的颜色
     */
    private int paintTextColor = 0xffffffff;

    /**
     * Contxt
     */
    private Context context;

    /**
     * 主线程传过来进程 0 - 100
     */
    private int progress;

    /**
     * 得到自定义视图的宽度
     */
    private int viewWidth;


    private RectF pieOval;
    private RectF pieOvalIn;
    /**
     * 得到自定义视图的Y轴中心点
     */
    private int viewCenterY;

    /**
     * 画左边已完成进度条的画笔
     */
    private Paint paintLeft = new Paint();
    /**
     * 画左边已完成进度条的画笔
     */
    private Paint paintEnd = new Paint();
    /**
     * 画左边已完成进度条的画笔
     */
    private Paint paintEndBig = new Paint();
    /**
     * 画右边未完成进度条的画笔
     */
    private Paint paintRight = new Paint();

    /**
     * 画中间的百分比文字的画笔
     */
    private Paint paintText = new Paint();

    /**
     * 要画的文字的宽度
     */
    private int textWidth;

    /**
     * 画文字时底部的坐标
     */
    private float textBottomY;

    /**
     * 包裹文字的矩形
     */
    private Rect rect = new Rect();

    private int r;
    private int bigR;
    private float radius;

//    private int sR;//气泡下方三角半径
    private int jR;//气泡上方三角半径
    private int bott;

    /**
     * 文字总共移动的长度（即从0%到100%文字左侧移动的长度）
     */
//    private int totalMovedLength;
    public NumberProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        // 构造器中初始化数据
        r = dip2px(context, 3);
        bigR = dip2px(context, 5);
        radius = dip2px(context, 3) / 2;
//        sR = dip2px(context, 6);
        jR = dip2px(context, 6);
        bott = dip2px(context, 6);
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {


        //设置百分比文字的尺寸
        int paintTextSizePx = sp2px(context, paintTextSize);

        // 已完成进度条画笔的属性
        paintLeft.setColor(paintLeftColor);
        paintLeft.setStrokeWidth(dip2px(context, 1));
        paintLeft.setAntiAlias(true);
        paintLeft.setDither(true);
        paintLeft.setStyle(Paint.Style.FILL);
       // 未完成进度条画笔的属性
        paintRight.setColor(paintRightColor);
        paintRight.setStrokeWidth(dip2px(context, 1));
        paintRight.setDither(true);
        paintRight.setAntiAlias(true);
        paintRight.setStyle(Paint.Style.FILL);

        // 小圆画笔
        paintEnd.setColor(paintEndColor);
        paintEnd.setAntiAlias(true);
        paintEnd.setStyle(Paint.Style.FILL);

        // 大圆画笔
        paintEndBig.setColor(paintEndBigColor);
        paintEndBig.setAntiAlias(true);
        paintEndBig.setStyle(Paint.Style.FILL);


        // 百分比文字画笔的属性
        paintText.setColor(paintTextColor);
        paintText.setTextSize(paintTextSizePx);
        paintText.setAntiAlias(true);
        paintText.setTypeface(Typeface.DEFAULT_BOLD);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //得到float型进度
        float progressFloat = progress / 100.0f;
        int viewHeight = getMeasuredHeight();
        viewWidth = getMeasuredWidth()-4*jR;
        viewCenterY = viewHeight-bigR;
        float currentMovedLen = viewWidth * progressFloat+2*jR;
        String str = progress+"%";
        Rect bounds = new Rect();
        paintText.getTextBounds(str, 0, str.length(), bounds);
        textWidth = bounds.width();
        textBottomY = bounds.height();

//        pieOval = new RectF(currentMovedLen - 2*sR, viewCenterY -bigR - sR, currentMovedLen + 2*sR, viewCenterY);//扇形
//        canvas.drawArc(pieOval, -60, -60, true, paintLeft);
        canvas.drawLine(currentMovedLen,viewCenterY -bigR - 12,currentMovedLen,viewCenterY -bigR,paintLeft);
        //气泡上半部分
        canvas.drawRoundRect(new RectF(currentMovedLen-2*jR, viewCenterY -bigR - 12-2*jR, currentMovedLen+2*jR, viewCenterY -r -12), 2*radius, 2*radius, paintLeft);


        canvas.drawText(progress+"%",currentMovedLen-textWidth/2,viewCenterY-r/2-bigR/2- 12-jR+textBottomY/2,paintText);//文字


        canvas.drawRoundRect(new RectF(2*jR, viewCenterY-radius, currentMovedLen, viewCenterY+radius), radius, radius, paintLeft);
        canvas.drawRoundRect(new RectF(currentMovedLen, viewCenterY-radius, viewWidth+2*jR, viewCenterY+radius), radius, radius,
                paintRight);
        pieOval = new RectF(currentMovedLen - bigR, viewCenterY - bigR, currentMovedLen + bigR, viewCenterY + bigR);
        pieOvalIn = new RectF(currentMovedLen - r, viewCenterY - r, currentMovedLen + r, viewCenterY + r);

//        canvas.drawArc(pieOval, 0, 360, true, paintEndBig);
//        canvas.drawArc(pieOvalIn, 0, 360, true, paintEnd);
    }

    /**
     * @param progress 外部传进来的当前进度
     */
    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    public static int dip2px(Context ctx, float dp) {
        float density = ctx.getResources().getDisplayMetrics().density;
        //dp = px/density
        int px = (int) (dp * density + 0.5f);
        return px;
    }
    public static int sp2px(Context context, float spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics());
    }
}