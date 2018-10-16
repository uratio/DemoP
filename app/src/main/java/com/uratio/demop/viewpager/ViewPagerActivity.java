package com.uratio.demop.viewpager;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.uratio.demop.R;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);

        ViewPager viewPager = findViewById(R.id.viewPager);

        List<Fragment> fragments = new ArrayList<>();

//        for (int i = 0; i < 3; i++) {
            fragments.add(PagerFragment.newInstance(1));
            fragments.add(PagerFragment2.newInstance(2));
            fragments.add(PagerFragment3.newInstance(3));
            fragments.add(PagerFragment3.newInstance(4));
//        }

        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(),fragments));
    }
}
