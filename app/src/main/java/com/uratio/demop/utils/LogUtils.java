package com.uratio.demop.utils;

import android.util.Log;

import java.util.Arrays;
import java.util.Locale;

/**
 * @author lang
 * @data 2021/6/29
 */
public class LogUtils {

    public static void v(String mess) {
        if (CommonConfig.isDebug) {
            Log.v(getTag(), buildMessage(mess));
        }
    }

    public static void d(String mess) {
        if (CommonConfig.isDebug) {
            Log.d(getTag(), buildMessage(mess));
        }
    }

    public static void i(String mess) {
        if (CommonConfig.isDebug) {
            Log.i(getTag(), buildMessage(mess));
        }
    }

    public static void w(String mess) {
        if (CommonConfig.isDebug) {
            Log.w(getTag(), buildMessage(mess));
        }
    }

    public static void e(String mess) {
        if (CommonConfig.isDebug) {
//            Log.e(getTag(), buildMessage(mess));
            log(Log.ERROR, getTag(), buildMessage(mess));
        }
    }

    public static void e(String tag, String mess) {
        if (CommonConfig.isDebug) {
//            Log.e(getTag(), buildMessage(mess));
            log(Log.ERROR, tag, buildMessage(mess));
        }
    }

    private static String getTag() {
        StackTraceElement[] trace = new Throwable().fillInStackTrace()
                .getStackTrace();
        String callingClass = "";
        for (int i = 2; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(LogUtils.class)) {
                callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass
                        .lastIndexOf('.') + 1);
                break;
            }
        }
        return callingClass;
    }

    private static String buildMessage(String msg) {
        StackTraceElement[] trace = new Throwable().fillInStackTrace()
                .getStackTrace();
        String caller = "";
        for (int i = 2; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(LogUtils.class)) {
                caller = trace[i].getMethodName();
                break;
            }
        }
        return String.format(Locale.US, "[%d] %s: %s", Thread.currentThread()
                .getId(), caller, msg);
    }

    /**
     * 打印日志到控制台（解决Android控制台丢失长日志记录）
     *
     * @param priority
     * @param tag
     * @param content
     */
    private static void log(int priority, String tag, String content) {
        // 1. 测试控制台最多打印4062个字节，不同情况稍有出入（注意：这里是字节，不是字符！！）
        // 2. 字符串默认字符集编码是utf-8，它是变长编码一个字符用1~4个字节表示
        // 3. 这里字符长度小于1000，即字节长度小于4000，则直接打印，避免执行后续流程，提高性能哈
        if (content.length() < 1000) {
            Log.println(priority, tag, content);
            return;
        }

        // 一次打印的最大字节数
        int maxByteNum = 4000;

        // 字符串转字节数组
        byte[] bytes = content.getBytes();

        // 超出范围直接打印
        if (maxByteNum >= bytes.length) {
            Log.println(priority, tag, content);
            return;
        }

        // 分段打印计数
        int count = 1;
        // 在数组范围内，则循环分段
        while (maxByteNum < bytes.length) {
            // 按字节长度截取字符串
            String subStr = cutStr(bytes, maxByteNum);
            // 打印日志
            print(priority, tag, cutStr(bytes, maxByteNum));

            // 截取出尚未打印字节数组
            bytes = Arrays.copyOfRange(bytes, subStr.getBytes().length, bytes.length);

            count++;
            // 避免有超长日志一直打印
            if (count == 10) {
                break;
            }
        }

        // 打印剩余部分
        print(priority, tag, new String(bytes));
    }

    /**
     * 按字节长度截取字节数组为字符串
     *
     * @param bytes
     * @param subLength
     * @return
     */
    private static String cutStr(byte[] bytes, int subLength) {
        // 边界判断
        if (bytes == null || subLength < 1) {
            return null;
        }

        // 超出范围直接返回
        if (subLength >= bytes.length) {
            return new String(bytes);
        }

        // 复制出定长字节数组，转为字符串
        String subStr = new String(Arrays.copyOf(bytes, subLength));

        // 避免末尾字符是被拆分的，这里减1使字符串保持完整
        return subStr.substring(0, subStr.length() - 1);
    }

    private static void print(int priority, String tag, String msg) {
        switch (priority) {
            case Log.VERBOSE:
                Log.v(tag, msg);
                break;
            case Log.DEBUG:
                Log.d(tag, msg);
                break;
            case Log.INFO:
                Log.i(tag, msg);
                break;
            case Log.WARN:
                Log.w(tag, msg);
                break;
            case Log.ERROR:
                Log.e(tag, msg);
                break;
        }
    }
}
