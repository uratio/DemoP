package com.uratio.demop.ripple;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.uratio.demop.R;
import com.uratio.demop.utils.DisplayUtils;

public class WavePointSurfaceView2 extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = WavePointSurfaceView2.class.getSimpleName();

    // 波纹颜色
    private static final int DEF_START_COLOR = 0xFF266BDE;
    private static final int DEF_END_COLOR = 0xFF13E4F4;
    // 波纹宽度
    private static final float DEF_LINE_WIDTH = 2;
    // 移动速度
    private static final int DEF_SPEED = 4;
    // 振幅
    private static final float DEF_AMPLITUDE = 60f;
    // 开始结束时振幅比例
    private static final float DEF_AMPLITUDE_RADIO = 0.5f;

    private static final long SLEEP_TIME = 5;
    private Context mContext;
    private SurfaceHolder mHolder;
    private final Object mSurfaceLock = new Object();
    private DrawThread mThread;

    //渐变颜色
    private int startColor;
    private int endColor;
    private int[] colors1 = {0xFF9C27B0, 0xFF00BCD4};
    private int[] colors2 = {0xFF8BC34A, 0xFFFF5722};
    private int[] colors3 = {0xFF3F51B5, 0xFFFFEB3B};
    // 画笔
    private Paint mPaint1;
    private Paint mPaint2;
    private Paint mPaint3;
    private Path mPath1;
    private Path mPath2;
    private Path mPath3;

    // view的宽度
    private int viewWidth;
    // view中心高度
    private float halfH;
    // 画笔宽度
    private float lineW;

    // 波纹移动的速度
    private int speed;
    // 波纹当前移动的距离
    private int offSet;
    // 过渡阶段宽度
    private int interim = 0;

    // 存放原始波纹的每个y坐标点
    private float[] wave1;
    private float[] wave2;
    private float[] wave3;

    // 波2、3的初相位百分比
    private float phaseRatio2 = 1f / 3;
    private float phaseRatio3 = 2f / 3;

    //振幅（根据声音大小动态修改）
    private float amplitude;
    private float amplitudeP;
    //周期
    private float period;

    /**
     * 过渡阶段
     */
    //过渡阶段1：周期
    private float stepOneW1;
    private float stepOneW2;
    private float stepOneW3;
    //过渡阶段1：振幅
    private float stepOneH1;
    private float stepOneH2;
    private float stepOneH3;
    //过渡阶段1：相对于正常振幅百分比
    private float stepOneRadio = 0.2f;
    //过渡阶段2：周期
    private float stepTwoW;
    //过渡阶段2：振幅
    private float stepTwoH1;
    private float stepTwoH2;
    private float stepTwoH3;

    /**
     * 阶段：
     * 1：动画开始阶段
     * 2：动画开始过渡阶段
     * 3：根据声音震动阶段
     * 4：动画结束过渡阶段
     * 5：动画结束阶段
     * 6: 回到阶段 1，并停止绘画
     */
    private int step = 1;

    public WavePointSurfaceView2(Context context) {
        this(context, null);
    }

    // xml布局构造方法
    public WavePointSurfaceView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WavePointSurfaceView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    // 初始化
    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WavePointView);
        startColor = a.getColor(R.styleable.WavePointView_wp_start_color, DEF_START_COLOR);
        endColor = a.getColor(R.styleable.WavePointView_wp_end_color, DEF_END_COLOR);
        lineW = DisplayUtils.dp2px(context, a.getDimension(R.styleable.WavePointView_wp_line_width, DEF_LINE_WIDTH));
        speed = DisplayUtils.dp2px(context, a.getInteger(R.styleable.WavePointView_wp_speed, DEF_SPEED));
        amplitude = a.getFloat(R.styleable.WavePointView_wp_amplitude, DEF_AMPLITUDE);
        amplitudeP = a.getFloat(R.styleable.WavePointView_wp_amplitude_radio, DEF_AMPLITUDE_RADIO);

        mHolder = getHolder();
        mHolder.addCallback(this);

        lineW = DisplayUtils.dp2px(context, DEF_LINE_WIDTH);
        // 创建画笔
        mPaint1 = createPaint();
        mPaint2 = createPaint();
        mPaint3 = createPaint();

        mPath1 = new Path();
        mPath2 = new Path();
        mPath3 = new Path();
    }

    private Paint createPaint() {
        Paint paint = new Paint();
        // 设置绘画风格为实线
        paint.setStyle(Style.STROKE);
        // 抗锯齿
        paint.setAntiAlias(true);
        paint.setStrokeWidth(lineW);
        return paint;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float manHeight = Math.max(amplitude * 2 + lineW, getMeasuredHeight());
        setMeasuredDimension(getMeasuredWidth(), (int) manHeight);
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

        //过渡阶段：周长
        stepOneW1 = period / 4;
        stepOneW2 = period * (1f / 4 + phaseRatio2 / 2);
        stepOneW3 = period * (1f / 4 + phaseRatio3 / 2);
        stepOneH1 = amplitude * amplitudeP * stepOneRadio;
        stepOneH2 = amplitude * amplitudeP * stepOneRadio * (1 - phaseRatio2 / 2);
        stepOneH3 = amplitude * amplitudeP * stepOneRadio * (1 - phaseRatio3 / 2);

        stepTwoW = period / 2;
        stepTwoH1 = amplitude * amplitudeP / 2f + stepOneH1;
        stepTwoH2 = amplitude * amplitudeP / 2f + stepOneH2;
        stepTwoH3 = amplitude * amplitudeP / 2f + stepOneH3;

        // 初始化保存波形图的数组(保证最后一个坐标有整个波长的位移空间)
        wave1 = new float[(int) (w + period * 2)];
        wave2 = new float[(int) (w + period * 2)];
        wave3 = new float[(int) (w + period * 2)];

        // 计算每个点y坐标
        for (int i = 0; i < w + period * 2; i++) {
            wave1[i] = (float) (amplitude * Math.sin(2f * Math.PI / period * i));
            wave2[i] = (float) (amplitude * Math.sin(2f * Math.PI / period * i + 2f * Math.PI * phaseRatio2));
            wave3[i] = (float) (amplitude * Math.sin(2f * Math.PI / period * i + 2f * Math.PI * phaseRatio3));
        }

        if (mPaint1 != null) {
            mPaint1.setShader(new LinearGradient(0, 0, w, h, colors1, null, Shader.TileMode.CLAMP));
        }
        if (mPaint2 != null) {
            mPaint2.setShader(new LinearGradient(0, 0, w, h, colors2, null, Shader.TileMode.CLAMP));

        }
        if (mPaint3 != null) {
            mPaint3.setShader(new LinearGradient(0, 0, w, h, colors3, null, Shader.TileMode.CLAMP));
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //设置画布  背景透明
        setZOrderOnTop(true);
        mHolder.setFormat(PixelFormat.TRANSLUCENT);

        mThread = new DrawThread(holder);
        mThread.setRun(true);
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        synchronized (mSurfaceLock) {  //这里需要加锁，否则doDraw中有可能会crash
            if (mThread != null) {
                mThread.setRun(false);
            }
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
                        //绘制内容
                        doDraw(canvas);
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

    /**
     * 绘制内容
     */
    private void doDraw(Canvas canvas) {
        //清除之前绘制内容
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
        //移动画笔到中心高度
        canvas.translate(0, halfH);

        mPath1.reset();
        mPath2.reset();
        mPath3.reset();

        if (step == 1) {
            drawStep1();
        } else if (step == 2) {
            drawStep2();
        } else if (step == 3) {
            drawStep3();
        } else if (step == 4) {
            drawStep4();
        } else if (step == 5) {
            drawStep5();
        } else {
            //回到阶段1，停止绘制
            step = 1;
            mThread.setRun(false);
        }

        canvas.drawPath(mPath1, mPaint1);
        canvas.drawPath(mPath2, mPaint2);
        canvas.drawPath(mPath3, mPaint3);
    }

    /**
     * 动画开始阶段
     */
    private void drawStep1() {
        mPath3.moveTo(viewWidth / 2 - interim, 0);
        mPath3.rLineTo(2 * interim, 0);
        // 更新偏移量
        resetOffsetStep1();
    }

    /**
     * 动画开始过渡阶段
     */
    private void drawStep2() {
        if (offSet < viewWidth) {
            mPath1.moveTo(0, 0);
            mPath2.moveTo(0, 0);
            mPath3.moveTo(0, 0);
            mPath1.lineTo(viewWidth - offSet, 0);
            mPath2.lineTo(viewWidth - offSet, 0);
            mPath3.lineTo(viewWidth - offSet, 0);
        } else {
            mPath1.moveTo(viewWidth - offSet, 0);
            mPath2.moveTo(viewWidth - offSet, 0);
            mPath3.moveTo(viewWidth - offSet, 0);
        }

        // 汇合阶段
        mPath1.rQuadTo(stepOneW1 / 4, 0, stepOneW1 / 2, stepOneH1);
        mPath1.rQuadTo(stepOneW1 / 4, stepOneH1, stepOneW1 / 2, stepOneH1);

        mPath2.rQuadTo(stepOneW2 / 4, 0, stepOneW2 / 2, -stepOneH2);
        mPath2.rQuadTo(stepOneW2 / 4, -stepOneH2, stepOneW2 / 2, -stepOneH2);

        mPath3.rQuadTo(stepOneW3 / 4, 0, stepOneW3 / 2, stepOneH3);
        mPath3.rQuadTo(stepOneW3 / 4, stepOneH3, stepOneW3 / 2, stepOneH3);

        // 过渡2阶段
        mPath1.rQuadTo(stepTwoW / 4, 0, stepTwoW / 2, -stepTwoH1);
        mPath1.rQuadTo(stepTwoW / 4, -stepTwoH1, stepTwoW / 2, -stepTwoH1);

        mPath2.rQuadTo(stepTwoW / 4, 0, stepTwoW / 2, stepTwoH2);
        mPath2.rQuadTo(stepTwoW / 4, stepTwoH2, stepTwoW / 2, stepTwoH2);

        mPath3.rQuadTo(stepTwoW / 4, 0, stepTwoW / 2, -stepTwoH3);
        mPath3.rQuadTo(stepTwoW / 4, -stepTwoH3, stepTwoW / 2, -stepTwoH3);

        if (offSet > stepOneW1 + stepTwoW) {
            for (int i = 0; i < viewWidth + period - stepOneW1 - stepTwoW; i++) {
                mPath1.lineTo(i + stepOneW1 + stepTwoW + viewWidth - offSet,
                        wave1[(int) (i + stepOneW1 + stepTwoW)] * amplitudeP);
                mPath2.lineTo(i + stepOneW2 + stepTwoW + viewWidth - offSet,
                        wave2[(int) (i + stepOneW2 + stepTwoW)] * amplitudeP);
                mPath3.lineTo(i + stepOneW3 + stepTwoW + viewWidth - offSet,
                        wave3[(int) (i + stepOneW3 + stepTwoW)] * amplitudeP);
            }
        }
        // 更新偏移量
        resetOffsetStep2();
    }

    /**
     * 根据声音震动阶段
     */
    private void drawStep3() {
        mPath1.moveTo(0, amplitudeP * wave1[offSet]);
        mPath2.moveTo(0, amplitudeP * wave2[offSet]);
        mPath3.moveTo(0, amplitudeP * wave3[offSet]);
        for (int i = 1; i < viewWidth; i++) {
            mPath1.lineTo(i, amplitudeP * wave1[offSet + i]);
            mPath2.lineTo(i, amplitudeP * wave2[offSet + i]);
            mPath3.lineTo(i, amplitudeP * wave3[offSet + i]);
        }

        // 更新偏移量
        resetOffsetStep3();
    }

    /**
     * 动画结束过渡阶段
     */
    private void drawStep4() {
        mPath1.moveTo(-offSet, 0);
        mPath2.moveTo(-offSet, 0);
        mPath3.moveTo(-offSet, 0);

        for (int i = 0; i < viewWidth + period - stepOneW1 - stepTwoW; i++) {
            mPath1.lineTo(i - offSet, wave1[(int) (2 * (stepOneW1 + stepTwoW) - viewWidth + i)] * amplitudeP);
        }
        for (int i = 0; i < viewWidth + period - stepOneW2 - stepTwoW; i++) {
            mPath3.lineTo(i - offSet, wave2[(int) (2 * (stepOneW2 + stepTwoW) - viewWidth + i)] * amplitudeP);
        }
        for (int i = 0; i < viewWidth + period - stepOneW3 - stepTwoW; i++) {
            mPath2.lineTo(i - offSet, wave3[(int) (2 * (stepOneW3 + stepTwoW) - viewWidth + i)] * amplitudeP);
        }

        // 过渡2阶段
        mPath1.rQuadTo(stepTwoW / 4, 0, stepTwoW / 2, stepTwoH1);
        mPath1.rQuadTo(stepTwoW / 4, stepTwoH1, stepTwoW / 2, stepTwoH1);

        mPath3.rQuadTo(stepTwoW / 4, 0, stepTwoW / 2, -stepTwoH2);
        mPath3.rQuadTo(stepTwoW / 4, -stepTwoH2, stepTwoW / 2, -stepTwoH2);

        mPath2.rQuadTo(stepTwoW / 4, 0, stepTwoW / 2, stepTwoH3);
        mPath2.rQuadTo(stepTwoW / 4, stepTwoH3, stepTwoW / 2, stepTwoH3);

        // 汇合阶段
        mPath1.rQuadTo(stepOneW1 / 4, 0, stepOneW1 / 2, -stepOneH1);
        mPath1.rQuadTo(stepOneW1 / 4, -stepOneH1, stepOneW1 / 2, -stepOneH1);

        mPath3.rQuadTo(stepOneW2 / 4, 0, stepOneW2 / 2, stepOneH2);
        mPath3.rQuadTo(stepOneW2 / 4, stepOneH2, stepOneW2 / 2, stepOneH2);

        mPath2.rQuadTo(stepOneW3 / 4, 0, stepOneW3 / 2, -stepOneH3);
        mPath2.rQuadTo(stepOneW3 / 4, -stepOneH3, stepOneW3 / 2, -stepOneH3);

        mPath1.rLineTo(viewWidth, 0);

        // 更新偏移量
        resetOffsetStep4();
    }

    /**
     * 动画结束阶段
     */
    private void drawStep5() {
        mPath3.moveTo(viewWidth / 2 - interim, 0);
        mPath3.rLineTo(2 * interim, 0);
        // 更新偏移量
        resetOffsetStep5();
    }

    private void resetOffsetStep1() {
        interim += speed;
        if (interim > viewWidth / 2) {
            interim = viewWidth / 2;
            //进入过渡阶段
            step = 2;
        }
    }

    private void resetOffsetStep2() {
        offSet = offSet + speed;

        if (offSet >= period + viewWidth) {
            offSet = 0;
            //根据声音震动阶段
//            step = 3;
            mThread.setRun(false);
        }
    }

    private int count = 0;

    private void resetOffsetStep3() {
        offSet = offSet + speed;

        if (count > 1) {
            if (offSet > period / 4) {
                offSet = 0;
                step = 4;
                return;
            }
        }

        if (offSet >= period) {
            count++;
            offSet = 0;
        }
    }

    private void resetOffsetStep4() {
        offSet = offSet + speed;

        if (offSet >= viewWidth + period) {
            offSet = 0;
            //根据声音震动阶段
            step = 5;
        }
    }

    private void resetOffsetStep5() {
        interim -= speed;
        if (interim < 0) {
            interim = 0;
            //回到阶段1，停止绘制
            step = 6;
        }
    }
}
