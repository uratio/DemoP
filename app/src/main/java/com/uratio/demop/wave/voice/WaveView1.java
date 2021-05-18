package com.uratio.demop.wave.voice;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author： lizhi.
 * @date：2021/3/30-3:50 PM
 * @desc:
 */
public class WaveView1 extends View {

    private int width;
    private int height;
    private int lineWidth = 5;
    private int spaceWidth = 5;
    private Paint mPaint;
    private List<Integer> datas = new ArrayList<>();
    private int maxLine;
    private long drawTime;
    private int invalidateTime = 50;

    public WaveView1(Context context) {
        super(context);
        initPaint();
    }

    public WaveView1(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public WaveView1(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(0xFFFF6D26);
        mPaint.setStrokeWidth(lineWidth);
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w;
        height = h;
        maxLine = w / (lineWidth + spaceWidth);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.translate(0, height / 2);
        if (datas.size() == 0) {
            for (int i = 0; i < 1000; i++) {
                int x = i * (lineWidth * 2);
                canvas.drawLine(x,-1, x, 1, mPaint);
            }
            return;
        }
        for (int i = 0; i < datas.size(); i++) {
            int x = i * (lineWidth * 2);
            canvas.drawLine(x, -datas.get(i), x, datas.get(i), mPaint);
        }
    }

    public void startAnim(int volume) {
        int marAnimNum = maxLine / 5;
        datas.clear();
        volume = volume / 10;

        if (volume == 0)
            volume = 10;
        for (int q = 0; q < 100; q++) {

            for (int i = 0; i < marAnimNum; i++) {
                datas.add(new Random().nextInt(10));
            }
            if (volume > height / 2)
                volume = (height / 2 - lineWidth);
            for (int j = marAnimNum; j < maxLine - marAnimNum; j++) {
                datas.add(new Random().nextInt(volume));
            }
            for (int i = 0; i < marAnimNum; i++) {
                datas.add(new Random().nextInt(10));
            }
            if (System.currentTimeMillis() - drawTime > invalidateTime) {
                invalidate();
                drawTime = System.currentTimeMillis();
            }
        }
    }

    /**
     * 重置
     */
    public void resetView() {
        datas.clear();
        invalidate();
    }
}
