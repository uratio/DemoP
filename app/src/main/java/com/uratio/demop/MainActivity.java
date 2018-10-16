package com.uratio.demop;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Icon;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Handler;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.uratio.demop.gl.OpenGLActivity;
import com.uratio.demop.phone.RecorderService;
import com.uratio.demop.refresh.RefreshActivity;
import com.uratio.demop.runnable.ThreadTestActivity;
import com.uratio.demop.scanbank.ScanIOCardActivity;
import com.uratio.demop.scanbank.ScanOpenCVActivity;
import com.uratio.demop.shortcut.TestActivity;
import com.uratio.demop.text.TextActivity;
import com.uratio.demop.viewpager.ViewPagerActivity;
import com.uratio.demop.viewstub.ViewStubActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private Handler handler = new Handler();

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Locale aDefault = Locale.getDefault();
        
        /*requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        this.getWindow().setAttributes(lp);*/

//        openFullScreenModel();
        setContentView(R.layout.activity_main);

        /*int flag = Settings.Global.getInt(getContentResolver(), Settings.Global.INSTALL_NON_MARKET_APPS, 0);
        if (flag == 0) {
            Settings.Global.putInt(getContentResolver(), Settings.Global.INSTALL_NON_MARKET_APPS, 1);
        }*/


        textView = findViewById(R.id.text);

        textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

//        getDisplayCutout();

        testMethod();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED || ContextCompat
                .checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat
                .checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO}, 1111);
        } else {
            startService();
        }

        List<ShortcutInfo> infos = new ArrayList<>();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
                ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);

                //得到，使用ShortcutInfo.Builder设置属性
                shortcutManager.getMaxShortcutCountPerActivity();
                for(int i = 0;i < shortcutManager.getMaxShortcutCountPerActivity(); i++){
                    Intent intent = new Intent(this, TestActivity.class);
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.putExtra("msg", "我和朋友" + (1+i) + "聊天");
                    ShortcutInfo info = new ShortcutInfo.Builder(this, "id" + i)
                            .setShortLabel("名"+(1+i))
                            .setLongLabel("朋友:" + (1+i))//优先选择长名称显示
                            .setIcon(Icon.createWithResource(this, R.mipmap.ic_launcher_round))
                            .setIntent(intent)
                            .build();
                    Log.i("data", "**** id=" + info.getId());
                    infos.add(info);
                }
                shortcutManager.setDynamicShortcuts(infos);

            }
        }
        NotificationListenerService service = new NotificationListenerService() {
            @Override
            public void onNotificationPosted(StatusBarNotification sbn) {
                super.onNotificationPosted(sbn);
            }

            @Override
            public void onNotificationPosted(StatusBarNotification sbn, RankingMap rankingMap) {
                super.onNotificationPosted(sbn, rankingMap);
            }

            @Override
            public void onNotificationRemoved(StatusBarNotification sbn) {
                super.onNotificationRemoved(sbn);
            }

            @Override
            public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap) {
                super.onNotificationRemoved(sbn, rankingMap);
            }

            @Override
            public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap, int reason) {
                super.onNotificationRemoved(sbn, rankingMap, reason);
            }
        };
    }

    private void testMethod() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ArrayMap<String, Integer> map = new ArrayMap<>();
            map.put("1", 172);
            map.put("3", 122);
            map.put("7", 112);
            map.put("4", 142);
            map.put("2", 162);
            map.put("g", 401);
            map.put("t", 72);
            map.put("b", 19);
            map.put("versionNumber1", 219);
            map.put("platform", 155);

            for (ArrayMap.Entry<String, Integer> entry : map.entrySet()) {
                Log.i("data", entry.getKey() + "::" + entry.getValue() + '\t');
            }
        }
        float a = 15f;//定义一个浮点变量a
        int b = (int) a % 24;
        float a1 = 26f;//定义一个浮点变量a
        int b1 = (int) a1 % 24;
        Log.i("data", "**** b=" + b + '\t' + "b1=" + b1);


        List<String> list = new ArrayList<>();
        list.add("user_id");
        list.add("version_number");
        list.add("platform");
        list.add("token");
        list.add("type");
        list.add("currentPage");
        list.add("pageSize");
        Collections.sort(list);
        Log.i("data", "**** list=" + list.toString());

        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String s = "";
            String[] cameraIdList = manager.getCameraIdList();
            for (int i = 0; i < cameraIdList.length; i++) {
                s += cameraIdList[i] + "，";

            }
            Log.i("data", "cameraIdList=" + s);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Log.i("data", "CameraAccessException=" + e.getMessage());
        }

    }

    /**
     * 启动服务
     */
    private void startService() {
        Log.i("recordPhone", "**启动服务");
        Intent intent = new Intent(MainActivity.this, RecorderService.class);
        startService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1111) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startService();
            } else {
                Toast.makeText(this, "需要同意权限后", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getDisplayCutout() {
        final View decorView = getWindow().getDecorView();
        decorView.post(new Runnable() {
            @RequiresApi(api = 28)
            @Override
            public void run() {
                DisplayCutout displayCutout = decorView.getRootWindowInsets().getDisplayCutout();

                Log.i("TAG", "安全区域距离屏幕左边的距离 SafeInsetLeft:" + displayCutout.getSafeInsetLeft());
                Log.i("TAG", "安全区域距离屏幕右部的距离 SafeInsetRight:" + displayCutout.getSafeInsetRight());
                Log.i("TAG", "安全区域距离屏幕顶部的距离 SafeInsetTop:" + displayCutout.getSafeInsetTop());
                Log.i("TAG", "安全区域距离屏幕底部的距离 SafeInsetBottom:" + displayCutout.getSafeInsetBottom());

                List<Rect> rects = displayCutout.getBoundingRects();
                if (rects == null || rects.size() == 0) {
                    Log.e("TAG", "不是刘海屏");
                } else {
                    Log.e("TAG", "刘海屏数量:" + rects.size());
                    for (Rect rect : rects) {
                        Log.e("TAG", "刘海屏区域：" + rect);
                    }
                }

               /* 作者：singwhatiwanna
                链接：https://juejin.im/post/5b1930835188257d7541ba33
                来源：掘金
                著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。*/
            }
        });
    }

    //在使用LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES的时候，状态栏会显示为白色，这和主内容区域颜色冲突,
    //所以我们要开启沉浸式布局模式，即真正的全屏模式,以实现状态和主体内容背景一致
    public void openFullScreenModel() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        this.getWindow().setAttributes(lp);
        View decorView = this.getWindow().getDecorView();
        int systemUiVisibility = decorView.getSystemUiVisibility();
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        systemUiVisibility |= flags;
        this.getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        Log.i("data", "**** return=" + super.onKeyDown(keyCode, event));
        return super.onKeyDown(keyCode, event);
    }

    public void clickView(View view) {
        switch (view.getId()){
            case R.id.to_ViewPager:
                startActivity(new Intent(MainActivity.this, ViewPagerActivity.class));
                break;
            case R.id.to_ViewStub:
                startActivity(new Intent(MainActivity.this, ViewStubActivity.class));
                break;
            case R.id.to_refresh:
                startActivity(new Intent(MainActivity.this, RefreshActivity.class));
                break;
            case R.id.to_IOCard:
                startActivity(new Intent(MainActivity.this, ScanIOCardActivity.class));
                break;
            case R.id.to_OpenCV:
                startActivity(new Intent(MainActivity.this, ScanOpenCVActivity.class));
                break;
            case R.id.to_text:
                startActivity(new Intent(MainActivity.this, TextActivity.class));
                break;
            case R.id.to_openGL:
                startActivity(new Intent(MainActivity.this, OpenGLActivity.class));
                break;
            case R.id.to_thread:
                startActivity(new Intent(MainActivity.this, ThreadTestActivity.class));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    textView.setText("我是子线程中的view");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("Main_Runnable", "run: "+e.getMessage());
                }
            }
        }).start();

    }
}
