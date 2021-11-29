package com.uratio.demop.shadow;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

import org.jetbrains.annotations.NotNull;

/**
 * @author lang
 * @data 2021/11/24
 */
public class CusCardView extends CardView {
    public CusCardView(@NonNull @NotNull Context context) {
        super(context);
    }

    public CusCardView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CusCardView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
