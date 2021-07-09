package com.uratio.demop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.style.ImageSpan;
import android.util.Log;

public class CenteredImageSpan extends ImageSpan {

    private static final String TAG = CenteredImageSpan.class.getSimpleName();

    public CenteredImageSpan(Context context, final int drawableRes) {
        super(context, drawableRes);
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text,
                     int start, int end, float x,
                     int top, int y, int bottom, @NonNull Paint paint) {
        // image to draw
        Drawable b = getDrawable();
        b.setBounds(0, 0, 45, 45);
        // font metrics of text to be replaced
        Paint.FontMetricsInt fm = paint.getFontMetricsInt();
        Log.e(TAG,
                "draw: text=" + text + " \nstart=" + start + " end=" + end+ " x=" + x + " top=" + top + " y=" + y +
                        " bottom=" + bottom);
        int transY = (y + fm.descent + y + fm.ascent) / 2 - b.getBounds().bottom / 2;
        Log.e(TAG,
                "draw: fm.top=" + fm.top + " fm.ascent=" + fm.ascent + " fm.descent=" + fm.descent + " b.getBounds" +
                        "()" +
                        ".bottom=" + b.getBounds().bottom);

        Log.e(TAG, "draw: transY=" + transY);
        canvas.save();
        canvas.translate(x, transY);
        b.draw(canvas);
        canvas.restore();
    }
}