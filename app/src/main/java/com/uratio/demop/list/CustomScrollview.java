package com.uratio.demop.list;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class CustomScrollview extends ScrollView {
    private int downX;
    private int downY;
    private int mTouchSlop;

    public CustomScrollview(Context context) {
        super(context);
    }

    public CustomScrollview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomScrollview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private ScrollViewListener scrollViewListener = null;
    public interface ScrollViewListener {
        void onScrollChanged(CustomScrollview scrollView, int x, int y, int oldX, int oldY);

    }
    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (scrollViewListener != null){
            scrollViewListener.onScrollChanged(this,l,t,oldl,oldt);
        }
    }
}