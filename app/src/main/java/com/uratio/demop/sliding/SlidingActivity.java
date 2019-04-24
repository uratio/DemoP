package com.uratio.demop.sliding;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.uratio.demop.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SlidingActivity extends AppCompatActivity {
//    private SlideRecyclerView recyclerView;

//    private InventoryAdapter adapter;
//    private List<Inventory> mInventories = new ArrayList<>();

    private RecyclerView recyclerView;

    private SlidingAdapter adapter;
//    private SwipeLayoutAdapter adapter;
    private List<Account> accountList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding);

        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        /*mInventories.clear();
        Inventory inventory;
        Random random = new Random();
        for (int i = 0; i < 50; i++) {
            inventory = new Inventory();
            inventory.setItemDesc("测试数据" + i);
            inventory.setQuantity(random.nextInt(100000));
            inventory.setItemCode("0120816");
            inventory.setDate("20180219");
            inventory.setVolume(random.nextFloat());
            mInventories.add(inventory);
        }
        adapter = new InventoryAdapter(this, mInventories);
        recyclerView.setAdapter(adapter);
        adapter.setOnDeleteClickListener(new InventoryAdapter.OnDeleteClickLister() {
            @Override
            public void onDeleteClick(View view, int position) {
                mInventories.remove(position);
                adapter.notifyDataSetChanged();
                recyclerView.closeMenu();
            }
        });*/

        accountList.clear();
        Account account = new Account();
        for (int i = 0; i < 24; i++) {
            account.setCity_name("回复"+(i+1));
            account.setTitle("用户名"+(i+1));
            account.setAccount("1234156413"+(i+1));
            accountList.add(account);
        }
        adapter = new SlidingAdapter(this,accountList);
        recyclerView.setAdapter(adapter);

        adapter.setmIDeleteBtnClickListener(new SlidingAdapter.IonSlidingViewClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(SlidingActivity.this, "点击了"+position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteBtnCilck(View view, int position) {
                Toast.makeText(SlidingActivity.this, "点击了"+position+"删除", Toast.LENGTH_SHORT).show();
            }
        });

        /*adapter = new SwipeLayoutAdapter(this,accountList);
        recyclerView.setAdapter(adapter);

        adapter.setmIDeleteBtnClickListener(new SwipeLayoutAdapter.IonSlidingViewClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(SlidingActivity.this, "点击了"+position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteBtnCilck(View view, int position) {
                Toast.makeText(SlidingActivity.this, "点击了"+position+"删除", Toast.LENGTH_SHORT).show();
            }
        });*/


        Log.i("pinyin", ChineseToPinyinHelper.getInstance().getPinyin("李塞"));
        Log.i("pinyin", ChineseToPinyinHelper.getInstance().getPinyin("李塞-"));
        Log.i("pinyin", ChineseToPinyinHelper.getInstance().getPinyin("李塞*"));
        Log.i("pinyin", ChineseToPinyinHelper.getInstance().getPinyin("李塞a"));
        Log.i("pinyin", ChineseToPinyinHelper.getInstance().getPinyin("李塞r"));
        Log.i("pinyin", ChineseToPinyinHelper.getInstance().getPinyin("李森"));
    }
}
