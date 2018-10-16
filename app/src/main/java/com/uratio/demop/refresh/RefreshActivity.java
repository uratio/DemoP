package com.uratio.demop.refresh;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ScrollView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.uratio.demop.R;

public class RefreshActivity extends AppCompatActivity {
    private PullToRefreshScrollView pullToRefresh;
    private String TAG = "data";

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    //刷新结束
                    pullToRefresh.onRefreshComplete();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh);

        pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

        pullToRefresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                Log.i(TAG, "******setOnRefreshListener: ******");
                handler.sendEmptyMessageDelayed(1,2500);
            }
        });
    }
}
