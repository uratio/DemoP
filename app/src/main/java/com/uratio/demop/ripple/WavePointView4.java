package com.uratio.demop.ripple;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DrawFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Shader;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.uratio.demop.R;
import com.uratio.demop.utils.DisplayUtils;

public class WavePointView4 extends View {
    private static final String TAG = WavePointView4.class.getSimpleName();

    // 波纹颜色
    private static final int DEF_START_COLOR = 2;
    private static final int DEF_END_COLOR = 2;
    // 波纹宽度
    private static final float DEF_LINE_WIDTH = 2;
    // 移动速度
    private static final int DEF_SPEED = 6;
    // 振幅
    private static final float DEF_AMPLITUDE = 60f;
    // 开始结束时振幅比例
    private static final float DEF_AMPLITUDE_RADIO = 0.5f;

    //渐变颜色
    private int startColor;
    private int endColor;
    // 画笔
    private Paint mPaint1;
    private Paint mPaint2;
    private Paint mPaint3;
    private Path mPath1;
    private Path mPath2;
    private Path mPath3;

    // 创建画布过滤
    private DrawFilter mDrawFilter;
    // view的宽度
    private int viewWidth;
    // view中心高度
    private float halfH;
    // 画笔宽度
    private float lineW;

    // 第一个波纹移动的速度
    private int oneSeep;
    // 第二个波纹移动的速度
    private int twoSeep = oneSeep;
    // 第三个波纹移动的速度
    private int threeSeep = oneSeep;

    // 第一个波纹移动速度的像素值
    private int oneSeepPx;
    // 第二个波纹移动速度的像素值
    private int twoSeepPx;
    // 第三个波纹移动速度的像素值
    private int threeSeepPx;

    // 存放原始波纹的每个y坐标点
    private float[] wave;
    private float[] wave1;
    private float[] wave2;
    private float[] wave3;

    // 第一个波纹当前移动的距离（相当于初相位）
    private int offSetOne;
    // 第二个波纹当前移动的距离
    private int offSetTwo;
    // 第三个波纹当前移动的距离
    private int offSetThree;
    private int offSet1;
    private int offSet2;
    private int offSet3;

    //振幅（根据声音大小动态修改）
    private float amplitude;
    private float amplitudeP;
    //周期
    private float period;
    private float period1;
    private float period2;
    private float period3;

    private int startIndex = 0;
    private int endIndex = 0;

    public WavePointView4(Context context) {
        this(context, null);
    }

    // xml布局构造方法
    public WavePointView4(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WavePointView4(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    // 初始化
    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WavePointView);
        startColor = a.getColor(R.styleable.WavePointView_wp_start_color, DEF_START_COLOR);
        endColor = a.getColor(R.styleable.WavePointView_wp_end_color, DEF_END_COLOR);
        lineW = DisplayUtils.dp2px(context, a.getDimension(R.styleable.WavePointView_wp_line_width, DEF_LINE_WIDTH));
        oneSeep = a.getInteger(R.styleable.WavePointView_wp_speed, DEF_SPEED);
        amplitude = a.getFloat(R.styleable.WavePointView_wp_amplitude, DEF_AMPLITUDE);
        amplitudeP = a.getFloat(R.styleable.WavePointView_wp_amplitude_radio, DEF_AMPLITUDE_RADIO);


        lineW = DisplayUtils.dp2px(context, DEF_LINE_WIDTH);
        // 创建画笔
        mPaint1 = new Paint();
        // 设置绘画风格为实线
        mPaint1.setStyle(Style.STROKE);
        // 抗锯齿
        mPaint1.setAntiAlias(true);
        mPaint1.setStrokeWidth(lineW);

        mPaint2 = new Paint();
        // 设置绘画风格为实线
        mPaint2.setStyle(Style.STROKE);
        // 抗锯齿
        mPaint2.setAntiAlias(true);
        mPaint2.setStrokeWidth(lineW);

        mPaint3 = new Paint();
        // 设置绘画风格为实线
        mPaint3.setStyle(Style.STROKE);
        // 抗锯齿
        mPaint3.setAntiAlias(true);
        mPaint3.setStrokeWidth(lineW);

        mPath1 = new Path();
        mPath2 = new Path();
        mPath3 = new Path();

        // 设置图片过滤波和抗锯齿
        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);


        // 第一个波的像素移动值 换算成手机像素值让其在各个手机移动速度差不多
        oneSeepPx = DisplayUtils.dp2px(context, oneSeep);
        // 第二个波的像素移动值
        twoSeepPx = DisplayUtils.dp2px(context, twoSeep);
        // 第三个波的像素移动值
        threeSeepPx = DisplayUtils.dp2px(context, threeSeep);


    }

    // 大小改变
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 获取view的宽高
        viewWidth = w;
        halfH = h / 2f;

        startIndex = w;
        endIndex = w;

        // 设置波形图周期（一个周期有多长的）
        period = w * 0.8f;
        period1 = w * 0.8f;
        period2 = w * 0.8f;
        period3 = w;

        // 初始化保存波形图的数组(保证最后一个坐标有整个波长的位移空间)
        wave = new float[(int) (w + period)];
        wave1 = new float[(int) (w + period1)];
        wave2 = new float[(int) (w + period2)];
        wave3 = new float[(int) (w + period3)];

        offSetOne = 0;
        offSetTwo = (int) (period / 3);
        offSetThree = (int) (period * 2 / 3);

        // 计算每个点y坐标
        for (int i = 0; i < w + period; i++) {
            wave[i] = (float) (amplitude * Math.sin(2f * Math.PI / period * i));
            wave1[i] = (float) (amplitude * Math.sin(2f * Math.PI / period * i));
            wave2[i] = (float) (amplitude * Math.sin(2f * Math.PI / period * i + 2f * Math.PI / 3));
            wave3[i] = (float) (amplitude * Math.sin(2f * Math.PI / period * i - 2f * Math.PI / 3));
        }
//        for (int i = 0; i < w + period1; i++) {
//            wave1[i] = (float) (amplitude * Math.sin(2f * Math.PI / period1 * i));
//        }
//        for (int i = 0; i < w + period2; i++) {
//            wave2[i] = (float) (amplitude * Math.sin(2f * Math.PI / period2 * i + Math.PI));
//        }
//        for (int i = 0; i < w + period3; i++) {
//            wave3[i] = (float) (amplitude * Math.sin(2f * Math.PI / period3 * i + 20));
//        }

        if (mPaint1 != null) {
            mPaint1.setShader(new LinearGradient(0, 0, w, h, 0xFF266BDE, 0xFF13E4F4, Shader.TileMode.REPEAT));
        }
        if (mPaint2 != null) {
            mPaint2.setShader(new LinearGradient(0, 0, w, h, 0xFF9C27B0, 0xFF3F51B5, Shader.TileMode.REPEAT));

        }
        if (mPaint3 != null) {
            mPaint3.setShader(new LinearGradient(0, 0, w, h, 0xFF03A9F4, 0xFFBC0BDE, Shader.TileMode.REPEAT));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float manHeight = Math.max(amplitude * 2 + lineW, getMeasuredHeight());
        setMeasuredDimension(getMeasuredWidth(), (int) manHeight);
    }

    // 绘画方法
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(mDrawFilter);

        canvas.translate(0, halfH);


        /**
         * 简谐运动表达式；
         *      x = A sin(ωt + φ) = A sin(2π/T*t + φ) = A sin(2πft + φ)
         *      y = A sin(ωx + φ) = A sin(2π/T*x + φ) = A sin(2πfx + φ)
         *
         *          (ωx+φ)——相位
         *          A: 振幅
         *          ω：圆周率（角频率）： ω = 2π / T = 2πf
         *          T：周期
         *          f：频率
         *          t: 时间
         *          φ：初相位
         */

        mPath1.reset();
        mPath2.reset();
        mPath3.reset();

        /**
         * 使用path绘制：同速度，不同偏移量，初相位相同
         */
//        mPath1.moveTo(0, wave[offSetOne]);
//        mPath2.moveTo(0, wave[offSetTwo]);
//        mPath3.moveTo(0, wave[offSetThree]);
//        for (int i = 0; i < viewWidth; i++) {
//            mPath1.lineTo(i, wave[offSetOne + i]);
//            mPath2.lineTo(i, wave[offSetTwo + i]);
//            mPath3.lineTo(i, wave[offSetThree + i]);
//        }
//        canvas.drawPath(mPath1, mPaint1);
//        canvas.drawPath(mPath2, mPaint2);
//        canvas.drawPath(mPath3, mPaint3);

        /**
         * 使用path绘制：同速度，同偏移量，初相位不同
         */
//        mPath1.moveTo(0, wave1[offSet1]);
//        mPath2.moveTo(0, wave2[offSet2]);
//        mPath3.moveTo(0, wave3[offSet3]);
//        for (int i = 1; i < viewWidth; i++) {
//            mPath1.lineTo(i, amplitudeP * wave1[offSet1 + i]);
//            mPath2.lineTo(i, amplitudeP * wave2[offSet2 + i]);
//            mPath3.lineTo(i, amplitudeP * wave3[offSet3 + i]);
//        }
//        canvas.drawPath(mPath1, mPaint1);
//        canvas.drawPath(mPath2, mPaint2);
//        canvas.drawPath(mPath3, mPaint3);


//        mPath.moveTo(100, 100);
//      //rQuardto的位置是相对的
//        mPath.rQuadTo(20, 20, 40, 0);
//        mPath.rQuadTo(20, -20, 40, 0);


        float startPeriod0 = 200;
        float startPeriod1 = 400;
        float startPeriod2 = 600;
        float startPeriod3 = 800;
        float startPeriod4 = 1000;
        /*startIndex = 0;

        mPath1.moveTo(-offSet1, 0);
        if (startIndex >= 0) {
            mPath1.rQuadTo(startIndex, 0, startIndex, 0);
            for (int i = 0; i < viewWidth - startIndex; i++) {
                *//*if (i > period1) {
//                    mPath1.rQuadTo(0, -wave1[i] / amplitude / 2, 1, wave1[i] / amplitude / 2);
                } else {
                    float dy1 = wave1[i] / amplitude / 6f;
//                    float dy1 = (float) (Math.sin(2f * Math.PI / startPeriod * i));
//                    mPath1.rQuadTo(0, dy1, 1, dy1);
                }*//*
                if (i > startPeriod1) {
//                    mPath1.rQuadTo(0, -wave1[i] / amplitude / 2, 1, wave1[i] / amplitude / 2);
                    mPath1.lineTo(startIndex + i, amplitudeP * wave1[(int) (i - startPeriod1)]);
                } else if (i == startPeriod1) {
                    mPath1.rQuadTo(0, amplitudeP * wave1[0], 1, amplitudeP * wave1[0]);
                } else {
//                    float dy1 = wave1[i] / amplitude / 6f;
                    float dy1 = (float) (Math.sin(2f * Math.PI / startPeriod1 * i) / 4f);
                    mPath1.rQuadTo(0, dy1, 1, -dy1);
                }
            }
//            mPath1.rQuadTo(period1 *3/ 4, -wave1[(int) (period1 / 4)] * 2, period1 / 2, 0);
//            mPath1.rQuadTo(period1 / 4, -wave1[(int) (period1 / 4)] * 2, period1 / 2, 0);
        } else {
            offSet1 += oneSeep;
            if (offSet1 >= period1) {
                offSet1 = 0;
            }
            for (int i = 0; i < viewWidth; i++) {
                mPath1.rQuadTo(0, -wave1[i] / amplitude / 2 , 1, wave1[i] / amplitude / 2);
            }
//            mPath1.rQuadTo(period1 / 4, -wave1[(int) (period1 / 4)] * 2, period1 / 2, 0);
        }
//        mPath1.rQuadTo(period1 / 4, -wave1[(int) (period1 / 4)] * 2, period1 / 2, 0);
//        mPath1.rQuadTo(period1 / 4, wave1[(int) (period1 / 4)] * 2, period1 / 2, 0);
//        mPath1.rQuadTo(period1 / 4, -wave1[(int) (period1 / 4)] * 2, period1 / 2, 0);
//        mPath1.rQuadTo(period1 / 4, wave1[(int) (period1 / 4)] * 2, period1 / 2, 0);
//        mPath1.rQuadTo(period1 / 4, -wave1[(int) (period1 / 4)] * 2, period1 / 2, 0);

        startIndex -= oneSeep;
        if (startIndex <= 0) startIndex = 0;*/


        mPath1.moveTo(0, 0);
        mPath2.moveTo(0, 0);
        mPath3.moveTo(0, 0);

        float start1 = period / 4;
        float start2 = period * 5 / 12;
        float start3 = period * 7 / 12;
        float height1 = amplitude * amplitudeP * 0.4f;
        float height2 = amplitude * amplitudeP * 0.8f;
        float height3 = amplitude * amplitudeP * 0.8f;

        mPath1.rQuadTo(start1/4, 0, start1/2, height1);
        mPath1.rQuadTo(start1/4, height1, start1/2, height1);

        mPath2.rQuadTo(start2/4, 0, start2/2, -height2);
        mPath2.rQuadTo(start2/4, -height2, start2/2, -height2);

        mPath3.rQuadTo(start3/4, 0, start3/2, height3);
        mPath3.rQuadTo(start3/4, height3, start3/2, height3);

        float stepTwoW = period / 2;
        float stepTwoH1 = amplitude * amplitudeP / 2f + height1;
        float stepTwoH2 = amplitude * amplitudeP / 2f + height2;
        float stepTwoH3 = amplitude * amplitudeP / 2f + height3;

        mPath1.rQuadTo(stepTwoW/4, 0, stepTwoW/2, -stepTwoH1);
        mPath1.rQuadTo(stepTwoW/4, -stepTwoH1, stepTwoW/2, -stepTwoH1);

        mPath2.rQuadTo(stepTwoW/4, 0, stepTwoW/2, stepTwoH2);
        mPath2.rQuadTo(stepTwoW/4, stepTwoH2, stepTwoW/2, stepTwoH2);

        mPath3.rQuadTo(stepTwoW/4, 0, stepTwoW/2, -stepTwoH3);
        mPath3.rQuadTo(stepTwoW/4, -stepTwoH3, stepTwoW/2, -stepTwoH3);

        for (int i = 0; i < viewWidth; i++) {
            mPath1.lineTo(i + start1 + stepTwoW, wave1[(int) (offSetOne + i + start1 + stepTwoW)]  * amplitudeP);
            mPath2.lineTo(i + start2 + stepTwoW, wave2[(int) (offSetOne + i + start2 + stepTwoW)]  * amplitudeP);
            mPath3.lineTo(i + start3 + stepTwoW, wave3[(int) (offSetOne + i + start3 + stepTwoW)]  * amplitudeP);
        }

//        for (int i = 0; i < startPeriod1 / 2; i++) {
//            float dy1 = (float) (Math.sin(2f * Math.PI / startPeriod1 * i) / 6);
//            mPath3.rQuadTo(0.5f, -dy1, 1, -dy1);
//        }
        float sum = 0;
//        for (int i = 0; i < startPeriod1 / 2; i++) {
//            sum += (float) (Math.sin(2f * Math.PI / startPeriod1 * i));
//            float dy1 = (float) (Math.sin(2f * Math.PI / startPeriod1 * i) / 3);
//            mPath1.rQuadTo(0.5f, dy1, 1, dy1);
////            mPath2.rQuadTo(0, -dy1, 1, -dy1);
////            mPath3.rQuadTo(0.5f, dy1 * 3 / 2, 1, dy1 * 3 / 2);
//            Log.e(TAG, "onDraw: sum=" + sum);
//        }
//        for (int i = 0; i < startPeriod2 / 2; i++) {
//            float dy1 = (float) (Math.sin(2f * Math.PI / startPeriod2 * i) / 4);
//            mPath2.rQuadTo(0.5f, -dy1, 1, -dy1);
//        }
//        for (int i = 0; i < startPeriod0 / 2; i++) {
//            float dy1 = (float) (Math.sin(2f * Math.PI / startPeriod0 * i) / 2);
//            mPath3.rQuadTo(0, -dy1, 1, -dy1);
//        }

//        for (int i = 0; i < period1; i++) {
//            float dy1 = wave1[(int) (i + period1/2)] / amplitude / 3f;
//            mPath1.rQuadTo(0.5f, dy1, 1, dy1);
//            mPath3.rQuadTo(0.5f, dy1, 1, dy1);
//        }
//        for (int i = 0; i < period2; i++) {
//            float dy1 = wave2[(int) (i + period2/2)] / amplitude / 3f;
//            mPath2.rQuadTo(0.5f, dy1, 1, dy1);
//        }
//        for (int i = 0; i < period3; i++) {
//            float dy1 = wave3[(int) (i + period3/2)] / amplitude / 3f;
//            mPath3.rQuadTo(0, dy1, 1, dy1);
//        }


//        for (int i = 0; i < period / 4; i++) {
//            float dy1 = (float) (Math.sin(2f * Math.PI / period * 2 * i) / 3);
//            mPath1.rQuadTo(0, dy1, 1, dy1);
//        }
//        for (int i = 0; i < period * 5 / 12; i++) {
//            float dy1 = (float) (Math.sin(2f * Math.PI / period / 5 * 6 * i) / 5);
//            mPath2.rQuadTo(0, -dy1, 1, -dy1);
//        }
//        for (int i = 0; i < period * 7 / 12; i++) {
//            float dy1 = (float) (Math.sin(2f * Math.PI / period / 7 * 6 * i) / 7);
//            mPath3.rQuadTo(0, dy1, 1, dy1);
//        }
//        mPath1.rQuadTo(period1 / 4, -wave1[(int) (period1 / 4)] * 2, period1 / 2, 0);
//        mPath1.rQuadTo(period1 / 4, wave1[(int) (period1 / 4)] * 2, period1 / 2, 0);
//        mPath1.rQuadTo(period1 / 4, -wave1[(int) (period1 / 4)] * 2, period1 / 2, 0);
//        mPath1.rQuadTo(period1 / 4, wave1[(int) (period1 / 4)] * 2, period1 / 2, 0);
//        mPath1.rQuadTo(period1 / 4, -wave1[(int) (period1 / 4)] * 2, period1 / 2, 0);

        /*
        // fail

        mPath1.moveTo(0, wave1[offSet1]);
        mPath2.moveTo(0, wave2[offSet2]);
        mPath3.moveTo(0, wave3[offSet3]);
        for (int i = 0; i < viewWidth; i++) {
            if (i < period / 4 && isFirst) {
                float dy1 = (float) (Math.sin(2f * Math.PI / period * 2 * i) * 99 / 300);
                mPath1.rQuadTo(0, dy1, 1, dy1);
            } else {
                mPath1.lineTo(i, amplitudeP * wave1[offSet1 + i]);
            }
            if (i < period * 5 / 12 && isFirst) {
                float dy1 = (float) (Math.sin(2f * Math.PI / period / 5 * 6 * i) / 5);
                mPath2.rQuadTo(0, -dy1, 1, -dy1);
            } else {
                mPath2.lineTo(i, amplitudeP * wave2[offSet2 + i]);
            }
            if (i < period * 7 / 12 && isFirst) {
                float dy1 = (float) (Math.sin(2f * Math.PI / period / 7 * 6 * i) / 7);
                mPath3.rQuadTo(0, dy1, 1, dy1);
            } else {
                mPath3.lineTo(i, amplitudeP * wave3[offSet3 + i]);
            }
        }*/


//        mPath1.rQuadTo(200,  amplitude* 2, 400, 0);
//        mPath2.rQuadTo(200,  amplitude* 2, 400, amplitude* 2);
//        mPath3.rQuadTo(100,  -amplitude, 200, -amplitude);
//
//        canvas.drawLine(0, amplitude* 2, 400, amplitude* 2 + 1, mPaint2);

//        mPath2.rQuadTo(100,  0, 200, amplitude);
//        mPath2.rQuadTo(100,  amplitude, 200, amplitude);
//        mPath2.rQuadTo(100,  0, 200, -amplitude);
//        mPath2.rQuadTo(100,  -amplitude, 200, -amplitude);
//        mPath2.rQuadTo(50,  0, 50, -50);
//        mPath2.rQuadTo(200,  -amplitude, 400, -amplitude);
//        mPath1.rQuadTo(0, wave1[(int) (period1 / 2)] * 2, period1 / 4, 0);
//        canvas.drawLine(800, amplitude - 1, viewWidth, amplitude, mPaint2);
//        mPath2.rQuadTo(200,  amplitude* 2, 250, amplitude*2);
//

        canvas.drawPath(mPath1, mPaint1);
        canvas.drawPath(mPath2, mPaint2);
        canvas.drawPath(mPath3, mPaint3);

        Paint paint = new Paint();
        // 设置绘画风格为实线
        paint.setStyle(Style.STROKE);
        // 抗锯齿
        paint.setAntiAlias(true);
        paint.setColor(0xFFFF0000);
        paint.setStrokeWidth(2);
        canvas.drawLine(0, 0, viewWidth, 0, paint);


        // 更新偏移量
        resetOffset();

//        SystemClock.sleep(5);
//        postInvalidate();
    }

    private void resetOffset() {
        offSetOne = offSetOne + oneSeepPx;
        offSetTwo = offSetTwo + twoSeepPx;
        offSetThree = offSetThree + threeSeepPx;

        //超过一个周期置为 0
        if (offSetOne >= period) {
            offSetOne = 0;
        }
        if (offSetTwo >= period) {
            offSetTwo = 0;
        }
        if (offSetThree >= period) {
            offSetThree = 0;
        }

        offSet1 = offSet1 + oneSeepPx;
        offSet2 = offSet2 + twoSeepPx;
        offSet3 = offSet3 + threeSeepPx;

        //超过一个周期置为 0
        if (offSet1 >= period1) {
            offSet1 = 0;
            isFirst = false;
        }
        if (offSet2 >= period2) {
            offSet2 = 0;
        }
        if (offSet3 >= period3) {
            offSet3 = 0;
        }
    }

    boolean isFirst = true;
}
