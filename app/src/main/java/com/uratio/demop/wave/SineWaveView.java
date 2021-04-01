package com.uratio.demop.wave;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DrawFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
 
import java.util.Arrays;
 
public class SineWaveView extends View {
 
    // 波纹颜色
    private static final int WAVE_PAINT_COLOR = 0x533cbabf;
 
    // 第一个波纹移动的速度
    private int oneSeep = 1;
 
    // 第二个波纹移动的速度
    private int twoSeep = 4;
 
    private int threeSeep = 3;
 
    // 第一个波纹移动速度的像素值
    private int oneSeepPxil;
    // 第二个波纹移动速度的像素值
    private int twoSeepPxil;
 
    private int threeSeepPxil;
 
    // 存放原始波纹的每个y坐标点
    private float wave[];
 
    // 存放第一个波纹的每一个y坐标点
    private float oneWave[];
 
    // 存放第二个波纹的每一个y坐标点
    private float twoWave[];
 
    private float threeWave[];
 
    // 第一个波纹当前移动的距离
    private int oneNowOffSet=dpChangPx(135);
    // 第二个波纹当前移动的
    private int twoNowOffSet=dpChangPx(265);
 
    private int threeNowOffSet;
 
    // 振幅高度
    private int amplitude = dpChangPx(35);
    ;
 
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
        oneSeepPxil = dpChangPx(oneSeep);
 
        // 第二个波的像素移动值
        twoSeepPxil = dpChangPx(twoSeep);
 
        threeSeepPxil = dpChangPx(threeSeep);
    }
 
    // 绘画方法
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(mDrawFilter);
 
        oneNowOffSet = oneNowOffSet + oneSeepPxil;
 
        twoNowOffSet = twoNowOffSet + twoSeepPxil;
 
        threeNowOffSet = threeNowOffSet + threeSeepPxil;
 
        if (oneNowOffSet >= viewWidth) {
            oneNowOffSet = 0;
        }
        if (twoNowOffSet >= viewWidth) {
            twoNowOffSet = 0;
        }
        if (threeNowOffSet >= viewWidth) {
            threeNowOffSet = 0;
        }
 
        reSet();
 
        Log.e("fmy", Arrays.toString(twoWave));


        float cy = viewHeight / 2;
        float startY = cy - amplitude * (float) (Math.sin(oneNowOffSet * 2 * (float) Math.PI / 360.0f));
        float endY =
                cy - amplitude * (float) (Math.sin(oneNowOffSet * 2 * (float) Math.PI / 360.0f + 2 * Math.PI * 1));
//
        LinearGradient gradient = new LinearGradient(0, startY, viewWidth, endY,
                0xFF7Bf0F9, 0xFF6E9CE9, Shader.TileMode.REPEAT);
        mPaint.setShader(gradient);

//        mPath.reset();

//        mPath.setFillType(Path.FillType.INVERSE_WINDING);
//        mPaint.setStyle(Paint.Style.FILL);
//        mPaint.setShader(null);
//        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        mPaint.setColor(getResources().getColor(android.R.color.transparent));
//        canvas.drawPath(mPath, mPaint);
        for (int i = 0; i < viewWidth; i++) {
 
            canvas.drawLine(i, viewHeight, i, viewHeight - amplitude - oneWave[i], mPaint);
            canvas.drawLine(i, viewHeight, i, viewHeight - amplitude - twoWave[i], mPaint);
            canvas.drawLine(i, viewHeight, i, viewHeight - amplitude - threeWave[i], mPaint);
        }
//        mPaint.setXfermode(null);
 
        SystemClock.sleep(5);
        postInvalidate();
    }
 
    public void reSet() {
        // one是指 走到此处的波纹的位置 (这个理解方法看个人了)
        int one = viewWidth - oneNowOffSet;
        // 把未走过的波纹放到最前面 进行重新拼接
        System.arraycopy(wave, oneNowOffSet, oneWave, 0, one);
        // 把已走波纹放到最后
        System.arraycopy(wave, 0, oneWave, one, oneNowOffSet);
 
        // one是指 走到此处的波纹的位置 (这个理解方法看个人了)
        int two = viewWidth - twoNowOffSet;
        // 把未走过的波纹放到最前面 进行重新拼接
        System.arraycopy(wave, twoNowOffSet, twoWave, 0, two);
        // 把已走波纹放到最后
        System.arraycopy(wave, 0, twoWave, two, twoNowOffSet);
 
 
        // one是指 走到此处的波纹的位置 (这个理解方法看个人了)
        int three = viewWidth - threeNowOffSet;
        // 把未走过的波纹放到最前面 进行重新拼接
        System.arraycopy(wave, threeNowOffSet, threeWave, 0, three);
        // 把已走波纹放到最后
        System.arraycopy(wave, 0, threeWave, three, threeNowOffSet);
 
 
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
        // 设置波形图周期
        float zq = (float) (Math.PI * 2 / w);
 
        // 设置波形图的周期
        for (int i = 0; i < viewWidth; i++) {
            wave[i] = (float) (amplitude * Math.sin(zq * i));
        }
 
 
    }
 
    // dp换算成px 为了让移动速度在各个分辨率的手机的都差不多
    public int dpChangPx(int dp) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        return (int) (metrics.density * dp + 0.5f);
    }
 
}
