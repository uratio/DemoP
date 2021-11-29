package com.uratio.demop.gaussian;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.uratio.demop.R;
import com.uratio.demop.utils.DisplayUtils;

/**
 * @author lang
 * @data 2021/11/25
 * 投影布局：可根据需求修改继承的父view，如：RelativeLayout、LinearLayout
 * 注意：需要将投影布局的父布局属性 clipChildren 设置为 false
 */
public class ShadowLayout extends FrameLayout {
    public static final int DEF_DEPTH = 3;
    public static final int DEF_ALPHA = 60;

    //背景画笔
    private final Paint bgPaint;
    //阴影画笔
    private final Paint shadowPaint;
    //背景色
    private Drawable background;
    //背景圆角角度
    private int radius;
    //投影深度
    private int shadowDepth;
    //投影透明度
    private int shadowAlpha;
    //投影画笔宽度
    private final float lineW;

    public ShadowLayout(Context context) {
        this(context, null);
    }

    public ShadowLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShadowLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ShadowLayout);
        background = a.getDrawable(R.styleable.ShadowLayout_sdl_background);
        radius = a.getDimensionPixelOffset(R.styleable.ShadowLayout_sdl_radius, 0);
        shadowDepth = a.getDimensionPixelOffset(R.styleable.ShadowLayout_sdl_depth, 0);
        shadowAlpha = a.getInteger(R.styleable.ShadowLayout_sdl_shadow_alpha, DEF_ALPHA);

        if (shadowDepth == 0) {
            shadowDepth = DisplayUtils.dp2px(DEF_DEPTH);
        }

        bgPaint = new Paint();
        // 设置绘画风格为实线
        bgPaint.setStyle(Paint.Style.FILL);
        // 抗锯齿
        bgPaint.setAntiAlias(true);

        shadowPaint = new Paint();
        // 设置绘画风格为实线
        shadowPaint.setStyle(Paint.Style.STROKE);
        // 抗锯齿
        shadowPaint.setAntiAlias(true);
        shadowPaint.setColor(Color.BLACK);
        shadowPaint.setAlpha(shadowAlpha);
        lineW = DisplayUtils.dp2px2(0.5f);
        shadowPaint.setStrokeWidth(lineW);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        //绘制背景
        if (background != null) {
            if (background instanceof ColorDrawable) {
                // 颜色类型
                bgPaint.setColor(((ColorDrawable) background).getColor());
            } else if (background instanceof BitmapDrawable){
                // 图片类型
                BitmapShader shader = new BitmapShader(((BitmapDrawable) background).getBitmap(), Shader.TileMode.CLAMP,
                        Shader.TileMode.CLAMP);
                bgPaint.setShader(shader);
            } else if (background instanceof GradientDrawable) {
                // 根节点为 shape 类型的drawable资源（不好绘制，暂时搁置）
//                GradientDrawable gradientDrawable = (GradientDrawable) background;
                bgPaint.setColor(Color.TRANSPARENT);
            } else {
                bgPaint.setColor(Color.TRANSPARENT);
            }
        }
        RectF rectF = new RectF(0, 0, width, height);
        canvas.drawRoundRect(rectF, radius, radius, bgPaint);

        //绘制阴影
        int shadowSize = (int) (shadowDepth * 1.2f);
        for (int i = 0; i < shadowSize; i++) {
            shadowPaint.setAlpha((int) (1f * shadowAlpha * (shadowSize - i - 1) / (shadowSize + (i + 1) * 2)));
            float line = lineW * (i + 0.5f);
            canvas.drawRoundRect(-line, -line, width + line, height + line, radius + line, radius + line, shadowPaint);
        }
        canvas.save();

        super.dispatchDraw(canvas);
    }

    public void setBackground(Drawable background) {
        this.background = background;
        postInvalidate();
    }

    public void setRadius(int radius) {
        this.radius = radius;
        postInvalidate();
    }

    public void setShadowDepth(int shadowDepth) {
        this.shadowDepth = DisplayUtils.dp2px(shadowDepth);
        postInvalidate();
    }

    public void setShadowAlpha(int shadowAlpha) {
        this.shadowAlpha = shadowAlpha;
        postInvalidate();
    }
}
