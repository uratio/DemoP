package com.uratio.demop.ripple;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.uratio.demop.R;
import com.uratio.demop.utils.DisplayUtils;

public class WavePointSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = WavePointSurfaceView.class.getSimpleName();

    // 波纹宽度
    private static final float DEF_LINE_WIDTH = 2;
    // 移动速度
    private static final float DEF_SPEED_RATE = 0.5f;
    // 振幅
    private static final float DEF_AMPLITUDE = 30f;
    // 开始结束时振幅比例
    private static final float DEF_AMPLITUDE_RADIO = 1f;
    // 最大音量
    private static final int MAX_VOLUME = 3000;

    private static final long SLEEP_TIME = 5;
    private Context mContext;
    private SurfaceHolder mHolder;
    private final Object mSurfaceLock = new Object();
    private DrawThread mThread;

    //渐变颜色
    private int startColor;
    private int endColor;
    private int[] colors;
    private int[] colors1 = {0xFF9C27B0, 0xFF00BCD4};//蓝
    private int[] colors2 = {0xFF8BC34A, 0xFFFF5722};//红
    private int[] colors3 = {0xFF3F51B5, 0xFFFFEB3B};//黄
    // 画笔
    private Paint mPaint1;
    private Paint mPaint2;
    private Paint mPaint3;

    // view的宽度
    private int viewWidth;
    // view中心高度
    private float halfH;
    // 画笔宽度
    private float lineW;

    // 波纹移动的速度
    private int speed;
    // 波纹移动的速度比例（1：100）
    private float speedRate;
    // 波纹当前移动的距离
    private int offSet;
    // 过渡阶段宽度
    private int interim = 0;

    // 存放原始波纹的每个y坐标点
    private float[] wave1;
    private float[] wave2;
    private float[] wave3;

    //振幅（根据声音大小动态修改）
    private float amplitude;
    private float amplitudeP;
    //周期
    private float period;

    /**
     * 阶段：
     *      1：动画开始阶段
     *      2：动画开始过渡阶段
     *      3：根据声音震动阶段
     *      4：动画结束过渡阶段
     *      5：动画结束阶段
     *      6: 回到阶段 1，并停止绘画
     */
    private int step = 1;
    private boolean canDraw = true;
    private boolean speaking = true;
    //第三段计数，必须走过一段
    private int count = 0;

    private WaveListener listener;

    public WavePointSurfaceView(Context context) {
        this(context, null);
    }

    // xml布局构造方法
    public WavePointSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WavePointSurfaceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    // 初始化
    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WavePointSurfaceView);
        startColor = a.getColor(R.styleable.WavePointSurfaceView_wpv_start_color, 0);
        endColor = a.getColor(R.styleable.WavePointSurfaceView_wpv_end_color, 0);
        lineW = DisplayUtils.dp2px(a.getDimension(R.styleable.WavePointSurfaceView_wpv_line_width, DEF_LINE_WIDTH));
        speedRate = a.getFloat(R.styleable.WavePointSurfaceView_wpv_speed_rate, DEF_SPEED_RATE);
        amplitude = a.getFloat(R.styleable.WavePointSurfaceView_wpv_amplitude, DEF_AMPLITUDE);
        amplitudeP = a.getFloat(R.styleable.WavePointSurfaceView_wpv_amplitude_radio, DEF_AMPLITUDE_RADIO);

        if (startColor != 0 && endColor != 0) {
            colors = new int[]{startColor, endColor};
        }

        // 创建画笔
        mPaint1 = createPaint();
        mPaint2 = createPaint();
        mPaint3 = createPaint();

        mHolder = getHolder();
        mHolder.addCallback(this);

        setZOrderOnTop(true);
        mHolder.setFormat(PixelFormat.TRANSLUCENT);
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
        speed = (int) (viewWidth * speedRate / 100);

        // 设置波形图周期（一个周期有多长的）
        period = w * 0.8f;

        // 初始化保存波形图的数组(保证最后一个坐标有整个波长的位移空间)
        wave1 = new float[(int) (period * 2)];
        wave2 = new float[(int) (period * 2)];
        wave3 = new float[(int) (period * 2)];

//        // 计算每个点y坐标
        for (int i = 0; i < period * 2; i++) {
            wave1[i] = (float) (amplitude * Math.sin(2f * Math.PI / period * i));
            // 波2、3的初相位百分比
            wave2[i] = (float) (amplitude * Math.sin(2f * Math.PI / period * i + 2f * Math.PI / 4));
            wave3[i] = (float) (amplitude * Math.sin(2f * Math.PI / period * i + 2f * Math.PI / 2));
        }

        if (mPaint1 != null) {
            int[] arrColor = colors == null || colors.length == 0 ? colors1 : colors;
            mPaint1.setShader(new LinearGradient(0, 0, w, h, arrColor, null, Shader.TileMode.CLAMP));
        }
        if (mPaint2 != null) {
            int[] arrColor = colors == null || colors.length == 0 ? colors2 : colors;
            mPaint2.setShader(new LinearGradient(0, 0, w, h, arrColor, null, Shader.TileMode.CLAMP));

        }
        if (mPaint3 != null) {
            int[] arrColor = colors == null || colors.length == 0 ? colors3 : colors;
            mPaint3.setShader(new LinearGradient(0, 0, w, h, arrColor, null, Shader.TileMode.CLAMP));
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (!isShown()) {
            //隐藏的时候，当前动画已完成，主动调用了GONE，清除线程数据
            onDestroy();
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
            while (canDraw) {
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
        canvas.drawColor(Color.YELLOW, PorterDuff.Mode.CLEAR);
        //移动画笔到中心高度
        canvas.translate(0, halfH);

        if (step == 1) {
            drawStep1(canvas);
        } else if (step == 2) {
            drawStep2(canvas);
        } else if (step == 3) {
            drawStep3(canvas);
        } else if (step == 4) {
            drawStep4(canvas);
        } else if (step == 5){
            drawStep5(canvas);
        } else {
            //回到阶段1，停止绘制
            step = 1;
            canDraw = false;
            mThread.setRun(false);
            if (listener != null) {
                listener.onAnimFinished();
            }
        }
    }

    /**
     * 动画开始阶段
     */
    private void drawStep1(Canvas canvas) {
        for (int i = viewWidth / 2 - interim; i < viewWidth / 2 + interim; i++) {
            canvas.drawPoint(i, lineW, mPaint3);
        }
        // 更新偏移量
        resetOffsetStep1();
    }

    /**
     * 动画开始过渡阶段
     */
    private void drawStep2(Canvas canvas) {
        int front = viewWidth - offSet;
        //直线部分
        for (int i = 0; i < front; i++) {
            canvas.drawPoint(i, lineW, mPaint3);
        }
        //正弦曲线部分
        for (int i = front; i < viewWidth; i++) {
            int i1 = i - offSet;
            while (i1 < 0) {
                i1 = (int) (period + i1);
            }
            float ratio;
            if (offSet < period / 4) {
                //曲线长度在 1/4 个周期长度之内，根据固定长度计算高度比率
                ratio = 1f * (i - front) * 4 / period;
            } else {
                ratio = 1f * (i - front) / offSet;
            }
            canvas.drawPoint(i, wave1[i1] * ratio + lineW, mPaint1);
            canvas.drawPoint(i, wave2[i1] * ratio + lineW, mPaint2);
            canvas.drawPoint(i, wave3[i1] * ratio + lineW, mPaint3);
        }

        // 更新偏移量
        resetOffsetStep2();
    }

    /**
     * 根据声音震动阶段
     */
    private void drawStep3(Canvas canvas) {
        for (int i = 0; i < period; i++) {
            canvas.drawPoint(i, amplitudeP * wave1[offSet + i] + lineW, mPaint1);
            canvas.drawPoint(i, amplitudeP * wave2[offSet + i] + lineW, mPaint2);
            canvas.drawPoint(i, amplitudeP * wave3[offSet + i] + lineW, mPaint3);
        }
        for (int i = (int) period; i < viewWidth; i++) {
            canvas.drawPoint(i, amplitudeP * wave1[(int) (offSet + i - period)] + lineW, mPaint1);
            canvas.drawPoint(i, amplitudeP * wave2[(int) (offSet + i - period)] + lineW, mPaint2);
            canvas.drawPoint(i, amplitudeP * wave3[(int) (offSet + i - period)] + lineW, mPaint3);
        }

        // 更新偏移量
        resetOffsetStep3();
    }

    /**
     * 动画结束过渡阶段
     */
    private void drawStep4(Canvas canvas) {
        //正弦曲线部分
        int front = viewWidth - offSet;
        for (int i = 0; i < front; i++) {
            int i1 = i - offSet;
            while (i1 < 0) {
                i1 = (int) (period + i1);
            }
            float ratio;
            if (front < period / 4) {
                //曲线长度在 1/4 个周期长度之内，根据固定长度计算高度比率
                ratio = 1f * (front - i) * 4 / period;
            } else {
                ratio = 1f * (front - i) / front;
            }
            canvas.drawPoint(i, wave1[i1] * ratio + lineW, mPaint1);
            canvas.drawPoint(i, wave2[i1] * ratio + lineW, mPaint2);
            canvas.drawPoint(i, wave3[i1] * ratio + lineW, mPaint3);
        }
        //直线部分
        for (int i = front; i < viewWidth; i++) {
            canvas.drawPoint(i, lineW, mPaint3);
        }

        // 更新偏移量
        resetOffsetStep4();
    }

    /**
     * 动画结束阶段
     */
    private void drawStep5(Canvas canvas) {
        for (int i = viewWidth / 2 - interim; i < viewWidth / 2 + interim; i++) {
            canvas.drawPoint(i, lineW, mPaint3);
        }

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

        if (offSet >= viewWidth) {
            offSet = (int) period;
            //根据声音震动阶段
            step = 3;
            //扩大移动速度为2倍
            speed = speed * 2;
        }
    }

    private void resetOffsetStep3() {
        offSet = offSet - speed;

        if (!speaking && count > 1) {
            if (offSet > period / 4) {
                offSet = 0;
                step = 4;
                //恢复移动速度
                speed = speed / 2;
                return;
            }
        }

        if (offSet < 0) {
            count++;
            offSet = (int) period;
        }
    }

    private void resetOffsetStep4() {
        offSet = offSet + speed;

        if (offSet >= viewWidth) {
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

    public void start() {
        step = 1;
        canDraw = true;
        speaking = true;
        count = 0;

        mThread = new DrawThread(mHolder);
        mThread.setRun(true);
        mThread.start();
    }

    public void resume() {
        canDraw = true;
        mThread = new DrawThread(mHolder);
        mThread.setRun(true);
        mThread.start();
    }

    public void pause() {
        canDraw = false;
    }

    public void stop() {
        speaking = false;
    }

    public void setVolume(int volume) {
        //转换为实际可变高度
        if (step == 3) {
            if (volume >= MAX_VOLUME) {
                amplitudeP = 2;
            } else if (volume > MAX_VOLUME / 2) {
                amplitudeP = 1.5f;
            } else {
                amplitudeP = 1;
            }
        } else {
            amplitudeP = 1;
        }
    }

    public void setWaveListener(WaveListener listener) {
        this.listener = listener;
    }

    public interface WaveListener {
        void onAnimFinished();
    }

    public void onDestroy() {
        synchronized (mSurfaceLock) {  //这里需要加锁，否则doDraw中有可能会crash
            if (mThread != null) {
                mThread.setRun(false);
                canDraw = false;
            }
        }
    }
}
