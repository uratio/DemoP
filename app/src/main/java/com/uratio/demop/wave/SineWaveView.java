package com.uratio.demop.wave;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DrawFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Shader;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import java.util.Arrays;

public class SineWaveView extends View {
    private static final String TAG = SineWaveView.class.getSimpleName();

    // 波纹颜色
    private static final int WAVE_PAINT_COLOR = 0x533cbabf;

    // 第一个波纹移动的速度
    private int oneSeep = 6;
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
    // 存放第一个波纹的每一个y坐标点
    private float oneWave[];
    // 存放第二个波纹的每一个y坐标点
    private float twoWave[];
    // 存放第三个波纹的每一个y坐标点
    private float threeWave[];

    // 第一个波纹当前移动的距离（相当于初相位）
    private int oneNowOffSet;
    // 第二个波纹当前移动的距离
    private int twoNowOffSet;
    // 第三个波纹当前移动的距离
    private int threeNowOffSet;

    //振幅（根据声音大小动态修改）
    private float amplitude = 45.0f;
    //频率
    private float frequency;
    //周期
    private float period;
    //相位（随坐标移动发生变化）
    private float phase;

    // 画笔
    private Paint mPaint;

    // 创建画布过滤
    private DrawFilter mDrawFilter;

    // view的宽度
    private int viewWidth;

    // view高度
    private int viewHeight;

    // xml布局构造方法
    public SineWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    // 初始化
    private void init() {
        // 创建画笔
        mPaint = new Paint();
        // 设置画笔颜色
        mPaint.setColor(WAVE_PAINT_COLOR);
        // 设置绘画风格为实线
        mPaint.setStyle(Style.FILL);
        // 抗锯齿
        mPaint.setAntiAlias(true);
        // 设置图片过滤波和抗锯齿
        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        // 第一个波的像素移动值 换算成手机像素值让其在各个手机移动速度差不多
        oneSeepPx = dpChangPx(oneSeep);
        // 第二个波的像素移动值
        twoSeepPx = dpChangPx(twoSeep);
        // 第三个波的像素移动值
        threeSeepPx = dpChangPx(threeSeep);

        // 每个波当前起始位置相差 一个波长 x 的 1/6
//        int x = (int) (amplitude * (Math.sin(frequency * 2 * Math.PI)));
//        Log.e(TAG, "init: 一个波x轴长度 x=" + x);
        oneNowOffSet = dpChangPx(0);
        twoNowOffSet = dpChangPx(100);
        threeNowOffSet = dpChangPx(200);
    }

    // 绘画方法
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(mDrawFilter);

        oneNowOffSet = oneNowOffSet + oneSeepPx;
        twoNowOffSet = twoNowOffSet + twoSeepPx;
        threeNowOffSet = threeNowOffSet + threeSeepPx;

        //超过一个周期置为 0
        if (oneNowOffSet >= period) {
            oneNowOffSet = 0;
        }
        if (twoNowOffSet >= period) {
            twoNowOffSet = 0;
        }
        if (threeNowOffSet >= period) {
            threeNowOffSet = 0;
        }

        reSet();

//        Log.e("fmy", Arrays.toString(twoWave));


        int lineHeight = 3;

        LinearGradient gradient = new LinearGradient(0, 0, viewWidth, lineHeight,
                0xFF13E4F4, 0xFF266BDE, Shader.TileMode.REPEAT);
        mPaint.setShader(gradient);

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

        mPaint.setStrokeWidth(3);

//        LinearGradient gradient = null;
        for (int i = 0; i < viewWidth; i++) {

            canvas.drawLine(i, viewHeight - amplitude - oneWave[i] - lineHeight, i,
                    viewHeight - amplitude - oneWave[i], mPaint);

            canvas.drawLine(i, viewHeight - amplitude - twoWave[i] - lineHeight, i,
                    viewHeight - amplitude - twoWave[i], mPaint);

            canvas.drawLine(i, viewHeight - amplitude - threeWave[i] - lineHeight, i,
                    viewHeight - amplitude - threeWave[i], mPaint);
        }
//        mPaint.setXfermode(null);

        SystemClock.sleep(5);
        postInvalidate();
    }

    public void reSet() {
        // one是指 走到此处的波纹的位置
        int one = viewWidth - oneNowOffSet;
        // 把未走过的波纹放到最前面 进行重新拼接
        System.arraycopy(wave, oneNowOffSet, oneWave, 0, one);
        // 把已走波纹放到最后
        System.arraycopy(wave, (int) (viewWidth - period), oneWave, one, oneNowOffSet);

        // two是指 走到此处的波纹的位置
        int two = viewWidth - twoNowOffSet;
        // 把未走过的波纹放到最前面 进行重新拼接
        System.arraycopy(wave, twoNowOffSet, twoWave, 0, two);
        // 把已走波纹放到最后
        System.arraycopy(wave, (int) (viewWidth - period), twoWave, two, twoNowOffSet);

        // three是指 走到此处的波纹的位置
        int three = viewWidth - threeNowOffSet;
        // 把未走过的波纹放到最前面 进行重新拼接
        System.arraycopy(wave, threeNowOffSet, threeWave, 0, three);
        // 把已走波纹放到最后
        System.arraycopy(wave, (int) (viewWidth - period), threeWave, three, threeNowOffSet);
    }

    // 大小改变
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 获取view的宽高
        viewHeight = h;
        viewWidth = w;

        // 初始化保存波形图的数组
        wave = new float[w];
        oneWave = new float[w];
        twoWave = new float[w];
        threeWave = new float[w];
        // 设置波形图周期（一个周期有多长的）
        period = w * 0.8f;
        frequency = 1 / period;
        //角速度
//        double ω = (2 * Math.PI / period) = 2 * Math.PI * frequency;

        // 计算每个点y坐标
        for (int i = 0; i < viewWidth; i++) {
            wave[i] = (float) (amplitude * Math.sin(2 * Math.PI * frequency * i));
        }


    }

    // dp换算成px 为了让移动速度在各个分辨率的手机的都差不多
    public int dpChangPx(int dp) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        return (int) (metrics.density * dp + 0.5f);
    }

}
