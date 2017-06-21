package com.tianfangIMS.im.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.BitmapCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.tianfangIMS.im.ConstantValue;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.activity.LoginActivity;
import com.tianfangIMS.im.bean.OneGroupBean;
import com.tianfangIMS.im.bean.UserBean;
import com.tianfangIMS.im.dialog.LoadDialog;
import com.tianfangIMS.im.dialog.TalkPhoneDialog;
import com.tianfangIMS.im.utils.NToast;

import net.qiujuer.genius.blur.StackBlur;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import io.rong.common.RLog;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.UserInfo;
import io.rong.ptt.JoinSessionCallback;
import io.rong.ptt.PTTClient;
import io.rong.ptt.PTTSession;
import io.rong.ptt.PTTSessionStateListener;
import io.rong.ptt.RequestToSpeakCallback;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by LianMengYu on 2017/2/9.
 * 对讲fragment
 */

public class IntercomFragment extends BaseFragment implements View.OnClickListener, PTTSessionStateListener {
    public static IntercomFragment Instance = null;
    private List<String> participants;
    private PTTClient pttClient;
    private Button ceshi;
    private AudioTrack audioTrack;

    public static IntercomFragment getInstance() {
        if (Instance == null) {
            Instance = new IntercomFragment();
        }
        return Instance;
    }

    ImageView main_call_blur;
    ImageView main_call_header;
    private Conversation.ConversationType mConversationType;
    ImageView main_call_free, main_call_flash, main_call_talk, main_call_end;
    private String userid;
    private UserInfo userInfo;
    private TextView intercom_name;
    String sessionId;
    UserInfo useinfo;
    private Group Groupinfo;
    String InterComName;
    private static Set allSet = new HashSet();
    private static List alllist = new ArrayList();
    private AlertDialog simpledialog;
    private String phone, dialogname, dialoglogo;
    private TalkPhoneDialog talkPhoneDialog;
    private String CallPriv;
    private int isPtt = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.intercom_layout, container, false);
        CallPriv = getActivity().getSharedPreferences("config", Activity.MODE_PRIVATE).getString("callphonepriv", "");
        sessionId = getActivity().getSharedPreferences("CompanyCode", Activity.MODE_PRIVATE).getString("CompanyCode", "");
        pttClient = PTTClient.getInstance();
        Intent intent = getActivity().getIntent();
        main_call_blur = (ImageView) view.findViewById(R.id.main_call_blur);
        main_call_header = (ImageView) view.findViewById(R.id.main_call_header);
        main_call_free = (ImageView) view.findViewById(R.id.main_call_free);
        main_call_flash = (ImageView) view.findViewById(R.id.main_call_flash);
        main_call_talk = (ImageView) view.findViewById(R.id.main_call_talk);
        intercom_name = (TextView) view.findViewById(R.id.intercom_name);
        main_call_end = (ImageView) view.findViewById(R.id.main_call_end);
        ceshi = (Button) view.findViewById(R.id.ceshi);
        ceshi.setOnClickListener(this);
        main_call_end.setOnClickListener(this);
        setListener();
        mConversationType = Conversation.ConversationType.valueOf(intent.getData()
                .getLastPathSegment().toUpperCase(Locale.getDefault()));
        userid = intent.getData().getQueryParameter("targetId");
        //获取userinfo
        if (mConversationType == Conversation.ConversationType.PRIVATE) {
//            userInfo = RongUserInfoManager.getInstance().getUserInfo(userid);
            GetPrivate(userid);
            if (!TextUtils.isEmpty(CallPriv)) {
                if (CallPriv.equals("false")) {
                    main_call_flash.setVisibility(View.INVISIBLE);
                } else {
                    main_call_flash.setVisibility(View.VISIBLE);
                }
            } else {
                NToast.shortToast(getActivity(), "紧急呼叫权限获取失败");
            }
        }
        if (mConversationType == Conversation.ConversationType.GROUP) {
            main_call_flash.setVisibility(View.INVISIBLE);
            userid = intent.getData().getQueryParameter("targetId");
            GetGroupUserInfo();
        }
        Intent intentpage = getActivity().getIntent();
        int page = intentpage.getIntExtra("intercom", 0);//第一个参数是取值的key,第二个参数是默认值
        Log.e("asd13sadqwe123", "----:" + page);
        if (page == 1) {
            JoinInterCom();
        }
        setListener();
        return view;
    }

    private void GetPrivate(String id) {
        OkGo.post(ConstantValue.GETONEPERSONINFO)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .headers("cookie", sessionId)
                .params("userid", id)
                .execute(new StringCallback() {
                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (!TextUtils.isEmpty(s) && !s.equals("{}")) {
                            if ((s.trim()).startsWith("<!DOCTYPE")) {
                                NToast.shortToast(getActivity(), "请重新登陆");
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                RongIM.getInstance().logout();
                                getActivity().finish();
                            } else {
                                Gson gson = new Gson();
                                UserBean bean = gson.fromJson(s, UserBean.class);
                                intercom_name.setText(bean.getName());
                                phone = bean.getMobile();
                                dialogname = bean.getName();
                                dialoglogo = bean.getLogo();
                                if (TextUtils.isEmpty(bean.getLogo())) {
                                    getBitmap(ConstantValue.ImageFile + "defaultlogo.png");
                                } else {
                                    getBitmap(ConstantValue.ImageFile + bean.getLogo());
                                }
                            }
                        }
                    }
                });
    }


    private void GetGroupUserInfo() {
        OkGo.post(ConstantValue.GETONEGROUPINFO)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .headers("cookie", sessionId)
                .params("groupid", userid)
                .execute(new StringCallback() {
                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (!TextUtils.isEmpty(s) && !s.equals("{}")) {
                            if ((s.trim()).startsWith("<!DOCTYPE")) {
                                NToast.shortToast(getActivity(), "Session过期，请重新登陆");
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();
                            }
                            Gson gson = new Gson();
                            OneGroupBean oneGroupBean = gson.fromJson(s, OneGroupBean.class);
                            intercom_name.setText(oneGroupBean.getText().getName());
                            getBitmap(ConstantValue.ImageFile + oneGroupBean.getText().getLogo());
                        } else {
                            Log.e("intercom", "没有获取数据：" + s);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Log.e("intercom", "返回数据错误" + call);
                    }
                });

    }

    private void getBitmap(String path) {
        OkGo.post(path)
                .tag(this)
                .headers("cookie", sessionId)
                .execute(new BitmapCallback() {
                    @Override
                    public void onSuccess(Bitmap bitmap, Call call, Response response) {
                        LoadDialog.dismiss(getActivity());
                        if (bitmap != null) {
                            try {
                                Bitmap newBitmap = StackBlur.blur(bitmap, 20, false);
                                main_call_blur.setImageBitmap(newBitmap);
                                main_call_header.setImageBitmap(bitmap);
                            } catch (ArrayIndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void setListener() {
        main_call_free.setOnClickListener(this);
        main_call_flash.setOnClickListener(this);
        main_call_talk.setOnClickListener(this);
    }

    //请求说话，抢麦
    boolean requestToSpeak(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            main_call_talk.setImageResource(R.mipmap.talk_voice_waiting);
            pttClient.requestToSpeak(new RequestToSpeakCallback() {
                //抢麦成功
                @Override
                public void onReadyToSpeak(long maxDurationMillis) {

                }

                //抢麦失败
                @Override
                public void onFail(String msg) {
                    RLog.e("onFail", "start speak error " + msg);
                    if (msg.equals("can not get mic")) {
                        Toast toast = Toast.makeText(getActivity(), "对讲忙，请稍候", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();
                    }
                }

                //说话超时，通过服务器设定时长，如果超过自动停止说话
                @Override
                public void onSpeakTimeOut() {
                    NToast.shortToast(getActivity(), "通话超时");
                }
            });
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
            main_call_talk.setImageResource(R.mipmap.talk_voice_green_connect);
            pttClient.stopSpeak();
        }
        return true;
    }

    //    public void setSpeakerphoneOn (boolean on){
//        audioManager.setSpeakerphoneOn(on);
//        if(!on){
//            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
//        }
//    }
    boolean flag = true;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_call_free:
                if (flag) {
                    main_call_free.setImageResource(R.drawable.talk_voice_mode);
                    setSpeakerphoneOn(false);
                    flag = false;//开启了扬声器，讲图片设置为听筒
                } else {
                    main_call_free.setImageResource(R.drawable.talk_voice_handsfree);
                    setSpeakerphoneOn(true);
                    flag = true;
                }
                break;
            case R.id.main_call_flash:
//                getUserOnline(userid);
                CallPhone(phone);
                break;
            case R.id.main_call_talk:
                JoinInterCom();
                break;
            case R.id.main_call_end:
                pttClient.leaveSession();
                main_call_talk.setImageResource(R.drawable.talk_voice_normal);
                NToast.shortToast(getActivity(), "对讲已挂断");
                main_call_talk.setOnTouchListener(null);
                break;
        }
    }

    private void setSpeakerphoneOn(boolean on) {
        AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        if (on) {
            audioManager.setSpeakerphoneOn(true);
        } else {
            audioManager.setSpeakerphoneOn(false);//关闭扬声器
            getActivity().setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        }
    }

    private void JoinInterCom() {
        pttClient.init(getContext(), true, 12000);
        pttClient.joinSession(mConversationType, userid, new JoinSessionCallback() {
            @Override
            public void onSuccess(List<String> list) {
                main_call_talk.setImageResource(R.mipmap.talk_voice_green_connect);
                pttClient.setPttSessionStateListener(getInstance());
                main_call_talk.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return requestToSpeak(v, event);
                    }
                });
            }

            @Override
            public void onError(String s) {
                Log.e("OnSuccess", "对讲链接失败:" + s);
            }
        });
    }

//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(android.os.Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 1:
//                    main_call_talk.setImageResource(R.mipmap.talk_voice_green_connect);
//                    pttClient.setPttSessionStateListener(getInstance());
//                    main_call_talk.setOnTouchListener(new View.OnTouchListener() {
//                        @Override
//                        public boolean onTouch(View v, MotionEvent event) {
//                            return requestToSpeak(v, event);
//                        }
//                    });
//                    break;
//                case 0:
//                    JoinInterCom();
//                    break;
//            }
//        }
//    };

    @Override
    public void onParticipantChanged(PTTSession pttSession, List<String> userIds) {
    }

    //    /**
//     * * 获取用户在线状态
//     *
//     * @param userId   所要获取的用户Id
//     * @param callback 回调方法 @see {io.rong.imlib.IRongCallback.IGetUserOnlineStatusCallback}
//     *                 如果用户为离线状态,回调中返回的用户在线信息列表为 null
//     *                 用户在线状态信息 @see {io.rong.imlib.model.UserOnlineStatusInfo}
//     *                 <p/>
//     */
//    //public void getUserOnlineStatus
    private void CallPhone(final String phone) {
        talkPhoneDialog = new TalkPhoneDialog(getActivity(), dialoglogo, phone, dialogname);
        talkPhoneDialog.show();
    }

    @Override
    public void onMicHolderChanged(PTTSession pttSession, String holderUserId) {
        Log.e("PTTTTTT", "onMicHolderChanged------:" + pttSession.getTargetId() + "---holderUserId-:" + pttSession.getInitiator());
        InterComName = holderUserId;
    }

    @Override
    public void onNetworkError(String msg) {
        Log.e("PTT", "onNetworkError------:" + msg);

    }

    private void Listaaa(List ls) {
        List list = new ArrayList(Arrays.asList(new Object[ls.size()]));
        Collections.copy(list, ls);
    }

    //差集
    public static List diff(List ls, List ls2) {
        List list = new ArrayList(Arrays.asList(new Object[ls.size()]));
        Collections.copy(list, ls);
        list.removeAll(ls2);
        return list;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        pttClient.leaveSession();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // 处理权限申请的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 授权成功，继续打电话
                    Intent intentPhone = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                    intentPhone.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intentPhone);
                } else {
                    // 授权失败！
                    NToast.shortToast(getActivity(), "授权失败！");
                }
                break;
            }
        }

    }

}
