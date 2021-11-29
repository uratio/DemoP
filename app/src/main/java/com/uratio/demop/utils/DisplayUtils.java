package com.uratio.demop.utils;

import android.util.TypedValue;

import com.uratio.demop.MyApplication;

/**
 * @author lang
 * @data 2021/3/31
 */
public class DisplayUtils {
    public static int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, MyApplication.getCtx().getResources().getDisplayMetrics());
    }

    public static float dp2px2(float dpVal) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, MyApplication.getCtx().getResources().getDisplayMetrics());
    }
}
