package com.uratio.demop.list;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.uratio.demop.R;

import java.util.ArrayList;
import java.util.List;

public class RcvListActivity extends AppCompatActivity {
//    private TopicScrollView scrollView;
    private RcvForScrollView recyclerView;
    private ListView listView;

    private List<String> dataList = new ArrayList();

    private RcvListAdapter adapter;
    private ListViewAdapter adapter2;

    private View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rcv_list);

//        scrollView = findViewById(R.id.scrollView);
        recyclerView = findViewById(R.id.recyclerView);
        listView = findViewById(R.id.listView);

        headerView = LayoutInflater.from(this).inflate(R.layout.layout_header,null);

//        scrollView.smoothScrollTo(0,0);

        for (int i = 0; i < 10; i++) {
            dataList.add("第" + (1 + i) + "条item");
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RcvListAdapter(this, dataList);

        recyclerView.setAdapter(adapter);
//        recyclerView.enableDefaultSwipeRefresh(false);
//        recyclerView.reenableLoadmore();
        adapter.internalExecuteLoadingView();

        /*recyclerView.setDefaultOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("data", "onRefresh: 下拉刷新");
            }
        });
        recyclerView.setOnLoadMoreListener(new UltimateRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMore(int itemsCount, int maxLastVisiblePosition) {
                Log.i("data", "onRefresh: 加载更多");
            }
        });*/
        /*scrollView.setScrollViewListener(new TopicScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(TopicScrollView scrollView, int x, int y, int oldX, int oldY) {
                Log.i("data", "--------------------------------------");
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int firstCompletelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                Log.i("data", "firstCompletelyVisibleItemPosition: " + firstCompletelyVisibleItemPosition);
                if (firstCompletelyVisibleItemPosition == 0)
                    Log.i("data", "滑动到顶部");

                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                int lastCompletelyVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                Log.i("data", "lastVisibleItemPosition"+lastVisibleItemPosition+"   lastCompletelyVisibleItemPosition: " + lastCompletelyVisibleItemPosition);
                if (lastVisibleItemPosition == lastCompletelyVisibleItemPosition && lastCompletelyVisibleItemPosition == layoutManager.getItemCount() - 1) {
                    Log.i("data", "滑动到底部");
                    int size = dataList.size();
                    for (int i = 0; i < 3; i++) {
                        dataList.add("第" + (size + i) + "条item");
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });*/
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.i("data", "onScrollStateChanged: newState=" + newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                /*if (isScrollBottom()) {
                    int size = dataList.size();
                    for (int i = 0; i < 3; i++) {
                        dataList.add("第" + (size + i) + "条item");
                    }
                }*/
                Log.i("data", "--------------------------------------");
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int firstCompletelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                Log.i("data", "firstCompletelyVisibleItemPosition: " + firstCompletelyVisibleItemPosition);
                if (firstCompletelyVisibleItemPosition == 0)
                    Log.i("data", "滑动到顶部");

                int lastCompletelyVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                Log.i("data", "lastCompletelyVisibleItemPosition: " + lastCompletelyVisibleItemPosition);
                if (lastCompletelyVisibleItemPosition == layoutManager.getItemCount() - 1) {
                    Log.i("data", "滑动到底部");
                    int size = dataList.size();
                    for (int i = 0; i < 3; i++) {
                        dataList.add("第" + (size + i) + "条item");
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });

        listView.addHeaderView(headerView);

        adapter2 = new ListViewAdapter(this, dataList);
        listView.setAdapter(adapter2);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.d("data", "##### firstVisibleItem="+firstVisibleItem+"  visibleItemCount="+visibleItemCount+"  totalItemCount="+totalItemCount+" #####");
                /*if (firstVisibleItem == 0) {
                    Log.d("data", "##### 滚动到顶部 #####");
                } else */if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
                    Log.d("data", "##### 滚动到底部 ######");
                    int size = dataList.size();
                    for (int i = 0; i < 3; i++) {
                        dataList.add("第" + (size + i) + "条item");
                    }
                    adapter2.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * recyclerView 滑动到底部
     */
    public boolean isScrollBottom() {
        if (recyclerView == null) {
            return false;
        } else {
            if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange()
                    && recyclerView.computeVerticalScrollOffset() != 0) {
                return true;
            }
            return false;
        }
    }
}
