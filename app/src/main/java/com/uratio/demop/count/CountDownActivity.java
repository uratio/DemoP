package com.uratio.demop.count;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.uratio.demop.R;

public class CountDownActivity extends AppCompatActivity {
    private CircleProgressbar tvRedSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_down);

        tvRedSkip = findViewById(R.id.tv_red_skip);

        tvRedSkip.setTimeMillis(3000);

        tvRedSkip.setProgressType(CircleProgressbar.ProgressType.COUNT);
        tvRedSkip.setCountdownProgressListener(0, new CircleProgressbar.OnCountdownProgressListener() {
            @Override
            public void onProgress(int what, int progress) {
                if (progress == 100){
                    Toast.makeText(CountDownActivity.this, "到时间了", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void ClickView(View view) {
        switch (view.getId()){
            case R.id.start:
                tvRedSkip.start();
                break;
            case R.id.stop:
                tvRedSkip.stop();
                break;
            case R.id.shop:
//                Intent intent = new Intent();
//                intent.setAction("android.intent.action.VIEW");
//                Uri content_url = Uri.parse("http://39.106.11.228/static/app/app-tencent-release.apk");
//                intent.setData(content_url);
//                startActivity(intent);
                StringBuilder localStringBuilder = new StringBuilder().append("market://details?id=");
                String str = "com.manpower.sbc";
                localStringBuilder.append(str);
                Uri localUri = Uri.parse("market://details?id=com.manpower.sbc");
                Intent intent = new Intent("android.intent.action.VIEW", localUri);
                startActivity(intent);
                break;
            case R.id.guide:
                break;
        }
    }
}
