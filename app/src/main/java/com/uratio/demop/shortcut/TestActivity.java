package com.uratio.demop.shortcut;

import android.content.Intent;
import android.content.pm.ShortcutManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.uratio.demop.MainActivity;
import com.uratio.demop.R;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        handler.sendEmptyMessage(0);

        List<String> ids = new ArrayList<>();
        ids.add("id0");
        ids.add("id1");
        ids.add("id2");
        ids.add("id3");
        ids.add("id4");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
                ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
//                shortcutManager.removeDynamicShortcuts(ids);
//                shortcutManager.disableShortcuts(ids);
            }
        }

        Intent remove = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
        remove.putExtra(Intent.EXTRA_SHORTCUT_NAME, "shortcut");
        Intent action2 = new Intent(this, MainActivity.class);
        //重点
        action2.setAction(Intent .ACTION_VIEW);
        remove.putExtra(Intent.EXTRA_SHORTCUT_INTENT, action2);
        sendBroadcast(remove);
    }
}
