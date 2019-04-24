package com.uratio.demop.sliding;

import java.io.Serializable;

public class Account implements Serializable {
    /**
     * city_id : 100000
     * id : 1
     * title : 用户名
     * account : 132456164613
     * index : 0
     */

    private String city_id;
    private String city_name;
    private int id;
    private String title;
    private String account;
    private int index;

    @Override
    public String toString() {
        return "Account{" +
                "city_id=" + city_id +
                ", city_name='" + city_name + '\'' +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", account='" + account + '\'' +
                ", index=" + index +
                '}';
    }

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
