package com.uratio.demop.touch;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MyView extends View {
    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.i("touch", "MyView___dispatchTouchEvent: ");
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i("touch", "MyView___onTouchEvent: ");
        return super.onTouchEvent(event);
    }

    //启动线程/ 动画
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    //停止线程/ 动画
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
