package com.uratio.myptr;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.ImageView;

import java.security.InvalidParameterException;

public class PullToRefreshView extends ViewGroup {

    private static final int DRAG_MAX_DISTANCE = 120;
    private static final float DRAG_RATE = .5f;
    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;

    public static final int STYLE_SUN = 0;
    public static final int MAX_OFFSET_ANIMATION_DURATION = 700;

    private static final int INVALID_POINTER = -1;

    private View mTarget;
    private ImageView mRefreshView;
    private Interpolator mDecelerateInterpolator;
    private int mTouchSlop;
    private int mTotalDragDistance;
    private BaseRefreshView mBaseRefreshView;
    private float mCurrentDragPercent;
    private int mCurrentOffsetTop;
    private boolean mRefreshing;
    private int mActivePointerId;
    private boolean mIsBeingDragged;
    private float mInitialMotionY;
    private int mFrom;
    private float mFromDragPercent;
    private boolean mNotify;
    private OnRefreshListener mListener;

    private int mTargetPaddingTop;
    private int mTargetPaddingBottom;
    private int mTargetPaddingRight;
    private int mTargetPaddingLeft;

    public PullToRefreshView(Context context) {
        this(context, null);
    }

    public PullToRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 自定义属性（实际上这个属性没什么用处）
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RefreshView);
        final int type = a.getInteger(R.styleable.RefreshView_type, STYLE_SUN);
        a.recycle();

        // 动画插值器
        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
        // 滑动触发的临界距离
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        // 触发下拉刷新拖动的总距离
        mTotalDragDistance = Utils.convertDpToPixel(context, DRAG_MAX_DISTANCE);
        // 头部刷新的ImageView
        mRefreshView = new ImageView(context);
        // 根据type设置刷新样式
        setRefreshStyle(type);
        // 将头部刷新ImageVeiw添加到当前的PullToRefreshView
        addView(mRefreshView);

        setWillNotDraw(false);
        ViewCompat.setChildrenDrawingOrderEnabled(this, true);
    }

    public void setRefreshStyle(int type) {
        setRefreshing(false);
        switch (type) {
            case STYLE_SUN:
                // new一个刷新的Drawable
                mBaseRefreshView = new SunRefreshView(getContext(), this);
                break;
            default:
                throw new InvalidParameterException("Type does not exist");
        }
        // 设置头部刷新的ImageView(mRefreshView)设自定义的Drawable(mBaseRefreshView)
        mRefreshView.setImageDrawable(mBaseRefreshView);
//        mRefreshView.setImageResource(R.drawable.image);
    }

    /**
     * This method sets padding for the refresh (progress) view.
     */
    public void setRefreshViewPadding(int left, int top, int right, int bottom) {
        mRefreshView.setPadding(left, top, right, bottom);
    }

    public int getTotalDragDistance() {
        return mTotalDragDistance;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 确保需要刷新的子控件Target已经添加
        ensureTarget();
        if (mTarget == null) return;

        // 测量mTarget和mRefreshView
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingRight() - getPaddingLeft(), MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY);
        mTarget.measure(widthMeasureSpec, heightMeasureSpec);
        mRefreshView.measure(widthMeasureSpec, heightMeasureSpec);
    }

    private void ensureTarget() {
        if (mTarget != null)
            return;
        if (getChildCount() > 0) {
            // 遍历子View，找到mTarget
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child != mRefreshView) {
                    mTarget = child;
                    mTargetPaddingBottom = mTarget.getPaddingBottom();
                    mTargetPaddingLeft = mTarget.getPaddingLeft();
                    mTargetPaddingRight = mTarget.getPaddingRight();
                    mTargetPaddingTop = mTarget.getPaddingTop();
                }
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
// enable 或者 mTarget能滑动 或者 正在刷新，此时不拦截，交给child来分发ev
        if (!isEnabled() || canChildScrollUp() || mRefreshing) {
            return false;
        }
        // 事件action
        final int action = MotionEventCompat.getActionMasked(ev);
        // 根据事件类型，处理拦截逻辑
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 手指down时，设置mTarget的偏移量为0
                // 相当于初始化mTarget的mCurrentOffsetTop以及头部刷新的Drawable（mBaseRefreshView）
                setTargetOffsetTop(0, true);
                // 活动手指ID（触发拖动的手指）
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                // 不是正在被拖动
                mIsBeingDragged = false;
                // 活动手指初始按下时的Y坐标
                final float initialMotionY = getMotionEventY(ev, mActivePointerId);
                if (initialMotionY == -1) {
                    return false;
                }
                mInitialMotionY = initialMotionY;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
                    return false;
                }
                // 获取活动手指的Y坐标（当前可能有多个手指在屏幕上move，只需处理活动手指即可）
                final float y = getMotionEventY(ev, mActivePointerId);
                if (y == -1) {
                    return false;
                }
                // 移动的距离yDiff大于临界值并且当前没有被拖动
                // 改变拖动的状态值mIsBeingDragged为正在拖动
                final float yDiff = y - mInitialMotionY;
                if (yDiff > mTouchSlop && !mIsBeingDragged) {
                    mIsBeingDragged = true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // 手指cancel或者up，拖动状态为false，活动手指invalid。
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
            case MotionEventCompat.ACTION_POINTER_UP:
                // 多个手指在屏幕上，当第二个手指抬起时，需要更新活动手指
                onSecondaryPointerUp(ev);
                break;
        }
        // 只要当前处于拖动状态，就拦截事件，否则不拦截
        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent ev) {
        // 当前没有被拖动，不处理
        if (!mIsBeingDragged) {
            return super.onTouchEvent(ev);
        }
        // 根据事件action处理不同事件逻辑
        final int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                // 获取当前事件中活动手指的pointerIndex
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                // 根据活动手指pointerIndex获取Y坐标
                // 计算出手指的移动距离yDiff
                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float yDiff = y - mInitialMotionY;
                // mTarget需要滚动的距离scrollTop
                final float scrollTop = yDiff * DRAG_RATE;
                // 当前拖动百分比mCurrentDragPercent
                mCurrentDragPercent = scrollTop / mTotalDragDistance;
                if (mCurrentDragPercent < 0) {
                    return false;
                }
                // 以下逻辑都是根据当前拖动百分比和mTarget需要滚动的距离来计算出当前move事件中mTarget需要达到的目标Y坐标
                // 即每一次移动都需要计算出即将要达到的位置的Y坐标，通过该即将到达的Y坐标以及当前的偏移量，
                // 就能计算出这次手指移动时mTarget所需要的偏移量
                // 做如此处理主要是让拖动距离超过触发刷新的距离时继续拖动有一个阻尼效果
                float boundedDragPercent = Math.min(1f, Math.abs(mCurrentDragPercent));
                float extraOS = Math.abs(scrollTop) - mTotalDragDistance;
                float slingshotDist = mTotalDragDistance;
                float tensionSlingshotPercent = Math.max(0,
                        Math.min(extraOS, slingshotDist * 2) / slingshotDist);
                float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow(
                        (tensionSlingshotPercent / 4), 2)) * 2f;
                float extraMove = (slingshotDist) * tensionPercent / 2;
                // targetY为此次手指移动mTarget即将达到的Y坐标
                int targetY = (int) ((slingshotDist * boundedDragPercent) + extraMove);
                // 设置mBaseRefreshView(头部刷新Drawable)的百分比，用以更新刷新动画
                mBaseRefreshView.setPercent(mCurrentDragPercent, true);
                // 设置mTarget偏移量，实现下拉
                setTargetOffsetTop(targetY - mCurrentOffsetTop, true);
                break;
            }
            case MotionEventCompat.ACTION_POINTER_DOWN:
                // 新的手指按下时，更新触发拖动活动手指
                final int index = MotionEventCompat.getActionIndex(ev);
                mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                break;
            case MotionEventCompat.ACTION_POINTER_UP:
                // 多点触控，手指抬起时更新活动手指
                onSecondaryPointerUp(ev);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (mActivePointerId == INVALID_POINTER) {
                    return false;
                }
                // 手指up或cancel，根据活动手指计算出mTarget滚动的距离overScrollTop
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float overScrollTop = (y - mInitialMotionY) * DRAG_RATE;
                // 改变拖动状体
                mIsBeingDragged = false;
                if (overScrollTop > mTotalDragDistance) {
                    // mTarget被拖动的距离大于触发刷新的拖动距离时，设置当前刷新状态true
                    setRefreshing(true, true);
                } else {
                    // 否则，当前刷新状态为false,并且通过动画让mTarget回到最初状态
                    mRefreshing = false;
                    animateOffsetToStartPosition();
                }
                // 活动手指invelid
                mActivePointerId = INVALID_POINTER;
                return false;
            }
        }
        // 消费掉当前Touch事件
        return true;
    }

    private void animateOffsetToStartPosition() {
        mFrom = mCurrentOffsetTop;
        mFromDragPercent = mCurrentDragPercent;
        long animationDuration = Math.abs((long) (MAX_OFFSET_ANIMATION_DURATION * mFromDragPercent));

        mAnimateToStartPosition.reset();
        mAnimateToStartPosition.setDuration(animationDuration);
        mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
        mAnimateToStartPosition.setAnimationListener(mToStartListener);
        mRefreshView.clearAnimation();
        mRefreshView.startAnimation(mAnimateToStartPosition);
    }

    private void animateOffsetToCorrectPosition() {
        mFrom = mCurrentOffsetTop;
        mFromDragPercent = mCurrentDragPercent;

        mAnimateToCorrectPosition.reset();
        mAnimateToCorrectPosition.setDuration(MAX_OFFSET_ANIMATION_DURATION);
        mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
        mRefreshView.clearAnimation();
        mRefreshView.startAnimation(mAnimateToCorrectPosition);

        if (mRefreshing) {
            mBaseRefreshView.start();
            if (mNotify) {
                if (mListener != null) {
                    mListener.onRefresh();
                }
            }
        } else {
            mBaseRefreshView.stop();
            animateOffsetToStartPosition();
        }
        mCurrentOffsetTop = mTarget.getTop();
        mTarget.setPadding(mTargetPaddingLeft, mTargetPaddingTop, mTargetPaddingRight, mTotalDragDistance);
    }

    private final Animation mAnimateToStartPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            moveToStart(interpolatedTime);
        }
    };

    private final Animation mAnimateToCorrectPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            int targetTop;
            int endTarget = mTotalDragDistance;
            targetTop = (mFrom + (int) ((endTarget - mFrom) * interpolatedTime));
            int offset = targetTop - mTarget.getTop();

            mCurrentDragPercent = mFromDragPercent - (mFromDragPercent - 1.0f) * interpolatedTime;
            mBaseRefreshView.setPercent(mCurrentDragPercent, false);

            setTargetOffsetTop(offset, false /* requires update */);
        }
    };

    private void moveToStart(float interpolatedTime) {
        int targetTop = mFrom - (int) (mFrom * interpolatedTime);
        float targetPercent = mFromDragPercent * (1.0f - interpolatedTime);
        int offset = targetTop - mTarget.getTop();

        mCurrentDragPercent = targetPercent;
        mBaseRefreshView.setPercent(mCurrentDragPercent, true);
        mTarget.setPadding(mTargetPaddingLeft, mTargetPaddingTop, mTargetPaddingRight, mTargetPaddingBottom + targetTop);
        setTargetOffsetTop(offset, false);
    }

    public void setRefreshing(boolean refreshing) {
        if (mRefreshing != refreshing) {
            setRefreshing(refreshing, false /* notify */);
        }
    }
    /**
     * 设置PullToRefreshView的刷新状态
     * @param refreshing 是否正在刷新
     * @param notify     是否回调onRefresh
     */
    private void setRefreshing(boolean refreshing, final boolean notify) {
        if (mRefreshing != refreshing) {
            mNotify = notify;
            ensureTarget();
            mRefreshing = refreshing;
            if (mRefreshing) {
                // 正在刷新，设置刷新Drawable（mBaseRefreshView）的percent，用以更新刷新动画
                mBaseRefreshView.setPercent(1f, true);
                // 通过动画让mTarget偏移到正在刷新的位置。
                animateOffsetToCorrectPosition();
            } else {
                // 不是正在刷新，通过动画使mTarget偏移到初始位置。
                animateOffsetToStartPosition();
            }
        }
    }

    private Animation.AnimationListener mToStartListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mBaseRefreshView.stop();
            mCurrentOffsetTop = mTarget.getTop();
        }
    };

    /**
     * 当有多个手指在屏幕上时，有一个手指抬起时，需要处理的逻辑
     * 多点触控时，手指的down和up之间，只有通过ID才能识别手指，当有手指抬起时，需要更新活动手指。
     * @param ev
     */
    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
        }
    }

    private float getMotionEventY(MotionEvent ev, int activePointerId) {
        final int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
        if (index < 0) {
            return -1;
        }
        return MotionEventCompat.getY(ev, index);
    }

    private void setTargetOffsetTop(int offset, boolean requiresUpdate) {
        mTarget.offsetTopAndBottom(offset);
        mBaseRefreshView.offsetTopAndBottom(offset);
        mCurrentOffsetTop = mTarget.getTop();
        if (requiresUpdate && android.os.Build.VERSION.SDK_INT < 11) {
            invalidate();
        }
    }

    private boolean canChildScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mTarget instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTarget;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return mTarget.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mTarget, -1);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        ensureTarget();
        if (mTarget == null)
            return;

        // 获取PullToRefreshView的宽高以及padding值
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = getPaddingRight();
        int bottom = getPaddingBottom();

        // 根据PullToRefreshView的宽高和内边界来布局mTarget、mRefreshView
        mTarget.layout(left, top + mCurrentOffsetTop, left + width - right, top + height - bottom + mCurrentOffsetTop);
        mRefreshView.layout(left, top, left + width - right, top + height - bottom);
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    public interface OnRefreshListener {
        void onRefresh();
    }
}

