package com.tianfangIMS.im.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.bean.SealSearchConversationResult;
import com.tianfangIMS.im.dialog.CleanChatLogDialog;
import com.tianfangIMS.im.utils.CommonUtil;
import com.tianfangIMS.im.utils.NToast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ImageMessage;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by LianMengYu on 2017/1/21.
 * 个人聊天设置页面
 */
public class PrivateChatDetailActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "PrivateChatDetailActivity";
    private static final int SEARCH_TYPE_FLAG = 1;
    private UserInfo mUserInfo;
    private Conversation.ConversationType mConversationType;
    private String fromConversationId;
    private ImageView iv_user_photo;
    private TextView tv_user_name;
    private Context mContext;
    private ImageView iv_createGroup;
    private RelativeLayout privateChat_file, privateChat_searchChatting;
    private LinearLayout ly_privatechat_clean;
    private CompoundButton CompoundButton;
    PhotoViewAttacher mAttacher;
    private SharedPreferences.Editor editor;
    private SharedPreferences sp;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private SealSearchConversationResult mResult;
    private boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privatechatdetail_activity);
        mContext = this;
        setTitle("聊天信息");
        init();
        fromConversationId = getIntent().getStringExtra("TargetId");
        mConversationType = (Conversation.ConversationType) getIntent().getSerializableExtra("conversationType");
        if (!TextUtils.isEmpty(fromConversationId)) {
            mUserInfo = RongUserInfoManager.getInstance().getUserInfo(fromConversationId);
            updateUI();
        }
        System.out.println("看看userinfo里都有什么:" + fromConversationId);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        sp = getSharedPreferences("config", MODE_PRIVATE);
        editor = sp.edit();
        RongIMClient.getInstance().getConversationNotificationStatus(mConversationType, fromConversationId, new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
            @Override
            public void onSuccess(Conversation.ConversationNotificationStatus conversationNotificationStatus) {
                if (conversationNotificationStatus.getValue() == 1) {
                    flag = sp.getBoolean("PrivatechatisOpen", false);
                } else {
                    flag = sp.getBoolean("PrivatechatisOpen", true);
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });

        CompoundButton.setChecked(flag);
    }

    private void init() {
        iv_user_photo = (ImageView) this.findViewById(R.id.iv_user_photo);
        tv_user_name = (TextView) this.findViewById(R.id.tv_conversationdetail_username);
        iv_createGroup = (ImageView) this.findViewById(R.id.iv_createGroup);
        privateChat_file = (RelativeLayout) this.findViewById(R.id.privateChat_file);
        ly_privatechat_clean = (LinearLayout) this.findViewById(R.id.ly_privatechat_clean);
        iv_user_photo = (ImageView) this.findViewById(R.id.iv_user_photo);
        CompoundButton = (CompoundButton) this.findViewById(R.id.sw_conversationdetail_notfaction);
        privateChat_searchChatting = (RelativeLayout) this.findViewById(R.id.privateChat_searchChatting);

        privateChat_searchChatting.setOnClickListener(this);
        iv_user_photo.setOnClickListener(this);
        ly_privatechat_clean.setOnClickListener(this);
        privateChat_file.setOnClickListener(this);
        iv_createGroup.setOnClickListener(this);
        CompoundButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    RongIMClient.getInstance().setConversationNotificationStatus(mConversationType, fromConversationId,
                            Conversation.ConversationNotificationStatus.DO_NOT_DISTURB, new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
                                @Override
                                public void onSuccess(Conversation.ConversationNotificationStatus conversationNotificationStatus) {
                                    Log.e("免打扰", "---:" + conversationNotificationStatus);
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {
                                    Log.e("免打扰", "---:" + errorCode);
                                    NToast.shortToast(mContext, "开启免打扰失败");
                                }
                            });
                    editor.putBoolean("PrivatechatisOpen", true);
                    editor.apply();
                } else {
                    RongIMClient.getInstance().setConversationNotificationStatus(mConversationType, fromConversationId,
                            Conversation.ConversationNotificationStatus.NOTIFY, new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
                                @Override
                                public void onSuccess(Conversation.ConversationNotificationStatus conversationNotificationStatus) {
                                    Log.e("免打扰", "---:" + conversationNotificationStatus);
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {
                                    Log.e("免打扰", "---:" + errorCode);
                                    NToast.shortToast(mContext, "关闭免打扰失败");
                                }
                            });
                    editor.putBoolean("PrivatechatisOpen", false);
                    editor.apply();
                }
            }
        });
    }

    /**
     * 获取历史小的图片消息内容
     */
    private void GetHistoryMessages() {
        List<Message> list = RongIMClient.getInstance().getHistoryMessages(mConversationType, fromConversationId, -1, Integer.MAX_VALUE);
        List<ImageMessage> msg = new ArrayList<>();
        List<Uri> urilist = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            MessageContent messageContent = list.get(i).getContent();
            if (messageContent instanceof ImageMessage) {
                ImageMessage imageMessage = (ImageMessage) messageContent;
                msg.add(imageMessage);
            }
        }
        for (int j = 0; j < msg.size(); j++) {
            Uri aa = msg.get(j).getLocalUri();
            if (aa != null) {
                urilist.add(aa);
            }
        }
        if (urilist != null && urilist.size() > 0) {
            Intent intent = new Intent(mContext, SelectPhoteActivity.class);
            intent.putExtra("photouri", (Serializable) urilist);
            Log.e("", "---:" + urilist);
            startActivity(intent);
        } else {
            NToast.shortToast(mContext, "没有数据");
        }
    }

    private void updateUI() {
        if (mUserInfo != null) {
            Picasso.with(mContext)
                    .load(mUserInfo.getPortraitUri())
                    .resize(50, 50)
                    .into(iv_user_photo);
            tv_user_name.setText(mUserInfo.getName());
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("PrivateChatDetail Page")
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_createGroup:
                Intent intent = new Intent(mContext, AddGroupActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("PrivateChat", fromConversationId);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.privateChat_file:
                GetHistoryMessages();
                break;
            case R.id.ly_privatechat_clean:
                CleanChatLogDialog dialog = new CleanChatLogDialog(mContext, mConversationType, fromConversationId);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable());
                dialog.show();
                CommonUtil.SetCleanDialogStyle(dialog);
                break;
            case R.id.iv_user_photo:
                Intent detailintent = new Intent(mContext, FriendPersonInfoActivity.class);
                Bundle detailbundle = new Bundle();
                detailbundle.putString("userId", fromConversationId);
                detailintent.putExtras(detailbundle);
                detailintent.putExtra("conversationType", Conversation.ConversationType.PRIVATE);
                startActivity(detailintent);
                this.finish();
                break;
            case R.id.privateChat_searchChatting:
                Intent searchIntent = new Intent(mContext, SearchChattingDetailActivity.class);
                ArrayList<Message> arrayList = new ArrayList<>();
                searchIntent.putParcelableArrayListExtra("filterMessages", arrayList);
                mResult = new SealSearchConversationResult();
                Conversation conversation = new Conversation();
                conversation.setTargetId(fromConversationId);
                conversation.setConversationType(mConversationType);
                mResult.setConversation(conversation);
                if (mUserInfo != null) {
                    String portraitUri = mUserInfo.getPortraitUri().toString();
                    mResult.setId(mUserInfo.getUserId());
                    if (!TextUtils.isEmpty(portraitUri)) {
                        mResult.setPortraitUri(portraitUri);
                    }
                    if (!TextUtils.isEmpty(mUserInfo.getName())) {
                        mResult.setTitle(mUserInfo.getName());
                    }
                } else {
                    UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(conversation.getTargetId());
                    mResult.setId(conversation.getTargetId());
                    String portraitUri = userInfo.getPortraitUri().toString();
                    if (!TextUtils.isEmpty(portraitUri)) {
                        mResult.setPortraitUri(portraitUri);
                    }
                    if (!TextUtils.isEmpty(userInfo.getName())) {
                        mResult.setTitle(userInfo.getName());
                    } else {
                        mResult.setTitle(userInfo.getUserId());
                    }
                }
                searchIntent.putExtra("searchConversationResult", mResult);
                searchIntent.putExtra("flag", SEARCH_TYPE_FLAG);
                startActivity(searchIntent);
                break;
        }
    }
}
