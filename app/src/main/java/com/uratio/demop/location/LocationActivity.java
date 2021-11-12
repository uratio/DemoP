package com.uratio.demop.location;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
import com.baidu.mapapi.search.core.PoiInfo;
import com.uratio.demop.R;
import com.uratio.demop.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class LocationActivity extends AppCompatActivity implements BDMapLocation.BDLocationListener {
    private TextView tvLocation;
    private TextView tvPOI;
    private TextView tvPOITitle;
    private EditText etInput;
    private EditText etRadius;
    private EditText etLatitude;
    private EditText etLongitude;
    private CheckBox cb_need_near_data;
    private CheckBox cbLimit;
    private CheckBox cbScope;

    private BDMapLocation bdMapLocation;
    private BDLocation location;
    private int page = 0;
    private boolean radiusLimit;
    private int scope = 1;
    private boolean needNearData = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        tvLocation = findViewById(R.id.tv_location);
        tvPOI = findViewById(R.id.tv_poi);
        tvPOITitle = findViewById(R.id.tv_poi_title);
        etInput = findViewById(R.id.et_input);
        etRadius = findViewById(R.id.et_radius);
        etLatitude = findViewById(R.id.et_latitude);
        etLongitude = findViewById(R.id.et_longitude);
        cb_need_near_data = findViewById(R.id.cb_need_near_data);
        cbLimit = findViewById(R.id.cb_limit);
        cbScope = findViewById(R.id.cb_scope);

        bdMapLocation = new BDMapLocation(this, this);

        cb_need_near_data.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                needNearData = b;
            }
        });
        cbLimit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                radiusLimit = b;
            }
        });
        cbScope.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                scope = b ? 2 : 1;
            }
        });

        getPersimmions();
    }

    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            /*
             * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
             */
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), 10001);
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(permission)) {
                return true;
            } else {
                permissionsList.add(permission);
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        LogUtils.e("onReceiveLocation  *****  location=" + location);
        if (location == null) return;

        this.location = location;

        // 显示当前信息
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("经度：" + location.getLatitude());
        stringBuilder.append("\n纬度："+ location.getLongitude());
        stringBuilder.append("\n状态码："+ location.getLocType());
        stringBuilder.append("\n国家：" + location.getCountry());
        stringBuilder.append("\n城市："+ location.getCity());
        stringBuilder.append("\n区：" + location.getDistrict());
        stringBuilder.append("\n街道：" + location.getStreet());
        stringBuilder.append("\n地址：" + location.getAddrStr());
        stringBuilder.append("\n描述：" + location.getLocationDescribe());

        tvLocation.setText(stringBuilder.toString());


        StringBuilder sbPoi = new StringBuilder();
        List<Poi> poiList = location.getPoiList();
        if (poiList != null && poiList.size() > 0) {
            for (int i = 0; i <poiList.size(); i++) {
                Poi poi = poiList.get(i);

                String poiName = poi.getName();    //获取POI名称
                String poiTags = poi.getTags();    //获取POI类型
                String poiAddr = poi.getAddr();    //获取POI地址 //获取周边POI信息

                sbPoi.append("\n名称："+poiName+"  类型:"+poiTags+"  周边POI信息:"+poiAddr);
            }
        }
        sbPoi.append("————————————————————————————");
    }

    @Override
    public void onGetPoiResult(List<PoiInfo> poiInfoList) {
        if (poiInfoList == null || poiInfoList.size() == 0) {
            tvPOI.setText("未找到结果");
            return;
        }
        StringBuilder sbPoi = new StringBuilder();
        if (poiInfoList.size() > 0) {
            tvPOITitle.setText("获取周围POI信息结果：第" + page + "页，共" + poiInfoList.size() + "条");
            for (int i = 0; i <poiInfoList.size(); i++) {
                PoiInfo poiInfo = poiInfoList.get(i);

                String poiName = poiInfo.getName();    //获取POI名称
                String poiTags = poiInfo.getTag();    //获取POI类型
                String address = poiInfo.getAddress();    //获取POI地址 //获取周边POI信息




                sbPoi.append("\n名称：" + poiName);
                sbPoi.append("\n经度：" + poiInfo.getLocation().latitude);
                sbPoi.append("\n纬度：" + poiInfo.getLocation().longitude);
                sbPoi.append("\n详细地址：" + address);
                sbPoi.append("\n——————— 第" + (i+1) + "条 ———————");
            }
        }

        tvPOI.setText(sbPoi.toString());
    }

    public void onClickView(View view) {
        int radius = Integer.parseInt(etRadius.getText().toString());
        switch (view.getId()) {
            case R.id.btn_start_location:
                bdMapLocation.startLocation();
                break;
            case R.id.btn_stop_location:
                bdMapLocation.stopLocation();
                tvLocation.setText("");
                break;
            case R.id.btn_gps_location:
                bdMapLocation.getGPSLocation();
                break;
            case R.id.btn_cur_page:
                page--;
                if (page < 0) page = 0;
                break;
            case R.id.btn_next_page:
                page++;
                break;
            case R.id.btn_search:
                if (location == null) {
                    Toast.makeText(LocationActivity.this, "先进行定位", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etInput.getText().toString())) {
                    Toast.makeText(LocationActivity.this, "先输入搜索名称", Toast.LENGTH_SHORT).show();
                    return;
                }
                bdMapLocation.searchPOI(location, radius, etInput.getText().toString(), page, radiusLimit, scope);
                break;
            case R.id.btn_search2:
                if (TextUtils.isEmpty(etInput.getText().toString())) {
                    Toast.makeText(LocationActivity.this, "先输入搜索名称", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etLatitude.getText().toString()) || TextUtils.isEmpty(etLongitude.getText().toString())) {
                    Toast.makeText(LocationActivity.this, "先输入经纬度", Toast.LENGTH_SHORT).show();
                    return;
                }
                BDLocation bdLocation = new BDLocation();
                bdLocation.setLatitude(Double.parseDouble(etLatitude.getText().toString()));
                bdLocation.setLongitude(Double.parseDouble(etLongitude.getText().toString()));
                bdMapLocation.searchPOI(bdLocation, radius, etInput.getText().toString(), page, radiusLimit, scope);
                break;
            case R.id.btn_bd_api:
                try {
                    Intent i1 = new Intent();
                    // 展示地图
                    i1.setData(Uri.parse("baidumap://map/show?center=40.057406655722,116.29644071728&zoom=11&traffic=on&bounds=37.8608310000,112.5963090000,42.1942670000,118.9491260000&src=andr.baidu.openAPIdemo"));
                    startActivity(i1);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_clear_location:
                tvLocation.setText("");
                break;
            case R.id.btn_check_service:
                boolean checkLocationService = bdMapLocation.checkLocationService();
                Toast.makeText(LocationActivity.this, checkLocationService ? "已开启GPS位置服务" : "未开启GPS位置服务",
                        Toast.LENGTH_SHORT).show();
                if (!checkLocationService) {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                    if (intent.resolveActivity(getPackageManager()) != null) {
//                        startActivityForResult(intent, 1000);
//                    } else {
//                        Toast.makeText(this, "该设备不支持位置服务", Toast.LENGTH_SHORT).show();
//                    }
                }
                break;
            case R.id.btn_geocode_location:
                if (location == null) {
                    Toast.makeText(LocationActivity.this, "先进行定位", Toast.LENGTH_SHORT).show();
                    return;
                }
                bdMapLocation.getGeoCodeResult(needNearData, location.getLatitude(), location.getLongitude(), page);
                break;
            case R.id.btn_geocode:
                bdMapLocation.getGeoCodeResult(needNearData, Double.parseDouble(etLatitude.getText().toString()),
                        Double.parseDouble(etLongitude.getText().toString()), page);
                break;
        }
    }
}