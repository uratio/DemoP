package com.uratio.demop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class CustomPopupView extends LinearLayout {
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path path = new Path();
    private Path path1 = new Path();
    private Path path2 = new Path();

    public CustomPopupView(Context context) {
        super(context);
        init(context, null);
    }

    public CustomPopupView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomPopupView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        path.moveTo(width-60,20);
        path.lineTo(width-40,0);
        path.lineTo(width-20,20);
        path.close();
        canvas.drawPath(path,paint);
        path1.addRoundRect(0,20,width,height,20,20, Path.Direction.CW);
        canvas.drawPath(path1,paint);

        paint.setColor(0xFFCCCCCC);
        paint.setStyle(Paint.Style.STROKE);
        path2.addArc(0,20,40,60,-180,90);
        path2.lineTo(width-60,20);
        path2.lineTo(width-40,0);
        path2.lineTo(width-20,20);
        path2.addArc(width-41,20,width-1,60,-90,90);
        path2.arcTo(width-41,height-41,width-1,height-1,-360,90, true);
        path2.arcTo(0,height-41,40,height-1,-270,90, true);
        path2.lineTo(0,40);
        canvas.drawPath(path2,paint);
        super.dispatchDraw(canvas);
    }
}
