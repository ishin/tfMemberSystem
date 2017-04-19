package com.tianfangIMS.im.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
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
import com.tianfangIMS.im.bean.OneGroupBean;
import com.tianfangIMS.im.dialog.LoadDialog;

import net.qiujuer.genius.blur.StackBlur;

import java.util.List;
import java.util.Locale;

import io.rong.common.RLog;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.utilities.PermissionCheckUtil;
import io.rong.imlib.model.Conversation;
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

    public static IntercomFragment getInstance() {
        if (Instance == null) {
            Instance = new IntercomFragment();
        }
        return Instance;
    }
    ImageView main_call_blur;
    ImageView main_call_header;
    private Conversation.ConversationType mConversationType;
    ImageView main_call_free, main_call_flash, main_call_talk;
    private String userid;
    private UserInfo userInfo;
    private TextView intercom_name;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.intercom_layout, container, false);
        Intent intent = getActivity().getIntent();
        main_call_blur = (ImageView) view.findViewById(R.id.main_call_blur);
        main_call_header = (ImageView) view.findViewById(R.id.main_call_header);
        main_call_free = (ImageView) view.findViewById(R.id.main_call_free);
        main_call_flash = (ImageView) view.findViewById(R.id.main_call_flash);
        main_call_talk = (ImageView) view.findViewById(R.id.main_call_talk);
        intercom_name = (TextView) view.findViewById(R.id.intercom_name);
        ceshi = (Button) view.findViewById(R.id.ceshi);
        ceshi.setOnClickListener(this);
        setListener();
        mConversationType = Conversation.ConversationType.valueOf(intent.getData()
                .getLastPathSegment().toUpperCase(Locale.getDefault()));
        userid = intent.getData().getQueryParameter("targetId");
        //获取userinfo
        if (mConversationType == Conversation.ConversationType.PRIVATE) {
            userInfo = RongUserInfoManager.getInstance().getUserInfo(userid);
            if (userInfo != null) {
                Log.e("已经不等于空了：", "---:" + userInfo);
                intercom_name.setText(userInfo.getName());
                Log.e("intercom", "确实是否执行：" + userInfo.getName());
                getBitmap(userInfo.getPortraitUri().toString());
            }
        }
        if (mConversationType == Conversation.ConversationType.GROUP) {
            userid = intent.getData().getQueryParameter("targetId");
            GetGroupUserInfo();
            Log.e("intercom", "群组id" + userid);
        }
        if (!PermissionCheckUtil.checkPermissions(getActivity(), new String[]{android.Manifest.permission.RECORD_AUDIO})) {
            PermissionCheckUtil.requestPermissions(getInstance(), new String[]{Manifest.permission.RECORD_AUDIO});
        }
        setListener();
        return view;
    }

    private void GetGroupUserInfo() {
        OkGo.post(ConstantValue.GETONEGROUPINFO)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .params("groupid", userid)
                .execute(new StringCallback() {
                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (!TextUtils.isEmpty(s) && !s.equals("{}")) {
                            Gson gson = new Gson();
                            OneGroupBean oneGroupBean = gson.fromJson(s, OneGroupBean.class);
                            intercom_name.setText(oneGroupBean.getText().getName());
                            getBitmap(ConstantValue.ImageFile + oneGroupBean.getText().getLogo());
                            Log.e("intercom", "群组成员都有什么：" + oneGroupBean.getText().getName());
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
                .execute(new BitmapCallback() {
//                    @Override
//                    public void onBefore(BaseRequest request) {
//                        super.onBefore(request);
//                        LoadDialog.show(getActivity());
//                    }

                    @Override
                    public void onSuccess(Bitmap bitmap, Call call, Response response) {
                        LoadDialog.dismiss(getActivity());
                        if (bitmap != null) {
                            Bitmap newBitmap = StackBlur.blur(bitmap, (int) 20, false);
                            main_call_blur.setImageBitmap(newBitmap);
                            main_call_header.setImageBitmap(bitmap);
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
//                    updateMicHolder(RongIMClient.getInstance().getCurrentUserId());

                }

                //抢麦失败
                @Override
                public void onFail(String msg) {
                    RLog.e("onFail", "start speak error " + msg);
                }


                //说话超时，通过服务器设定时长，如果超过自动停止说话
                @Override
                public void onSpeakTimeOut() {
                    Toast.makeText(getActivity(), "speak time out", Toast.LENGTH_SHORT).show();
//                    updateMicHolder("");
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
                getActivity().getSystemService(Context.AUDIO_SERVICE);
                AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
                if (flag) {
                    main_call_free.setImageResource(R.drawable.talk_voice_mode);
                    audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                    flag = false;//开启了扬声器，讲图片设置为听筒
                    audioManager.setMicrophoneMute(false);
                    audioManager.setSpeakerphoneOn(true);
                } else {
                    main_call_free.setImageResource(R.drawable.talk_voice_handsfree);
                    audioManager.setMode(AudioManager.MODE_IN_CALL);
                    flag = true;
                }
                break;
            case R.id.main_call_flash:
                break;
            case R.id.main_call_talk:
                Toast.makeText(getActivity(), "点击了对讲", Toast.LENGTH_SHORT).show();
                JoinInterCom();
                break;
            case R.id.ceshi:
                break;
        }
    }

    private void JoinInterCom() {
        pttClient = PTTClient.getInstance();
        pttClient.init(getContext());
        pttClient.joinSession(mConversationType, userid, new JoinSessionCallback() {
            @Override
            public void onSuccess(List<String> list) {
                Log.e("OnSuccess", "测试对讲连接成功");
                main_call_talk.setImageResource(R.mipmap.talk_voice_green_connect);
                pttClient.setPttSessionStateListener(getInstance());
                PTTSession pttSession = pttClient.getCurrentPttSession();
                participants = pttSession.getParticipantIds();
                pttClient = PTTClient.getInstance();
                Log.e("你好", "::" + list);
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
    @Override
    public void onMicHolderChanged(PTTSession pttSession, String s) {

    }

    @Override
    public void onParticipantChanged(PTTSession pttSession, List<String> list) {

    }

    @Override
    public void onNetworkError(String s) {

    }
}
