package com.uratio.demop.ripple;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.uratio.demop.R;

public class RippleActivity extends AppCompatActivity {
    private RecordButton recordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ripple);

        recordButton = findViewById(R.id.record_button);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (recordButton != null) {
            recordButton.onDestroy();
        }
    }
}
