package com.uratio.demop.gaussian;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.github.mmin18.widget.RealtimeBlurView;
import com.marshalchen.ultimaterecyclerview.GlideApp;
import com.uratio.demop.BaseActivity;
import com.uratio.demop.R;
import com.uratio.demop.utils.LogUtils;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * 高斯模糊实现：
 * 1、使用原生 RenderScript
 * 2、使用 Glide 加载高斯模糊图
 * 3、使用 RealtimeBlurView 添加遮罩层 View
 */
public class GaussianBlurActivity extends BaseActivity {
    private ImageView ivBg;
    private ImageView ivShow;
    private RealtimeBlurView realtimeBlur;
    private TextView tvGlide;
    private SeekBar seekBar;
    private View viewShape1, viewShape2, viewShape3, viewShape4;
    private ShadowLayout shadowLayout;
    private TextView tvDepth;
    private SeekBar seekBarDepth;
    private TextView tvAlpha;
    private SeekBar seekBarAlpha;

    private TypedArray arrImg;
    private int index = 0;

    private View windowView;
    private WindowManager.LayoutParams params;
    private WindowManager.LayoutParams layoutParams;

    private Bundle bundle;
    private Runnable postRun = new Runnable() {
        @Override
        public void run() {
            if (bundle == null) return;
            String obj = bundle.getString("obj");
            String data = bundle.getString("data");
            LogUtils.e("handler打印：obj=" + obj + "\tdata=" + data);
        }
    };
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String obj = (String) msg.obj;
            String data = msg.getData().getString("data");
            LogUtils.e("handler打印：obj=" + obj + "\tdata=" + data);
        }
    };

    @Override
    protected int getContentLayout() {
        return R.layout.activity_gaussian_blur;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ivBg = findViewById(R.id.iv_bg);
        ivShow = findViewById(R.id.iv_show);
        realtimeBlur = findViewById(R.id.realtime_blur);
        tvGlide = findViewById(R.id.tv_glide);
        seekBar = findViewById(R.id.seekBar);
        viewShape1 = findViewById(R.id.view_shape1);
        viewShape2 = findViewById(R.id.view_shape2);
        viewShape3 = findViewById(R.id.view_shape3);
        viewShape4 = findViewById(R.id.view_shape4);
        shadowLayout = findViewById(R.id.shadow_layout);
        tvDepth = findViewById(R.id.tv_depth);
        seekBarDepth = findViewById(R.id.sb_depth);
        tvAlpha = findViewById(R.id.tv_alpha);
        seekBarAlpha = findViewById(R.id.sb_alpha);

        arrImg = getResources().obtainTypedArray(R.array.arrImageBg);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tvGlide.setText("Glide高斯模糊（0~25）: radius=" + i);
                ivShow.setVisibility(View.VISIBLE);
                GlideApp.with(activity).load(R.drawable.icon_service_header)
                        .apply(RequestOptions.bitmapTransform(new BlurTransformation(i, 5)))
                        .into(ivShow);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!Settings.canDrawOverlays(getApplicationContext())) {
                //启动Activity让用户授权
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent,100);
            }
        }

        windowView = LayoutInflater.from(activity).inflate(R.layout.layout_shade_window, null);

        layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.gravity = Gravity.LEFT | Gravity.CENTER;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.x =200;
        layoutParams.y =0;

        logType(viewShape1.getBackground());
        logType(viewShape2.getBackground());
        logType(viewShape3.getBackground());
        logType(viewShape4.getBackground());


        seekBarDepth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tvDepth.setText("投影深度（1~15）=" + i);
                shadowLayout.setShadowDepth(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarAlpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tvAlpha.setText("投影透明度（1~255）=" + i);
                shadowLayout.setShadowAlpha(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    public void onClickView(View view) {
        ViewGroup contentView = null;
        switch (view.getId()) {
            case R.id.btn_change_bg:
                index++;
                if (index >= arrImg.length()) {
                    index = 0;
                }
                ivBg.setImageDrawable(ContextCompat.getDrawable(activity, arrImg.getResourceId(index, R.drawable.navicon_0)));
                break;
            case R.id.btn_show:
                ivShow.setVisibility(View.VISIBLE);
                Bitmap bitmap = viewSnapshot(ivBg);
                ivShow.setImageBitmap(blur(bitmap, 25));
                break;
            case R.id.btn_hide:
                ivShow.setVisibility(View.GONE);
                break;
            case R.id.btn_show_top:
                realtimeBlur.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_hide_top:
                realtimeBlur.setVisibility(View.GONE);
                break;
            case R.id.btn_add_window:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(activity)) {
                        startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 10086);
                    }
                }
                WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                windowManager.addView(windowView, layoutParams);
                break;
            case R.id.btn_add_top:
                contentView = getWindow().findViewById(Window.ID_ANDROID_CONTENT);
                contentView.addView(windowView);
                break;
            case R.id.btn_delete_top:
                contentView = getWindow().findViewById(Window.ID_ANDROID_CONTENT);
                contentView.removeView(windowView);
                break;
            case R.id.btn_delay_log:
                /**
                 * 方法一：使用 removeCallbacksAndMessages 移除所有历史进行中的消息，再进行消息发送
                 */
                handler.removeCallbacksAndMessages(null);
                Message message = new Message();
                message.obj = "obj数据";
                Bundle mBundle = new Bundle();
                mBundle.putString("data", "范德萨");
                message.setData(mBundle);
                handler.sendMessageDelayed(message, 3000);
                break;
            case R.id.btn_delay_log2:
                /**
                 * 方法二：使用 removeCallbacks 移除固定的一个消息类型，再进行消息发送
                 */
                handler.removeCallbacks(postRun);
                bundle = null;
                bundle = new Bundle();
                bundle.putString("obj", "发货第三款");
                bundle.putString("data", "范德萨");
                handler.postDelayed(postRun, 3000);
                break;
        }
    }

    private void logType(Drawable drawable) {
        if (drawable instanceof ColorDrawable) {
            LogUtils.e("background类型：ColorDrawable");
        } else if (drawable instanceof ShapeDrawable) {
            LogUtils.e("background类型：ShapeDrawable");
        } else if (drawable instanceof BitmapDrawable) {
            LogUtils.e("background类型：BitmapDrawable");
        } else if (drawable instanceof LayerDrawable) {
            LogUtils.e("background类型：LayerDrawable");
        } else {
            LogUtils.e("background类型：其他Drawable");
        }
    }

    /**
     * 对View进行截图
     */
    public static Bitmap viewSnapshot(View view) {
        //使控件可以进行缓存
        view.setDrawingCacheEnabled(true);
        //获取缓存的 Bitmap
        Bitmap drawingCache = view.getDrawingCache();
        //复制获取的 Bitmap
        drawingCache = Bitmap.createBitmap(drawingCache);
        //关闭视图的缓存
        view.setDrawingCacheEnabled(false);

        return drawingCache;
    }

    private Bitmap blur(Bitmap bitmap, float radius) {
        Bitmap output = Bitmap.createBitmap(bitmap); // 创建输出图片
        RenderScript rs = RenderScript.create(this); // 构建一个RenderScript对象
        ScriptIntrinsicBlur gaussianBlue = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs)); // 创建高斯模糊脚本
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap); // 创建用于输入的脚本类型
        Allocation allOut = Allocation.createFromBitmap(rs, output); // 创建用于输出的脚本类型
        gaussianBlue.setRadius(radius); // 设置模糊半径，范围0f<radius<=25f
        gaussianBlue.setInput(allIn); // 设置输入脚本类型
        gaussianBlue.forEach(allOut); // 执行高斯模糊算法，并将结果填入输出脚本类型中
        allOut.copyTo(output); // 将输出内存编码为Bitmap，图片大小必须注意
        rs.destroy(); // 关闭RenderScript对象，API>=23则使用rs.releaseAllContexts()
        return output;
    }
}