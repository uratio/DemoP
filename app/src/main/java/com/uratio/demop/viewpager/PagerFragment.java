package com.uratio.demop.viewpager;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.uratio.demop.R;

import java.util.ArrayList;
import java.util.List;

public class PagerFragment extends Fragment {
    private OrderViewAdapter adapter;

    private ProgressBar mProgress;
    private TextView mLoadMore;
    private LinearLayout mLoadMoreView;

    private UltimateRecyclerView mOrderList;
    private List<Integer> list = new ArrayList<>();


    private int page = 1;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    mOrderList.setRefreshing(false);
                    break;
                case 1:
                    if (list == null || list.size() < 1) {
//                        ViewUtils.setVisibility(layoutEmpty, View.VISIBLE);
                    } else {
//                        ViewUtils.setVisibility(layoutEmpty, View.GONE);
                        mProgress.setVisibility(View.GONE);
                        mLoadMore.setText("加载完成");
                    }
                    break;
            }
        }
    };

    public static PagerFragment newInstance(int index) {
        PagerFragment fragment = new PagerFragment();
        Bundle args = new Bundle();
        args.putInt("index", index);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment, null);

        TextView textView = view.findViewById(R.id.text);
        int index = getArguments().getInt("index");
        textView.setText("Fragment_" + index);

        switch (index) {
            case 1:
                view.setBackgroundColor(Color.parseColor("#20FF0000"));
                break;
            case 2:
                view.setBackgroundColor(Color.parseColor("#2000FF00"));
                break;
            case 3:
                view.setBackgroundColor(Color.parseColor("#200000FF"));
                break;
        }

        mOrderList = view.findViewById(R.id.recyclerView);

        for (int i = 0; i < 12; i++) {
            list.add(i + 1);
        }

//        LinearLayoutManager layout = new LinearLayoutManager(getContext());
//        layout.setOrientation(LinearLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(layout);

        mOrderList.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new OrderViewAdapter(getActivity(), list);
        mLoadMoreView = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.adapter_loadmore_item, null);
        mProgress = (ProgressBar) mLoadMoreView.findViewById(R.id.progress);
        mLoadMore = (TextView) mLoadMoreView.findViewById(R.id.content);
        mOrderList.setLoadMoreView(mLoadMoreView);
        mOrderList.setAdapter(adapter);
        mOrderList.enableDefaultSwipeRefresh(false);
        mOrderList.reenableLoadmore();
        adapter.internalExecuteLoadingView();

        mOrderList.setAdapter(adapter);

        // 设置加载更多监听
//        mOrderList.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
//            @Override
//            public void onLoadMore() {
//                Log.i("data", "onLoadMore:  加载更多 ");
//                page++;
//                if (page <= 2) {
//                    // 加载更多
//                    list.add(14);
//                    list.add(15);
//                    list.add(16);
//                    list.add(17);
//                    list.add(18);
//                    list.add(19);
//                    adapter.notifyDataSetChanged();
//                }
//            }
//        });
        mOrderList.setDefaultOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("data", "onLoadMore:  下拉刷新 ");
                handler.sendEmptyMessageDelayed(0,2000);
            }
        });
        mOrderList.setOnLoadMoreListener(new UltimateRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMore(int itemsCount, int maxLastVisiblePosition) {
                Log.i("data", "onLoadMore:  加载更多 ");
                page++;
                if (page <= 2) {
                    // 加载更多
                    list.add(14);
                    list.add(15);
                    list.add(16);
                    list.add(17);
                    list.add(18);
                    list.add(19);
                    adapter.notifyDataSetChanged();
                }
                handler.sendEmptyMessageDelayed(1,2000);
            }
        });

        return view;
    }

    public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

        //用来标记是否正在向左滑动
        private boolean isSlidingToLeft = false;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
            // 当不滑动时
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                // 获取最后一个完全显示的itemPosition
                int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();
                int itemCount = manager.getItemCount();

                // 判断是否滑动到了最后一个Item，并且是向左滑动
                if (lastItemPosition == (itemCount - 1) && isSlidingToLeft) {
                    // 加载更多
                    onLoadMore();
                }
            }
        }


        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            // dx值大于0表示正在向左滑动，小于或等于0表示向右滑动或停止
            isSlidingToLeft = dx > 0;
        }


        /**
         * 加载更多回调
         */
        public abstract void onLoadMore();
    }
}
