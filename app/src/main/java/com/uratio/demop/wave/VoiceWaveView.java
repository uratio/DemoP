package com.uratio.demop.wave;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;

public class VoiceWaveView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = DrawThread.class.getSimpleName();
    private static final long SLEEP_TIME = 1000;
    private Context mContext;
    private SurfaceHolder mHolder;
    private final Object mSurfaceLock = new Object();
    private DrawThread mThread;
    // 画笔
    private Paint mPaint;
    private Path mPathOne;
    private Path mPathTwo;
    private Path mPathThree;
    // 创建画布过滤
    private DrawFilter mDrawFilter;
    // view的宽度
    private int viewWidth;
    // view高度
    private int viewHeight;

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

    public VoiceWaveView(Context context) {
        this(context, null);
    }

    public VoiceWaveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoiceWaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.mContext = context;
        mHolder = getHolder();
        mHolder.addCallback(this);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setStrokeWidth(3);
        // 设置图片过滤波和抗锯齿
        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        mPathOne = new Path();
        mPathTwo = new Path();
        mPathThree = new Path();

        // 第一个波的像素移动值 换算成手机像素值让其在各个手机移动速度差不多
        oneSeepPx = dpChangPx(oneSeep);
        // 第二个波的像素移动值
        twoSeepPx = dpChangPx(twoSeep);
        // 第三个波的像素移动值
        threeSeepPx = dpChangPx(threeSeep);

        // 每个波当前起始位置相差
        oneNowOffSet = dpChangPx(0);
        twoNowOffSet = dpChangPx(100);
        threeNowOffSet = dpChangPx(200);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
//        setZOrderOnTop(true);//设置画布  背景透明
//        mHolder.setFormat(PixelFormat.TRANSLUCENT);

        mThread = new DrawThread(surfaceHolder);
        mThread.setRun(true);
        mThread.start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        //这里可以获取SurfaceView的宽高等信息
        viewHeight = height;
        viewWidth = width;

        // 初始化保存波形图的数组
        wave = new float[width];
        oneWave = new float[width];
        twoWave = new float[width];
        threeWave = new float[width];
        // 设置波形图周期（一个周期有多长的）
        period = width * 0.8f;
        frequency = 1 / period;
        //角速度
//        double ω = (2 * Math.PI / period) = 2 * Math.PI * frequency;

        // 计算每个点y坐标
//        for (int i = 0; i < viewWidth; i++) {
//            wave[i] = (float) (amplitude * Math.sin(2 * Math.PI * frequency * i));
//        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        synchronized (mSurfaceLock) {  //这里需要加锁，否则doDraw中有可能会crash
            mThread.setRun(false);
        }
    }

    private class DrawThread extends Thread {
        private SurfaceHolder mHolder;
        private boolean mIsRun = false;

        public DrawThread(SurfaceHolder holder) {
            super(TAG);
            mHolder = holder;
        }

        @Override
        public void run() {
            while (true) {
                synchronized (mSurfaceLock) {
                    if (!mIsRun) {
                        return;
                    }
                    Canvas canvas = mHolder.lockCanvas();
                    if (canvas != null) {
                        doDraw(canvas);  //这里做真正绘制的事情
                        mHolder.unlockCanvasAndPost(canvas);
                    }
                }
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void setRun(boolean isRun) {
            this.mIsRun = isRun;
        }
    }

    private void doDraw(Canvas canvas) {
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

//        reSet();

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

        float cy = viewHeight / 2;// centerY：高度中心 y 坐标

//        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        mPathOne.reset();
        for (int i = 0; i < viewWidth; i++) {
            if (i == 0) {
                mPathOne.moveTo(0, cy - amplitude * (float) (Math.sin(2 * Math.PI * frequency * i / viewWidth)));
            } else {
                mPathOne.lineTo(i, cy - amplitude * (float) (Math.sin(2 * Math.PI * frequency * i / viewWidth)));
            }
        }
//        mPathOne.lineTo(viewWidth - 1, 0);
        mPathOne.close();

        canvas.drawPath(mPathOne, mPaint);


        //多个正弦用数组移动
//        for (int i = 0; i < viewWidth - 1; i++) {
//            canvas.drawLine(i, viewHeight - amplitude - oneWave[i] - lineHeight, i,
//                    viewHeight - amplitude - oneWave[i], mPaint);
//            canvas.drawLine(i, viewHeight - amplitude - twoWave[i] - lineHeight, i,
//                    viewHeight - amplitude - twoWave[i], mPaint);
//            canvas.drawLine(i, viewHeight - amplitude - threeWave[i] - lineHeight, i,
//                    viewHeight - amplitude - threeWave[i], mPaint);
//        }

        //单个正弦移动
//        for (int i = 0; i < viewWidth; i++) {
//            canvas.drawLine(i,
//                    cy - amplitude * (float) (Math.sin(oneNowOffSet + 2 * Math.PI * frequency * i)) - lineHeight,
//                    i,
//                    cy - amplitude * (float) (Math.sin(oneNowOffSet + 2 * Math.PI * frequency * i)),
//                    mPaint);
//
//        }

        //多个正弦移动
//        for (int i = 0; i < viewWidth - 1; i++) {
//            canvas.drawLine((float) i,
//                    cy - amplitude * (float) (Math.sin(oneNowOffSet + 2 * Math.PI * frequency *
//                    i / viewWidth)),
//                    (float) (i + 1),
//                    cy - amplitude * (float) (Math.sin(oneNowOffSet + 2 * Math.PI * frequency *
//                    (i + 1) / viewWidth)), mPaint);
//
//            canvas.drawLine((float) i,
//                    cy - amplitude * (float) (Math.sin(twoNowOffSet + 2 * Math.PI * frequency *
//                    i / viewWidth)),
//                    (float) (i + 1),
//                    cy - amplitude * (float) (Math.sin(twoNowOffSet + 2 * Math.PI * frequency *
//                    (i + 1) / viewWidth)), mPaint);
//
//            canvas.drawLine((float) i,
//                    cy - amplitude * (float) (Math.sin(threeNowOffSet + 2 * Math.PI * frequency
//                    * i / viewWidth)),
//                    (float) (i + 1),
//                    cy - amplitude * (float) (Math.sin(threeNowOffSet + 2 * Math.PI * frequency
//                    * (i + 1) / viewWidth)), mPaint);
//
//        }

//        mPaint.setXfermode(null);
//        SystemClock.sleep(5);
//        postInvalidate();
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


    // dp换算成px 为了让移动速度在各个分辨率的手机的都差不多
    public int dpChangPx(int dp) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        return (int) (metrics.density * dp + 0.5f);
    }
}
