package com.uratio.demop.ripple;

import android.content.Context;
import android.graphics.Canvas;
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
import android.view.View;
import android.view.WindowManager;

import com.uratio.demop.utils.DisplayUtils;

public class WavePointView extends View {
    private static final String TAG = WavePointView.class.getSimpleName();

    // 波纹颜色
    private static final int WAVE_PAINT_COLOR = 0x533cbabf;

    // 第一个波纹移动的速度
    private int oneSeep = 3;
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
    private float wave[];

    // 第一个波纹当前移动的距离（相当于初相位）
    private int offSetOne;
    // 第二个波纹当前移动的距离
    private int offSetTwo;
    // 第三个波纹当前移动的距离
    private int offSetThree;

    //振幅（根据声音大小动态修改）
    private float amplitude = 45.0f;
    //频率
    private float frequency;
    //周期
    private float period;
    //相位（随坐标移动发生变化）
    private float phase;

    // 画笔
    private Paint mPaint1;
    private Paint mPaint2;
    private Paint mPaint3;
    private float lineW;

    // 创建画布过滤
    private DrawFilter mDrawFilter;

    // view的宽度
    private int viewWidth;

    // view中心高度
    private float halfH;

    // xml布局构造方法
    public WavePointView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    // 初始化
    private void init(Context context) {
        lineW = DisplayUtils.dp2px(context, 2) / 2f;
        // 创建画笔
        mPaint1 = new Paint();
        // 设置画笔颜色
        mPaint1.setColor(WAVE_PAINT_COLOR);
        // 设置绘画风格为实线
        mPaint1.setStyle(Style.FILL);
        // 抗锯齿
        mPaint1.setAntiAlias(true);
        mPaint1.setStrokeWidth(lineW);

        mPaint2 = new Paint();
        // 设置画笔颜色
        mPaint2.setColor(WAVE_PAINT_COLOR);
        // 设置绘画风格为实线
        mPaint2.setStyle(Style.FILL);
        // 抗锯齿
        mPaint2.setAntiAlias(true);
        mPaint2.setStrokeWidth(lineW);

        mPaint3 = new Paint();
        // 设置画笔颜色
        mPaint3.setColor(WAVE_PAINT_COLOR);
        // 设置绘画风格为实线
        mPaint3.setStyle(Style.FILL);
        // 抗锯齿
        mPaint3.setAntiAlias(true);
        mPaint3.setStrokeWidth(lineW);

        // 设置图片过滤波和抗锯齿
        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);


        // 第一个波的像素移动值 换算成手机像素值让其在各个手机移动速度差不多
        oneSeepPx = DisplayUtils.dp2px(context, oneSeep);
        // 第二个波的像素移动值
        twoSeepPx = DisplayUtils.dp2px(context, twoSeep);
        // 第三个波的像素移动值
        threeSeepPx = DisplayUtils.dp2px(context, threeSeep);

        // 每个波当前起始位置相差 一个波长 x 的 1/6
//        int x = (int) (amplitude * (Math.sin(frequency * 2 * Math.PI)));
//        Log.e(TAG, "init: 一个波x轴长度 x=" + x);
        offSetOne = DisplayUtils.dp2px(context, 0);
        offSetTwo = DisplayUtils.dp2px(context, 100);
        offSetThree = DisplayUtils.dp2px(context, 200);

    }

    // 绘画方法
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(mDrawFilter);

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

        for (int i = 0; i < viewWidth; i++) {
            canvas.drawLine(i, amplitude - wave[offSetOne + i], i, amplitude - wave[offSetOne +i] + lineW * 2, mPaint1);
            canvas.drawLine(i, amplitude - wave[offSetTwo + i], i, amplitude - wave[offSetTwo + i] + lineW * 2, mPaint2);
            canvas.drawLine(i, amplitude - wave[offSetThree + i], i, amplitude - wave[offSetThree + i] + lineW * 2, mPaint3);

//            canvas.drawLine(i, halfH - wave[offSetOne + i] - lineW, i, halfH - wave[offSetOne +i] + lineW, mPaint1);
//            canvas.drawLine(i, halfH - wave[offSetTwo + i] - lineW, i, halfH - wave[offSetTwo + i] + lineW, mPaint2);
//            canvas.drawLine(i, halfH - wave[offSetThree + i] - lineW , i, halfH - wave[offSetThree + i] + lineW, mPaint3);


        }

        Path path = new Path();
        path.moveTo(0,100);
//        path.rQuadTo();
        for (int i = 0; i < viewWidth - 1; i++) {
        }
        path.rQuadTo(viewWidth / 2, halfH * 2 - wave[viewWidth / 2] - lineW * 2, viewWidth, halfH * 2 - wave[viewWidth]);
        path.rQuadTo(viewWidth / 2, - (halfH * 2 - wave[viewWidth / 2] - lineW * 2), viewWidth, halfH * 2 - wave[viewWidth]);
        canvas.drawPath(path, mPaint1);

        SystemClock.sleep(5);
        postInvalidate();
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
        frequency = 1 / period;
        // 初始化保存波形图的数组(保证最后一个坐标有整个波长的位移空间)
        wave = new float[(int) (w + period)];

        // 计算每个点y坐标
        for (int i = 0; i < w + period; i++) {
            wave[i] = (float) (amplitude * Math.sin(2 * Math.PI * frequency * i));
        }

        if (mPaint1 != null) {
            mPaint1.setShader(new LinearGradient(0, 0, w, h, 0xFF13E4F4, 0xFF266BDE, Shader.TileMode.REPEAT));
        }
        if (mPaint2 != null) {
            mPaint2.setShader(new LinearGradient(0, 0, w, h, 0xFF9C27B0, 0xFF3F51B5, Shader.TileMode.REPEAT));

        }
        if (mPaint3 != null) {
            mPaint3.setShader(new LinearGradient(0, 0, w, h, 0xFF03A9F4, 0xFF673AB7, Shader.TileMode.REPEAT));
        }
    }

    // dp换算成px 为了让移动速度在各个分辨率的手机的都差不多
    public int dpChangPx(int dp) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        return (int) (metrics.density * dp + 0.5f);
    }

}
