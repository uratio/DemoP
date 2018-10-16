package com.uratio.demop.viewpager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class CustomRcv extends RecyclerView {
    private int mLastX;
    private int mLastY;

    public CustomRcv(@NonNull Context context) {
        super(context);
    }

    public CustomRcv(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRcv(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 事件分发
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        int x = (int)ev.getX();
        int y = (int)ev.getY();

        switch (ev.getAction()){

            case MotionEvent.ACTION_DOWN:
                Log.i("CustomViewPager:","dispatch_ACTION_DOWN");
                mLastX = x;
                mLastY = y;
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("CustomViewPager:","dispatch_ACTION_MOVE");
                int dx = Math.abs(x - mLastX);
                int dy = Math.abs(y - mLastY);

                if(dx * 4 > dy){
                    if(x >= mLastX){
                        // 上一页（ViewCompat.canScrollHorizontally(this,-1);用于判断ViewPager是否滑动到了左右边界）
                        if(ViewCompat.canScrollHorizontally(this,-1)){
                            getParent().requestDisallowInterceptTouchEvent(true);
                        }else {
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }
                    }else {
                        // 下一页
                        if(ViewCompat.canScrollHorizontally(this,1)){
                            getParent().requestDisallowInterceptTouchEvent(true);
                        }else {
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }
                    }
                }else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Log.i("CustomViewPager:","ACTION_UP/ACTION_CANCEL");
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }

        return super.dispatchTouchEvent(ev);
    }
}
