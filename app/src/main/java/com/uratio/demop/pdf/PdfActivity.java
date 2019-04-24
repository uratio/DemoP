package com.uratio.demop.pdf;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.uratio.demop.R;

public class PdfActivity extends AppCompatActivity {
    private WebView webView;
    private String path = "https://uplt.rrb365.com/pdf/SS201811281607169693.pdf";
    private String SDPATH = Environment.getExternalStorageDirectory() + "/";//获取文件夹

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);


        webView = findViewById(R.id.webView);

//        Uri uri = Uri.parse(path);
//        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);

    }
}
