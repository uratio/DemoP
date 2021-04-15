package com.uratio.demop.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * @author lang
 * @data 2021/3/31
 */
public class DisplayUtils {
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }
}
