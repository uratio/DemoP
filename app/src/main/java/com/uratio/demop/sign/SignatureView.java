package com.uratio.demop.sign;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import com.uratio.demop.R;

/**
 * 签名控件
 */
public class SignatureView extends View {
    static final int BACKGROUND_COLOR = Color.WHITE;
    static final int BRUSH_COLOR = Color.BLACK;

    private Paint paint;
    private Canvas cacheCanvas;
    private Bitmap cacheBitmap;
    private Path path;

    private int widthPixels = 0;
    private int heightPixels = 0;

    //	@SuppressWarnings("unused")
    private String transCode = "";

    public Config config = Config.RGB_565;

    private float lastTouchX;
    private float lastTouchY;
    private final RectF dirtyRect = new RectF();
    private float strokeWidth = 5f;
    private float halfStrokeWidth = strokeWidth / 2;
    private Context mContext;
    int count = 0;

    private int hintTextSize = 0;
    private boolean isMoved = false;

    public SignatureView(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (cacheCanvas == null) {
            this.widthPixels = right - left;
            this.heightPixels = bottom - top;
            if (this.widthPixels > 0 && this.heightPixels > 0)
                init();
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    private void init() {
        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        switch (dm.densityDpi) {
            case 120:
            case 160:
                strokeWidth = 2.5f * 3;
                break;
            case 240:
                strokeWidth = 3.75f * 3;
                break;
            case 320:
                strokeWidth = 5.625f * 3;
                break;
            case 480:
            default:
                strokeWidth = 8.4375f * 3;
                break;
        }
        halfStrokeWidth = strokeWidth / 2;

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(strokeWidth);
        path = new Path();

        hintTextSize = getContext().getResources().getDimensionPixelSize(R.dimen.sp_16);

        if (cacheBitmap == null) {
            cacheBitmap = Bitmap.createBitmap(widthPixels, heightPixels, config);
        }

        if (cacheCanvas == null) {
            cacheCanvas = new Canvas(cacheBitmap);
            drawBg(true);
        }
    }

    public static Bitmap GetRoundedCornerBitmap(Bitmap bitmap) {

        try {

            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()));

            final float roundPx = 14;
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(Color.BLACK);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

            final Rect src = new Rect(0, 0, bitmap.getWidth(),

                    bitmap.getHeight());

            canvas.drawBitmap(bitmap, src, rect, paint);

            return output;

        } catch (Exception e) {

            return bitmap;

        }

    }

    //设置交易特征码
    public void setTransCode(String transCode) {
        this.transCode = transCode;
    }

    public Bitmap getBitmap() {
        return cacheBitmap;
    }

    public void destroy() {
        if (cacheBitmap != null && !cacheBitmap.isRecycled()) {
            cacheBitmap.recycle();
            cacheBitmap = null;
        }
    }

    public int getCount() {
        return count;
    }

    public void clear() {
        if (cacheCanvas != null) {
            count = 0;

            path.reset();
            cacheCanvas.drawPaint(paint);
            drawBg(true);
            invalidate();
        }
    }

    /**
     * 绘制圆角背景
     */
    private void drawBg(boolean drawText) {
        cacheCanvas.drawARGB(0, 0, 0, 0);
//        cacheCanvas.drawColor(getContext().getResources().getColor(R.color.color_window_bg), PorterDuff.Mode.CLEAR);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
//        paint.setColor(Color.WHITE);
//        cacheCanvas.drawRect(new RectF(0, 0, widthPixels, heightPixels), paint);
//        paint.setColor(getContext().getResources().getColor(R.color.color_window_bg));
//        cacheCanvas.drawRect(new RectF(0, 0, widthPixels, heightPixels), paint);

        paint.setColor(Color.WHITE);
        int space = 100;
        RectF rectF = new RectF(0 + space, 0 + space, widthPixels - space, heightPixels - space);
        cacheCanvas.drawRoundRect(rectF, 30, 30, paint);
        if (drawText) {
            isMoved = false;
            drawHintText(paint);
        }
    }

    private void drawHintText(Paint paint) {
        paint.setColor(0xFFCCCCCC);
        paint.setTextSize(hintTextSize);
        Rect bounds = new Rect();
        String hintText = "请签名！";
        paint.getTextBounds(hintText, 0, hintText.length(), bounds);

        int x = (widthPixels - bounds.width()) / 2;
        int y = (heightPixels + bounds.height()) / 2;
        cacheCanvas.drawText(hintText, x, y, paint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //huanggq 修复cacheBitmap为空时,出现空指针异常,至于为什么cacheBitmap会被置为空，暂时不清楚，设置了SignatureActivity finish日志，是否能重抓到现象
        if (cacheBitmap != null && !cacheBitmap.isRecycled()) {
            canvas.drawBitmap(cacheBitmap, 0, 0, null);
            canvas.drawPath(path, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(eventX, eventY);
                lastTouchX = eventX;
                lastTouchY = eventY;
                if (!isMoved) {
                    isMoved = true;
                    drawBg(false);
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                count++;
            case MotionEvent.ACTION_UP:
                resetDirtyRect(eventX, eventY);
                int historySize = event.getHistorySize();
                for (int i = 0; i < historySize; i++) {
                    float historicalX = event.getHistoricalX(i);
                    float historicalY = event.getHistoricalY(i);
                    expandDirtyRect(historicalX, historicalY);
                    path.lineTo(historicalX, historicalY);
                }
                path.lineTo(eventX, eventY);
                cacheCanvas.drawPath(path, paint);
                break;

            default:
                //debug("Ignored touch event: " + event.toString());
                return false;
        }

        invalidate((int) (dirtyRect.left - halfStrokeWidth),
                (int) (dirtyRect.top - halfStrokeWidth),
                (int) (dirtyRect.right + halfStrokeWidth),
                (int) (dirtyRect.bottom + halfStrokeWidth));

        lastTouchX = eventX;
        lastTouchY = eventY;

        return true;
    }

    private void expandDirtyRect(float historicalX, float historicalY) {
        if (historicalX < dirtyRect.left) {
            dirtyRect.left = historicalX;
        } else if (historicalX > dirtyRect.right) {
            dirtyRect.right = historicalX;
        }

        if (historicalY < dirtyRect.top) {
            dirtyRect.top = historicalY;
        } else if (historicalY > dirtyRect.bottom) {
            dirtyRect.bottom = historicalY;
        }
    }

    private void resetDirtyRect(float eventX, float eventY) {
        dirtyRect.left = Math.min(lastTouchX, eventX);
        dirtyRect.right = Math.max(lastTouchX, eventX);
        dirtyRect.top = Math.min(lastTouchY, eventY);
        dirtyRect.bottom = Math.max(lastTouchY, eventY);
    }

    /**
     * 给一张Bitmap添加水印文字。
     *
     * @param src 原图片
     * @return 已经添加水印后的Bitmap
     */
    public Bitmap addTextWatermark(Bitmap src) {
        if (src == null || src.getWidth() == 0 || src.getHeight() == 0)
            return null;

        Bitmap ret = src.copy(src.getConfig(), true);

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/TIMES.TTF"));
        paint.setTextSize(20);
        Rect bounds = new Rect();
        paint.getTextBounds(transCode, 0, transCode.length(), bounds);

        int x = (src.getWidth() - bounds.width()) / 2;
        int y = (src.getHeight() + bounds.height()) / 2;

        canvas.drawText(transCode, x, y, paint);
        return ret;
    }
}