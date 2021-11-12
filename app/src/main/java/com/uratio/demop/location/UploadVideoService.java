package com.uratio.demop.location;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.uratio.demop.utils.LogUtils;

/**
 * @author lang
 * @data 2021/9/17
 * 视频调查上传服务
 */
public class UploadVideoService extends Service {
    private static final String TAG = UploadVideoService.class.getSimpleName();



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.e(TAG, " ************onBind************ ");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e(TAG, " ************onCreate************ ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.e(TAG, " ************onStartCommand************ ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.e(TAG, " ************onDestroy************ ");
    }
}
