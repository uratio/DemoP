package com.uratio.demop.viewpager;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uratio.demop.R;

import java.util.ArrayList;
import java.util.List;

public class PagerFragment extends Fragment {
    private RcvAdapter adapter;
    private int page = 1;

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

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        layout.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layout);

        final List<Integer> list = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            list.add(i + 1);
        }
        adapter = new RcvAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);

        // 设置加载更多监听
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
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
