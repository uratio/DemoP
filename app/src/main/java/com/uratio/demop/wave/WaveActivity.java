package com.uratio.demop.wave;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.uratio.demop.R;

public class WaveActivity extends AppCompatActivity {
    private WaveBallProgress waveBall;
    private SineWave sineWave;
    private View view;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
//                    sineWave.Set();
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

        view = findViewById(R.id.view);
        view.setAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_cycle_interpolator));
    }

    public void ClickView(View view) {
        view.getAnimation().start();
    }
}
