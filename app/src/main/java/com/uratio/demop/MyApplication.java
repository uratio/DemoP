package com.uratio.demop;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.view.DisplayCutout;

public class MyApplication extends Application{
//    @RequiresApi(api = 28)
    @Override
    public void onCreate() {
        super.onCreate();

//        DisplayCutout displayCutout = new DisplayCutout();
        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }
    }
}
