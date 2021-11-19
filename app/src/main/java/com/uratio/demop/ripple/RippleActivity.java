package com.uratio.demop.ripple;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.uratio.demop.R;

import java.util.concurrent.ThreadLocalRandom;

public class RippleActivity extends AppCompatActivity {
    private WavePointSurfaceView waveView;
    private WavePointSurfaceView2 waveView2;

    private ThreadLocalRandom random = ThreadLocalRandom.current();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    int nextInt = random.nextInt(0, 3500);
                    waveView.setVolume(nextInt);
                    handler.sendEmptyMessageDelayed(0, 200);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ripple);

        waveView = findViewById(R.id.wave_view);
        waveView2 = findViewById(R.id.wave_view2);

        handler.sendEmptyMessage(0);
    }

    public void onClickView(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                waveView2.start();
                waveView.start();
                break;
            case R.id.btn_pause:
                waveView2.pause();
                waveView.pause();
                break;
            case R.id.btn_resume:
                waveView2.resume();
                waveView.resume();
                break;
            case R.id.btn_stop:
                waveView2.stop();
                waveView.stop();
                break;
        }
    }
}
