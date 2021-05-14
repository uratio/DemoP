package com.uratio.demop.wave.voice;

import android.animation.ValueAnimator;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.uratio.demop.R;

import java.io.File;
import java.io.IOException;

public class VoiceWaveActivity extends AppCompatActivity implements Runnable {
    private WaveView waveView;
    private WaveSurfaceView waveSurface;
    private WaveMoveView waveMoveView;

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
                    int maxAmplitude = mMediaRecorder.getMaxAmplitude();
                    waveSurface.setVolume(maxAmplitude);
                    double ratio = (double) maxAmplitude / 100;
                    double db = 0;// 分贝
                    //默认的最大音量是100,可以修改，但其实默认的，在测试过程中就有不错的表现
                    //你可以传自定义的数字进去，但需要在一定的范围内，比如0-200，就需要在xml文件中配置maxVolume
                    //同时，也可以配置灵敏度sensibility
                    double v = Math.log10(ratio);
                    if (ratio > 1) {
                        db = 20 * v;
                    }
                    waveView.setVolume((int) db);
                    waveMoveView.setVolume(maxAmplitude);
                    break;
            }
        }
    };

    private View view;
    private ValueAnimator animator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_wave);

        handler.sendEmptyMessage(0);

        waveView = findViewById(R.id.wave_view);
        waveSurface = findViewById(R.id.wave_surface);
        waveMoveView = findViewById(R.id.wave_line_view);
        view = findViewById(R.id.view);

        if (mMediaRecorder == null)
            mMediaRecorder = new MediaRecorder();

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "helloP.log");
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
            mMediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread thread = new Thread(this);
        thread.start();

        animator = new ValueAnimator();
        animator.setDuration(1500);
        animator.setTarget(view);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.e("VoiceWaveActivity", "onAnimationUpdate: getAnimatedValue=" + animation.getAnimatedValue());
//                view.setTranslationY((Float) animation.getAnimatedValue());
            }
        });
    }

    public void onClickView(View view) {
        switch (view.getId()) {
            case R.id.btn_start1:
                animator.start();
                break;
            case R.id.btn_start2:
                animator.setIntValues(0, 400, 0);
                animator.start();
                break;
            case R.id.btn_start3:
                animator.setIntValues(0, 400);
                animator.setStartDelay(1000);
                animator.start();
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