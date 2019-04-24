package com.uratio.demop.sliding;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.uratio.demop.R;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Administrator on 2017/3/7.
 */
public class SlidingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SlidingButtonView.IonSlidingButtonListener {

    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private LayoutInflater inflater = null;
    private Context mContext;
    private IonSlidingViewClickListener mIDeleteBtnClickListener;
    private List<Account> list;
    private SlidingButtonView mMenu = null;

    public SlidingAdapter(Context context, List<Account> list) {
        this.list = list;
        this.mContext = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_slidingbuttonview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            //设置内容布局的宽为屏幕宽度
            int widthPixels = mContext.getResources().getDisplayMetrics().widthPixels;
            ((ViewHolder) holder).layoutContent.getLayoutParams().width = widthPixels;

            int height = ((ViewHolder) holder).layoutContent.getLayoutParams().height;
//            MyApplication.logmsg_d("高的：height="+height);

            ((ViewHolder) holder).layoutDelete.getLayoutParams().width = height+50;

            /*AutoRelativeLayout.LayoutParams params = new AutoRelativeLayout.LayoutParams(height,height);
            ((ViewHolder) holder).layoutDelete.setLayoutParams(params);*/

            final Account data = list.get(position);
            ((ViewHolder) holder).iv_avatar.setText(data.getCity_name());
            ((ViewHolder) holder).tv_title.setText(data.getTitle());
            ((ViewHolder) holder).tv_date.setText(data.getAccount());

//            if ("counselorInfo".equals(from)){
//                ((ViewHolder) holder).layoutDelete.setVisibility(View.GONE);
//            }

            ((ViewHolder) holder).layoutContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //判断是否有删除菜单打开
                    if (menuIsOpen()) {
                        closeMenu();//关闭菜单
                    } else {
                        int n = holder.getLayoutPosition();
                        mIDeleteBtnClickListener.onItemClick(v, n);
                    }
                }
            });
            ((ViewHolder) holder).layoutDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int n = holder.getLayoutPosition();
                    mIDeleteBtnClickListener.onDeleteBtnCilck(v, n);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void removeData(int position) {
        list.remove(position);
        notifyItemRemoved(position);

    }
    public void reloadData(List<Account> mList,boolean isClear){
        if (isClear){
            list.clear();
        }
        list.addAll(mList);
        notifyDataSetChanged();
    }

    /**
     * 删除菜单打开信息接收
     */
    @Override
    public void onMenuIsOpen(View view) {
        mMenu = (SlidingButtonView) view;
    }

    /**
     * 滑动或者点击了Item监听
     *
     * @param slidingButtonView
     */
    @Override
    public void onDownOrMove(SlidingButtonView slidingButtonView) {
        if (menuIsOpen()) {
            if (mMenu != slidingButtonView) {
                closeMenu();
            }
        }
    }

    /**
     * 关闭菜单
     */
    public void closeMenu() {
        mMenu.closeMenu();
        mMenu = null;

    }

    /**
     * 判断是否有菜单打开
     */
    public Boolean menuIsOpen() {
        if (mMenu != null) {
            return true;
        }
        return false;
    }

    public void setmIDeleteBtnClickListener(IonSlidingViewClickListener mIDeleteBtnClickListener) {
        this.mIDeleteBtnClickListener = mIDeleteBtnClickListener;
    }

    public interface IonSlidingViewClickListener {
        void onItemClick(View view, int position);

        void onDeleteBtnCilck(View view, int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDelete;
        LinearLayout layoutDelete;
        TextView iv_avatar;
        TextView tv_title;
        TextView tv_date;
        RelativeLayout layoutContent;

        ViewHolder(View view) {
            super(view);
            tvDelete = view.findViewById(R.id.tv_delete);
            layoutDelete = view.findViewById(R.id.layout_delete);
            iv_avatar = view.findViewById(R.id.item_image);
            tv_title = view.findViewById(R.id.item_titleOrCounselor);
            tv_date = view.findViewById(R.id.item_timeOrJobTitle);
            layoutContent = view.findViewById(R.id.layout_content);
            ((SlidingButtonView) itemView).setSlidingButtonListener(SlidingAdapter.this);
        }
    }
}