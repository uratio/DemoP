package com.uratio.demop;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.uratio.demop.gaussian.GaussianBlurDialog;
import com.uratio.demop.utils.Constants;
import com.uratio.demop.utils.LogUtils;

import java.util.List;

/**
 * @author lang
 * @data 2021/11/22
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected Activity activity;
    private GaussianBlurDialog gaussianBlurDialog;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContentLayout() != 0) {
            setContentView(getContentLayout());
        }
        activity = this;
//        gaussianBlurDialog = new GaussianBlurDialog(activity);
        initView(savedInstanceState);

    }

    /*
     * 初始化布局
     */
    protected abstract int getContentLayout();

    protected abstract void initView(Bundle savedInstanceState);

    @Override
    protected void onStart() {
        super.onStart();
        /*Constants.foregroundActivities++;
        ViewGroup contentView = (ViewGroup) activity.getWindow().getDecorView();
        if (Constants.foregroundActivities == 1 && contentView.getChildCount() >= 2) {
//            contentView.removeViewAt(contentView.getChildCount() - 1);
            if (gaussianBlurDialog != null) {
                gaussianBlurDialog.dismiss();
            }
        }*/
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (!isTopActivity()) {
//            if (gaussianBlurDialog != null) {
//                gaussianBlurDialog.show();
//            }
//        }
    }

    @Override
    protected void onStop() {
        /*Constants.foregroundActivities--;
        if (Constants.foregroundActivities == 0) {
//            ViewGroup contentView = (ViewGroup) activity.getWindow().getDecorView();
//            View windowView = LayoutInflater.from(activity).inflate(R.layout.layout_shade_window, null);
//            contentView.addView(windowView);
            if (gaussianBlurDialog != null) {
                gaussianBlurDialog.show();
            }
        }*/
        super.onStop();
    }

    private boolean isTopActivity() {
        //_context是一个保存的上下文
        ActivityManager __am = (ActivityManager) MyApplication.getCtx().getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> __list = __am.getRunningAppProcesses();
        if (__list.size() == 0) return false;
        for (ActivityManager.RunningAppProcessInfo __process : __list) {
            LogUtils.d(Integer.toString(__process.importance));
            LogUtils.d(__process.processName);
            if (__process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                    __process.processName.equals(getPackageName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
