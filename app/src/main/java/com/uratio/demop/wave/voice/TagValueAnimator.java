package com.uratio.demop.wave.voice;

import android.animation.ValueAnimator;

/**
 * @author lang
 * @data 2021/5/14
 */
class TagValueAnimator extends ValueAnimator implements ValueAnimator.AnimatorUpdateListener {
    private int tag;
    private AnimatorUpdateListener updateListener;

    public TagValueAnimator() {
        addUpdateListener(this);
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public void addUpdateListener(AnimatorUpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        if (updateListener != null) {
            updateListener.onAnimationUpdate(animation, tag);
        }
    }

    public interface AnimatorUpdateListener {
        void onAnimationUpdate(ValueAnimator animation, int tag);
    }
}
