package com.tianfangIMS.im.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.squareup.picasso.Picasso;
import com.tianfangIMS.im.ConstantValue;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.adapter.AMapShareAdapter;
import com.tianfangIMS.im.adapter.UserPhotoAdatper;
import com.tianfangIMS.im.bean.LocationBean;
import com.tianfangIMS.im.dialog.ShareLoactionDialog;
import com.tianfangIMS.im.utils.CommonUtil;
import com.tianfangIMS.im.utils.NToast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.rong.imageloader.core.DisplayImageOptions;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by LianMengYu on 2017/2/17.
 * 1，进入位置共享，启动gps 搜索，如果检索到坐标位置，并且和上次时间间隔>30s，提交我的位置。
 * 2 ，进入共享地图后，先获取所有人的位置坐标，如果位置坐标有，并且有效，就把这人加入到共享
 * 如果坐标无效（90，180）或者没有，就把这人退出共享. 启动timer，每30s更新一次
 * 3，如果我退出共享，终止gps，并且向服务器提交无效坐标lat=90, long = 180
 */
public class AMapShareActivity extends BaseActivity implements LocationSource, AMapLocationListener, AMap.OnMarkerClickListener, ShareLoactionDialog.DialogItemClickListener {
    MapView mMapView = null;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
//    public AMapLocationListener mLocationListener;
    public AMap aMap = null;
    public OnLocationChangedListener mlistener = null;
    private Context mContext;
    private UserPhotoAdatper adatper;
    private Handler mHandler;
    private Timer timer = new Timer();
    private Handler mHandler2;
    private Timer timer2 = new Timer();
    private TimerTask task2;
    private TimerTask task;
    private String userID;
    private Conversation.ConversationType mConversationType;
    private String mTargetId;
    private String Title;
    private GridView gv_AMapUser;
    private TextView tv_AMapuserNumber;
    private DisplayImageOptions options;// 展示图片的工具
    private List<LocationBean> locationBeanList;//所有群组的数据
    private List<LocationBean> LocationData = new ArrayList<>();//过滤有效位置之后的数据
    private RelativeLayout rl_gridView;
    private GridView gv_sharelocationForDialog;
    Double latitude;
    Double longitude;
    int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.amapshare_layout);
        //这里以ACCESS_COARSE_LOCATION为例
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(AMapShareActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, 1);//自定义的code
        }
        //获取地图控件引用
        init();
        mContext = this;
        if (CommonUtil.isOPen(mContext) == false) {
            CommonUtil.openGPS(mContext);
        }
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        aMap = mMapView.getMap();
        aMap.setLocationSource(this);
        initLocation();
        //接收消息类型
        mConversationType = (Conversation.ConversationType) getIntent().getSerializableExtra("conversationType");
        mTargetId = (String) getIntent().getSerializableExtra("mTargetId");
        Title = getIntent().getStringExtra("title");
        setTitle(Title);
        mHandler2 = new Handler() {
            @Override
            public void handleMessage(Message msg1) {
                super.handleMessage(msg1);
                switch (msg1.what) {
                    case 1:
                        GetUsreLocation();
                        break;
                }
            }
        };
        task2 = new TimerTask() {
            @Override
            public void run() {
                Message msg1 = new Message();
                msg1.what = 1;
                mHandler2.sendMessage(msg1);
            }
        };
        if (timer2 != null) {
            timer2.schedule(task2, 0, 10000);
        }
    }

    private void init() {
        mMapView = (MapView) findViewById(R.id.map);
        gv_AMapUser = (GridView) this.findViewById(R.id.gv_AMapUser);
        tv_AMapuserNumber = (TextView) this.findViewById(R.id.tv_AMapuserNumber);
        rl_gridView = (RelativeLayout) this.findViewById(R.id.rl_gridView);
        gv_sharelocationForDialog = (GridView) this.findViewById(R.id.gv_sharelocation);
    }

    //对GridView 显示的宽高经行设置
    private void SettingGridView(List<LocationBean> list) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int size = list.size();//要显示数据的个数
        //gridview的layout_widht,要比每个item的宽度多出2个像素，解决不能完全显示item的问题
        int allWidth = (int) (82 * size * density);
        int allheight = (int) (82 * size * density);
        //int allWidth = (int) ((width / 3 ) * size + (size-1)*3);//也可以这样使用，item的总的width加上horizontalspacing
        int itemWidth = (int) (65 * density);//每个item宽度
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(allWidth, allheight);
        gv_AMapUser.setLayoutParams(params1);
        gv_AMapUser.setColumnWidth(itemWidth);
        gv_AMapUser.setHorizontalSpacing(3);
        gv_AMapUser.setStretchMode(GridView.NO_STRETCH);
        gv_AMapUser.setNumColumns(size);
    }

    private void GetUsreLocation() {
        userID = RongIMClient.getInstance().getCurrentUserId();
        if (mConversationType.equals(Conversation.ConversationType.PRIVATE)) {
            type = 2;
        } else if (mConversationType.equals(Conversation.ConversationType.GROUP)) {
            type = 1;
        }
        OkGo.post(ConstantValue.GETLOCATION)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .params("userid", userID)
                .params("targetid", mTargetId)
                .params("type", type)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.e("mConversationtype", "参数值:" + type + "---useriD:" + userID + "---mtatgetid：" + mTargetId);
                        Log.e("mConversationtype", "返回地址:" + s);
                        if (!TextUtils.isEmpty(s) && !s.equals("{}")) {
                            Gson gson = new Gson();
                            Map<String, Object> locationInfoMap = gson.fromJson(s, new TypeToken<Map<String, Object>>() {
                            }.getType());
                            if ((double) locationInfoMap.get("code") == 0.0) {
                                NToast.shortToast(mContext, "没有获取好友位置信息");
                            }
                            if ((double) locationInfoMap.get("code") == 1.0) {
                                Gson gson1 = new Gson();
                                String s1 = locationInfoMap.get("text").toString();
                                Log.e("code是多少：", ":" + s1);
                                locationBeanList = gson1.fromJson(s1, new TypeToken<List<LocationBean>>() {
                                }.getType());
                                Log.e("LocationData：", "locationBeanList:" + locationBeanList);
                                for (int i = 0; i < locationBeanList.size(); i++) {
                                    if (CommonUtil.StringToDouble(locationBeanList.get(i).getLatitude()) != 90.0 &&
                                            CommonUtil.StringToDouble(locationBeanList.get(i).getLongtitude()) != 180.0) {
                                        if (!LocationData.contains(locationBeanList.get(i))) {
                                            LocationData.add(locationBeanList.get(i));
                                        }
                                    }
                                }
                                tv_AMapuserNumber.setText("共" + LocationData.size() + "人在共享位置");
                                AMapShareAdapter adapter = new AMapShareAdapter(LocationData, mContext);
                                SettingGridView(LocationData);
                                gv_AMapUser.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                for (int i = 0; i < LocationData.size(); i++) {
                                    DrawBlueLite(CommonUtil.StringToDouble(LocationData.get(i).getLatitude()),
                                            CommonUtil.StringToDouble(LocationData.get(i).getLongtitude()),
                                            LocationData.get(i).getUserID(),
                                            LocationData.get(i).getLogo());
                                }
                            }
                        }
                    }
                });
    }

    //绘制坐标原点
    private void DrawBlueLite(Double latitude, Double longitude, String userID, String url) {
        View iconView = LayoutInflater.from(mContext).inflate(R.layout.view_infowindow, null);
        ImageView iv_amap_photo = (ImageView) iconView.findViewById(R.id.iv_amap_photo);
        ImageView locImageView = (ImageView) iconView.findViewById(R.id.iv_amap_mark);
        if (userID.equals(RongIMClient.getInstance().getCurrentUserId() + ".0")) {
            locImageView.setImageResource(R.mipmap.amap_location_blue);//
        } else {
            locImageView.setImageResource(R.mipmap.amap_location_other);
        }
        if (!TextUtils.isEmpty(url)) {
            String image = ConstantValue.ImageFile + url;
            Picasso.with(mContext)
                    .load(image)
                    .resize(50, 50)
                    .placeholder(R.mipmap.default_portrait)
                    .config(Bitmap.Config.ARGB_8888)
                    .error(R.mipmap.default_portrait)
                    .into(iv_amap_photo);
//            Bitmap bm = ImageLoader.getInstance().loadImageSync(image);
//            if (bm != null) {
//                iv_amap_photo.setImageBitmap(bm);
//            }
        }
        //设置中心点和缩放比例
        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng);
        markerOption.draggable(true);//设置Marker可拖动
        markerOption.icon(BitmapDescriptorFactory.fromView(iconView));
//        CircleOptions circleOptions = new CircleOptions();
//        circleOptions.center(latLng);
//        circleOptions.radius(50);
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        aMap.addMarker(markerOption);
//        aMap.addCircle(circleOptions);
//        marker.setPositionByPixels(mMapView.getWidth() / 2 - 50, mMapView.getHeight() / 2 + 100);
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
        Log.e("huoquzuobiaohyuandian", "---:" + latLng);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18));
//        adatper = new UserPhotoAdatper(mContext,latLng,"");
        aMap.setOnMarkerClickListener(this);
    }
    //根据坐标定位，并且缩放显示的位置
    private void Locationforlatlng(LatLng latLng) {
//        MarkerOptions markerOption = new MarkerOptions();
//        markerOption.position(latLng);
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18));
    }
    /**
     * 初始化定位
     */
    private void initLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);

        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。
        // 如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(2000);
        mLocationClient.setLocationOption(mLocationOption);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否强制刷新WIFI，默认为true，强制刷新。
        mLocationOption.setWifiActiveScan(false);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(30000);
        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);
        StartLocation();
    }

    /**
     * 开始定位
     */
    private void StartLocation() {
        //给定位客户端对象设置定位参数0
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    //获取定位的经纬度
    AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    latitude = aMapLocation.getLatitude();
                    longitude = aMapLocation.getLongitude();
                    PushlatLng(latitude.toString(), longitude.toString());
                    Log.e("AmapOnSuccess", "latitude:"
                            + latitude + ", longitude:"
                            + longitude);
//                    LatLng latLng = new LatLng(latitude, longitude);
//                    for (int i = 0; i < locationBeanList.size(); i++) {
//                        if (CommonUtil.StringToDouble(locationBeanList.get(i).getLatitude()) != 90.0 &&
//                                CommonUtil.StringToDouble(locationBeanList.get(i).getLongtitude()) != 180.0) {
//                            DrawBlueLite(latitude,
//                                    longitude,
//                                    locationBeanList.get(i).getUserID(),
//                                    locationBeanList.get(i).getLogo());
//                        }
//
//                    }

                    mHandler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            switch (msg.what) {
                                case 1:
                                    PushlatLng(latitude.toString(), longitude.toString());
                                    break;
                            }
                        }
                    };
                    task = new TimerTask() {
                        @Override
                        public void run() {
                            Message msg = new Message();
                            msg.what = 1;
                            mHandler.sendMessage(msg);
                        }
                    };
                    if (timer != null) {
                        timer.schedule(task, 0, 30000);
                    }
                }

                if (aMapLocation.getErrorCode() == 12) {
                    NToast.longToast(mContext, "请开启手机APP定位权限");
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }
        }
    };

    //提交经纬度
    private void PushlatLng(String latitude, String longtitude) {
        userID = RongIMClient.getInstance().getCurrentUserId();
        Log.e("计时器打印：", "精度：" + latitude + "--纬度" + longtitude + "---userID:" + userID);
        OkGo.post(ConstantValue.SUBLOCATION)
                .tag(this)
                .params("userid", userID)
                .params("latitude", latitude)
                .params("longitude", longtitude)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.e("resultCode", "---:" + s);
                    }
                });
    }

    //在退出程序时，将经纬度设置为无效
    private void PushlatLngIsNull() {
        userID = RongIMClient.getInstance().getCurrentUserId();
        OkGo.post(ConstantValue.SUBLOCATION)
                .tag(this)
                .params("userid", userID)
                .params("latitude", 90)
                .params("longitude", 180)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.e("isresultCode", "---:" + s);
                    }
                });
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
//        if (aMapLocation != null) {
//            if (aMapLocation.getErrorCode() == 0) {
//                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
//                aMapLocation.getLatitude();//获取纬度
//                aMapLocation.getLongitude();//获取经度
//                aMapLocation.getAccuracy();//获取精度信息
//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date date = new Date(aMapLocation.getTime());
//                df.format(date);//定位时间
//                Log.e("AmapSuccess", "获取定位来源：" + aMapLocation.getLocationType() + "-获取纬度：" + aMapLocation.getLatitude() + "---获取经度:" + aMapLocation.getLongitude()
//                        + "--/获取精度信息：" + aMapLocation.getAccuracy() + "----获取定位时间：" + df.format(date));
//            } else {
//                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
//                Log.e("AmapError", "location Error, ErrCode:"
//                        + aMapLocation.getErrorCode() + ", errInfo:"
//                        + aMapLocation.getErrorInfo());
//            }
//        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mlistener = onLocationChangedListener;
        if(mLocationClient != null){

        }
    }

    @Override
    public void deactivate() {
        mlistener = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        timer.cancel();//取消任务
        PushlatLngIsNull();//取消30s提交位置，同时提交无聊位置，退出位置共享
        mLocationClient = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 初始化方向传感器,待做
     */
//    private SensorManager mSensorManager;
//    private Sensor mSensor;
//    private void initSensor(){
//        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        aSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
////        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);陀螺仪
//        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.SENSOR_DELAY_GAME),
//                SensorManager.SENSOR_DELAY_GAME);
//    }
    public static String sHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length() - 1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ShareLoactionDialog shareLoactiondialog;

    @Override
    public boolean onMarkerClick(Marker marker) {
        shareLoactiondialog = new ShareLoactionDialog(mContext, LocationData, R.style.dialog, marker, this);
        shareLoactiondialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        shareLoactiondialog.show();
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id, Marker marker) {
        Log.e("GridViewOnItem", "---LocationData:" + LocationData);
        LatLng latLng = new LatLng(CommonUtil.StringToDouble(LocationData.get(position).getLatitude()),
                CommonUtil.StringToDouble(LocationData.get(position).getLongtitude().toString()));
        Log.e("GridViewOnItem", "----:" + latLng);
        Locationforlatlng(latLng);
        shareLoactiondialog.dismiss();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //可在此继续其他操作。
    }
}
