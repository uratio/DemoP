package com.uratio.demop.view;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.uratio.demop.BaseActivity;
import com.uratio.demop.R;
import com.uratio.demop.utils.LogUtils;
import com.uratio.demop.view.shadow.ShadowLayout;

public class CustomViewActivity extends BaseActivity {
    private View viewShape1, viewShape2, viewShape3, viewShape4;
    private ShadowLayout shadowLayout;
    private TextView tvDepth;
    private SeekBar seekBarDepth;
    private TextView tvAlpha;
    private SeekBar seekBarAlpha;

    private TextView tvAutoText;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_custom_view;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        //自定义阴影
        initShadowLayout();

        //自动缩放文本
        initAutoText();
    }

    private void initAutoText() {
        tvAutoText = findViewById(R.id.tv_auto_text);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tvAutoText.getLayoutParams();
        params.height = getResources().getDimensionPixelOffset(R.dimen.dp_50);

    }

    private void initShadowLayout() {
        viewShape1 = findViewById(R.id.view_shape1);
        viewShape2 = findViewById(R.id.view_shape2);
        viewShape3 = findViewById(R.id.view_shape3);
        viewShape4 = findViewById(R.id.view_shape4);
        shadowLayout = findViewById(R.id.shadow_layout);
        tvDepth = findViewById(R.id.tv_depth);
        seekBarDepth = findViewById(R.id.sb_depth);
        tvAlpha = findViewById(R.id.tv_alpha);
        seekBarAlpha = findViewById(R.id.sb_alpha);

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

        logType(viewShape1.getBackground());
        logType(viewShape2.getBackground());
        logType(viewShape3.getBackground());
        logType(viewShape4.getBackground());
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

}