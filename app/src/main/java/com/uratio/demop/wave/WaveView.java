package com.uratio.demop.wave;import android.content.Context;import android.graphics.Canvas;import android.graphics.Paint;import android.support.annotation.Nullable;import android.util.AttributeSet;import android.view.View;import java.util.Random;/** * @author： lizhi. * @date：2021/3/30-3:50 PM * @desc: */public class WaveView extends View {    private int width;    private int height;    private int volume = 0;    private int lineWidth = 10;    private int spaceWidth = 10;    private int startX = 0;    private Paint mPaint;    private float[] waves;    private int maxLines;    private long drawTime;    private int invalidateTime = 100;    public WaveView(Context context) {        super(context);        initPaint();    }    public WaveView(Context context, @Nullable AttributeSet attrs) {        super(context, attrs);        initPaint();    }    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {        super(context, attrs, defStyleAttr);        initPaint();    }    private void initPaint() {        mPaint = new Paint();        mPaint.setColor(0xFFFF6D26);        mPaint.setStrokeWidth(lineWidth);        mPaint.setAntiAlias(true);        mPaint.setFilterBitmap(true);        mPaint.setStrokeCap(Paint.Cap.ROUND);        mPaint.setStyle(Paint.Style.FILL);    }    @Override    protected void onSizeChanged(int w, int h, int oldw, int oldh) {        width = w;        height = h;        maxLines = w / (lineWidth + spaceWidth);        maxLines = maxLines % 2 == 1 ? maxLines : maxLines - 1;        if (waves == null) {            waves = new float[maxLines];        }        for (int i = 0; i < waves.length; i++) {            waves[i] = 3 * lineWidth;        }        // 绘画起始位置        startX = (width + spaceWidth - maxLines * (lineWidth + spaceWidth)) / 2;    }    @Override    public void draw(Canvas canvas) {        super.draw(canvas);        canvas.translate(startX, height / 2);        for (int i = 0; i < waves.length; i++) {            int x = startX + i * (lineWidth + spaceWidth);            canvas.drawLine(x, -waves[i], x, waves[i], mPaint);        }    }    public void startAnim(int volume) {//        int marAnimNum = maxLines / 5;//        datas.clear();//        volume = volume/10;////        if(volume == 0)//            volume = 10;//        for (int q = 0; q < 100; q++) {////            for (int i = 0; i < marAnimNum; i++) {//                datas.add(new Random().nextInt(10));//            }//            if (volume > height/2)//                volume = (height/2-10);//            for (int j = marAnimNum; j < maxLines - marAnimNum; j++) {//                datas.add(new Random().nextInt(volume));//            }//            for (int i = 0; i < marAnimNum; i++) {//                datas.add(new Random().nextInt(10));//            }//            if (System.currentTimeMillis() - drawTime > invalidateTime) {//                invalidate();//                drawTime = System.currentTimeMillis();//            }//        }    }}