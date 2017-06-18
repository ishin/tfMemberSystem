package com.tianfangIMS.im.utils;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

/**
 * User: LJM
 * Date&Time: 2016-08-17 & 22:36
 * Describe: 获取经纬度工具类
 * <p>
 * 需要权限
 * <!--用于进行网络定位-->
 * <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"></uses-permission>
 * <!--用于访问GPS定位-->
 * <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"></uses-permission>
 * <!--获取运营商信息，用于支持提供运营商信息相关的接口-->
 * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission>
 * <!--用于访问wifi网络信息，wifi信息会用于进行网络定位-->
 * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"></uses-permission>
 * <!--这个权限用于获取wifi的获取权限，wifi信息会用来进行网络定位-->
 * <uses-permission android:name="android.permission.CHANGE_WIFI_STATE"></uses-permission>
 * <!--用于访问网络，网络定位需要上网-->
 * <uses-permission android:name="android.permission.INTERNET"></uses-permission>
 * <!--用于读取手机当前的状态-->
 * <uses-permission android:name="android.permission.READ_PHONE_STATE"></uses-permission>
 * <!--写入扩展存储，向扩展卡写入数据，用于写入缓存定位数据-->
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"></uses-permission>
 * 需要在application 配置的mate-data 和sevice
 * <service android:name="com.amap.api.include_search_toolbar.APSService" >
 * </service>
 * <meta-data
 * android:name="com.amap.api.v2.apikey"
 * android:value="key"/>
 * 另外，还需要一个key xxx.jks
 */
public class AMapLocUtils implements AMapLocationListener {

    private static AMapLocUtils instance;
    private AMapLocationClient locationClient = null;  // 定位
    private AMapLocationClientOption locationOption = null;  // 定位设置

    public AMapLocationClient getLocationClient() {
        return locationClient;
    }

    public void setLocationClient(AMapLocationClient locationClient) {
        this.locationClient = locationClient;
    }

    public static AMapLocUtils getInstance() {
        if (instance == null) {
            instance = new AMapLocUtils();
        }
        return instance;
    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        mLonLatListener.getLonLat(aMapLocation);

    }

    private LonLatListener mLonLatListener;

    public void setLocationListener(Context context, LonLatListener lonLatListener) {

        //初始化定位
        locationClient = new AMapLocationClient(context);
        //初始化定位参数
        locationOption = new AMapLocationClientOption();
        //设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位回调监听，这里要实现AMapLocationListener接口，AMapLocationListener接口只有onLocationChanged方法可以实现，用于接收异步返回的定位结果，参数是AMapLocation类型。
        locationClient.setLocationListener(this);
        locationOption.setOnceLocationLatest(true);
        //设置是否只定位一次,默认为false
        locationOption.setOnceLocation(true); // 单次定位
        locationOption.setNeedAddress(true);//逆地理编码
        //定位刷新时长
        locationOption.setInterval(30000);
        //设置是否强制刷新WIFI，默认为强制刷新
        locationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        locationOption.setMockEnable(false);
        locationOption.setLocationCacheEnable(false);
        mLonLatListener = lonLatListener;//接口
        locationClient.setLocationOption(locationOption);// 设置定位参数


    }


    public interface LonLatListener {
        void getLonLat(AMapLocation aMapLocation);
    }
}