package com.tianfangIMS.im.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tianfangIMS.im.R;
import com.tianfangIMS.im.dialog.ChatFileMessageLongDialog;
import com.tianfangIMS.im.dialog.LoadDialog;
import com.tianfangIMS.im.dialog.PTTPushDialog;
import com.tianfangIMS.im.fragment.CallPhoneFragment;
import com.tianfangIMS.im.fragment.IntercomFragment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.rong.imkit.RongExtension;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imkit.manager.IUnReadMessageObserver;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Discussion;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.FileMessage;

/**
 * Created by LianMengYu on 2017/1/16.
 */

public class ConversationActivity extends BaseActivity implements View.OnClickListener, IUnReadMessageObserver, ViewPager.OnPageChangeListener, PTTPushDialog.DialogCallBackListener {
    private static final String TAG = "ConversationActivity";
    private String title;
    /**
     * 会话类型
     */
    private Conversation.ConversationType mConversationType;
    /**
     * 对方id
     */
    private String mTargetId;
    private boolean isFromPush = false;
    private String ImageLogo;//对方头像
    private LoadDialog mDialog;
    /**
     * 位置与聊天资料Button
     */
    private ImageButton loactionButton, contactsButton;
    public static ViewPager mViewpager;
    private List<Fragment> mFragment = new ArrayList<>();
    private ConversationFragment conversationDynamicFragment = null;
    private Fragment mConversationFragment = null;
    private ImageView tag_message, tag_intercom, tag_call;
    private List<ImageView> imageViewList = new ArrayList<>();
    private LinearLayout ll_talk;
    private UserInfo mUserInfo;
    private Context mContext;
    private RongExtension extension;
    private int page;
    List<Message> messageslist = new ArrayList<>();
    private boolean update = true;
    private FragmentManager fragmentManager;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private SharedPreferences sp_ptt;
    private int page11;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        sp_ptt = getSharedPreferences("pttActivity", Activity.MODE_PRIVATE);
        editor = sp_ptt.edit();
        mContext = this;
        initParentView();
        try {
            initConversationViewPager();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        RongIM.setConversationBehaviorListener(new MyConversationBehaviorListener());
        final Intent intent = getIntent();
        mTargetId = intent.getData().getQueryParameter("targetId");
        UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(RongIM.getInstance().getCurrentUserId());
        if (userInfo != null) {
            RongIM.getInstance().refreshUserInfoCache(userInfo);
        }
        //10000 为 Demo Server 加好友的 id，若 targetId 为 10000，则为加好友消息，默认跳转到 NewFriendListActivity
        // Demo 逻辑
        if (mTargetId != null && mTargetId.equals("10000")) {
            return;
        }
        mConversationType = Conversation.ConversationType.valueOf(intent.getData()
                .getLastPathSegment().toUpperCase(Locale.getDefault()));
        title = intent.getData().getQueryParameter("title");
        mConversationType = Conversation.ConversationType.valueOf(intent.getData()
                .getLastPathSegment().toUpperCase(Locale.getDefault()));
        messageslist = RongIM.getInstance().getHistoryMessages(mConversationType, mTargetId, -1, Integer.MAX_VALUE);
        isPushMessage(intent);
        /**
         * 设置右边Button
         */
        if (mConversationType.equals(Conversation.ConversationType.GROUP)) {
            contactsButton.setBackground(getResources().getDrawable(R.mipmap.conversation_contacts));
            contactsButton.setBackgroundDrawable(getResources().getDrawable(R.mipmap.conversation_contacts));
        } else if (mConversationType.equals(Conversation.ConversationType.PRIVATE) | mConversationType.equals(Conversation.ConversationType.PUBLIC_SERVICE) | mConversationType.equals(Conversation.ConversationType.DISCUSSION)) {
            contactsButton.setBackgroundDrawable(getResources().getDrawable(R.mipmap.conversation_contacts));
        } else {
            contactsButton.setVisibility(View.GONE);
            contactsButton.setClickable(false);
        }
        contactsButton.setOnClickListener(this);
        loactionButton.setOnClickListener(this);

        // android 6.0 以上版本，监听SDK权限请求，弹出对应请求框。
        if (Build.VERSION.SDK_INT >= 23) {
            RongIM.getInstance().setRequestPermissionListener(new RongIM.RequestPermissionsListener() {
                @SuppressLint("NewApi")
                @Override
                public void onPermissionRequest(String[] permissions, final int requestCode) {
                    for (final String permission : permissions) {
                        if (shouldShowRequestPermissionRationale(permission)) {
                            requestPermissions(new String[]{permission}, requestCode);
                        } else {
                            int isPermissionGranted = checkSelfPermission(permission);
                            if (isPermissionGranted != PackageManager.PERMISSION_GRANTED) {
                                new android.app.AlertDialog.Builder(ConversationActivity.this)
                                        .setMessage("你需要在设置里打开以下权限:" + permission)
                                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                            @SuppressLint("NewApi")
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                requestPermissions(new String[]{permission}, requestCode);
                                            }
                                        })
                                        .setNegativeButton("取消", null)
                                        .create().show();
                            }
                            return;
                        }
                    }
                }
            });
        }
        setActionBarTitle(mConversationType, mTargetId);
    }

    @Override
    public int callBack(int msg) {
        return msg;
    }

    private void initParentView() {
        loactionButton = getIv_conversation_loaction();
        contactsButton = getIv_conversation_contacts();
        tag_message = getIv_talk_message();
        tag_intercom = getIv_talk_intercom();
        tag_call = getIv_talk_call();
        ll_talk = getll_talk();

        ll_talk.setVisibility(View.VISIBLE);
        loactionButton.setVisibility(View.VISIBLE);
        contactsButton.setVisibility(View.VISIBLE);
        contactsButton.setVisibility(View.VISIBLE);
        tag_message.setVisibility(View.VISIBLE);
        tag_intercom.setVisibility(View.VISIBLE);
        tag_call.setVisibility(View.VISIBLE);

    }

    private void initConversationViewPager() throws IndexOutOfBoundsException {
        mViewpager = (ViewPager) this.findViewById(R.id.conversation);
//        mViewpager.setCurrentItem(page);'
        mConversationFragment = initConversation();
        mFragment.add(mConversationFragment);
        mFragment.add(new IntercomFragment());
        mFragment.add(new CallPhoneFragment());
        //添加tag
        imageViewList.add(tag_message);
        imageViewList.add(tag_intercom);
        imageViewList.add(tag_call);
        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragment.get(position);
            }

            @Override
            public int getCount() {
                return mFragment.size();
            }
        };
        try {
            Field field = mViewpager.getClass().getField("mCurItem");
            field.setAccessible(true);
            field.setInt(mViewpager, page);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intentpage = getIntent();
        page = intentpage.getIntExtra("intercom", 0);//第一个参数是取值的key,第二个参数是默认值
//        int page1 = sp_ptt.getInt("pttkey", 0);
        Log.e("page", "---:" + page);
        mViewpager.setAdapter(fragmentPagerAdapter);
        mViewpager.setCurrentItem(page);
        // 通过数据修改
        fragmentPagerAdapter.notifyDataSetChanged();
        // 切换到指定页面
        mViewpager.setOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                tag_message.setImageResource(R.mipmap.talk_message_selected);
                tag_call.setImageResource(R.mipmap.talk_call);
                tag_intercom.setImageResource(R.mipmap.talk_intercom);
                break;
            case 1:
                tag_message.setImageResource(R.mipmap.talk_message);
                tag_call.setImageResource(R.mipmap.talk_call);
                tag_intercom.setImageResource(R.mipmap.talk_intercom_selected);
                break;
            case 2:
                tag_message.setImageResource(R.mipmap.talk_message);
                tag_call.setImageResource(R.mipmap.talk_call_selected);
                tag_intercom.setImageResource(R.mipmap.talk_intercom);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        Log.e(TAG, "滑动的状态" + state);
    }

    @Override
    public void onCountChanged(int count) {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    protected void initData() {

        final Conversation.ConversationType[] conversationTypes = {
                Conversation.ConversationType.PRIVATE,
                Conversation.ConversationType.GROUP, Conversation.ConversationType.SYSTEM,
                Conversation.ConversationType.PUBLIC_SERVICE, Conversation.ConversationType.APP_PUBLIC_SERVICE
        };
        RongIM.getInstance().addUnReadMessageCountChangedObserver(this, conversationTypes);
//        getConversationPush();// 获取 push 的 id 和 target
//        getPushMessage();
    }

//    private void enterActivity() {
//        String token = sp.getString("token", "");
//        if (token.equals("default")) {
//            startActivity(new Intent(ConversationActivity.this, LoginActivity.class));
//        } else {
////            reconnect(token);
//            startActivity(new Intent(ConversationActivity.this, MainActivity.class));
//        }
//    }

    /**
     * 判断是否是 Push 消息，判断是否需要做 connect 操作
     */
    private void isPushMessage(Intent intent) {
        if (intent == null || intent.getData() == null)
            return;
        //push
        if (intent.getData().getScheme().equals("rong") && intent.getData().getQueryParameter("isFromPush") != null) {
            //通过intent.getData().getQueryParameter("push") 为true，判断是否是push消息
            if (intent.getData().getQueryParameter("isFromPush").equals("true")) {
                //只有收到系统消息和不落地 push 消息的时候，pushId 不为 null。而且这两种消息只能通过 server 来发送，客户端发送不了。
                //RongIM.getInstance().getRongIMClient().recordNotificationEvent(id);
                if (mDialog != null && !mDialog.isShowing()) {
                    mDialog.show();
                }
                isFromPush = true;
                enterActivity();
            } else if (RongIM.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED)) {
                if (mDialog != null && !mDialog.isShowing()) {
                    mDialog.show();
                }
                if (intent.getData().getPath().contains("conversation/system")) {
                    Intent intent1 = new Intent(mContext, MainActivity.class);
                    intent1.putExtra("systemconversation", true);
                    startActivity(intent1);
                    return;
                }
                enterActivity();
            } else {
                if (intent.getData().getPath().contains("conversation/system")) {
                    Intent intent1 = new Intent(mContext, MainActivity.class);
                    intent1.putExtra("systemconversation", true);
                    startActivity(intent1);
                    return;
                }
                enterFragment(mConversationType, mTargetId);
            }

        } else {
            if (RongIM.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED)) {
                if (mDialog != null && !mDialog.isShowing()) {
                    mDialog.show();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        enterActivity();
                    }
                }, 300);
            } else {
                enterFragment(mConversationType, mTargetId);
            }
        }
    }

    /**
     * 收到 push 消息后，选择进入哪个 Activity
     * 如果程序缓存未被清理，进入 MainActivity
     * 程序缓存被清理，进入 LoginActivity，重新获取token
     * <p>
     * 作用：由于在 manifest 中 intent-filter 是配置在 ConversationActivity 下面，所以收到消息后点击notifacition 会跳转到 DemoActivity。
     * 以跳到 MainActivity 为例：
     * 在 ConversationActivity 收到消息后，选择进入 MainActivity，这样就把 MainActivity 激活了，当你读完收到的消息点击 返回键 时，程序会退到
     * MainActivity 页面，而不是直接退回到 桌面。
     */
    private void enterActivity() {
        String token = sp.getString("token", "");
        if (token.equals("default")) {
            Log.e("ConversationActivity push", "push2");
            startActivity(new Intent(ConversationActivity.this, LoginActivity.class));
        } else {
            Log.e("ConversationActivity push", "push3");
            reconnect(token);
        }
    }

    private void reconnect(String token) {
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                Log.e(TAG, "---onTokenIncorrect--");
            }

            @Override
            public void onSuccess(String s) {
                Log.i(TAG, "---onSuccess--" + s);
                Log.e("ConversationActivity push", "push4");
                if (mDialog != null)
                    mDialog.dismiss();
                initConversation();
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                Log.e(TAG, "---onError--" + e);
                if (mDialog != null)
                    mDialog.dismiss();

                initConversation();
            }
        });
    }


    private Fragment initConversation() {
        if (conversationDynamicFragment == null) {
            Intent intent = getIntent();
            mTargetId = intent.getData().getQueryParameter("targetId");
            mConversationType = Conversation.ConversationType.valueOf(intent.getData().getLastPathSegment().toUpperCase(Locale.getDefault()));
            conversationDynamicFragment = new ConversationFragment();
            Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                    .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
                    .appendQueryParameter("targetId", mTargetId).build();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            conversationDynamicFragment.setUri(uri);
            transaction.commit();
            return conversationDynamicFragment;
        } else {
            return conversationDynamicFragment;
        }
    }

    //    /**
//     * 设置私聊界面 ActionBar
//     */
    private void setPrivateActionBar(String targetId) {
        if (!TextUtils.isEmpty(title)) {
            if (title.equals("null")) {
                if (!TextUtils.isEmpty(targetId)) {
                    UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(targetId);
                    if (userInfo != null) {
                        setTitle(userInfo.getName());
                    }
                }
            } else {
                setTitle(title);
            }

        } else {
            setTitle(targetId);
        }
    }

    //
//    /**
//     * 设置群聊界面 ActionBar
//     *
//     * @param targetId 会话 Id
//     */
    private void setGroupActionBar(String targetId) {
        if (!TextUtils.isEmpty(title)) {
            setTitle(title);
        } else {
            setTitle(targetId);
        }
    }

    //
//    /**
//     * 设置讨论组界面 ActionBar
//     */
    private void setDiscussionActionBar(String targetId) {

        if (targetId != null) {

            RongIM.getInstance().getDiscussion(targetId
                    , new RongIMClient.ResultCallback<Discussion>() {
                        @Override
                        public void onSuccess(Discussion discussion) {
                            setTitle(discussion.getName());
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode e) {
                            if (e.equals(RongIMClient.ErrorCode.NOT_IN_DISCUSSION)) {
                                setTitle("不在讨论组中");
                                supportInvalidateOptionsMenu();
                            }
                        }
                    });
        } else {
            setTitle("讨论组");
        }
    }

    /**
     * 设置标题
     */
    public void setTitle(String title) {
        setTitle(title, false);
    }

    /**
     * 设置会话页面 Title
     *
     * @param conversationType 会话类型
     * @param targetId         目标 Id
     */
    private void setActionBarTitle(Conversation.ConversationType conversationType, String targetId) {

        if (conversationType == null)
            return;
        if (conversationType.equals(Conversation.ConversationType.PRIVATE)) {
            setPrivateActionBar(targetId);
        } else if (conversationType.equals(Conversation.ConversationType.GROUP)) {
            setGroupActionBar(targetId);
        } else if (conversationType.equals(Conversation.ConversationType.DISCUSSION)) {
            setDiscussionActionBar(targetId);
        } else if (conversationType.equals(Conversation.ConversationType.CHATROOM)) {
            setTitle(title);
        } else if (conversationType.equals(Conversation.ConversationType.SYSTEM)) {
            setTitle("系统消息");
        } else if (conversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE)) {
            setTitle("意见反馈");
        } else {
            setTitle("聊天");
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * 根据 targetid 和 ConversationType 进入到设置页面
     */
    private void enterSettingActivity() {

        if (mConversationType == Conversation.ConversationType.PUBLIC_SERVICE
                || mConversationType == Conversation.ConversationType.APP_PUBLIC_SERVICE) {
            RongIM.getInstance().startPublicServiceProfile(this, mConversationType, mTargetId);
        } else {
            Intent intent = null;
            if (mConversationType == Conversation.ConversationType.GROUP) {
                intent = new Intent(this, GroupDetailActivity.class);
                intent.putExtra("conversationType", Conversation.ConversationType.GROUP);
            } else if (mConversationType == Conversation.ConversationType.PRIVATE) {
                intent = new Intent(this, PrivateChatDetailActivity.class);
                intent.putExtra("conversationType", Conversation.ConversationType.PRIVATE);
            } else if (mConversationType == Conversation.ConversationType.DISCUSSION) {
                intent = new Intent(this, DiscussionDetailActivity.class);
                intent.putExtra("TargetId", mTargetId);
                startActivityForResult(intent, 166);
                return;
            }
            intent.putExtra("TargetId", mTargetId);
            if (intent != null) {
                startActivityForResult(intent, 500);
//                this.finish();
            }

        }
    }

    //传递userinfo到位置共享
    private void JoinAMapShare() {
        Intent intent = null;
        intent = new Intent(this, AMapShareLocationActivity.class);
        if (mConversationType == Conversation.ConversationType.GROUP) {
            intent.putExtra("conversationType", Conversation.ConversationType.GROUP);
        } else if (mConversationType == Conversation.ConversationType.PRIVATE) {
            intent.putExtra("conversationType", Conversation.ConversationType.PRIVATE);
        }
        intent.putExtra("mTargetId", mTargetId);
        intent.putExtra("title", title);
        if (intent != null) {
            startActivity(intent);
        }
    }

    private ConversationFragmentEx fragmentEx;

    /**
     * 加载会话页面 ConversationFragmentEx 继承自 ConversationFragment
     *
     * @param mConversationType 会话类型
     * @param mTargetId         会话 Id
     */
    private void enterFragment(Conversation.ConversationType mConversationType, String mTargetId) {

        fragmentEx = new ConversationFragmentEx();

        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
                .appendQueryParameter("targetId", mTargetId).build();

        fragmentEx.setUri(uri);
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        fragmentEx.setUri(uri);
//        transaction.commitAllowingStateLoss();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //xxx 为你要加载的 id
        transaction.commitAllowingStateLoss();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_conversation_contacts:
                enterSettingActivity();
                break;
            case R.id.iv_conversation_location:
                JoinAMapShare();
        }
    }

    private class MyConversationBehaviorListener implements RongIM.ConversationBehaviorListener {
        @Override
        public boolean onMessageClick(Context context, View view, Message message) {
            return false;
        }

        @Override
        public boolean onUserPortraitClick(Context context, Conversation.ConversationType mConversationType, UserInfo userInfo) {
//            NToast.longToast(context, "点击了头像" + userInfo.getUserId() + "会话类型" + mConversationType.getValue());
            String userID = userInfo.getUserId().toString();
            Intent intent = new Intent(mContext, FriendPersonInfoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("userId", userID);
            intent.putExtras(bundle);
            intent.putExtra("conversationType", Conversation.ConversationType.PRIVATE);
            startActivity(intent);
            finish();

//            }
            return true;
        }

        @Override
        public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
            return false;
        }

        @Override
        public boolean onMessageLinkClick(Context context, String s) {
            return false;
        }

        @Override
        public boolean onMessageLongClick(Context context, View view, Message message) {
            //文件消息转发与删除逻辑
            MessageContent messageContent = message.getContent();
            if (messageContent instanceof FileMessage) {
                ChatFileMessageLongDialog chatFileMessageDialog = new ChatFileMessageLongDialog(mContext, message);
                chatFileMessageDialog.show();
                return true;
            } else {
                return false;
            }
        }
    }

}
