package com.uratio.demop.livedata;

import android.arch.lifecycle.LiveData;
import android.support.annotation.MainThread;

import java.math.BigDecimal;

/**
 * @author lang
 * @data 2021/6/29
 */
public class StockLiveData extends LiveData<BigDecimal> {
    private static StockLiveData sInstance;
    private StockManager stockManager;
    private SimplePriceListener listener = new SimplePriceListener() {

        @Override
        public void onPriceChanged(BigDecimal price) {
            setValue(price);
        }
    };

    @MainThread
    public static StockLiveData get(String symbol) {
        if (sInstance == null) {
            sInstance = new StockLiveData(symbol);
        }
        return sInstance;
    }

    public StockLiveData(String symbol) {
        stockManager = new StockManager(symbol);
    }

    @Override
    protected void onActive() {
        stockManager.requestPriceUpdates(listener);
    }

    @Override
    protected void onInactive() {
        stockManager.removeUpdates(listener);
    }
}
