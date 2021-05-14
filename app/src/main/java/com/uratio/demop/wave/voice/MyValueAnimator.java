package com.uratio.demop.wave.voice;

import android.animation.ValueAnimator;
import android.util.Log;

/**
 * @author lang
 * @data 2021/5/14
 */
class MyValueAnimator extends ValueAnimator implements ValueAnimator.AnimatorUpdateListener {
    private int tag;
    private MyAnimatorUpdateListener updateListener;

    public MyValueAnimator() {
        addUpdateListener(this);
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public void addUpdateListener(MyAnimatorUpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
//        Log.e("MyValueAnimator", "onAnimationUpdate: tag=" +tag+ "  getAnimatedValue=" + animation.getAnimatedValue());

        if (updateListener != null) {
            updateListener.onAnimationUpdate(animation, tag);
        }
    }

    public interface MyAnimatorUpdateListener {
        void onAnimationUpdate(ValueAnimator animation, int tag);
    }
}
