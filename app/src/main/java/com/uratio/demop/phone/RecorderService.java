package com.uratio.demop.phone;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Easzz on 2015/12/6.
 */
public class RecorderService extends Service {
    private String TAG = "recordPhone";

    private MediaRecorder recorder; //录音的一个实例
    private MyListener listener;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //获得电话管理器
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        //启动监听.传入一个listener和监听的事件,
        if (listener == null) {
            listener = new MyListener();
        }
        Log.i(TAG, "**启动监听");
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    class MyListener extends PhoneStateListener {

        //在电话状态改变的时候调用
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            Log.i(TAG, "**state=" + state + '\t' + "incomingNumber=" + incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    //空闲状态
                    if (recorder != null) {
                        Log.i(TAG, "**停止录音");
                        recorder.stop();//停止录音
                        recorder.release();//释放资源
                        recorder = null;
                    }
                    break;

                case TelephonyManager.CALL_STATE_RINGING:
                    Log.i(TAG, "**响铃状态");
                    //响铃状态  需要在响铃状态的时候初始化录音服务
                    if (recorder == null) {
                        recorder = new MediaRecorder();//初始化录音对象
                        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);//设置录音的输入源(麦克)
                        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);//设置音频格式(3gp)
                        createRecorderFile();//创建保存录音的文件夹  

                        recorder.setOutputFile("sdcard/recorder" + "/" + getCurrentTime() + ".3gp"); //设置录音保存的文件
                        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);//设置音频编码
                        try {
                            recorder.prepare();//准备录音
                            Log.i(TAG, "**准备录音");
                        } catch (IOException e) {
                            Log.i(TAG, "**准备recorder错误=" + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //摘机状态（接听）
                    Log.i(TAG, "**点击了接听按钮");
                    if (recorder != null) {
                        Log.i(TAG, "**开始录音");
                        recorder.start(); //接听的时候开始录音
                    }else {
                        Log.i(TAG, "**录音失败，recorder == null");
                    }
                    break;
            }
        }

        //创建保存录音的目录
        private void createRecorderFile() {
            String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();

            String filePath = absolutePath + "/recorder";
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        }

        //获取当前时间，以其为名来保存录音
        private String getCurrentTime() {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = new Date();
            String str = format.format(date);
            return str;

        }
    }
}