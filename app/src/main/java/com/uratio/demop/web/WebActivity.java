package com.uratio.demop.web;

import android.net.http.SslError;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.uratio.demop.R;

public class WebActivity extends AppCompatActivity {
//    private LinearLayout layout;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

//        layout = findViewById(R.id.layout);
        webView = findViewById(R.id.webView);
//        webView = new WebView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        webView.setLayoutParams(params);

        webView.setWebViewClient(new WebViewClient());//防止连接到浏览器中打开

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        WebSettings webSettings = webView.getSettings(); //支持缩放，默认为true。 .setUseWideViewPort(true);
        // 缩放至屏幕的大小
        webSettings.setLoadWithOverviewMode(true);
        // 设置默认编码
        webSettings.setDefaultTextEncodingName("utf-8");
        //设置自动加载图片
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setJavaScriptEnabled(true);// 设置支持解析JavaScript语句
        webSettings.setSupportZoom(false);// 用于设置webview放大
        webSettings.setBuiltInZoomControls(false);
        webSettings.setAllowFileAccess(true);
        webSettings.setDomStorageEnabled(true);


        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //注意：super句话一定要删除，或者注释掉，否则又走handler.cancel()默认的不支持https的了。
                //super.onReceivedSslError(view, handler, error);
                //handler.cancel(); // Android默认的处理方式
                //handler.handleMessage(Message msg); // 进行其他处理

                handler.proceed(); // 接受所有网站的证书
            }
        });

//        webView.loadUrl("https://apis.map.qq.com/uri/v1/routeplan?type=bus&from=我的家&fromcoord=39.980683,116.302&to=中关村&tocoord=39.9836,116.3164&policy=1&referer=com.zuojuyouzuo.zjyz");
        webView.loadUrl("https://apis.map.qq.com/uri/v1/routeplan?type=drive&to=云智汇（深圳）高新科技服务有限公司&tocoord=22.60408,114.057793&referer=VCDBZ-FVU36-ODYSL-MZ2YN-YBVHV-XQF3G");

//        layout.addView(webView);
    }
}
