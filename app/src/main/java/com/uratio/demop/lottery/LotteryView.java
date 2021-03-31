package com.uratio.demop.lottery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.SystemClock;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.uratio.demop.utils.DensityUtils;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LotteryView extends SurfaceView implements SurfaceHolder.Callback {
    private Context context;
    /**
     * holder
     */
    private SurfaceHolder mHolder;


    private List<Prize> prizes;
    private boolean flags;

    private int lottery = 6; //设置中奖号码

    private int current = 2; //抽奖开始的位置

    private int count = 0; //旋转次数累计

    private int countDown; //倒计次数，快速旋转完成后，需要倒计多少次循环才停止

    private int transfer = 0x30000000;//中奖背景

    private int MAX = 50; //最大旋转次数

    private OnTransferWinningListener listener;

    private Bitmap bmBg;

    private int bgColor;

    private Bitmap bmChoose;

    private Bitmap bmPrizeBg;

    private int paddingSize = 22;//内部padding值

    private int itemSpace = 8;//item的间距

    private int paddingSizeRadio = 75;//内部padding原始尺寸比例

    private int itemSpaceRadio = 25;//item的间距原始尺寸比例

    private int imageMarginLRRadio = 20;//奖品图片距左右原始尺寸比例

    private int imageMarginTBRadio = 24;//奖品图片距上下原始尺寸比例

    private int itemRulerWidth = 90;//item的宽度原始尺寸比例

    private int rulerWidth = 690;//抽奖背景原始尺寸比例

    public void setOnTransferWinningListener(OnTransferWinningListener listener) {
        this.listener = listener;
    }

    public interface OnTransferWinningListener {
        /**
         * 点击开始抽奖
         */
        void onClickStart();

        /**
         * 中奖回调
         *
         * @param position
         */
        void onWinning(int position);
    }


    /**
     * 设置中奖号码
     *
     * @param lottery
     */
    public void setLottery(int lottery) {
        if (prizes != null && Math.round(prizes.size() / 2) == 0) {
            throw new RuntimeException("开始抽奖按钮不能设置为中奖位置！");
        }
        this.lottery = lottery;
    }

    /**
     * 设置中奖颜色
     *
     * @param transfer
     */
    public void setTransfer(int transfer) {
        this.transfer = transfer;
    }

    public void setPaddingSizeRadio(int paddingSizeRadio) {
        this.paddingSizeRadio = paddingSizeRadio;
        paddingSize = getMeasuredWidth() * paddingSizeRadio / 2 / rulerWidth;
    }

    public void setItemSpaceRadio(int itemSpaceRadio) {
        this.itemSpaceRadio = itemSpaceRadio;
        itemSpace = getMeasuredWidth() * itemSpaceRadio / 2 / rulerWidth;
    }

    public void setImageMarginLRRadio(int imageMarginLRRadio) {
        this.imageMarginLRRadio = imageMarginLRRadio;
    }

    public void setImageMarginTBRadio(int imageMarginTBRadio) {
        this.imageMarginTBRadio = imageMarginTBRadio;
    }

    public void setItemRulerWidth(int itemRulerWidth) {
        this.itemRulerWidth = itemRulerWidth;
    }

    public void setRulerWidth(int rulerWidth) {
        this.rulerWidth = rulerWidth;
    }

    public void setBmBg(Bitmap bmBg) {
        this.bmBg = bmBg;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public void setBmChoose(Bitmap bmChoose) {
        this.bmChoose = bmChoose;
    }

    public void setBmPrizeBg(Bitmap bmPrizeBg) {
        this.bmPrizeBg = bmPrizeBg;
    }

    /**
     * 设置奖品集合
     *
     * @param prizes
     */
    public void setPrizes(List<Prize> prizes) {
        this.prizes = prizes;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        handleTouch(event);
        return super.onTouchEvent(event);
    }

    /**
     * 触摸
     *
     * @param event
     */
    public void handleTouch(MotionEvent event) {
        int clickX = (int) event.getX();
        int clickY = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int startPosition = Math.round(prizes.size() / 2);
                Prize prize = prizes.get(startPosition);

                int width = (getMeasuredWidth() - 2 * paddingSize) / 3;
                int len = (int) Math.sqrt(prizes.size());
                int x1 = getPaddingLeft() + width * (Math.abs(startPosition) % len) + paddingSize + itemSpace;
                int y1 = getPaddingTop() + width * ((startPosition) / len) + paddingSize + itemSpace;

                int x2 = x1 + width - 2 * itemSpace;
                int y2 = y1 + width - 2 * itemSpace;

                if (clickX >= x1 && clickX <= x2 && clickY >= y1 && clickY <= y2){
                    if (!flags) {
                        setStartFlags(true);
                        prize.click();
                    }
                }
                break;
            default:
                break;
        }
    }

    private class SurfaceRunnable implements Runnable {
        @Override
        public void run() {
            while (flags) {
                Canvas canvas = null;
                try {
                    canvas = mHolder.lockCanvas();

                    drawBg(canvas);

                    drawPrize(canvas);

                    drawTransfer(canvas);

                    controllerTransfer();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (canvas != null)
                        mHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    //绘制所有奖品背景
    private void drawBg(Canvas canvas) {
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
        //分块画背景
        int width = getMeasuredWidth() / 3;
        int x1 = 0;
        int y1 = 0;

        int x2 = 0;
        int y2 = 0;

        int len = (int) Math.sqrt(prizes.size());

        /*for(int x=0;x<len*len;x++){

            Prize prize = prizes.get(x);

            int index=x;
            x1=getPaddingLeft()+width*(Math.abs(index)%len);
            y1=getPaddingTop()+width*(index/len);

            x2=x1+width;
            y2=y1+width;
            Rect rect=new Rect(x1,y1,x2,y2);

            Paint paint=new Paint();
            paint.setColor(prize.getBgColor());
            canvas.drawRect(rect, paint);
        }*/
        //使用图片背景
        x1 = getPaddingLeft();
        y1 = getPaddingTop();
        x2 = x1 + getMeasuredWidth();
        y2 = y1 + getMeasuredWidth();
        Rect rect = new Rect(x1, y1, x2, y2);
        Paint paint = new Paint();
        paint.setColor(bgColor);
        canvas.drawRect(rect, paint);
        canvas.drawBitmap(bmBg, null, rect, paint);
    }

    //绘制旋转的奖品背景
    private void drawTransfer(Canvas canvas) {
        int width = (getMeasuredWidth() - 2 * paddingSize) / 3;
        int x1;
        int y1;

        int x2;
        int y2;
        int len = (int) Math.sqrt(prizes.size());
        current = next(current, len);
        x1 = getPaddingLeft() + width * (Math.abs(current) % len) + paddingSize + itemSpace;
        y1 = getPaddingTop() + width * ((current) / len) + paddingSize + itemSpace;

        x2 = x1 + width - 2 * itemSpace;
        y2 = y1 + width - 2 * itemSpace;

        Rect rect = new Rect(x1, y1, x2, y2);
        //颜色选择背景
//        Paint paint=new Paint();
//        paint.setColor(transfer);
//        canvas.drawRect(rect, paint);
        //图片选择背景
        canvas.drawBitmap(bmChoose, null, rect, null);
    }

    //控制旋转的速度
    private void controllerTransfer() {
        if (count > MAX) {
            countDown++;
            SystemClock.sleep(count * 5);
        } else {
            SystemClock.sleep(count * 2);
        }

        count++;
        if (countDown > 2) {
            if (lottery == current) {
                countDown = 0;
                count = 0;
                setStartFlags(false);
                if (listener != null) {
                    //切换到主线程中运行
                    post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onWinning(current);
                        }
                    });

                }
            }
        }
    }

    public void setStartFlags(boolean flags) {
        this.flags = flags;
    }

    //绘制奖品背景
    private void drawPrize(Canvas canvas) {
        int width = (getMeasuredWidth() - 2 * paddingSize) / 3;
        int x1 = 0;
        int y1 = 0;

        int x2 = 0;
        int y2 = 0;

        int len = (int) Math.sqrt(prizes.size());

        for (int x = 0; x < len * len; x++) {

            Prize prize = prizes.get(x);

            int index = x;
            x1 = getPaddingLeft() + width * (Math.abs(index) % len) + paddingSize + itemSpace;
            y1 = getPaddingTop() + width * (index / len) + paddingSize + itemSpace;

            x2 = x1 + width - 2 * itemSpace;
            y2 = y1 + width - 2 * itemSpace;
            //奖品背景
            Rect rectBg = new Rect(x1, y1, x2, y2);
//            canvas.drawBitmap(bmPrizeBg, null, rectBg, null);
            if (x == (prizes.size() - 1) / 2) {
                canvas.drawBitmap(prize.getIcon(), null, rectBg, null);
            } else {
                canvas.drawBitmap(bmPrizeBg, null, rectBg, null);
//                //奖品图片
                int itemWidth = x2 - x1;
                int lr = itemWidth * imageMarginLRRadio / DensityUtils.dip2px(context, itemRulerWidth);
                int tb = itemWidth * imageMarginTBRadio / DensityUtils.dip2px(context, itemRulerWidth);

                int itemHeight = y2 - y1;

                Rect rectImg = new Rect(x1 + lr, y1 + tb, x2 - lr, y1 + itemHeight * 5 / 9 - tb);
                canvas.drawBitmap(prize.getIcon(), null, rectImg, null);
//                //奖品文字
                canvas.save();
                canvas.translate(x1, y1 + itemHeight * 5 / 9);
                TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
                textPaint.setColor(prize.getDescColor());

                int size = itemHeight * 11 / DensityUtils.dip2px(context, itemRulerWidth);
                textPaint.setTextSize(DensityUtils.sp2px(context, size));
                StaticLayout staticLayout = new StaticLayout(prize.getDesc(), textPaint, itemWidth, Layout.Alignment.ALIGN_CENTER, 1.2f, 0, true);
                staticLayout.draw(canvas);
                canvas.restore();
            }
        }
    }

    public void start() {
//        setLottery(getRandom());
        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(new SurfaceRunnable());
    }

    //获取随机中奖数，实际开发中一般中奖号码是服务器告诉我们的
    private int getRandom() {
        Random r = new Random();
        int nextInt = r.nextInt(prizes.size());
        if (nextInt % (Math.round(prizes.size() / 2)) == 0) {
            //随机号码等于中间开始位置，需要继续摇随机号
            return getRandom();
        }
        return nextInt;
    }

    //下一步
    public int next(int current, int len) {
        if (current + 1 < len) {
            return ++current;
        }

        if ((current + 1) % len == 0 & current < len * len - 1) {
            return current += len;
        }

        if (current % len == 0) {
            return current -= len;
        }

        if (current < len * len) {
            return --current;
        }

        return current;
    }


    public LotteryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        mHolder = this.getHolder();
        mHolder.addCallback(this);
    }

    public LotteryView(Context context) {
        this(context, null);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        Log.e("data", "surfaceChanged: width=" + width);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e("data", "surfaceCreated: width=" + getWidth());
        paddingSize = getMeasuredWidth() * paddingSizeRadio / 2 / rulerWidth;
        itemSpace = getMeasuredWidth() * itemSpaceRadio / 2 / rulerWidth;

        Canvas canvas = null;
        try {
            canvas = mHolder.lockCanvas();
            drawBg(canvas);
            drawPrize(canvas);

            Prize prize = prizes.get(Math.round(prizes.size() / 2));
            prize.setListener(new Prize.OnClickListener() {

                @Override
                public void onClick() {
                    if (listener != null) {
                        listener.onClickStart();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canvas != null)
                mHolder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        setStartFlags(false);
    }

    /**
     * 重新测量
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int width = Math.min(getMeasuredWidth(), getMeasuredHeight());
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());

    }
}
