package com.uratio.demop.ripple;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.uratio.demop.R;

public class RippleActivity extends AppCompatActivity {
    private WavePointSurfaceView waveView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ripple);

        waveView = findViewById(R.id.wave_view);
    }

    public void onClickView(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                waveView.start();
                break;
            case R.id.btn_stop:
                waveView.stop();
                break;
        }
    }
}
