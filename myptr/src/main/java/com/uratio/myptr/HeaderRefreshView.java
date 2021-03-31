package com.uratio.myptr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

public class HeaderRefreshView extends BaseRefreshView implements Animatable {

    public HeaderRefreshView(Context context, PullToRefreshView layout) {
        super(context, layout);
    }

    @Override
    public void setPercent(float percent, boolean invalidate) {

    }

    @Override
    public void offsetTopAndBottom(int offset) {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {

    }
}
