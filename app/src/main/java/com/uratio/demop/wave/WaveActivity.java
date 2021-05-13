package com.uratio.demop.wave;

import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.uratio.demop.R;

import java.io.File;
import java.io.IOException;

public class WaveActivity extends AppCompatActivity {
    private WaveBallProgress waveBall;
    private SineWave sineWave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wave);


        waveBall = findViewById(R.id.wave_ball);

        waveBall.startProgress(90);

        sineWave = findViewById(R.id.sine_wave);
    }
}
