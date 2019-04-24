package com.uratio.demop.lottery;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import java.io.Serializable;

public class Prize implements Serializable {
    private int Id;
    private String Name;
    private Bitmap icon;
    private String desc;
    private int bgColor;
    private int descColor;
    private OnClickListener listener;

    @Override
    public String toString() {
        return "Prize{" +
                "Id=" + Id +
                ", Name='" + Name + '\'' +
                ", icon=" + icon +
                ", desc='" + desc + '\'' +
                ", bgColor=" + bgColor +
                ", descColor=" + descColor +
                ", listener=" + listener +
                '}';
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public int getDescColor() {
        return descColor;
    }

    public void setDescColor(int descColor) {
        this.descColor = descColor;
    }

    public OnClickListener getListener() {
        return listener;
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    public interface OnClickListener {
        void onClick();
    }

    public void click() {
        listener.onClick();
    }
}
