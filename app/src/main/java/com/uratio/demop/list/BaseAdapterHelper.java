package com.uratio.demop.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/12/7.
 */
public abstract class BaseAdapterHelper<T> extends BaseAdapter {
    private Context context = null;
    private List<T> list = null;
    private LayoutInflater mInflater = null;

    public BaseAdapterHelper(Context context, List<T> list) {
        this.context = context;
        this.list = list;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItemView(position,convertView,parent,context,list,mInflater);
    }

    public abstract View getItemView(int position, View convertView, ViewGroup parent, Context
            context, List<T> list, LayoutInflater mInflater);

    public void reloadListView(List<T> mList, boolean isClear) {
        if (isClear) {
            list.clear();
        }
        if(mList!=null){
            list.addAll(mList);
        }
        notifyDataSetChanged();
    }
    public void addData(T t) {
        if(list!=null){
             list.add(t);
        }
        notifyDataSetChanged();
    }

    public  void clearOne(int position){
        list.remove(position);
        notifyDataSetChanged();
    }
    public void clearAll() {
        list.clear();
        notifyDataSetChanged();
    }
}
