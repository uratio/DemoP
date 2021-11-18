package com.uratio.demop.ripple;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Shader;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.uratio.demop.utils.DisplayUtils;

import java.util.Arrays;

public class WavePointView extends View {
    private static final String TAG = WavePointView.class.getSimpleName();

    // 波纹颜色
    private static final float DEF_LINE_WIDTH = 2;

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
    private int oneSeep = 0;
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

    //振幅（根据声音大小动态修改）
    private float amplitude = 45.0f;
    //周期
    private float period;
    //相位（随坐标移动发生变化）
    private float phase;

    // xml布局构造方法
    public WavePointView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    // 初始化
    private void init(Context context) {
        lineW = DisplayUtils.dp2px(DEF_LINE_WIDTH);
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
        oneSeepPx = DisplayUtils.dp2px(oneSeep);
        // 第二个波的像素移动值
        twoSeepPx = DisplayUtils.dp2px(twoSeep);
        // 第三个波的像素移动值
        threeSeepPx = DisplayUtils.dp2px(threeSeep);
    }

    // 绘画方法
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(mDrawFilter);

        canvas.translate(0, halfH);

        Paint paint = new Paint();
        // 设置绘画风格为实线
        paint.setStyle(Style.STROKE);
        // 抗锯齿
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1);
        paint.setColor(Color.RED);
        canvas.drawLine(0, 0, viewWidth, 0, paint);


        /**
         * 简谐运动表达式；
         *      x = A sin(ωt + φ) = A sin(2π/T*t + φ) = A sin(2πft + φ)
         *      y = A sin(ωx + φ) = A sin(2π/T*x + φ) = A sin(2πfx + φ)
         *
         *          (ωx+φ)——相位
         *          A: 振幅
         *          w：圆周率（角频率）： w = 2π / t = 2πf
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
        mPath1.moveTo(0, wave1[offSetOne]);
        mPath2.moveTo(0, wave2[offSetOne]);
        mPath3.moveTo(0, wave3[offSetOne]);
        for (int i = 1; i < viewWidth; i++) {
            mPath1.lineTo(i, wave1[offSetOne + i]);
            mPath2.lineTo(i, wave2[offSetOne + i]);
            mPath3.lineTo(i, wave3[offSetOne + i]);
        }
        canvas.drawPath(mPath1, mPaint1);
        canvas.drawPath(mPath2, mPaint2);
        canvas.drawPath(mPath3, mPaint3);

        /**
         * 直接绘制line
         */
//        mPath1.moveTo(0, wave[offSetOne]);
//        mPath2.moveTo(0, wave[offSetTwo]);
//        mPath3.moveTo(0, wave[offSetThree]);
//        for (int i = 0; i < viewWidth; i++) {
//            canvas.drawLine(i, wave[offSetOne + i] - lineW / 2f, i, wave[offSetOne +i] + lineW / 2f, mPaint1);
//            canvas.drawLine(i, wave[offSetTwo + i] - lineW / 2f, i, wave[offSetTwo + i] + lineW / 2f, mPaint2);
//            canvas.drawLine(i, wave[offSetThree + i] - lineW / 2f , i, wave[offSetThree + i] + lineW / 2f, mPaint3);
//        }

        // 更新偏移量
        resetOffset();

        SystemClock.sleep(5);
        postInvalidate();
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
    }

    // 大小改变
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 获取view的宽高
        viewWidth = w;
        halfH = h / 2f;

        // 设置波形图周期（一个周期有多长的）
        period = w * 0.8f;
        // 初始化保存波形图的数组(保证最后一个坐标有整个波长的位移空间)
        wave = new float[(int) (w + period)];
        wave1 = new float[(int) (w + period)];
        wave2 = new float[(int) (w + period)];
        wave3 = new float[(int) (w + period)];

        offSetOne = 0;
        offSetTwo = (int) (period / 3);
        offSetThree = (int) (period * 2 / 3);

        // 计算每个点y坐标
        for (int i = 0; i < w + period; i++) {
            wave[i] = (float) (amplitude * Math.sin(2f * Math.PI / period * i));
            wave1[i] = (float) (amplitude * Math.sin(2f * Math.PI / period * i));
            wave2[i] = (float) (amplitude * Math.sin(2f * Math.PI / period * i + Math.PI / 2));
            wave3[i] = (float) (amplitude * Math.sin(2f * Math.PI / period * i + Math.PI));
        }

        if (mPaint1 != null) {
            mPaint1.setShader(new LinearGradient(0, 0, w, h, 0xFF266BDE, 0xFF13E4F4, Shader.TileMode.CLAMP));
        }
        if (mPaint2 != null) {
            mPaint2.setShader(new LinearGradient(0, 0, w, h, 0xFF9C27B0, 0xFF3F51B5, Shader.TileMode.CLAMP));

        }
        if (mPaint3 != null) {
            mPaint3.setShader(new LinearGradient(0, 0, w, h, 0xFF03A9F4, 0xFFBC0BDE, Shader.TileMode.CLAMP));
        }
    }
}
