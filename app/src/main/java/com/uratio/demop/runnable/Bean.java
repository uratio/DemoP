package com.uratio.demop.runnable;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class Bean implements Serializable {
    private LinkedHashMap<String,City> map;

    @Override
    public String toString() {
        return "Bean{" +
                "map=" + map +
                '}';
    }

    public LinkedHashMap<String, City> getMap() {
        return map;
    }

    public void setMap(LinkedHashMap<String, City> map) {
        this.map = map;
    }
}
