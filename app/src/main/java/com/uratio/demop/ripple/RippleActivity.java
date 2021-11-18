package com.uratio.demop.ripple;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.uratio.demop.R;

public class RippleActivity extends AppCompatActivity {
    private WavePointSurfaceView2 waveView;
    private WavePointSurfaceView waveView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ripple);

        waveView = findViewById(R.id.wave_view);
        waveView2 = findViewById(R.id.wave_view2);
    }

    public void onClickView(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                waveView.start();
                waveView2.start();
                break;
            case R.id.btn_pause:
                waveView.pause();
                waveView2.pause();
                break;
            case R.id.btn_resume:
                waveView.resume();
                waveView2.resume();
                break;
            case R.id.btn_stop:
                waveView.stop();
                waveView2.stop();
                break;
        }
    }
}
