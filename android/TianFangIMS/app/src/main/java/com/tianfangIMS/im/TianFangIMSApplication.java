package com.tianfangIMS.im;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.view.WindowManager;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.store.PersistentCookieStore;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;
import com.tianfangIMS.im.activity.LoginActivity;
import com.tianfangIMS.im.bean.TopFiveUserInfoBean;
import com.tianfangIMS.im.service.FloatService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import io.rong.imkit.RongExtension;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;
import io.rong.ptt.PTTClient;
import io.rong.ptt.PTTSession;
import io.rong.ptt.PTTStateListener;
import io.rong.ptt.kit.PTTEndMessageItemProvider;
import io.rong.ptt.kit.PTTStartMessageItemProvider;

/**
 * Created by Lmy on 2016/12/29.
 * Application继承类
 */

public class TianFangIMSApplication extends Application implements PTTStateListener,RongIMClient.OnReceiveMessageListener{
    private static TianFangIMSApplication instance;
    private List<TopFiveUserInfoBean> data = new ArrayList<TopFiveUserInfoBean>(5);
    private int sum = 5;
    ReentrantLock lock = new ReentrantLock();
    UserInfo userinfo;
    TopFiveUserInfoBean floatbean;
    private String PrivateChatLogo;
    private String GroupLogo;
    private RongExtension extension;
    private boolean IsVibrator = true;
    private boolean IsSound = true;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    showDialog();
                    break;
            }
        }
    };
    PTTClient pttClient = PTTClient.getInstance();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        RongIM.setServerInfo("103.36.132.10:80","up.qbox.me/");
        RongIM.init(this);
        RongIM.registerMessageTemplate(new PTTStartMessageItemProvider());
        RongIM.registerMessageTemplate(new PTTEndMessageItemProvider());
//        PTTClient.setPTTServerBaseUrl("http://35.164.107.27:8080/rce/restapi/ptt");
//        RongExtensionManager.getInstance().registerExtensionModule(new PTTExtensionModule(this, true, 1000 * 60));
        OkGo.init(this);
        HttpHeaders headers = new HttpHeaders();
        headers.put("commonHeaderKey1", "commonHeaderValue1");    //header不支持中文
        headers.put("commonHeaderKey2", "commonHeaderValue2");
        HttpParams params = new HttpParams();
        params.put("commonParamsKey1", "commonParamsValue1");     //param支持中文,直接传,不要自己编码
        params.put("commonParamsKey2", "这里支持中文参数");
//以下设置的所有参数是全局参数,同样的参数可以在请求的时候再设置一遍,那么对于该请求来讲,请求中的参数会覆盖全局参数
        //好处是全局参数统一,特定请求可以特别定制参数1
        try {
            //以下都不是必须的，根据需要自行选择,一般来说只需要 debug,缓存相关,cookie相关的 就可以了
            OkGo.getInstance()

                    // 打开该调试开关,打印级别INFO,并不是异常,是为了显眼,不需要就不要加入该行
                    // 最后的true表示是否打印okgo的内部异常，一般打开方便调试错误
                    .debug("OkGo", Level.INFO, true)

                    //如果使用默认的 60秒,以下三行也不需要传
                    .setConnectTimeout(OkGo.DEFAULT_MILLISECONDS)  //全局的连接超时时间
                    .setReadTimeOut(OkGo.DEFAULT_MILLISECONDS)     //全局的读取超时时间
                    .setWriteTimeOut(OkGo.DEFAULT_MILLISECONDS)    //全局的写入超时时间

                    //可以全局统一设置缓存模式,默认是不使用缓存,可以不传,具体其他模式看 github 介绍 https://github.com/jeasonlzy/
                    .setCacheMode(CacheMode.NO_CACHE)

                    //可以全局统一设置缓存时间,默认永不过期,具体使用方法看 github 介绍
                    .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)

                    //可以全局统一设置超时重连次数,默认为三次,那么最差的情况会请求4次(一次原始请求,三次重连请求),不需要可以设置为0
                    .setRetryCount(3)

                    //如果不想让框架管理cookie（或者叫session的保持）,以下不需要
//              .setCookieStore(new MemoryCookieStore())            //cookie使用内存缓存（app退出后，cookie消失）
                    .setCookieStore(new PersistentCookieStore())        //cookie持久化存储，如果cookie不过期，则一直有效
                    //可以设置https的证书,以下几种方案根据需要自己设置
                    .setCertificates()                            //方法一：信任所有证书,不安全有风险
//              .setCertificates(new SafeTrustManager())            //方法二：自定义信任规则，校验服务端证书
//              .setCertificates(getAssets().open("srca.cer"))      //方法三：使用预埋证书，校验服务端证书（自签名证书）
//              //方法四：使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
//               .setCertificates(getAssets().open("xxx.bks"), "123456", getAssets().open("yyy.cer"))//

                    //配置https的域名匹配规则，详细看demo的初始化介绍，不需要就不要加入，使用不当会导致https握手失败
//               .setHostnameVerifier(new SafeHostnameVerifier())

                    //可以添加全局拦截器，不需要就不要加入，错误写法直接导致任何回调不执行
//                .addInterceptor(new Interceptor() {
//                    @Override
//                    public Response intercept(Chain chain) throws IOException {
//                        return chain.proceed(chain.request());
//                    }
//                })

                    //这两行同上，不需要就不要加入
                    .addCommonHeaders(headers)  //设置全局公共头
                    .addCommonParams(params);   //设置全局公共参数

        } catch (Exception e) {
            e.printStackTrace();
        }
        //获取接收消息的监听
//        RongIM.setOnReceiveMessageListener(new MyReceiveMessageListener());

        RongIM.setConnectionStatusListener(new MyConnectionStatusListener());
        pttClient.init(this);
        pttClient.setPttStateListener(this);
//        RongIMClient.setOnReceiveMessageListener(this);
        RongIM.setOnReceiveMessageListener(this);
    }

    private class MyConnectionStatusListener implements RongIMClient.ConnectionStatusListener {
        @Override
        public void onChanged(ConnectionStatus connectionStatus) {
            Log.e("MyConnectionStatusListener", "执行了没有" + connectionStatus);
            if (connectionStatus == ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT) {
                Message msg = Message.obtain();
                msg.what = 0;
                handler.sendMessage(msg);
            }
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static TianFangIMSApplication getInstance() {
        return instance;
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
        builder.setTitle("提示");
        builder.setMessage("此账号已在其他设备登陆");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sp = getBaseContext().getSharedPreferences("config", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.commit();
                getBaseContext().startActivity(new Intent(getBaseContext(), LoginActivity.class));
                Intent mIntent = new Intent(getBaseContext(), FloatService.class);
                getBaseContext().stopService(mIntent);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog simpledialog = builder.create();
        simpledialog.setCanceledOnTouchOutside(false);
        simpledialog.setCancelable(false);
        simpledialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        simpledialog.show();
    }

    @Override
    public void onSessionStart(PTTSession pttSession) {
        Log.e("PTT", "onSessionStart------:" + pttSession);
    }

    @Override
    public void onSessionTerminated(PTTSession pttSession) {
        Log.e("PTT", "onSessionTerminated------:" + pttSession);
    }

    @Override
    public void onParticipantChanged(PTTSession pttSession, List<String> userIds) {
        Log.e("PTT", "onParticipantChanged------:" + pttSession);
    }

    @Override
    public void onMicHolderChanged(PTTSession pttSession, String holderUserId) {
        Log.e("PTT", "onMicHolderChanged------:" + pttSession);
    }

    @Override
    public void onNetworkError(String msg) {
        Log.e("PTT", "onNetworkError------:" + msg);

    }

    @Override
    public boolean onReceived(io.rong.imlib.model.Message message, int i) {
        IsSound = getSharedPreferences("sound",MODE_PRIVATE).getBoolean("sound", true);
        IsVibrator = getSharedPreferences("vibrator",MODE_PRIVATE).getBoolean("vibrator",true);
        if(IsVibrator){
            Vibrator vibrator = (Vibrator)this.getSystemService(Service.VIBRATOR_SERVICE);
            vibrator.vibrate(500);
        }
        if(IsSound){
            MediaPlayer mp = new MediaPlayer();
            try {
                mp.setDataSource(this, RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                mp.prepare();
                mp.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
