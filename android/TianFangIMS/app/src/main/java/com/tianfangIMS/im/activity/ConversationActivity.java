package com.tianfangIMS.im.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.bean.LoginBean;
import com.tianfangIMS.im.dialog.LoadDialog;
import com.tianfangIMS.im.fragment.CallPhoneFragment;
import com.tianfangIMS.im.fragment.IntercomFragment;
import com.tianfangIMS.im.utils.CommonUtil;
import com.tianfangIMS.im.utils.NToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.rong.imkit.RongExtension;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imkit.fragment.UriFragment;
import io.rong.imkit.manager.IUnReadMessageObserver;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Discussion;
import io.rong.imlib.model.UserInfo;

/**
 * Created by LianMengYu on 2017/1/16.
 */

public class ConversationActivity extends BaseActivity implements View.OnClickListener, IUnReadMessageObserver, ViewPager.OnPageChangeListener {
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
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.conversation);
        setContentView(R.layout.conversation);
        mContext = this;
        initParentView();
        initConversationViewPager();
        final Intent intent = getIntent();
        mTargetId = intent.getData().getQueryParameter("targetId");
        Log.e("mTargetId", ":" + mTargetId);
        //10000 为 Demo Server 加好友的 id，若 targetId 为 10000，则为加好友消息，默认跳转到 NewFriendListActivity
        // Demo 逻辑
        if (mTargetId != null && mTargetId.equals("10000")) {
//            startActivity(new Intent(ConversationActivity.this, NewFriendListActivity.class));
            return;
        }

        mConversationType = Conversation.ConversationType.valueOf(intent.getData()
                .getLastPathSegment().toUpperCase(Locale.getDefault()));

        title = intent.getData().getQueryParameter("title");
        mConversationType = Conversation.ConversationType.valueOf(intent.getData()
                .getLastPathSegment().toUpperCase(Locale.getDefault()));
        /**
         * 设置右边Button
         */
        isPushMessage(intent);
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
//        enterFragment(mConversationType,mTargetId);
        setActionBarTitle(mConversationType, mTargetId);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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

    private void initConversationViewPager() {

        mConversationFragment = initConversation();
        mViewpager = (ViewPager) this.findViewById(R.id.conversation);
        mFragment.add(mConversationFragment);
        mFragment.add(IntercomFragment.getInstance());
        mFragment.add(CallPhoneFragment.getInstance());
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
        mViewpager.setAdapter(fragmentPagerAdapter);
//        mViewpager.setOffscreenPageLimit(4);
        mViewpager.setOnPageChangeListener(this);
//        initData();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.e(TAG, "当前那个页面被选中：" + position);
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
    //Activity的启动模式(launchMode),通过这个方法接受Intent
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        if (intent.getBooleanExtra("systemconversation", false)) {
//            mViewpager.setCurrentItem(0, false);
//        }
//    }

//    /**
//     * @Data_Time 2015年7月16日 下午10:37:12
//     * @Description { 处理Intent }
//     * @param intent
//     */
//    private void handleIntent(Intent intent){
//        if(intent!=null){
//            String news_code=intent.getExtras().getString("news_code");
////          LogUtil.e("MainTabActivity", "news_code  :"+news_code);
//            myprefs.news_code().put(news_code);
//        }
//    }
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

    //    @Override
//    public void initView() {
//
//    }
    private Fragment initConversation() {
        if (conversationDynamicFragment == null) {
            Intent intent = getIntent();
            mTargetId = intent.getData().getQueryParameter("targetId");
            page = intent.getIntExtra("page", 0);
            if (page == 1) {
                NToast.shortToast(mContext, "跳转成功");
            }
            mConversationType = Conversation.ConversationType.valueOf(intent.getData().getLastPathSegment().toUpperCase(Locale.getDefault()));
            ConversationFragment fragment = new ConversationFragment();

            Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                    .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
                    .appendQueryParameter("targetId", mTargetId).build();

            fragment.setUri(uri);

             /* 加载 ConversationFragment */
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            transaction.add(R.id.fragment_view, fragment);
            transaction.commit();
            return fragment;
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

    //
//    /**
//     * ATTENTION: This was auto-generated to implement the App Indexing API.
//     * See https://g.co/AppIndexing/AndroidStudio for more information.
//     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Conversation Page")
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

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

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

    private void enterActivity() {


        Gson gson = new Gson();
        LoginBean loginBean = gson.fromJson(CommonUtil.getUserInfo(mContext), LoginBean.class);
        String token = loginBean.getText().getToken();
        if (!TextUtils.isEmpty(token)) {
            if (token.equals("default")) {
                startActivity(new Intent(ConversationActivity.this, LoginActivity.class));
            } else {
                reconnect(token);
            }
        } else {
            NToast.shortToast(mContext, "token无效");
        }

    }

    private void reconnect(String token) {
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {

            }

            @Override
            public void onSuccess(String s) {
                if (mDialog != null)
                    mDialog.dismiss();

                enterFragment(mConversationType, mTargetId);

            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                if (mDialog != null)
                    mDialog.dismiss();

                enterFragment(mConversationType, mTargetId);
            }
        });

    }


    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    /**
     * 根据 targetid 和 ConversationType 进入到设置页面
     */
    private void enterSettingActivity() {

        if (mConversationType == Conversation.ConversationType.PUBLIC_SERVICE
                || mConversationType == Conversation.ConversationType.APP_PUBLIC_SERVICE) {
            RongIM.getInstance().startPublicServiceProfile(this, mConversationType, mTargetId);
        } else {
            UriFragment fragment = (UriFragment) getSupportFragmentManager().getFragments().get(0);
            //得到讨论组的 targetId
            mTargetId = fragment.getUri().getQueryParameter("targetId");

            if (TextUtils.isEmpty(mTargetId)) {
                NToast.shortToast(mContext, "讨论组尚未创建成功");
            }


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
                this.finish();
            }

        }
    }

    //传递userinfo到位置共享
    private void JoinAMapShare() {
        Intent intent = null;
        intent = new Intent(this, AMapShareActivity.class);
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

    /**
     * 加载会话页面 ConversationFragmentEx 继承自 ConversationFragment
     *
     * @param mConversationType 会话类型
     * @param mTargetId         会话 Id
     */
    private void enterFragment(Conversation.ConversationType mConversationType, String mTargetId) {


        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
                .appendQueryParameter("targetId", mTargetId).build();

//        fragment.setUri(uri);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //xxx 为你要加载的 id
//        transaction.add(R.id.conversation, fragment);
        transaction.commitAllowingStateLoss();
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
}
