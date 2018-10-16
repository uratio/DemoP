package com.uratio.demop.viewstub;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;

import com.uratio.demop.R;

public class ViewStubActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stub);

        final ViewStub viewStub = findViewById(R.id.viewSub);
        final View inflate = viewStub.inflate();
//        inflate.setVisibility(View.GONE);
        viewStub.setVisibility(View.GONE);

        findViewById(R.id.show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                inflate.setVisibility(View.VISIBLE);
                viewStub.setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.hide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                inflate.setVisibility(View.GONE);
                viewStub.setVisibility(View.GONE);
            }
        });

        /**
         * 如果多次对ViewStub进行infalte，会出现错误信息：java.lang.IllegalStateException: ViewStub must have a non-null ViewGroup viewParent
         * viewStub.inflate();
         */
    }
}
