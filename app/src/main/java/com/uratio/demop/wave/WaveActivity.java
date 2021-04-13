package com.uratio.demop.wave;

import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.uratio.demop.R;

import java.io.File;
import java.io.IOException;

public class WaveActivity extends AppCompatActivity implements Runnable {
    private WaveBallProgress waveBall;
    private SineWave sineWave;
    private WaveView waveView;

    private MediaRecorder mMediaRecorder;
    private boolean isAlive = true;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
//                    sineWave.Set();
                    break;
                case 1:
                    if(mMediaRecorder==null) return;
                    double ratio = (double) mMediaRecorder.getMaxAmplitude() / 100;
                    double db = 0;// 分贝
                    //默认的最大音量是100,可以修改，但其实默认的，在测试过程中就有不错的表现
                    //你可以传自定义的数字进去，但需要在一定的范围内，比如0-200，就需要在xml文件中配置maxVolume
                    //同时，也可以配置灵敏度sensibility
                    if (ratio > 1)
                        db = 20 * Math.log10(ratio);
                    waveView.setVolume((int) db);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wave);


        waveBall = findViewById(R.id.wave_ball);

        waveBall.startProgress(90);

        sineWave = findViewById(R.id.sine_wave);

        handler.sendEmptyMessage(0);

        waveView = findViewById(R.id.wave_view);

        if (mMediaRecorder == null)
            mMediaRecorder = new MediaRecorder();

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "hello.log");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mMediaRecorder.setOutputFile(file.getAbsolutePath());
        mMediaRecorder.setMaxDuration(1000 * 60 * 10);
        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaRecorder.start();

        Thread thread = new Thread(this);
        thread.start();
    }

    public void onClickView(View view) {
        switch (view.getId()) {
            case R.id.btn_volume:
                waveView.setVolume(32);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        isAlive = false;
        mMediaRecorder.release();
        mMediaRecorder = null;
        super.onDestroy();
    }

    @Override
    public void run() {
        while (isAlive) {
            handler.sendEmptyMessage(1);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
