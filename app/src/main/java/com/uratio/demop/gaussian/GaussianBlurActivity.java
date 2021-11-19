package com.uratio.demop.gaussian;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.github.mmin18.widget.RealtimeBlurView;
import com.marshalchen.ultimaterecyclerview.GlideApp;
import com.uratio.demop.R;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * 高斯模糊实现：
 * 1、使用原生 RenderScript
 * 2、使用 Glide 加载高斯模糊图
 * 3、使用 RealtimeBlurView 添加遮罩层 View
 */
public class GaussianBlurActivity extends AppCompatActivity {
    private Activity activity;
    private ImageView ivBg;
    private ImageView ivShow;
    private RealtimeBlurView realtimeBlur;
    private TextView tvGlide;
    private SeekBar seekBar;

    private TypedArray arrImg;
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gaussian_blur);
        activity = this;

        ivBg = findViewById(R.id.iv_bg);
        ivShow = findViewById(R.id.iv_show);
        realtimeBlur = findViewById(R.id.realtime_blur);
        tvGlide = findViewById(R.id.tv_glide);
        seekBar = findViewById(R.id.seekBar);

        arrImg = getResources().obtainTypedArray(R.array.arrImageBg);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tvGlide.setText("Glide高斯模糊（0~25）: radius=" + i);
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
    }

    public void onClickView(View view) {
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