package com.uratio.demop.runnable;

import java.io.Serializable;
import java.util.Map;

public class City implements Serializable {
    private String val;
    private Map<String,City> items;

    @Override
    public String toString() {
        return "City{" +
                "val='" + val + '\'' +
                ", items=" + items +
                '}';
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public Map<String, City> getItems() {
        return items;
    }

    public void setItems(Map<String, City> items) {
        this.items = items;
    }
}
