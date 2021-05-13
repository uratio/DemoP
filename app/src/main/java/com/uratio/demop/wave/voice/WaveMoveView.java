package com.uratio.demop.wave.voice;import android.content.Context;import android.graphics.Canvas;import android.graphics.Paint;import android.support.annotation.Nullable;import android.util.AttributeSet;import android.util.Log;import android.view.View;import java.util.ArrayList;import java.util.List;public class WaveMoveView extends View {    private int width;    private int height;    private int lineWidth = 10;    private int spaceWidth = 10;    private int startX = 0;    private Paint mPaint;    private List<Integer> waves;    private int maxLines;    private long drawTime;    private int invalidateTime = 100;    /**     * 灵敏度     */    private int sensibility = 4;    private int maxVolume = 10000;    public WaveMoveView(Context context) {        super(context);        initPaint();    }    public WaveMoveView(Context context, @Nullable AttributeSet attrs) {        super(context, attrs);        initPaint();    }    public WaveMoveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {        super(context, attrs, defStyleAttr);        initPaint();    }    private void initPaint() {        mPaint = new Paint();        mPaint.setColor(0xFFFF6D26);        mPaint.setStrokeWidth(lineWidth);        mPaint.setAntiAlias(true);        mPaint.setFilterBitmap(true);        mPaint.setStrokeCap(Paint.Cap.ROUND);        mPaint.setStyle(Paint.Style.FILL);    }    @Override    protected void onSizeChanged(int w, int h, int oldw, int oldh) {        width = w;        height = h;        maxLines = w / (lineWidth + spaceWidth);        maxLines = maxLines % 2 == 1 ? maxLines : maxLines - 1;        if (waves == null) {            waves = new ArrayList<>();        }        for (int i = 0; i < maxLines; i++) {            waves.add(lineWidth);        }        // 绘画起始位置        startX = (width + spaceWidth - maxLines * (lineWidth + spaceWidth)) / 2 + spaceWidth / 2;    }    @Override    public void draw(Canvas canvas) {        super.draw(canvas);        canvas.translate(startX, height / 2);        for (int i = 0; i < waves.size(); i++) {            int x = i * (lineWidth + spaceWidth);            canvas.drawLine(x, -waves.get(i), x, waves.get(i), mPaint);        }    }    public void setVolume(int volume) {        int targetVolume = 0;        double percent = (double) volume / maxVolume;        double oneP = (double) lineWidth * 2 / height;        if (percent > (1-oneP)) {            if (volume > maxVolume) maxVolume = volume;            targetVolume = height / 2 - lineWidth;        } else if (percent > oneP && percent < (1-oneP)) {            targetVolume = (int) (percent * height / 2);        } else {            targetVolume = lineWidth;        }        if (System.currentTimeMillis() - drawTime > invalidateTime) {            waves.add(targetVolume);            if (waves.size() > maxLines) {                waves.remove(0);            }            invalidate();            drawTime = System.currentTimeMillis();        }    }}