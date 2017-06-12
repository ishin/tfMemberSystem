package com.tianfangIMS.im;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.cookie.store.PersistentCookieStore;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;
import com.tianfangIMS.im.activity.LoginActivity;
import com.tianfangIMS.im.activity.MainActivity;
import com.tianfangIMS.im.bean.TopFiveUserInfoBean;
import com.tianfangIMS.im.bean.UserBean;
import com.tianfangIMS.im.dialog.PTTPushDialog;
import com.tianfangIMS.im.service.FloatService;
import com.tianfangIMS.im.utils.CommonUtil;
import com.tianfangIMS.im.utils.NToast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import io.rong.imkit.RongExtension;
import io.rong.imkit.RongIM;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.FileMessage;
import io.rong.message.ImageMessage;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;
import io.rong.ptt.PTTClient;
import io.rong.ptt.PTTSession;
import io.rong.ptt.PTTStateListener;
import io.rong.ptt.kit.PTTEndMessageItemProvider;
import io.rong.ptt.kit.PTTStartMessageItemProvider;
import io.rong.ptt.message.server.PTTEndMessage;
import io.rong.ptt.message.server.PTTMicHolderChangeMessage;
import io.rong.ptt.message.server.PTTParticipantChangeMessage;
import io.rong.ptt.message.server.PTTPingMessage;
import io.rong.ptt.message.server.PTTStartMessage;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Lmy on 2016/12/29.
 * Application继承类
 */

public class TianFangIMSApplication extends Application implements PTTStateListener, RongIMClient.OnReceiveMessageListener {
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
    private boolean isIntercom = true;
    PTTPushDialog dialog;
    Vibrator vibrator;
    MediaPlayer mp;
    MediaPlayer mpintercom;
    //    Timer timer = new Timer();
//    TimerTask task = new TimerTask() {
//        @Override
//        public void run() {
//            // 需要做的事:发送消息
//            Message message = new Message();
//            message.what = 3;
//            handler.sendMessage(message);
//        }
//    };
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    showDialog();
                    break;
                case 1:
                    if (getRunningActivityName().equals("com.tianfangIMS.im.activity.ConversationActivity")) {
                        NToast.longToast(getApplicationContext(), "点击发起对讲，接听对讲消息");
                    } else {
                        dialog = new PTTPushDialog(getApplicationContext(), msg.arg1, msg.obj);
                        Window window = dialog.getWindow();
                        window.setBackgroundDrawableResource(android.R.color.transparent);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                        if (Build.VERSION.SDK_INT >= 23) {
                            if (!Settings.canDrawOverlays(TianFangIMSApplication.this)) {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                NToast.shortToast(getApplicationContext(), "请添加权限，否则无法使用悬浮窗体");
                            } else {
                                dialog.show();
                            }
                        } else {
                            dialog.show();
                        }
                    }
                    break;
                case 2:
                    try {
                        if (dialog != null) {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    break;
//                case 3:
//                    if (dialog.isShowing()) {
//                        dialog.dismiss();
//                        timer.cancel();
//                    }
//                    break;

            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        RongIM.setServerInfo("103.36.132.10:80", "up.qbox.me/");
        RongIM.init(this);
        PTTClient pttClient = PTTClient.getInstance();
        RongIM.registerMessageTemplate(new PTTStartMessageItemProvider());
        RongIM.registerMessageTemplate(new PTTEndMessageItemProvider());
        OkGo.init(this);
        HttpHeaders headers = new HttpHeaders();
        headers.put("commonHeaderKey1", "commonHeaderValue1");    //header不支持中文
        headers.put("commonHeaderKey2", "commonHeaderValue2");
        HttpParams params = new HttpParams();
        params.put("commonParamsKey1", "commonParamsValue1");     //param支持中文,直接传,不要自己编码
        params.put("commonParamsKey2", "这里支持中文参数");
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
                    //可以全局统一设置缓存模式,默认是不使用缓存,可以不传
                    //就是这个  缓存的设置   根据自己的需求设置就行
                    .setCacheMode(CacheMode.NO_CACHE)
                    //可以全局统一设置缓存时间,默认永不过期
                    .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)
                    //可以全局统一设置超时重连次数,默认为三次,那么最差的情况会请求4次(一次原始请求,三次重连请求),不需要可以设置为0
                    .setRetryCount(3)
                    //如果不想让框架管理cookie（或者叫session的保持）,以下不需要
                    //.setCookieStore(new MemoryCookieStore())            //cookie使用内存缓存（app退出后，cookie消失）
                    .setCookieStore(new PersistentCookieStore())        //cookie持久化存储，如果cookie不过期，则一直有效
                    //可以设置https的证书,以下几种方案根据需要自己设置
                    .setCertificates()                                    //方法一：信任所有证书,不安全有风险
                    //.setCertificates(new SafeTrustManager())            //方法二：自定义信任规则，校验服务端证书
                    //.setCertificates(getAssets().open("srca.cer"))      //方法三：使用预埋证书，校验服务端证书（自签名证书）
                    //方法四：使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书
                    // .setCertificates(getAssets().open("xxx.bks"), "123456", getAssets().open("yyy.cer"))//

                    //配置https的域名匹配规则，详细看demo的初始化介绍，不需要就不要加入，使用不当会导致https握手失败
                    //.setHostnameVerifier(new SafeHostnameVerifier())
                    //可以添加全局拦截器，不需要就不要加入，错误写法直接导致任何回调不执行
                    //.addInterceptor(new Interceptor() {
                    // @Override
                    //public Response intercept(Chain chain) throws IOException {
                    //return chain.proceed(chain.request());
                    //}
                    //})
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
        RongIM.setOnReceiveMessageListener(this);
    }

    private class MyConnectionStatusListener implements RongIMClient.ConnectionStatusListener {
        @Override
        public void onChanged(ConnectionStatus connectionStatus) {
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
//                SharedPreferences sp = getBaseContext().getSharedPreferences("config", MODE_PRIVATE);
                RongIM.getInstance().logout();
//                SharedPreferences.Editor editor = sp.edit();
//                editor.clear();
//                editor.commit();
                getBaseContext().startActivity(new Intent(getBaseContext(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                Intent mIntent = new Intent(getBaseContext(), FloatService.class);
                getBaseContext().stopService(mIntent);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
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
        Log.e("PTT", "onSessionStart------:" + pttSession.getTargetId());
    }

    @Override
    public void onSessionTerminated(PTTSession pttSession) {
        Log.e("PTT", "onSessionTerminated------:" + pttSession);
    }

    @Override
    public void onParticipantChanged(PTTSession pttSession, List<String> userIds) {
        Log.e("PTT", "onParticipantChanged------:" + userIds + "---PttSession:" + pttSession);
    }

    @Override
    public void onMicHolderChanged(PTTSession pttSession, String holderUserId) {
        Log.e("PTT", "onMicHolderChanged------:" + pttSession);
    }

    @Override
    public void onNetworkError(String msg) {

    }

    private String getRunningActivityName() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String str = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        return str;
    }

    private void getNotification(final io.rong.imlib.model.Message message1) {
        RongIM.getInstance().getConversationNotificationStatus(message1.getConversationType(), message1.getTargetId(), new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
            @Override
            public void onSuccess(Conversation.ConversationNotificationStatus conversationNotificationStatus) {
                IsSound = getSharedPreferences("sound", MODE_PRIVATE).getBoolean("sound", true);
                IsVibrator = getSharedPreferences("vibrator", MODE_PRIVATE).getBoolean("vibrator", true);
                isIntercom = getSharedPreferences("intercom", MODE_PRIVATE).getBoolean("intercom", true);
                vibrator = (Vibrator) TianFangIMSApplication.this.getSystemService(Service.VIBRATOR_SERVICE);
                mp = new MediaPlayer();
                mpintercom = new MediaPlayer();
                MessageContent messageContent = message1.getContent();
                if (conversationNotificationStatus.getValue() == 1) {//关闭消息免打扰
                    if (IsVibrator) {
                        if (messageContent instanceof PTTPingMessage || messageContent instanceof PTTMicHolderChangeMessage) {
                            vibrator.cancel();
                        } else {
                            if (isIntercom) {
                                if (message1.getMessageDirection().equals(io.rong.imlib.model.Message.MessageDirection.RECEIVE)) {
                                    vibrator.vibrate(500);
                                }
                            } else {
                                if (messageContent instanceof PTTStartMessage || messageContent instanceof PTTParticipantChangeMessage || messageContent instanceof PTTEndMessage) {
                                    vibrator.cancel();
                                } else {
                                    vibrator.vibrate(500);
                                }
                            }
                        }
                    } else {
                        vibrator.cancel();
                    }
                    if (IsSound) {
                        if (isIntercom) {
                            if (message1.getMessageDirection().equals(io.rong.imlib.model.Message.MessageDirection.RECEIVE)) {
                                if (messageContent instanceof PTTMicHolderChangeMessage) {
                                    try {
                                        vibrator.cancel();
                                        mpintercom.stop();
                                        mp.stop();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    try {
                                        mp.setDataSource(TianFangIMSApplication.this, RingtoneManager
                                                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                                        mp.prepare();
                                        mp.start();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } else {
                            if (messageContent instanceof PTTStartMessage || messageContent instanceof PTTParticipantChangeMessage || messageContent instanceof PTTEndMessage) {
                                try {
                                    vibrator.cancel();
                                    mpintercom.stop();
                                    mp.stop();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if (message1.getMessageDirection().equals(io.rong.imlib.model.Message.MessageDirection.RECEIVE)) {
                                    if (messageContent instanceof PTTMicHolderChangeMessage) {
                                        try {
                                            vibrator.cancel();
                                            mpintercom.stop();
                                            mp.stop();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        try {
                                            mp.setDataSource(TianFangIMSApplication.this, RingtoneManager
                                                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                                            mp.prepare();
                                            mp.start();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if (isIntercom) {
                            if (messageContent instanceof PTTStartMessage || messageContent instanceof PTTEndMessage) {
                                if (message1.getMessageDirection().equals(io.rong.imlib.model.Message.MessageDirection.RECEIVE)) {
                                    try {
                                        mp.setDataSource(TianFangIMSApplication.this, RingtoneManager
                                                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                                        mp.prepare();
                                        mp.start();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                try {
                                    mpintercom.stop();
                                    mp.stop();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            try {
                                mp.stop();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (messageContent instanceof PTTPingMessage) {
                        try {
                            mpintercom.stop();
                            mp.stop();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {//开启消息免打扰
                    vibrator.cancel();
                    try {
                        mp.setDataSource(TianFangIMSApplication.this, RingtoneManager
                                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                        mp.prepare();
                        mp.stop();
                        mpintercom.prepare();
                        mpintercom.stop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }

    UserBean bean;
    UserInfo userInfo;

    private void GetNewUserInfo(String ids, String sessionId, final String flags) throws NullPointerException {
        OkGo.post(ConstantValue.GETONEPERSONINFO)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .headers("cookie", sessionId)
                .params("userid", ids)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (!TextUtils.isEmpty(s) && !s.equals("{}")) {
                            if ((s.trim()).startsWith("<!DOCTYPE")) {
                                NToast.shortToast(TianFangIMSApplication.this, "请重新登陆");
                                startActivity(new Intent(TianFangIMSApplication.this, LoginActivity.class));
                                RongIM.getInstance().logout();
                            } else {
                                Gson gson2 = new Gson();
                                bean = gson2.fromJson(s, UserBean.class);
                                if (flags.equals("RePrivateChat")) {
                                    userInfo = new UserInfo(bean.getId(), bean.getName(), Uri.parse(ConstantValue.ImageFile + bean.getLogo()));
                                    RongIM.getInstance().refreshUserInfoCache(userInfo);
                                } else if (flags.equals("ReNotifiChat")) {
                                    try {
                                        if (bean != null) {
                                            getUserinfos = new UserInfo(bean.getId(), bean.getName(), Uri.parse(ConstantValue.ImageFile + bean.getLogo()));
                                        }
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                });
    }

    UserInfo getUserinfos;
    Group getGroupinfos;
    String getUserName;
    String SID;

    private void GetInfos(io.rong.imlib.model.Message message) throws NullPointerException {
        if (message.getConversationType().getName().equals("private")) {
            getUserinfos = RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId());
            if (getUserinfos == null) {
                GetNewUserInfo(message.getTargetId(), SID, "RePrivateChat");
            } else {
                getUserName = getUserinfos.getName();
            }
        } else if (message.getConversationType().getName().equals("group")) {
            try {
                getGroupinfos = RongUserInfoManager.getInstance().getGroupInfo(message.getTargetId());
                getUserName = getGroupinfos.getName();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onReceived(final io.rong.imlib.model.Message message, int i) {
        GetInfos(message);
        SID = TianFangIMSApplication.this.getSharedPreferences("CompanyCode", Activity.MODE_PRIVATE).getString("CompanyCode", "");
        if (message.getConversationType().getName().equals("private")) {
            userInfo = RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId());
            if (userInfo == null) {
                if (!TextUtils.isEmpty(SID)) {
                    GetNewUserInfo(message.getSenderUserId(), SID, "ReNotifiChat");
                }
            } else {
                RongIM.getInstance().refreshUserInfoCache(userInfo);
            }
        }
        getNotification(message);
        if (isApplicationInBackground(TianFangIMSApplication.this)) {
            if (message.getContent() instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message.getContent();
                if (message.getMessageDirection().equals(io.rong.imlib.model.Message.MessageDirection.RECEIVE)) {
                    if (getUserName != null) {
                        Notifi(getUserName, textMessage.getContent());
                    } else {
                        Notifi("", textMessage.getContent());
                    }
                }
            } else if (message.getContent() instanceof ImageMessage) {
                ImageMessage imageMessage = (ImageMessage) message.getContent();
                if (message.getMessageDirection().equals(io.rong.imlib.model.Message.MessageDirection.RECEIVE)) {
                    if (getUserName != null) {
                        Notifi(getUserName, "[图片]");
                    } else {
                        Notifi("", "[图片]");
                    }
                }
            } else if (message.getContent() instanceof FileMessage) {
                FileMessage fileMessage = (FileMessage) message.getContent();
                if (message.getMessageDirection().equals(io.rong.imlib.model.Message.MessageDirection.RECEIVE)) {
                    if (getUserName != null) {
                        Notifi(getUserName, "[文件]" + fileMessage.getName());
                    } else {
                        Notifi("", "[文件]" + fileMessage.getName());
                    }
                }
            } else if (message.getContent() instanceof VoiceMessage) {
                VoiceMessage voiceMessage = (VoiceMessage) message.getContent();
                if (message.getMessageDirection().equals(io.rong.imlib.model.Message.MessageDirection.RECEIVE)) {
                    if (getUserName != null) {
                        Notifi(getUserName, "发来一条语音消息");
                    } else {
                        Notifi("", "发来一条语音消息");
                    }
                }
            } else if (message.getContent() instanceof PTTStartMessage) {
                if (message.getMessageDirection().equals(io.rong.imlib.model.Message.MessageDirection.RECEIVE)) {
                    int str = Integer.parseInt(message.getTargetId());
                    Message msg = Message.obtain();
                    msg.what = 1;
                    msg.arg1 = str;
                    msg.obj = message.getConversationType().getName();
                    handler.sendMessage(msg);
                }
//                Log.e("asdsad123", "" + message.getConversationType().getName());
//                if ((message.getConversationType().getName()).equals("private")) {
//                    Log.e("asda123asd", "private---targetid:" + message.getTargetId() + "---:" + message.getSenderUserId());
//                    UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(message.getTargetId());
//                    Notifi(userInfo.getName(), "发起了对讲消息");
//                } else if (message.getConversationType().equals("group")) {
//                    Log.e("asda123asd", "Group---targetid:" + message.getTargetId() + "---:" + message.getSenderUserId());
////                    GroupUserInfo groupUserInfo = RongUserInfoManager.getInstance().getGroupUserInfo(message.getTargetId());
////                    Notifi(groupUserInfo.getNickname(), "发起了对讲消息");
//                }
            } else if (message.getContent() instanceof PTTEndMessage) {
                if (message.getMessageDirection().equals(io.rong.imlib.model.Message.MessageDirection.RECEIVE)) {
                    int str = Integer.parseInt(message.getTargetId());
                    Message msg = Message.obtain();
                    msg.what = 2;
                    msg.arg1 = str;
                    msg.obj = message.getConversationType().getName();
                    handler.sendMessage(msg);
                }
            }
        } else {
            MessageContent messageContent = message.getContent();
            if (messageContent instanceof PTTStartMessage) {
//            PTTStartMessage pttStartMessage = (PTTStartMessage) message.getContent();
                if (message.getMessageDirection().equals(io.rong.imlib.model.Message.MessageDirection.RECEIVE)) {
                    int str = Integer.parseInt(message.getTargetId());
                    Message msg = Message.obtain();
                    msg.what = 1;
                    msg.arg1 = str;
                    msg.obj = message.getConversationType().getName();
                    handler.sendMessage(msg);
                }
            } else if (messageContent instanceof PTTEndMessage) {
                if (message.getMessageDirection().equals(io.rong.imlib.model.Message.MessageDirection.RECEIVE)) {
                    int str = Integer.parseInt(message.getTargetId());
                    Message msg = Message.obtain();
                    msg.what = 2;
                    msg.arg1 = str;
                    msg.obj = message.getConversationType().getName();
                    handler.sendMessage(msg);
                }
            } else if (message.getContent() instanceof InformationNotificationMessage) {
                InformationNotificationMessage notificationMessage = (InformationNotificationMessage) message.getContent();
                if (message.getMessageDirection().equals(io.rong.imlib.model.Message.MessageDirection.RECEIVE)) {
                    String str = notificationMessage.getMessage();
                    if ((str.trim()).startsWith("群聊已建立")) {
                        RongIM.getInstance().refreshGroupInfoCache(new Group(message.getTargetId(), notificationMessage.getExtra(), null));
                    } else if ((str.trim()).startsWith("群组已解散")) {
                        RongIM.getInstance().removeConversation(message.getConversationType(), message.getTargetId(), new RongIMClient.ResultCallback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean aBoolean) {
                                Log.e("Delete", "----:" + aBoolean);
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {

                            }
                        });
//                        RongIM.getInstance().refreshGroupInfoCache(new Group(message.getTargetId(), " ", null));
                    } else if ((str.trim()).startsWith("群名称已更改为")) {
                        if (TextUtils.isEmpty(subStr(str))) {
                            RongIM.getInstance().refreshGroupInfoCache(new Group(message.getTargetId(), " ", null));
                        } else {
                            RongIM.getInstance().refreshGroupInfoCache(new Group(message.getTargetId(), subStr(str), null));
                        }
                    } else if (CommonUtil.isConstant(str, "加入群组")) {
                        RongIM.getInstance().refreshGroupInfoCache(new Group(message.getTargetId(), notificationMessage.getExtra(), null));
                    } else if ((str.trim()).startsWith("建立好友关系")) {
                        RongIM.getInstance().refreshUserInfoCache(new UserInfo(message.getTargetId(),
                                notificationMessage.getExtra()
                                , Uri.parse(ConstantValue.ImageFile + "")));
                    } else if ((str.trim()).startsWith("语音对讲结束")) {
                        if (isIntercom) {
                            try {
                                mp.setDataSource(TianFangIMSApplication.this, RingtoneManager
                                        .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                                mp.start();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                mpintercom.stop();
                                mp.stop();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    else if((str.trim()).indexOf("离开群组") != -1){
                        RongIM.getInstance().refreshUserInfoCache(new UserInfo(message.getTargetId(),
                                notificationMessage.getExtra()
                                , Uri.parse(ConstantValue.ImageFile + "")));
                    }
                }
            } else if (message.getContent() instanceof PTTEndMessage) {
                if (message.getMessageDirection().equals(io.rong.imlib.model.Message.MessageDirection.RECEIVE)) {
                    if (isIntercom) {
                        try {
                            mp.setDataSource(TianFangIMSApplication.this, RingtoneManager
                                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                            mp.prepare();
                            mp.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            mpintercom.stop();
                            mp.stop();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (message.getConversationType().equals("PRIVATE")) {
                        UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(message.getTargetId());
                        NToast.shortToast(getApplicationContext(), userInfo.getName() + "已挂断对讲");
                    }
                }
            }
        }
        return true;
    }

    //自定义后台消息
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void Notifi(String title, String contant) {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(TianFangIMSApplication.this);
        builder.setContentText(contant);
        builder.setContentTitle(title);
        builder.setSmallIcon(R.mipmap.icon);
        builder.setTicker("新消息");
        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());
        Intent intent = new Intent(TianFangIMSApplication.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(TianFangIMSApplication.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        nm.notify(1, notification);
    }

    private String subStr(String str) {
        String aa = str.substring(7, str.length());
        return aa;
    }

    public static boolean isApplicationInBackground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskList = am.getRunningTasks(1);
        if (taskList != null && !taskList.isEmpty()) {
            ComponentName topActivity = taskList.get(0).topActivity;
            if (topActivity != null && !topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }
}
