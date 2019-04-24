package com.uratio.demop.text;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class MessageView extends TextView {
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);

    //View的显示区域。
    final Rect bounds = new Rect();

    public MessageView(Context context) {
        super(context);
    }

    public MessageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MessageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        Log.i("messageView", "onMeasure: width="+width+"   height="+height+"   size="+getTextSize());
        int size = Math.max(width,height);
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int size = Math.max(width,height);
        float outerRadius = size / 2;

        //画内部背景
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xFFF64633);
        canvas.drawCircle(outerRadius, outerRadius, outerRadius, paint);

        //画字
        paint2.setColor(getTextColors().getDefaultColor());
        paint2.setAntiAlias(true);
        paint2.setTextAlign(Paint.Align.CENTER);
        paint2.setTextSize(getTextSize());
        float textY = outerRadius - (paint2.descent() + paint2.ascent()) / 2;
        canvas.drawText(getText().toString(), outerRadius, textY, paint2);
    }
}
