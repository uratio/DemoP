package com.uratio.demop.list;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by Android on 2017/10/23.
 */

public class RcvForScrollView extends RecyclerView {
    public RcvForScrollView(Context context) {
        super(context);
    }

    public RcvForScrollView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RcvForScrollView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthSpec, expandSpec);
    }
}
