package com.uratio.demop.wave.voice;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.uratio.demop.utils.DisplayUtils;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author lang
 * @data 2021/5/13
 */
public class WaveSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = WaveSurfaceView.class.getSimpleName();

    private static final long SLEEP_TIME = 5;
    private Context mContext;
    private SurfaceHolder mHolder;
    private final Object mSurfaceLock = new Object();
    private DrawThread mThread;

    private Paint mPaint;
    private int color = 0xFFFF6D26;

    private int width;
    private int height;
    private int lineW = 2;
    private int spaceW = 4;
    private int startX = 0;
    private float[] waves;

    private long drawTime;
    private int invalidateTime = 100;

    /**
     * 灵敏度
     */
    private int sensibility = 4;

    private int maxVolume = 100;

    private boolean canDraw = true;

    public WaveSurfaceView(Context context) {
        this(context, null);
    }

    // xml布局构造方法
    public WaveSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveSurfaceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    // 初始化
    private void init(Context context, AttributeSet attrs) {
//        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WavePointView);
//        startColor = a.getColor(R.styleable.WavePointView_wp_start_color, DEF_START_COLOR);
//        endColor = a.getColor(R.styleable.WavePointView_wp_end_color, DEF_END_COLOR);
//        lineW = DisplayUtils.dp2px(context, a.getDimension(R.styleable.WavePointView_wp_line_width, DEF_LINE_WIDTH));
//        speed = DisplayUtils.dp2px(context, a.getInteger(R.styleable.WavePointView_wp_speed, DEF_SPEED));
//        amplitude = a.getFloat(R.styleable.WavePointView_wp_amplitude, DEF_AMPLITUDE);
//        amplitudeP = a.getFloat(R.styleable.WavePointView_wp_amplitude_radio, DEF_AMPLITUDE_RADIO);

        mHolder = getHolder();
        mHolder.addCallback(this);

        lineW = DisplayUtils.dp2px(context, lineW);
        startX = DisplayUtils.dp2px(context, startX);

        mPaint = new Paint();
        mPaint.setColor(color);
        mPaint.setStrokeWidth(lineW);
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int maxHeight = Math.max(20 * lineW, getMeasuredHeight());
        setMeasuredDimension(getMeasuredWidth(), maxHeight);
    }

    // 大小改变
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        int maxLines = w / (lineW + spaceW);
        maxLines = maxLines % 2 == 1 ? maxLines : maxLines - 1;

        if (waves == null) {
            waves = new float[maxLines];
        }
//        Arrays.fill(waves, lineW);

        // 绘画起始位置
        startX = (width + spaceW - maxLines * (lineW + spaceW)) / 2 + spaceW / 2;
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
                canDraw = false;
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
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
        //移动画笔到中心高度
        canvas.translate(startX, height / 2);

        for (int i = 0; i < waves.length; i++) {
            int x = i * (lineW + spaceW);
            canvas.drawLine(x, -waves[i], x, waves[i], mPaint);
        }
    }

    public void setVolume(int volume) {
        if (System.currentTimeMillis() - drawTime < invalidateTime) return;

        if (volume >= 3000) {
            volume = 100;
        } else {
            volume = (int) (volume / 30f);
        }

        if (volume > 50) {
            volume = (height / 2 - 10);
        }
        ThreadLocalRandom.current().nextInt(0, volume);

//        volume / maxVolume



        invalidate();
        drawTime = System.currentTimeMillis();
    }
}
