package com.uratio.demop.livedata;

import android.util.ArrayMap;

import java.util.Map;

/**
 * @author lang
 * @data 2021/6/29
 */
public class StockManager {
    private Map<String, SimplePriceListener> cacheMap = new ArrayMap<>();
    private String symbol;
    private SimplePriceListener listener;

    public StockManager(String symbol) {
        this.symbol = symbol;
        cacheMap.put(symbol, null);
    }

    public void requestPriceUpdates(SimplePriceListener listener) {
        this.listener = listener;
    }

    public void removeUpdates(SimplePriceListener listener) {
        if (this.listener == listener) {
            this.listener = null;
        }
    }
}
