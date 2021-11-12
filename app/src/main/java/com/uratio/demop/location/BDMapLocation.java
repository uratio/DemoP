package com.uratio.demop.location;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.uratio.demop.MyApplication;
import com.uratio.demop.utils.LogUtils;

import java.util.List;

/**
 * @author lang
 * @data 2021/9/15
 */
public class BDMapLocation extends BDAbstractLocationListener implements OnGetPoiSearchResultListener, OnGetGeoCoderResultListener {
    private final Activity activity;
    private final BDLocationListener listener;
    private final LocationClient mLocationClient;
    private PoiSearch mPoiSearch;
    private GeoCoder mCoder;

    public BDMapLocation(Activity activity, BDLocationListener listener) {
        this.activity = activity;
        this.listener = listener;
        //声明LocationClient类
        mLocationClient = new LocationClient(MyApplication.getCtx().getApplicationContext());
        //注册监听函数
        mLocationClient.registerLocationListener(this);

        mCoder = GeoCoder.newInstance();
        mCoder.setOnGetGeoCodeResultListener(this);

        initOptions();

    }

    private void initOptions() {
        LocationClientOption option = new LocationClientOption();

        //可选，设置定位模式，默认高精度
        //LocationMode.Hight_Accuracy：高精度；
        //LocationMode. Battery_Saving：低功耗；
        //LocationMode. Device_Sensors：仅使用设备；
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

        //可选，设置返回经纬度坐标类型，默认GCJ02
        //GCJ02：国测局坐标；
        //BD09ll：百度经纬度坐标；
        //BD09：百度墨卡托坐标；
        //海外地区定位，无需设置坐标类型，统一返回WGS84类型坐标
        option.setCoorType(String.valueOf(CoordType.BD09LL));

        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效
//        option.setScanSpan(1000);

        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true
        option.setOpenGps(true);

        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false
        option.setLocationNotify(true);

        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)
        option.setIgnoreKillProcess(false);

        //可选，设置是否收集Crash信息，默认收集，即参数为false
        option.SetIgnoreCacheException(false);

        //可选，V7.2版本新增能力
        //如果设置了该接口，首次启动定位时，会先判断当前Wi-Fi是否超出有效期，若超出有效期，会先重新扫描Wi-Fi，然后定位
        option.setWifiCacheTimeOut(5 * 60 * 1000);

        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false
        option.setEnableSimulateGps(false);

        //可选，是否需要地址信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的地址信息，此处必须为true
        option.setIsNeedAddress(true);

        //可选，设置是否需要最新版本的地址信息。默认需要，即参数为true
        option.setNeedNewVersionRgc(true);

        //可选，是否需要周边POI信息，默认为不需要，即参数为false
        //如果开发者需要获得周边POI信息，此处必须为true
        option.setIsNeedLocationPoiList(true);

        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
        mLocationClient.setLocOption(option);
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        if (location.getLocType() == BDLocation.TypeOffLineLocation) {
            // 离线定位成功
            LogUtils.e("baidu_location_result", "offline location success");
            if (listener != null) {
                listener.onReceiveLocation(location);
            }
        } else if (location.getLocType() == BDLocation.TypeOffLineLocationFail) {
            // 离线定位失败
            LogUtils.e("baidu_location_result", "offline location fail");
            getGPSLocation();
        } else {
            if (TextUtils.isEmpty(location.getAddrStr())) {
                getGPSLocation();
            } else {
                if (listener != null) {
                    listener.onReceiveLocation(location);
                }
            }
        }

        stopLocation();
    }

    public boolean checkLocationService() {
        /**
         * 手机是否开启位置服务，如果没有开启那么所有app将不能使用定位功能
         */
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        if (gps || network) {
//            return true;
//        }
//        return false;
    }

    /* 获取定位方法 */
    @SuppressLint("MissingPermission")
    public void getGPSLocation() {
        //1.获取系统LocationManager服务
        // 定义LocationManager对象
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);//低精度，中精度高精度获取不到location。
        criteria.setAltitudeRequired(false);//不要求海拔
        criteria.setBearingRequired(false);//不要求方位
        criteria.setCostAllowed(true);//允许有花费
        criteria.setPowerRequirement(Criteria.POWER_LOW);//低功耗

        String locationProvider = locationManager.getBestProvider(criteria, true);

        if (!TextUtils.isEmpty(locationProvider)) {
            //获取GPS最近的定位信息
            Location location = locationManager.getLastKnownLocation(locationProvider);
            if (location != null) {
                updateLocation(location);
            }
        }

        //每5秒获取一次GPS的定位信息
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //当GPS定位信息发生改变时，更新位置
                updateLocation(location);
            }

            @Override
            public void onProviderEnabled(String provider) {
                //当GPS LocationProvider可用时，更新位置
                updateLocation(null);
            }

            @Override
            public void onProviderDisabled(String provider) {
                updateLocation(null);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
        });
    }

    /**
     * 搜索周边POI信息
     */
    public void searchPOI(BDLocation location, int radius, String key, int page, boolean radiusLimit, int scope) {
        if (location == null) return;
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);

        /**
         *  PoiCiySearchOption 设置检索属性
         *  location 经纬度
         *  keyword 检索内容关键字（支持多个关键字并集检索，不同关键字间以$符号分隔，最多支持10个关键字检索。如:”银行$酒店”）
         *  pageNum	分页编号，默认返回第0页结果
         *  pageCapacity	设置每页容量，默认为10条结果
         *  tag	设置检索分类，如“美食”
         *  scope	1 或 空：返回基本信息；2：返回POI详细信息
         *  cityLimit	是否限制检索区域为城市内
         *  poiFilter	设置检索过滤条件，scope为2时有效
         */


        /**
         * 以天安门为中心，搜索半径 5公里 以内的餐厅
         */
        PoiNearbySearchOption poiNearbySearchOption = new PoiNearbySearchOption()
                .location(new LatLng(location.getLatitude(), location.getLongitude()))
                .radius(radius)
                //支持多个关键字并集检索，不同关键字间以$符号分隔，最多支持10个关键字检索。如:”银行$酒店”
                .keyword(key)
                .pageNum(page)
                .pageCapacity(20)
                .radiusLimit(radiusLimit)
                .scope(scope);
        mPoiSearch.searchNearby(poiNearbySearchOption);
    }

    /**
     * 逆地理编码（即坐标转地址）
     */
    public void getGeoCodeResult(boolean needNearData, double latitude, double longitude, int page) {
        //发起逆地理编码检索
        mCoder.reverseGeoCode(new ReverseGeoCodeOption()
                .extensionsRoad(needNearData)//是否需要坐标周围最近的道路数据
                .location(new LatLng(latitude, longitude))
                .pageNum(page)
                .pageSize(20)
                //设置是否返回新数据 默认值0不返回，1返回
                .newVersion(0)
                //POI召回半径，允许设置区间为0-1000米，超过1000米按1000米召回。默认值为1000
                .radius(5000));
    }

    public void startLocation() {
        //mLocationClient为第二步初始化过的LocationClient对象
        //调用LocationClient的start()方法，便可发起定位请求
        if (mLocationClient != null) {
            mLocationClient.start();
        }
    }

    public void stopLocation() {
        //mLocationClient为第二步初始化过的LocationClient对象
        //调用LocationClient的start()方法，便可发起定位请求
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
    }

    private void updateLocation(Location location) {
        if (listener != null) {
            BDLocation bdLocation = null;
            if (location != null) {
                bdLocation = new BDLocation();
                //初始化坐标转换工具类，指定源坐标类型和坐标数据
                CoordinateConverter converter  = new CoordinateConverter()
                        .from(CoordinateConverter.CoordType.GPS)
                        .coord(new LatLng(location.getLatitude(), location.getLongitude()));

                //desLatLng 转换后的坐标
                LatLng desLatLng = converter.convert();
                bdLocation.setLatitude(desLatLng.latitude);//经度
                bdLocation.setLongitude(desLatLng.longitude);//纬度
            }
            listener.onReceiveLocation(bdLocation);
        }
    }

    /**
     * 获取周边poi检索结果
     */
    @Override
    public void onGetPoiResult(PoiResult result) {
        if (result != null && result.error == SearchResult.ERRORNO.NO_ERROR) {
            if (listener != null) {
                listener.onGetPoiResult(result.getAllPoi());
            }
            return;
        }

        //其他情况：未找到结果
        if (listener != null) {
            listener.onGetPoiResult(null);
        }
    }

    /**
     * 返回参数已废弃，使用参数类型为 PoiDetailSearchResult 的方法
     *
     * @param poiDetailResult
     */
    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @Override
    public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

    public void onDestroy() {
        if (mPoiSearch != null) {
            mPoiSearch.destroy();
        }
        if (mCoder != null) {
            mCoder.destroy();
        }
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
            //其他情况：未找到结果
            if (listener != null) {
                listener.onGetPoiResult(null);
            }
        } else {
            if (listener != null) {
                listener.onGetPoiResult(reverseGeoCodeResult.getPoiList());
            }
        }
    }

    public interface BDLocationListener {
        void onReceiveLocation(BDLocation location);

        void onGetPoiResult(List<PoiInfo> poiInfoList);
    }
}
