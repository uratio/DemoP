package com.uratio.demop.viewpager;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.uratio.demop.R;

import java.util.List;

/**
 * Created by Android on 16.11.3.
 */

public class OrderViewAdapter extends UltimateViewAdapter<OrderViewAdapter.Holder>{
    private List<Integer> mData;
    private final FragmentActivity mActivity;
    private LayoutInflater mInflater;

    public OrderViewAdapter(FragmentActivity activity, List<Integer> list) {
        this.mData = list;
        this.mActivity = activity;
        mInflater = LayoutInflater.from(mActivity);
    }

    @Override
    public Holder newFooterHolder(View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        view.setLayoutParams(params);
        return new Holder(view);
    }

    @Override
    public Holder newHeaderHolder(View view) {
        return null;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent) {
        Holder holder = new Holder(mInflater.inflate(R.layout.item_fragment, parent, false));
        return holder;
    }

    @Override
    public int getAdapterItemCount() {
        return mData.size() == 0 ? -1 : mData.size();
    }

    @Override
    public long generateHeaderId(int position) {
        return -1;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        if (mData.size() > position) {
            holder.text.setText("item_"+mData.get(position));
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    class Holder extends UltimateRecyclerviewViewHolder {
        TextView text;

        public Holder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
        }
    }
}
