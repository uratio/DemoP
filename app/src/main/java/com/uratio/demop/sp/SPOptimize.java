package com.uratio.demop.sp;

/**
 * @author lang
 * @data 2023/4/27
 * <p>
 * SharedPreference apply 引起的 ANR 问题
 * 优化策略
 */
public class SPOptimize {
    publicstatic voidtryHackActivityThreadH() {
        try {
            if ((Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT)) {
                ReflectactivityThreadRef = Reflect.on(Class.forName("android.app.ActivityThread")).call("currentActivityThread");
                if (activityThreadRef != null) {
                    Handlerh = activityThreadRef.field("mH", Class.forName("android.app.ActivityThread$H")).<Handler>get();
                    if (h != null) {
                        ReflecthRef = Reflect.on(h);
                        Handler.CallbackhCallBack = hRef.field("mCallback", Handler.Callback.class).<Handler.Callback>get();
                        ActivityThreadHCallBackactivityThreadHCallBack = newActivityThreadHCallBack(h, hCallBack);
                        hRef.set("mCallback", activityThreadHCallBack);
                    }
                }
            }
        } catch (Throwablet) {
            t.printStackTrace();
            // ignore
        }
    }
}