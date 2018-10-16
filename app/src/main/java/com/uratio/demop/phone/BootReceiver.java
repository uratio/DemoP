package com.uratio.demop.phone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Easzz on 2015/12/6.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i=new Intent(context,RecorderService.class);
        //启动服务不需要到栈顶 ，因为没有前台界面。但是开机启动一个活动需要一个flag
        //i.setFlag(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(i);
    }
}