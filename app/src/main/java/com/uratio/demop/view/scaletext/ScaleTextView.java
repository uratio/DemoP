//package com.uratio.demop.view.scaletext;
//
//import android.content.Context;
//import android.support.annotation.Nullable;
//import android.util.AttributeSet;
//import android.util.DisplayMetrics;
//import android.util.TypedValue;
//import android.view.View;
//
///**
// * @author lang
// * @data 2021/12/2
// */
//public class ScaleTextView extends View {
//    // Default minimum size for auto-sizing text in scaled pixels.
//    private static final int DEFAULT_AUTO_SIZE_MIN_TEXT_SIZE_IN_SP = 12;
//    // Default maximum size for auto-sizing text in scaled pixels.
//    private static final int DEFAULT_AUTO_SIZE_MAX_TEXT_SIZE_IN_SP = 112;
//    // Default value for the step size in pixels.
//    private static final int DEFAULT_AUTO_SIZE_GRANULARITY_IN_PX = 1;
//
//    public ScaleTextView(Context context) {
//        this(context, null);
//    }
//
//    public ScaleTextView(Context context, @Nullable AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public ScaleTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//
//    }
//
//    public void setAutoSizeTextTypeWithDefaults() {
//        final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
//        final float autoSizeMinTextSizeInPx = TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_SP,
//                DEFAULT_AUTO_SIZE_MIN_TEXT_SIZE_IN_SP,
//                displayMetrics);
//        final float autoSizeMaxTextSizeInPx = TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_SP,
//                DEFAULT_AUTO_SIZE_MAX_TEXT_SIZE_IN_SP,
//                displayMetrics);
//
//        validateAndSetAutoSizeTextTypeUniformConfiguration(
//                autoSizeMinTextSizeInPx,
//                autoSizeMaxTextSizeInPx,
//                DEFAULT_AUTO_SIZE_GRANULARITY_IN_PX);
//        if (setupAutoSizeText()) {
//            autoSizeText();
//            invalidate();
//        }
//    }
//
//    /**
//     * If all params are valid then save the auto-size configuration.
//     *
//     * @throws IllegalArgumentException if any of the params are invalid
//     */
//    private void validateAndSetAutoSizeTextTypeUniformConfiguration(float autoSizeMinTextSizeInPx,
//                                                                    float autoSizeMaxTextSizeInPx, float autoSizeStepGranularityInPx) {
//        // First validate.
//        if (autoSizeMinTextSizeInPx <= 0) {
//            throw new IllegalArgumentException("Minimum auto-size text size ("
//                    + autoSizeMinTextSizeInPx + "px) is less or equal to (0px)");
//        }
//
//        if (autoSizeMaxTextSizeInPx <= autoSizeMinTextSizeInPx) {
//            throw new IllegalArgumentException("Maximum auto-size text size ("
//                    + autoSizeMaxTextSizeInPx + "px) is less or equal to minimum auto-size "
//                    + "text size (" + autoSizeMinTextSizeInPx + "px)");
//        }
//
//        if (autoSizeStepGranularityInPx <= 0) {
//            throw new IllegalArgumentException("The auto-size step granularity ("
//                    + autoSizeStepGranularityInPx + "px) is less or equal to (0px)");
//        }
//
//        // All good, persist the configuration.
////        mAutoSizeTextType = AUTO_SIZE_TEXT_TYPE_UNIFORM;
//        mAutoSizeMinTextSizeInPx = autoSizeMinTextSizeInPx;
//        mAutoSizeMaxTextSizeInPx = autoSizeMaxTextSizeInPx;
//        mAutoSizeStepGranularityInPx = autoSizeStepGranularityInPx;
//        mHasPresetAutoSizeValues = false;
//    }
//
//    private boolean setupAutoSizeText() {
//        // Calculate the sizes set based on minimum size, maximum size and step size if we do
//        // not have a predefined set of sizes or if the current sizes array is empty.
//        if (!mHasPresetAutoSizeValues || mAutoSizeTextSizesInPx.length == 0) {
//            final int autoSizeValuesLength = ((int) Math.floor((mAutoSizeMaxTextSizeInPx
//                    - mAutoSizeMinTextSizeInPx) / mAutoSizeStepGranularityInPx)) + 1;
//            final int[] autoSizeTextSizesInPx = new int[autoSizeValuesLength];
//            for (int i = 0; i < autoSizeValuesLength; i++) {
//                autoSizeTextSizesInPx[i] = Math.round(
//                        mAutoSizeMinTextSizeInPx + (i * mAutoSizeStepGranularityInPx));
//            }
//            mAutoSizeTextSizesInPx = cleanupAutoSizePresetSizes(autoSizeTextSizesInPx);
//        }
//
//        mNeedsAutoSizeText = true;
//        return mNeedsAutoSizeText;
//    }
//
//    /**
//     * Automatically computes and sets the text size.
//     */
//    private void autoSizeText() {
//        if (mNeedsAutoSizeText) {
//            if (getMeasuredWidth() <= 0 || getMeasuredHeight() <= 0) {
//                return;
//            }
//
//            final int availableWidth = mHorizontallyScrolling
//                    ? VERY_WIDE
//                    : getMeasuredWidth() - getTotalPaddingLeft() - getTotalPaddingRight();
//            final int availableHeight = getMeasuredHeight() - getExtendedPaddingBottom()
//                    - getExtendedPaddingTop();
//
//            if (availableWidth <= 0 || availableHeight <= 0) {
//                return;
//            }
//
//            synchronized (TEMP_RECTF) {
//                TEMP_RECTF.setEmpty();
//                TEMP_RECTF.right = availableWidth;
//                TEMP_RECTF.bottom = availableHeight;
//                final float optimalTextSize = findLargestTextSizeWhichFits(TEMP_RECTF);
//
//                if (optimalTextSize != getTextSize()) {
//                    setTextSizeInternal(TypedValue.COMPLEX_UNIT_PX, optimalTextSize,
//                            false /* shouldRequestLayout */);
//
//                    makeNewLayout(availableWidth, 0 /* hintWidth */, UNKNOWN_BORING, UNKNOWN_BORING,
//                            mRight - mLeft - getCompoundPaddingLeft() - getCompoundPaddingRight(),
//                            false /* bringIntoView */);
//                }
//            }
//        }
//        // Always try to auto-size if enabled. Functions that do not want to trigger auto-sizing
//        // after the next layout pass should set this to false.
//        mNeedsAutoSizeText = true;
//    }
//
//}
