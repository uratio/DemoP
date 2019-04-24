package com.uratio.demop.img;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.marshalchen.ultimaterecyclerview.GlideApp;
import com.uratio.demop.R;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class ImageActivity extends AppCompatActivity {
    private ImageView imageView;
    private DatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        imageView = findViewById(R.id.imageView);
        datePicker = findViewById(R.id.datePicker);
        RequestOptions options = new RequestOptions();
//        options.placeholder(R.drawable.video_default)// 正在加载中的图片  
//        options.error(R.drawable.video_error) // 加载失败的图片  
//        options.diskCacheStrategy(DiskCacheStrategy.ALL); // 磁盘缓存策略  
        options.bitmapTransform(new BlurTransformation(this, 25, 3));

        GlideApp.with(this)
                .load(R.drawable.icon_service_header)
//                .load("https://uplt.rrb365.com/image/20190117/2019011711371757346.jpg")
//                .dontAnimate()
//                .error(R.drawable.icon_service_header)
                // "14":模糊度；"3":图片缩放3倍后再进行模糊，缩放3-5倍个人感觉比较好。
//                .bitmapTransform(new BlurTransformation(this, 14, 3))
                .apply(options)
                .into(imageView);

//        DatePickerDialog dialog = new DatePickerDialog(this);
        final DatePickerDialog dialog=new DatePickerDialog(this, AlertDialog.THEME_HOLO_DARK, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                Toast.makeText(ImageActivity.this, year+"year "+(monthOfYear+1)+"month "+dayOfMonth+"day", Toast.LENGTH_SHORT).show();
            }
        }, 2013, 7, 20){
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                LinearLayout mSpinners = (LinearLayout) findViewById(getContext().getResources().getIdentifier("android:id/pickers", null, null));
                if (mSpinners != null) {
                    NumberPicker mMonthSpinner = (NumberPicker) findViewById(getContext().getResources().getIdentifier("android:id/month", null, null));
                    NumberPicker mYearSpinner = (NumberPicker) findViewById(getContext().getResources().getIdentifier("android:id/year", null, null));
                    mSpinners.removeAllViews();
                    if (mYearSpinner != null) {
                        mSpinners.addView(mYearSpinner);
                    }
                    if (mMonthSpinner != null) {
                        mSpinners.addView(mMonthSpinner);
                    }
                }
                View dayPickerView = findViewById(getContext().getResources().getIdentifier("android:id/day", null, null));
                if(dayPickerView != null){
                    dayPickerView.setVisibility(View.GONE);
                }
            }
        };
//        dialog.setTitle("开始选择");
        dialog.setTitle(null);
        dialog.show();
        DatePicker datePickerStart = dialog.getDatePicker();
        datePickerStart.setMaxDate(System.currentTimeMillis());


        ((ViewGroup)((ViewGroup)dialog.getDatePicker().getChildAt(0)).getChildAt(0)).getChildAt(2);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
    }
}
