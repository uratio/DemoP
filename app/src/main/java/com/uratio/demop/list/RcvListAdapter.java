package com.uratio.demop.list;

import android.content.Context;
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

public class RcvListAdapter extends UltimateViewAdapter<RcvListAdapter.Holder> {
    private Context context;
    private List<String> mData;
    private LayoutInflater mInflater;

    public RcvListAdapter(Context context, List<String> list) {
        this.context = context;
        this.mData = list;
        mInflater = LayoutInflater.from(context);
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
        Holder holder = new Holder(mInflater.inflate(R.layout.item_rcv_list, parent, false));
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
    public void onBindViewHolder(final Holder holder, final int position) {
        if (mData.size() > position) {
            final String data = mData.get(position);
            holder.tvItem.setText(data);
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
        TextView tvItem;

        public Holder(View itemView) {
            super(itemView);
            try {
                tvItem = itemView.findViewById(R.id.tv_item);
            } catch (IllegalStateException exception) {

            }
        }
    }
}
