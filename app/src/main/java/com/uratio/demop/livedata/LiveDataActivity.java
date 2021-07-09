package com.uratio.demop.livedata;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.uratio.demop.R;
import com.uratio.demop.utils.LogUtils;

import java.math.BigDecimal;
import java.util.List;

public class LiveDataActivity extends AppCompatActivity {
    private TextView tvShow;
    private NameViewModel model;
    private MutableLiveData<User> userLiveData;
    private LiveData<String> userName;
    private MutableLiveData<String> userId;
    private LiveData<User> userLiveData2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_data);
        tvShow = findViewById(R.id.tv_text);

        model = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(NameViewModel.class);

        Observer<String> nameObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable  String s) {
                tvShow.setText("名字：" + s);
            }
        };

        model.getCurrentName().observe(this, nameObserver);

        MyViewModel myViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(MyViewModel.class);
        myViewModel.getUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable  List<User> users) {
                // 更新UI
            }
        });

        //使用方法一：
        LiveData<BigDecimal> myPriceListener = new StockLiveData("LiveDataActivity");
        myPriceListener.observe(this, new Observer<BigDecimal>() {
            @Override
            public void onChanged(@Nullable  BigDecimal bigDecimal) {

            }
        });
        //使用方法二：
        StockLiveData.get("LiveDataActivity").observe(this, new Observer<BigDecimal>() {
            @Override
            public void onChanged(@Nullable  BigDecimal bigDecimal) {

            }
        });

        // LiveData数据转换
        userLiveData = new MutableLiveData<>();
//        userLiveData.setValue(new User("为", "法华寺"));
        userName = Transformations.map(userLiveData, new Function<User, String>() {
            @Override
            public String apply(User user) {
                LogUtils.e(user.name + " " + user.lastName);
                return user.name + " " + user.lastName;
            }
        });
        /**
         * 必须订阅才能实时改变值 ???
         */
//        userName.observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable  String s) {
//                LogUtils.e("onChanged" + s);
//            }
//        });
        LogUtils.e("userName.getValue()="+userName.getValue());

        userId = new MutableLiveData<>();
        userLiveData2 = Transformations.switchMap(userId, new Function<String, LiveData<User>>() {
            @Override
            public LiveData<User> apply(String s) {
                return getUser(s);
            }
        });
        userId.setValue("123456");
        LogUtils.e("userLiveData2.getValue()="+userLiveData2.getValue());
    }

    public MutableLiveData<User> getUser(String id) {
        MutableLiveData<User> data = new MutableLiveData<>();
        data.setValue(new User(id));
        return data;
    }

    public void onClickView(View view) {
        switch (view.getId()) {
            case R.id.btn_set_data:
                model.getCurrentName().setValue("joy boy");
                break;
            case R.id.btn_add_data1:
                userLiveData.setValue(new User("就是", "奥德赛"));
                LogUtils.e(userLiveData.getValue().toString());
                LogUtils.e("userName.getValue()="+userName.getValue());
                break;
            case R.id.btn_add_data2:
                userLiveData.setValue(new User("发的", "你是"));
                LogUtils.e(userLiveData.getValue().toString());
                LogUtils.e("userName.getValue()="+userName.getValue());
                break;
            case R.id.btn_add_data_s1:
                userId.setValue("00991122");
                LogUtils.e(userLiveData2.getValue().toString());
                LogUtils.e("userLiveData2.getValue()="+userLiveData2.getValue());
                break;
        }
    }
}