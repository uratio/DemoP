package com.uratio.demop.utils;

/**
 * @author lang
 * @data 2021/6/29
 */
public class CommonConfig {
    public static boolean isDebug;
    public static String HOST;

    public static void init(boolean isDebug, String host) {
        CommonConfig.isDebug = isDebug;
        CommonConfig.HOST = host;
    }
}
