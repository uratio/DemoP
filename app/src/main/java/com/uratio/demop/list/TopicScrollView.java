package com.uratio.demop.list;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ScrollView;

public class TopicScrollView extends ScrollView {

    private int downX;

    private int downY;

    private int mTouchSlop;

    private ScrollViewListener scrollViewListener = null;

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    public TopicScrollView(Context context) {

        super(context);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

    }

    public TopicScrollView(Context context, AttributeSet attrs) {

        super(context, attrs);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

    }

    public TopicScrollView(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {

        int action = e.getAction();

        switch (action) {

            case MotionEvent.ACTION_DOWN:

                downX = (int) e.getRawX();

                downY = (int) e.getRawY();

                break;

            case MotionEvent.ACTION_MOVE:

                int moveY = (int) e.getRawY();

                if (Math.abs(moveY - downY) > mTouchSlop) {

                    return true;

                }
        }
        return super.onInterceptTouchEvent(e);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (scrollViewListener != null){
            scrollViewListener.onScrollChanged(this,l,t,oldl,oldt);
        }
    }

    public interface ScrollViewListener {
        void onScrollChanged(TopicScrollView scrollView, int x, int y, int oldX, int oldY);

    }
}