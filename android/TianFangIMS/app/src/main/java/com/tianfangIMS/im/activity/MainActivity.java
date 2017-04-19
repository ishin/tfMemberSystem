package com.tianfangIMS.im.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.tianfangIMS.im.ConstantValue;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.adapter.ConversationAdapter;
import com.tianfangIMS.im.bean.GroupBean;
import com.tianfangIMS.im.bean.GroupListBean;
import com.tianfangIMS.im.bean.LoginBean;
import com.tianfangIMS.im.bean.SetSyncUserBean;
import com.tianfangIMS.im.bean.TopContactsBean;
import com.tianfangIMS.im.bean.TopContactsListBean;
import com.tianfangIMS.im.dialog.ConversationListLongDialog;
import com.tianfangIMS.im.dialog.MainPlusDialog;
import com.tianfangIMS.im.fragment.Contacts_Fragment;
import com.tianfangIMS.im.fragment.ConversationListDynamicActivtiy;
import com.tianfangIMS.im.fragment.Jobs_Fragment;
import com.tianfangIMS.im.fragment.Message_Fragment;
import com.tianfangIMS.im.fragment.Mine_Fragment;
import com.tianfangIMS.im.service.FloatService;
import com.tianfangIMS.im.utils.CommonUtil;
import com.tianfangIMS.im.utils.NToast;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imkit.manager.IUnReadMessageObserver;
import io.rong.imkit.manager.UnReadMessageManager;
import io.rong.imkit.model.GroupUserInfo;
import io.rong.imkit.model.UIConversation;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.ptt.PTTClient;
import io.rong.ptt.PTTSession;
import io.rong.ptt.PTTStateListener;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by LianMengYu on 2017/1/3.
 * 主要作为所有fragment的基类来使用
 * <p>
 * 我具体的实现，就是 在会话列表类中 implements 一个 RongIM.UserInfoProvider
 */

public class MainActivity extends BaseActivity implements View.OnClickListener, RongIM.UserInfoProvider, RongIM.GroupInfoProvider, IUnReadMessageObserver {
    private static final String TAG = "MainActivity";
    private LinearLayout ly_tab_menu_msg, ly_tab_menu_job, ly_tab_menu_contacts, ly_tab_menu_me;
    private TextView tv_tab_menu_msg, tv_tab_menu_job, tv_tab_menu_contacts, tv_tab_menu_me;
    private TextView tv_tab_menu_msg_num, tv_tab_menu_job_num, tv_tab_menu_contacts_num;
    private ImageView img_tab_menu_setting_partner;
    private FrameLayout fragment_container;

    private LinearLayout ly_set_firstFragment;

    private Message_Fragment message_fragment;
    private Jobs_Fragment Jobs_Fragment;
    private Contacts_Fragment Contacts_Fragment;
    private Mine_Fragment Me_Fragment;
    private ImageView main_plus;//首页“+”号
    //会话列表
    private Fragment mConversationListFragment = null;
    private Fragment mConversationList;
    //会话列表Activity
    private boolean isDebug;
    private Conversation.ConversationType[] mConversationsTypes = null;
    private boolean ischeck = true;
    //    FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private FragmentManager fragmentManager;
    private List<LoginBean> mLoginBeanList;
    private ImageView main_button;
    private TopContactsBean topContactsBean;
    private List<TopContactsBean> topContactsList;
    private ImageView main_tree;
    private ConversationListDynamicActivtiy conversationListDynamicActivtiy;//会话列表
    private Map<String, Boolean> supportedConversation;
    private LinearLayout search_layout;
    private EditText et_search;
    private UserInfo userInfo;
    private GroupUserInfo groupUserinfo;
    Intent mIntent;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetSyncUserGroup();
        Gson gson = new Gson();
        LoginBean loginBean = gson.fromJson(CommonUtil.getUserInfo(mContext), LoginBean.class);
        String token = loginBean.getText().getToken();
        if (!TextUtils.isEmpty(token)) {

            RongIM.connect(token, new RongIMClient.ConnectCallback() {
                @Override
                public void onTokenIncorrect() {
                }

                @Override
                public void onSuccess(String s) {
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                }
            });
        }
        setContentView(R.layout.mian_activity);
        GetFriendInfo();//持久化所有好友信息
        GetGroupInfo();//持久化所有群组信息
        setHeadVisibility(View.GONE);
        SystemBarTranslucentType(this);//改变状态栏的沉浸样式
        init();//初始化控件
        initFM();//初始化fragment
        mLoginBeanList = new ArrayList<LoginBean>();
        mLoginBeanList.add(GetUesrBean());
        RongIM.setUserInfoProvider(this, true);
        RongIM.setGroupInfoProvider(this, true);
        RongIM.getInstance().setMessageAttachedUserInfo(true);
        RongIM.setConversationBehaviorListener(new MyConversationBehaviorListener());
        RongIM.setConversationListBehaviorListener(new MyConversationListBehaviorListener());
        UnReadMessageManager.getInstance().addObserver(
                new Conversation.ConversationType[]{
                        Conversation.ConversationType.PRIVATE,
                        Conversation.ConversationType.CUSTOMER_SERVICE},
                this);
        RemoveSignOutGroupConversation();
        PTTClient pttClient = PTTClient.getInstance();
        pttClient.init(this);
        pttClient.setPttStateListener(new PTTStateListener() {
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
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * 设置头部+号是否可见
     *
     * @param visibility
     */
    public void setplusVisibility(int visibility) {
        main_plus.setVisibility(visibility);
    }

    public void settreeVisibility(int visibility) {
        main_tree.setVisibility(visibility);
    }

    public ImageView getIv_MainTree() {
        return main_tree;
    }

//    @Override
//    public void initView() {
//
//    }

    private void init() {
        ly_set_firstFragment = (LinearLayout) this.findViewById(R.id.main_ly_tab_menu_msg_new);
        ly_tab_menu_job = (LinearLayout) this.findViewById(R.id.ly_tab_menu_job);
        ly_tab_menu_contacts = (LinearLayout) this.findViewById(R.id.ly_tab_menu_contacts);
        ly_tab_menu_me = (LinearLayout) this.findViewById(R.id.ly_tab_menu_me);

        tv_tab_menu_msg = (TextView) this.findViewById(R.id.tv_tabmenu_msg);
        tv_tab_menu_job = (TextView) this.findViewById(R.id.tv_tabmenu_job);
        tv_tab_menu_contacts = (TextView) this.findViewById(R.id.tv_tabmenu_contacts);
        tv_tab_menu_me = (TextView) this.findViewById(R.id.tv_tabmenu_me);

        tv_tab_menu_msg_num = (TextView) this.findViewById(R.id.tv_tab_menu_msg_num);
        tv_tab_menu_job_num = (TextView) this.findViewById(R.id.tv_tab_menu_job_num);
        tv_tab_menu_contacts_num = (TextView) this.findViewById(R.id.tv_tab_menu_contacts_num);
        img_tab_menu_setting_partner = (ImageView) this.findViewById(R.id.img_tab_menu_setting_partner);
        main_plus = (ImageView) this.findViewById(R.id.main_plus);
        et_search = (EditText) this.findViewById(R.id.et_search);

        search_layout = (LinearLayout) this.findViewById(R.id.search_layout);
        tv_tab_menu_msg_num = (TextView) this.findViewById(R.id.tv_tab_menu_msg_num);

        ly_set_firstFragment.setOnClickListener(this);
        ly_tab_menu_job.setOnClickListener(this);
        ly_tab_menu_contacts.setOnClickListener(this);
        ly_tab_menu_me.setOnClickListener(this);
        main_plus.setOnClickListener(this);
        main_button = (ImageView) this.findViewById(R.id.main_plus);
        main_tree = (ImageView) this.findViewById(R.id.main_tree);
        main_tree.setOnClickListener(this);
        et_search.setFocusable(false);
        et_search.setOnClickListener(this);
        fragment_container = (FrameLayout) this.findViewById(R.id.fragment_container);
    }

    //用来移除已经退出的群组
    private void RemoveSignOutGroupConversation() {
        if (!TextUtils.isEmpty(getIntent().getStringExtra("TargetID"))) {
            Object object = getIntent().getSerializableExtra("conversationType");
            Conversation.ConversationType mConversationType = (Conversation.ConversationType) object;
            String id = getIntent().getStringExtra("TargetID");
            RongIM.getInstance().removeConversation(mConversationType, id);
        } else {
            return;
        }

    }

    //同步群组好友
    private void SetSyncUserGroup() {
        Gson gson = new Gson();
        LoginBean loginBean = gson.fromJson(CommonUtil.getUserInfo(mContext), LoginBean.class);
        String id = loginBean.getText().getId();
        OkGo.post(ConstantValue.SYNCUSERGROUP)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .params("userid", id)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (!TextUtils.isEmpty(s) && !s.equals("{}")) {
                            Gson gson = new Gson();
                            SetSyncUserBean syncUserBean = gson.fromJson(s, SetSyncUserBean.class);
                            if (syncUserBean.getCode().equals("200")) {
                            } else {
                                NToast.shortToast(mContext, "同步群组失败");
                            }
                        } else {
                            return;
                        }
                    }
                });
    }

    //获取所有好友信息
    private void GetFriendInfo() {
        Gson gson = new Gson();
        LoginBean loginBean = gson.fromJson(CommonUtil.getUserInfo(mContext), LoginBean.class);
        String UID = loginBean.getText().getAccount();
        Log.e(TAG, "看看好友的参数：" + UID);
        OkGo.post(ConstantValue.GETALLPERSONINFO)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .cacheKey("getfriendinfo")
                .execute(new StringCallback() {
                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (!TextUtils.isEmpty(s)) {
                            Log.e(TAG, "获取所有好友的信息:" + s);
                            CommonUtil.saveFrientUserInfo(mContext, s);
                        } else {
                            return;
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        NToast.shortToast(mContext, "信息提供者好友请求失败");
                        return;
                    }
                });
    }

    //获取所有群组信息
    private void GetGroupInfo() {
        Gson gson = new Gson();
        LoginBean loginBean = gson.fromJson(CommonUtil.getUserInfo(mContext), LoginBean.class);
        String UID = loginBean.getText().getId();
        OkGo.post(ConstantValue.GETALLGROUP)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .params("userid", UID)
                .execute(new StringCallback() {
                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (!TextUtils.isEmpty(s)) {
                            CommonUtil.saveGroupUserInfo(mContext, s);
                        } else {
                            return;
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        NToast.shortToast(mContext, "好友请求失败");
                        return;
                    }
                });
    }

    //重置所有文本的选中状态
    private void setSelected() {
        tv_tab_menu_msg.setSelected(false);
        tv_tab_menu_job.setSelected(false);
        tv_tab_menu_contacts.setSelected(false);
        tv_tab_menu_me.setSelected(false);


        tv_tab_menu_msg.setTextColor(this.getResources().getColor(R.color.colorNavigation));
        tv_tab_menu_job.setTextColor(this.getResources().getColor(R.color.colorNavigation));
        tv_tab_menu_contacts.setTextColor(this.getResources().getColor(R.color.colorNavigation));
        tv_tab_menu_me.setTextColor(this.getResources().getColor(R.color.colorNavigation));
    }

    private LoginBean GetUesrBean() {
        Gson gson = new Gson();
//        TopContactsBean bean = gson.fromJson(CommonUtil.getFrientUserInfo(mContext), TopContactsBean.class);
        LoginBean bean = gson.fromJson(CommonUtil.getUserInfo(mContext), LoginBean.class);
        return bean;
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
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
        GetGroupInfo();//持久化所有群组信息
        Log.e("生命周期", "onStart");
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("生命周期", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("生命周期", "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
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
//            if (mConversationType.equals(Conversation.ConversationType.GROUP)) {
//                Intent intentGroup = new Intent(mContext, FriendPersonInfoActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("userId", userID);
//                startActivity(intentGroup);
//            }
//            if (mConversationType.equals(Conversation.ConversationType.PRIVATE)) {
            Intent intent = new Intent(mContext, FriendPersonInfoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("userId", userID);
            intent.putExtras(bundle);
            intent.putExtra("conversationType", Conversation.ConversationType.PRIVATE);
            startActivity(intent);

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
            return false;
        }
    }

    private class MyConversationListBehaviorListener implements RongIM.ConversationListBehaviorListener {

        @Override
        public boolean onConversationClick(Context context, View view, UIConversation conversation) {
            return false;
        }

        @Override
        public boolean onConversationPortraitClick(Context context, Conversation.ConversationType conversationType, String targetId) {
            return false;
        }

        /**
         * 当长按会话头像后执行。
         *
         * @param context          上下文。
         * @param conversationType 会话类型。
         * @param targetId         被点击的用户id。
         * @return 如果用户自己处理了点击后的逻辑处理，则返回 true，否则返回 false，false 走融云默认处理方式。
         */
        @Override
        public boolean onConversationPortraitLongClick(Context context, Conversation.ConversationType conversationType, String targetId) {

            return false;
        }

        @Override
        public boolean onConversationLongClick(Context context, View view, final UIConversation conversation) {
            RongIMClient.getInstance().getConversationNotificationStatus(conversation.getConversationType(), conversation.getConversationTargetId(), new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
                @Override
                public void onSuccess(Conversation.ConversationNotificationStatus conversationNotificationStatus) {
                    ConversationListLongDialog dialog = new ConversationListLongDialog(mContext, conversation.getConversationType(), conversation.getConversationTargetId(), conversationNotificationStatus.getValue());
                    dialog.show();
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_ly_tab_menu_msg_new:
                setSelected();
                tv_tab_menu_msg.setSelected(true);
                tv_tab_menu_msg_num.setVisibility(View.INVISIBLE);
                main_tree.setVisibility(View.GONE);
                main_plus.setVisibility(View.VISIBLE);
                tv_tab_menu_msg.setTextColor(this.getResources().getColor(R.color.colorNaviationClick));
                SelectFragment(1);
                break;
            case R.id.ly_tab_menu_job:
                setSelected();
                tv_tab_menu_job.setSelected(true);
                tv_tab_menu_job_num.setVisibility(View.INVISIBLE);
                tv_tab_menu_job.setTextColor(this.getResources().getColor(R.color.colorNaviationClick));
                SelectFragment(2);
                break;
            case R.id.ly_tab_menu_contacts:
                setSelected();
                tv_tab_menu_contacts.setSelected(true);
                tv_tab_menu_contacts_num.setVisibility(View.INVISIBLE);
                main_tree.setVisibility(View.VISIBLE);
                main_plus.setVisibility(View.INVISIBLE);
                tv_tab_menu_contacts.setTextColor(this.getResources().getColor(R.color.colorNaviationClick));
                SelectFragment(3);
                break;
            case R.id.ly_tab_menu_me:
                setSelected();
                tv_tab_menu_me.setSelected(true);
                tv_tab_menu_me.setTextColor(this.getResources().getColor(R.color.colorNaviationClick));
                main_tree.setVisibility(View.INVISIBLE);
                main_plus.setVisibility(View.INVISIBLE);
                SelectFragment(4);
                break;
            case R.id.main_plus:
                MainPlusDialog mainPlusDialog = new MainPlusDialog(this);
                mainPlusDialog.update();
                mainPlusDialog.setBackgroundDrawable(new ColorDrawable(0000000000));
                mainPlusDialog.setOutsideTouchable(true);
                mainPlusDialog.setTouchable(true);
                mainPlusDialog.setFocusable(true);
                mainPlusDialog.showPopupWindow(main_plus);
                break;
            case R.id.et_search:
                startActivity(new Intent(this, SearchAllContactsActivity.class));
                break;
        }
    }

    private void SelectFragment(int i) {
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        hideAll(transaction);
        switch (i) {
            case 1:
                if (mConversationList == null) {
                    mConversationList = initConversationList();
                    transaction.add(R.id.fragment_container, mConversationList);
                    search_layout.setVisibility(View.VISIBLE);
                } else {
                    transaction.show(mConversationList);
                    search_layout.setVisibility(View.VISIBLE);
                }
                Log.i("TAG", "进入message");
                break;
            case 2:
                if (Jobs_Fragment == null) {
                    Jobs_Fragment = new Jobs_Fragment();
//                    transaction.hide(Jobs_Fragment);
                    search_layout.setVisibility(View.GONE);
                    transaction.add(R.id.fragment_container, Jobs_Fragment);
                } else {
                    transaction.show(Jobs_Fragment);
                    search_layout.setVisibility(View.GONE);
                }

                Log.i("TAG", "进入jobs");
                break;
            case 3:
                if (Contacts_Fragment == null) {
                    Contacts_Fragment = new Contacts_Fragment();
                    search_layout.setVisibility(View.VISIBLE);
                    transaction.add(R.id.fragment_container, Contacts_Fragment);
                } else {
                    transaction.show(Contacts_Fragment);
                    search_layout.setVisibility(View.VISIBLE);
                }

                Log.i("TAG", "进入jobs");
                break;
            case 4:
                if (Me_Fragment == null) {
                    Me_Fragment = new Mine_Fragment();
                    search_layout.setVisibility(View.GONE);
                    transaction.add(R.id.fragment_container, Me_Fragment);
                } else {
                    search_layout.setVisibility(View.GONE);
                    transaction.show(Me_Fragment);
                }

                Log.i("TAG", "进入jobs");
                break;
        }
        transaction.commit();
    }

    private void hideAll(FragmentTransaction ft) {
        if (mConversationList != null) {
            ft.hide(mConversationList);
        }
        if (Jobs_Fragment != null) {
            ft.hide(Jobs_Fragment);
        }
        if (Contacts_Fragment != null) {
            ft.hide(Contacts_Fragment);
        }
        if (Me_Fragment != null) {
            ft.hide(Me_Fragment);
        }
    }

    //
    private void SetIconIsTrue() {
        tv_tab_menu_msg.setSelected(ischeck);
        tv_tab_menu_msg_num.setVisibility(View.INVISIBLE);
        tv_tab_menu_msg.setTextColor(this.getResources().getColor(R.color.colorNaviationClick));
    }

    //设置进入的首页
    private void initFM() {
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
//        mConversationList = initConversationList();

        if (mConversationList == null) {
            mConversationList = initConversationList();
//            conversationListDynamicActivtiy = new ConversationListDynamicActivtiy();
//            mConversationList = new Message_Fragment();
            transaction.add(R.id.fragment_container, mConversationList);
            main_tree.setVisibility(View.GONE);
            main_plus.setVisibility(View.VISIBLE);
            Log.i("TAG", "主Fragment");
            SetIconIsTrue();
        } else {
            transaction.show(mConversationList);
        }
        transaction.commit();
    }

    //将Android状态栏改变为沉浸样式
    private void SystemBarTranslucentType(Activity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);

        }

    }

    private Fragment initConversationList() {
        if (mConversationListFragment == null) {
            ConversationListFragment listFragment = new ConversationListFragment();
            listFragment.setAdapter(new ConversationAdapter(RongContext.getInstance()));
            Uri uri;
//            if (isDebug) {
            uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                    .appendPath("conversationlist")
                    .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话是否聚合显示
                    .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")//群组
                    .appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")//公共服务号
                    .appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")//订阅号
                    .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//系统
                    .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "true")
                    .build();
//                mConversationsTypes = new Conversation.ConversationType[]{Conversation.ConversationType.PRIVATE,
//                        Conversation.ConversationType.GROUP,
//                        Conversation.ConversationType.PUBLIC_SERVICE,
//                        Conversation.ConversationType.APP_PUBLIC_SERVICE,
//                        Conversation.ConversationType.SYSTEM,
//                        Conversation.ConversationType.DISCUSSION
//                };
//            } else {
//                uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
//                        .appendPath("conversationlist")
//                        .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话是否聚合显示
//                        .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")//群组
//                        .appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")//公共服务号
//                        .appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")//订阅号
//                        .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "true")//系统
//                        .build();
//                mConversationsTypes = new Conversation.ConversationType[]{Conversation.ConversationType.PRIVATE,
//                        Conversation.ConversationType.GROUP,
//                        Conversation.ConversationType.PUBLIC_SERVICE,
//                        Conversation.ConversationType.APP_PUBLIC_SERVICE,
//                        Conversation.ConversationType.SYSTEM
//                };
//            }
            listFragment.setUri(uri);
//            mConversationListFragment = listFragment;
            return listFragment;
        } else {
            return mConversationListFragment;
        }
    }

    @Override
    public UserInfo getUserInfo(String s) {
        if (CommonUtil.isNumeric(s)) {
            Gson gson1 = new Gson();
            String jsondata = CommonUtil.getFrientUserInfo(mContext);
            if (!TextUtils.isEmpty(jsondata)) {
                Type listTypeJson = new TypeToken<Map<String, Object>>() {
                }.getType();
                Map<String, Object> map = gson1.fromJson(jsondata, listTypeJson);
                if (0.0 == (double) map.get("code")) {
                    return null;
                } else {
                    Gson gson = new Gson();
                    Type listType = new TypeToken<TopContactsListBean>() {
                    }.getType();
                    TopContactsListBean bean = gson.fromJson(CommonUtil.getFrientUserInfo(mContext), listType);
                    if (bean != null && bean.getText().size() > 0) {
                        for (int i = 0; i < bean.getText().size(); i++) {
                            if (bean.getText().get(i).getId().equals(s)) {
                                return new UserInfo(bean.getText().get(i).getId(), bean.getText().get(i).getFullname(), Uri.parse(ConstantValue.ImageFile + bean.getText().get(i).getLogo()));
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Group getGroupInfo(String groupId) {
        String str = CommonUtil.getGroupUserInfo(mContext);
        if (!TextUtils.isEmpty(str)) {
            Type listType = new TypeToken<Map<String, Object>>() {
            }.getType();
            Gson gson = new Gson();
            Map<String, Object> jsonData = gson.fromJson(str, listType);
            if ((double) jsonData.get("code") == 0.0) {
                Map<String, String> textData = (Map<String, String>) jsonData.get("text");
            } else {
                Type listType1 = new TypeToken<GroupListBean>() {
                }.getType();
                Gson gson1 = new Gson();
                GroupListBean GroupAllBean = gson1.fromJson(CommonUtil.getGroupUserInfo(mContext), listType1);
                ArrayList<GroupBean> GroupBeanList = GroupAllBean.getText();
                Log.e("aaaaaaaa","-----:"+GroupAllBean.getText());
                if (GroupBeanList != null && GroupBeanList.size() > 0) {
                    for (GroupBean i : GroupBeanList) {
                        if (i.getGID().equals(groupId)) {
                            return new Group(i.getGID(), i.getName(), Uri.parse(ConstantValue.ImageFile + i.getLogo()));
                        }
                    }
                }
            }
        }
        return null;
    }

    private long mExitTime;//退出时间

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(MainActivity.this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
//            MyConfig.clearSharePre(this, "users");
            if (RongIM.getInstance() != null)
                RongIM.getInstance().disconnect(true);

            Process.killProcess(Process.myPid());
//            finish();
//            System.exit(0);
//            RongIM.getInstance().disconnect();
            moveTaskToBack(false);
            mIntent = new Intent(this, FloatService.class);
            stopService(mIntent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIntent = new Intent(this, FloatService.class);
        stopService(mIntent);
    }

    @Override
    public void onCountChanged(int count) {
        if (count == 0) {
            tv_tab_menu_msg_num.setVisibility(View.GONE);
        } else if (count > 0 && count < 100) {
            tv_tab_menu_msg_num.setVisibility(View.VISIBLE);
            tv_tab_menu_msg_num.setText(String.valueOf(count));
        } else {
            tv_tab_menu_msg_num.setVisibility(View.VISIBLE);
            tv_tab_menu_msg_num.setText("···");
        }
    }

}

