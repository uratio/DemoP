package com.uratio.demop.livedata;

import java.math.BigDecimal;

/**
 * @author lang
 * @data 2021/6/29
 */
public interface SimplePriceListener {
    void onPriceChanged(BigDecimal price);
}
