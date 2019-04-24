package com.uratio.demop.list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ViewUtils;
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

public class ListViewAdapter extends BaseAdapterHelper<String> {

    public ListViewAdapter(Context context, List<String> list) {
        super(context, list);
    }

    @Override
    public View getItemView(int position, View convertView, ViewGroup parent, Context context, List<String> list, LayoutInflater mInflater) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_rcv_list, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final String data = list.get(position);
        holder.tvItem.setText(data);

        return convertView;
    }

    class ViewHolder {
        TextView tvItem;

        public ViewHolder(View itemView) {
            tvItem = itemView.findViewById(R.id.tv_item);
        }
    }
}
