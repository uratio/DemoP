package com.uratio.demop.viewpager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;

public class MyRcv extends RecyclerView {
    public MyRcv(@NonNull Context context) {
        super(context);
    }

    public MyRcv(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRcv(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // interceptTouch是自定义属性控制是否拦截事件
        if (true) {
            ViewParent parent = this;
            // 循环查找ViewPager, 请求ViewPager不拦截触摸事件
            while (!((parent = parent.getParent()) instanceof ViewPager)) {
                // nop
            }
            parent.requestDisallowInterceptTouchEvent(true);
        }
        return super.dispatchTouchEvent(ev);

    }
}
