package com.tianfangIMS.im.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.squareup.picasso.Picasso;
import com.tianfangIMS.im.ConstantValue;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.adapter.HorizontalPageLayoutManager;
import com.tianfangIMS.im.adapter.ItemDefAdapter;
import com.tianfangIMS.im.adapter.ItemDefTextAdapter;
import com.tianfangIMS.im.bean.LocationBean;
import com.tianfangIMS.im.utils.AMapLocUtils;
import com.tianfangIMS.im.utils.JsonUtil;
import com.tianfangIMS.im.utils.NToast;
import com.tianfangIMS.im.utils.PagingScrollHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import okhttp3.Call;
import okhttp3.Response;


public class AMapShareLocationActivity extends BaseActivity implements AMap.OnMarkerClickListener {
    private RecyclerView rv_list;
    private ItemDefAdapter itemDefAdapter;
    private List<Map<String, Object>> mData;
    private List<Map<String, Object>> mDatalist;
    private TextView tv_title;
    private MapView mapView;
    private AMap aMap;
    private boolean isFirstLoc = true;
    private LatLng location;

    private UiSettings mUiSettings;
    private MarkerOptions markerOption;
    String sessionId;
    private Conversation.ConversationType mConversationType;
    private String mTargetId;
    int type = 0;
    private List<LocationBean> locationBeanList;//所有群组的数据
    private List<LatLng> latLngs;
    private List<String> url;
    private List<String> uids;
    private String title;
    private Handler mHandler;
    private Timer timer = new Timer();
    private TimerTask task;
    Double latitude;
    Double longitude;
    List<LatLng> lisths;
    HashSet<LatLng> hs;
    List<Marker> mMarker = new ArrayList<>();
    private ImageButton btn_left_back;
    private AlertDialog simpledialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.amapshare_layout);
        sessionId = getSharedPreferences("CompanyCode", MODE_PRIVATE).getString("CompanyCode", "");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(AMapShareLocationActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, 1);//自定义的code
        }
        btn_left_back = getHeadLeftButton();
        btn_left_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishDialog();
            }
        });
        //接收消息类型
        mConversationType = (Conversation.ConversationType) getIntent().getSerializableExtra("conversationType");
        mTargetId = (String) getIntent().getSerializableExtra("mTargetId");
        title = getIntent().getStringExtra("title");
        initData();
        initViews();
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        initEvents();
    }

    /**
     * 初始化视图
     */
    private void initViews() {
        rv_list = (RecyclerView) findViewById(R.id.rv_list);
        mapView = (MapView) findViewById(R.id.map);
        initMap();
        rv_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        itemDefAdapter = new ItemDefAdapter(this, null);
        rv_list.setAdapter(itemDefAdapter);
    }


    /**
     * 初始化AMap对象
     */
    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
            mUiSettings = aMap.getUiSettings();
            // 自定义系统定位小蓝点
            MyLocationStyle myLocationStyle = new MyLocationStyle();
            myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                    .fromResource(R.drawable.amap_location_blue));// 设置小蓝点的图标
            myLocationStyle.strokeColor(Color.argb(0, 0, 0, 180));// 设置圆形的边框颜色
            myLocationStyle.radiusFillColor(Color.argb(180, 3, 145, 255));// 设置圆形的填充颜色
            myLocationStyle.strokeWidth(1f);// 设置圆形的边框粗细
            aMap.setMyLocationStyle(myLocationStyle);
            aMap.setMapType(AMap.MAP_TYPE_NORMAL);
            mUiSettings.setScaleControlsEnabled(true);// 设置地图默认的比例尺是否显示
            aMap.setOnMarkerClickListener(this);
            AMapLocUtils.getInstance().setLocationListener(this, new AMapLocUtils.LonLatListener() {
                @Override
                public void getLonLat(AMapLocation aMapLocation) {
                    Log.e("asdasd1e","asd111---:"+aMapLocation.getAccuracy());
                    if (aMapLocation != null) {
                        if (aMapLocation.getErrorCode() == 0) {
                            //定位成功回调信息，设置相关消息
                            aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
                            //获取纬度
                            //获取经度
                            location = new LatLng(aMapLocation.getLatitude(),
                                    aMapLocation.getLongitude());
                            latitude = aMapLocation.getLatitude();
                            longitude = aMapLocation.getLongitude();
                            subLocation(latitude + "", longitude + "");
                            getLocation();
                            if (isFirstLoc) {
                                //设置缩放级别
                                if (latitude != 90.0 && longitude != 180.0) {
                                    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15), 1000, null);
                                    isFirstLoc = false;
                                }
                            }
//                            定位完成后会回调，每隔30秒上传一次位置信息
//                            这个就是定位的回调   应该是你后台没上传成功
                        } else {
                            //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                            Log.e("AmapError", "location Error, ErrCode:"
                                    + aMapLocation.getErrorCode() + ", errInfo:"
                                    + aMapLocation.getErrorInfo());
                            if (aMapLocation.getErrorCode() == 12) {
                                NToast.shortToast(mContext, "请开启手机的定位权限");
                            } else if (aMapLocation.getErrorCode() == 7) {
                                NToast.shortToast(mContext, "KEY鉴权失败，请重新绑定");
                            } else if (aMapLocation.getErrorCode() == 9) {
                                NToast.shortToast(mContext, "初始化时出现异常，请重新启动定位");
                            } else if (aMapLocation.getErrorCode() == 11) {
                                NToast.shortToast(mContext, "定位时的基站信息错误");
                            } else if (aMapLocation.getErrorCode() == 13) {
                                NToast.shortToast(mContext, "定位失败，由于未获得WIFI列表和基站信息，且GPS当前不可用");
                            } else if (aMapLocation.getErrorCode() == 14) {
                                NToast.shortToast(mContext, "定位失败，设备当前GPS信号弱");
                            } else if (aMapLocation.getErrorCode() == 2) {
                                NToast.shortToast(mContext, "定位失败，请重新尝试");
                            } else {
                                Toast.makeText(getApplicationContext(), "定位失败," + aMapLocation.getErrorCode(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            });
            AMapLocUtils.getInstance().getLocationClient().startLocation();
            //每30秒走一次
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case 1:
                            subLocation(latitude + "", longitude + "");
                            getLocation();
                            AMapLocUtils.getInstance().getLocationClient().startLocation();
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

    }

    /**
     * 初始化事件
     */
    private void initEvents() {
        rv_list.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                showCommPopMenu();
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mData = new ArrayList<>();
        latLngs = new ArrayList<LatLng>();
        uids = new ArrayList<>();
        url = new ArrayList<>();
//        getLocation();
    }

    /**
     * 这个接口数据返回有问题
     * <p>
     * 缺少用户的昵称
     *
     * @param
     * @param
     * @param
     */
    private void getLocation() throws IndexOutOfBoundsException {
        String userID = RongIMClient.getInstance().getCurrentUserId();
        if (mConversationType.equals(Conversation.ConversationType.PRIVATE)) {
            type = 2;
        } else if (mConversationType.equals(Conversation.ConversationType.GROUP)) {
            type = 1;
        }
        OkGo.post(ConstantValue.GETLOCATION)
                .tag(this)
                .headers("cookie", sessionId)
                .params("userid", userID)
                .params("targetid", mTargetId)
                .params("type", type)
                .execute(new StringCallback() {
                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                    }

                    @Override
                    public void onSuccess(String json, Call call, Response response) {
                        mData.clear();
                        latLngs.clear();
                        uids.clear();
                        url.clear();
                        mDatalist = new ArrayList<Map<String, Object>>();
                        if (!TextUtils.isEmpty(json) && !json.equals("{}")) {
                            if ((json.trim()).startsWith("<!DOCTYPE")) {
                                NToast.shortToast(mContext, "Session过期，请重新登陆");
                                startActivity(new Intent(mContext, LoginActivity.class));
                                RongIM.getInstance().logout();
                                finish();
                            } else {
                                Gson gson = new Gson();
                                Map<String, Object> locationInfoMap = gson.fromJson(json, new TypeToken<Map<String, Object>>() {
                                }.getType());
                                if ((double) locationInfoMap.get("code") == 0.0) {
                                    NToast.shortToast(mContext, "没有获取好友位置信息");
                                }
                                if ((double) locationInfoMap.get("code") == 1.0) {
                                    String jsonstr = locationInfoMap.get("text").toString();
                                    mData.addAll(JsonUtil.listKeyMaps(jsonstr));
                                }
                                //根据Data数据循环出经纬度  下面是循环体
                                for (int i = 0; i < mData.size(); i++) {
                                    double latitude;
                                    double longtitude;
                                    if (mData.get(i).get("latitude") == null && mData.get(i).get("longtitude") == null) {
                                        latitude = 90.0;
                                        longtitude = 180.0;
                                    } else {
                                        latitude = (Double) mData.get(i).get("latitude");
                                        longtitude = (Double) mData.get(i).get("longtitude");
                                    }
                                    if (latitude != 90.0 && longtitude != 180.0) {
                                        latLngs.add(new LatLng(latitude, longtitude));
                                        url.add(mData.get(i).get("logo").toString());
                                        uids.add(mData.get(i).get("userID").toString());
                                        mDatalist.add(mData.get(i));
                                        itemDefAdapter.setNewData(mDatalist);
                                        setTitle(title + "(" + mDatalist.size() + ")");
                                        Log.e("onSuccess", "onSuccess: " + JsonUtil.toJson(latLngs));
                                    }
                                }
                                addMarkersToMaps(latLngs, url, uids);
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        Toast.makeText(AMapShareLocationActivity.this, "连接服务器异常", Toast.LENGTH_LONG).show();
                        super.onError(call, response, e);
                    }
                });
    }


    /**
     * @param latitude
     * @param longtitude
     */

    private void subLocation(String latitude, String longtitude) {
        int mineId = Integer.parseInt(RongIMClient.getInstance().getCurrentUserId());
        OkGo.post(ConstantValue.SUBLOCATION)
                .tag(this)
                .headers("cookie", sessionId)
                .params("userid", mineId)
                .params("latitude", latitude)
                .params("longitude", longtitude)
                .execute(new StringCallback() {
                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                    }

                    @Override
                    public void onSuccess(String json, Call call, Response response) {
                        if (!TextUtils.isEmpty(json) && json.equals("{}")) {
                            if ((json.trim()).startsWith("<!DOCTYPE")) {
                                NToast.shortToast(mContext, "Session过期，请重新登陆");
                                startActivity(new Intent(mContext, LoginActivity.class));
                                RongIM.getInstance().logout();
                                finish();
                            } else {
                                Gson gson = new Gson();
                                Map<String, Object> map = gson.fromJson(json, new TypeToken<Map<String, Object>>() {
                                }.getType());
                                String ids = map.get("code").toString();
                                if (ids.equals("0.0")) {
                                    NToast.shortToast(mContext, "参数不正确");
                                }
                                if (ids.equals("1.0")) {
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        Toast.makeText(AMapShareLocationActivity.this, "连接服务器异常", Toast.LENGTH_LONG).show();
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * dialog
     */
    private void showCommPopMenu() {
        final Dialog dialog = new Dialog(this, R.style.common_dialog);
        View view = View.inflate(getApplicationContext(), R.layout.popup_window_comm, null);
        PagingScrollHelper scrollHelper = new PagingScrollHelper();
        RecyclerView rv_list_pop = (RecyclerView) view.findViewById(R.id.rv_list_pop);
        rv_list_pop.setLayoutManager(new HorizontalPageLayoutManager(3, 6));
        ItemDefTextAdapter itemDefAdapter = new ItemDefTextAdapter(this, mDatalist);
        rv_list_pop.setAdapter(itemDefAdapter);
        scrollHelper.setUpRecycleView(rv_list_pop);
        rv_list_pop.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                //移动到点击的位置
                double latitude = (Double) mDatalist.get(position).get("latitude");
                double longtitude = (Double) mDatalist.get(position).get("longtitude");
                if (latitude != 90.0 && longtitude != 180.0) {
                    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longtitude), 15), 1000, null);
                    dialog.dismiss();
                } else {
                    NToast.longToast(mContext, "位置无效！");
                }
            }
        });
        dialog.setContentView(view);
        dialog.show();


        // 设置相关位置，一定要在 show()之后
        Window window = dialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    /**
     * 类型你自己规定
     *
     * @param list
     */
    private void addMarkersToMaps(List<LatLng> list, List<String> url, List<String> ids) {
//        aMap.addMarkers(null, true);
        aMap.clear();
        ArrayList<MarkerOptions> markerOptionlst = new ArrayList<MarkerOptions>();
        for (int i = 0; i < list.size(); i++) {
            View view = LayoutInflater.from(this).inflate(
                    R.layout.view_infowindow, null);
            ImageView iv_amap_photo = (ImageView) view.findViewById(R.id.iv_amap_photo);
            ImageView locImageView = (ImageView) view.findViewById(R.id.iv_amap_mark);
            if ((ids.get(i)).equals(RongIMClient.getInstance().getCurrentUserId() + ".0")) {
                locImageView.setImageResource(R.mipmap.amap_location_blue);
            } else {
                locImageView.setImageResource(R.mipmap.amap_location_other);
            }
            if (url != null && url.size() > 0) {
                String image = ConstantValue.ImageFile + url.get(i);
                Picasso.with(mContext)
                        .load(image)
                        .resize(50, 50)
                        .placeholder(R.mipmap.default_portrait)
                        .config(Bitmap.Config.ARGB_8888)
                        .error(R.mipmap.default_portrait)
                        .into(iv_amap_photo);
            }
            markerOption = new MarkerOptions()
                    .position(list.get(i))
                    .icon(BitmapDescriptorFactory.fromView(view))
                    .draggable(true);
            markerOptionlst.add(markerOption);
        }
        /**
         * 循环List
         * 往markerOptionlst  add  数据
         * 往markerOptionlst  直接拿addMarkersToMap()  方法
         */
        aMap.addMarkers(markerOptionlst, true);
        markerOptionlst.clear();
    }

    //在退出程序时，将经纬度设置为无效
    private void PushlatLngIsNull() {
        String userID = RongIMClient.getInstance().getCurrentUserId();
        OkGo.post(ConstantValue.SUBLOCATION)
                .tag(this)
                .headers("cookie", sessionId)
                .params("userid", userID)
                .params("latitude", "90")
                .params("longitude", "180")
                .execute(new StringCallback() {
                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
//                        LoadDialog.show(mContext);
                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
//                        LoadDialog.dismiss(mContext);
                        if (!TextUtils.isEmpty(s) && !s.equals("{}")) {
                            Log.i("onSuccess  没成功", "打印:" + s);
                            if ((s.trim()).startsWith("<!DOCTYPE")) {
                                NToast.shortToast(mContext, "Session过期，请重新登陆");
                                startActivity(new Intent(mContext, LoginActivity.class));
                                RongIM.getInstance().logout();
                                finish();
                            } else {
                                Gson gson = new Gson();
                                Map<String, Object> map = gson.fromJson(s, new TypeToken<Map<String, Object>>() {
                                }.getType());
                                String str = map.get("code").toString();
                                if (str.equals("1.0")) {
                                    NToast.shortToast(mContext, "清空位置信息");
                                }
                            }
                        }
                    }
                });
    }

    /**
     * marker点击时跳动一下
     */
    public void jumpPoint(final Marker marker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = aMap.getProjection();
        final LatLng markerLatlng = marker.getPosition();
        Point markerPoint = proj.toScreenLocation(markerLatlng);
        markerPoint.offset(0, -100);
        final LatLng startLatLng = proj.fromScreenLocation(markerPoint);
        final long duration = 1500;
        final Interpolator interpolator = new BounceInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * markerLatlng.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * markerLatlng.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        jumpPoint(marker);
        showCommPopMenu();
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
//        PushlatLngIsNull();
    }

    private void finishDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("提示");
        builder.setMessage("确定退出位置共享");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PushlatLngIsNull();
                mapView.onDestroy();
                timer.cancel();
                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        simpledialog = builder.create();
        simpledialog.setCanceledOnTouchOutside(false);
        simpledialog.setCancelable(false);
//        simpledialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        simpledialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            PushlatLngIsNull();
            finishDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

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

    @Override
    protected void onDestroy() {
        if (simpledialog != null) {
            simpledialog.dismiss();
        }
        super.onDestroy();
        mapView.onDestroy();
        PushlatLngIsNull();
        timer.cancel();
    }
}