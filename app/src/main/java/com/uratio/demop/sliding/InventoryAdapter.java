package com.uratio.demop.sliding;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uratio.demop.R;

import java.util.List;

/**
 * 清单列表adapter
 * <p>
 * Created by DavidChen on 2018/5/30.
 */

public class InventoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Inventory> list;
    private LayoutInflater inflater;

    private OnDeleteClickLister mDeleteClickListener;

    public void setOnDeleteClickListener(OnDeleteClickLister listener) {
        this.mDeleteClickListener = listener;
    }

    public InventoryAdapter(Context context, List<Inventory> list) {
        this.context = context;
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_inventroy,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        if (holder instanceof ViewHolder) {
            Inventory bean = list.get(i);

//            if (!((ViewHolder) holder).tvDelete.hasOnClickListeners()) {
                ((ViewHolder) holder).tvDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mDeleteClickListener != null) {
                            mDeleteClickListener.onDeleteClick(v, (Integer) v.getTag());
                        }
                    }
                });
//            }
            ((ViewHolder) holder).tvItemDesc.setText(bean.getItemDesc());
            String quantity = bean.getQuantity() + "箱";
            ((ViewHolder) holder).tvQuantity.setText(quantity);
            String detail = bean.getItemCode() + "/" + bean.getDate();
            ((ViewHolder) holder).tvDetail.setText(detail);
            String volume = bean.getVolume() + "方";
            ((ViewHolder) holder).tvVolume.setText(volume);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnDeleteClickLister {
        void onDeleteClick(View view, int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDelete;
        TextView tvQuantity;
        TextView tvItemDesc;
        TextView tvDetail;
        TextView tvVolume;

        ViewHolder(View view) {
            super(view);
            tvDelete = view.findViewById(R.id.tv_delete);
            tvQuantity = view.findViewById(R.id.tv_quantity);
            tvItemDesc = view.findViewById(R.id.tv_item_desc);
            tvDetail = view.findViewById(R.id.tv_detail);
            tvVolume = view.findViewById(R.id.tv_volume);
        }
    }
}
