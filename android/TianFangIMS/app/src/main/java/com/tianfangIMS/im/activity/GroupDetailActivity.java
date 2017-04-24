package com.tianfangIMS.im.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.tianfangIMS.im.ConstantValue;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.adapter.GroupDetailInfo_GridView_Adapter;
import com.tianfangIMS.im.bean.GroupBean;
import com.tianfangIMS.im.bean.GroupListBean;
import com.tianfangIMS.im.bean.LoginBean;
import com.tianfangIMS.im.bean.OneGroupBean;
import com.tianfangIMS.im.bean.SealSearchConversationResult;
import com.tianfangIMS.im.dialog.CleanChatLogDialog;
import com.tianfangIMS.im.dialog.LoadDialog;
import com.tianfangIMS.im.utils.CommonUtil;
import com.tianfangIMS.im.utils.NToast;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.rong.imkit.RongIM;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ImageMessage;
import okhttp3.Call;
import okhttp3.Response;

////import com.tianfangIMS.im.adapter.GroupDetailInfo_GridView_Adapter;

/**
 * Created by LianMengYu on 2017/1/21.
 */
public class GroupDetailActivity extends BaseActivity implements View.OnClickListener, GroupDetailInfo_GridView_Adapter.AddClickListener, GroupDetailInfo_GridView_Adapter.DelClickListener,
        AdapterView.OnItemClickListener {
    private static final String TAG = "GroupDetailActivity";
    private static final int SEARCH_TYPE_FLAG = 0;
    private String fromConversationId;
    private Conversation.ConversationType mConversationType;
    private UserInfo userInfo;
    private RelativeLayout rl_signout;
    private TextView tv_group_groupname;
    private RelativeLayout rl_changeGroupName;
    private int requestCode;//返回值
    private OneGroupBean oneGroupBean;
    private String GroupName;
    private RelativeLayout rl_group_file, rl_breakGroup, rl_group_clean;
    private Context mContext;
    private GridView gv_userinfo;
    private GroupDetailInfo_GridView_Adapter adapter;
    private CompoundButton sw_conversationdetail_notfaction;
    private SharedPreferences.Editor editor;
    private SharedPreferences sp;
    private RelativeLayout rl_group_findFile;
    private SealSearchConversationResult mResult;
    private RelativeLayout rl_movegroup;
    private ArrayList<GroupBean> GroupBeanList;
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groupdetail_layout);
        mContext = this;
        init();
        //群组会话界面点进群组详情
        fromConversationId = getIntent().getStringExtra("TargetId");
        mConversationType = (Conversation.ConversationType) getIntent().getSerializableExtra("conversationType");
        if (!TextUtils.isEmpty(fromConversationId)) {

            userInfo = RongUserInfoManager.getInstance().getUserInfo(fromConversationId);
            Log.e(TAG, "看看UserInfo有什么：" + fromConversationId);
        }
        GroupInfo();
        GetGroupUserInfo();
        sp = getSharedPreferences("config", MODE_PRIVATE);
        editor = sp.edit();
        boolean flag = sp.getBoolean("GroupchatisOpen", true);
        sw_conversationdetail_notfaction.setChecked(flag);
    }

    //对GridView 显示的宽高经行设置
    private void SettingGridView(ArrayList<GroupBean> list) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int size = list.size() + 1;//要显示数据的个数
        //gridview的layout_widht,要比每个item的宽度多出2个像素，解决不能完全显示item的问题
        int allWidth = (int) (82 * size * density);
        //int allWidth = (int) ((width / 3 ) * size + (size-1)*3);//也可以这样使用，item的总的width加上horizontalspacing
        int itemWidth = (int) (65 * density);//每个item宽度
        LinearLayout.LayoutParams params = new
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        gv_userinfo.setLayoutParams(params);
        gv_userinfo.setColumnWidth(itemWidth);
        gv_userinfo.setHorizontalSpacing(3);
        gv_userinfo.setStretchMode(GridView.NO_STRETCH);
        gv_userinfo.setNumColumns(size);
        gv_userinfo.setSelector(new ColorDrawable(Color.TRANSPARENT));
    }

    private void GetGroupUserInfo() {
        OkGo.post(ConstantValue.GROUPALLUSERINFO)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .params("groupid", fromConversationId)
                .execute(new StringCallback() {
                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        LoadDialog.show(mContext);
                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        LoadDialog.dismiss(mContext);
                        if (!TextUtils.isEmpty(s) && !s.equals("{}")) {
                            Type listType1 = new TypeToken<GroupListBean>() {
                            }.getType();
                            Gson gson1 = new Gson();
                            GroupListBean GroupAllBean = gson1.fromJson(s, listType1);
                            GroupBeanList = GroupAllBean.getText();
                            Log.e("GroupBeanList", "---:" + GroupBeanList);
                            setTitle("群信息" + "(" + GroupBeanList.size() + "人)");
                            adapter = new GroupDetailInfo_GridView_Adapter(mContext, GroupBeanList, GroupDetailActivity.this, GroupDetailActivity.this, flag);
//                            SettingGridView(GroupBeanList);
                            gv_userinfo.setAdapter(adapter);
                            setListViewHeightBasedOnChildren(gv_userinfo);
                            gv_userinfo.deferNotifyDataSetChanged();
                        }
                    }
                });
    }

    public static void setListViewHeightBasedOnChildren(GridView listView) {
        // 获取listview的adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        // 固定列宽，有多少列
        int col = 4;// listView.getNumColumns();
        int totalHeight = 0;
        // i每次加4，相当于listAdapter.getCount()小于等于4时 循环一次，计算一次item的高度，
        // listAdapter.getCount()小于等于8时计算两次高度相加
        for (int i = 0; i < listAdapter.getCount(); i += col) {
            // 获取listview的每一个item
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            // 获取item的高度和
            totalHeight += listItem.getMeasuredHeight();
        }

        // 获取listview的布局参数
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        // 设置高度
        params.height = totalHeight;
        // 设置margin
        ((ViewGroup.MarginLayoutParams) params).setMargins(10, 10, 10, 10);
        // 设置参数
        listView.setLayoutParams(params);
    }

    private void GetHistoryMessages() {
        List<Message> list = RongIMClient.getInstance().getHistoryMessages(mConversationType, fromConversationId, -1, Integer.MAX_VALUE);
        List<ImageMessage> msg = new ArrayList<>();
        List<Uri> urilist = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
//            Log.e(TAG, "回去消息:" + list.get(i).getObjectName()+"====消息类型id："+list.get(i).get);
            MessageContent messageContent = list.get(i).getContent();
            if (messageContent instanceof ImageMessage) {
                ImageMessage imageMessage = (ImageMessage) messageContent;
                msg.add(imageMessage);
            }
        }
        for (int j = 0; j < msg.size(); j++) {
            Uri aa = msg.get(j).getRemoteUri();
            urilist.add(aa);
        }
        if (urilist != null && urilist.size() > 0) {
            Intent intent = new Intent(mContext, SelectPhoteActivity.class);
            intent.putExtra("photouri", (Serializable) urilist);
            startActivity(intent);
        } else {
            NToast.shortToast(mContext, "没有数据");
        }
    }

    private void init() {
        rl_signout = (RelativeLayout) this.findViewById(R.id.rl_signout);
        tv_group_groupname = (TextView) this.findViewById(R.id.tv_group_groupname);
        rl_changeGroupName = (RelativeLayout) this.findViewById(R.id.rl_changeGroupName);
        rl_group_file = (RelativeLayout) this.findViewById(R.id.rl_group_file);
        rl_breakGroup = (RelativeLayout) this.findViewById(R.id.rl_breakGroup);
        gv_userinfo = (GridView) this.findViewById(R.id.gv_userinfo);
        rl_group_clean = (RelativeLayout) this.findViewById(R.id.rl_group_clean);
        sw_conversationdetail_notfaction = (CompoundButton) this.findViewById(R.id.sw_conversationdetail_notfaction);
        rl_group_findFile = (RelativeLayout) this.findViewById(R.id.rl_group_findFile);
        rl_movegroup = (RelativeLayout) this.findViewById(R.id.rl_movegroup);


        gv_userinfo.setOnItemClickListener(this);
        rl_movegroup.setOnClickListener(this);
        rl_group_findFile.setOnClickListener(this);
        rl_group_clean.setOnClickListener(this);
        rl_signout.setOnClickListener(this);
        rl_changeGroupName.setOnClickListener(this);
        rl_group_file.setOnClickListener(this);
        rl_breakGroup.setOnClickListener(this);
        sw_conversationdetail_notfaction.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
                    editor.putBoolean("GroupchatisOpen", true);
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
                    editor.putBoolean("GroupchatisOpen", false);
                    editor.apply();
                }
            }
        });
    }

    private void GroupInfo() {
        OkGo.post(ConstantValue.GETONEGROUPINFO)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .params("groupid", fromConversationId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (!TextUtils.isEmpty(s) && !s.equals("{}")) {
                            Gson gson = new Gson();
                            oneGroupBean = gson.fromJson(s, OneGroupBean.class);
                            IsLongGroupName(oneGroupBean.getText().getName());
                            LoginBean loginBean = gson.fromJson(CommonUtil.getUserInfo(mContext), LoginBean.class);
                            if (oneGroupBean.getText().getMid().equals(loginBean.getText().getId())) {
                                rl_movegroup.setVisibility(View.VISIBLE);

                                rl_breakGroup.setVisibility(View.VISIBLE);
                                flag = true;
                            } else {
                                rl_signout.setVisibility(View.VISIBLE);
                                flag = false;
                            }
                        }
                    }
                });
    }

    /**
     * 退出群组
     */
    private void SingOutGroup() {
        final Gson gson = new Gson();
        LoginBean loginBean = gson.fromJson(CommonUtil.getUserInfo(mContext), LoginBean.class);
        String userid = loginBean.getText().getId();
        OkGo.post(ConstantValue.SINGOUTGROUP)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                //要退出群的ＩＤ
                .params("groupids", userid)
                //群组的ID
                .params("groupid", fromConversationId)
                .execute(new StringCallback() {
                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        LoadDialog.show(mContext);
                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        LoadDialog.dismiss(mContext);
                        if (!TextUtils.isEmpty(s) && !s.equals("{}")) {
                            Gson gson1 = new Gson();
                            Map<String, Object> map = gson1.fromJson(s, new TypeToken<Map<String, Object>>() {
                            }.getType());
                            if ((map.get("code").toString()).equals("1.0")) {
                                NToast.shortToast(mContext, "您已经退出群组");
                                RongIM.getInstance().clearMessages(mConversationType, fromConversationId);
                                Intent intent = new Intent(mContext, MainActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("TargetID", fromConversationId);
                                bundle.putSerializable("conversationType", mConversationType);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                            } else {
                                NToast.shortToast(mContext, "退出群组失败");
                            }
                        }
                    }
                });
    }

    //当群名称大于规定数量时，显示未命名
    private void IsLongGroupName(String name) {
        int longName = name.length();
        if (longName > 100) {
            tv_group_groupname.setText("未命名");
        } else {
            tv_group_groupname.setText(name);
        }
    }

    private void BreakGroupUser() {
        Gson gson = new Gson();
        LoginBean loginBean = gson.fromJson(CommonUtil.getUserInfo(mContext), LoginBean.class);
        String userid = loginBean.getText().getId();
        OkGo.post(ConstantValue.DISSGROUP)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .params("userid", userid)
                .params("groupid", fromConversationId)
                .execute(new StringCallback() {
                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        LoadDialog.show(mContext);
                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        LoadDialog.dismiss(mContext);
                        if (!TextUtils.isEmpty(s) && !s.equals("{}")) {
                            Gson gson1 = new Gson();
                            Map<String, Object> map = gson1.fromJson(s, new TypeToken<Map<String, Object>>() {
                            }.getType());
                            if ((map.get("code").toString()).equals("1.0")) {
                                Intent intent = new Intent(mContext, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                NToast.shortToast(mContext, "解散群组失败");
                            }
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_signout:
                SingOutGroup();
                break;
            case R.id.rl_changeGroupName:
                Intent intent = new Intent(mContext, ChangeGroupNameActivity.class);
                requestCode = 0;
                Bundle bundle = new Bundle();
                GroupName = oneGroupBean.getText().getName();
                Log.e("打印数据","单个的："+GroupName);
                bundle.putSerializable("GroupBean", oneGroupBean);
                intent.putExtras(bundle);
                startActivityForResult(intent, requestCode);
                break;
            case R.id.rl_group_file:
                GetHistoryMessages();
                break;
            case R.id.rl_breakGroup:
                BreakGroupUser();
                break;
            case R.id.rl_group_clean:
                CleanChatLogDialog dialog = new CleanChatLogDialog(mContext, mConversationType, fromConversationId);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable());
                dialog.show();
                CommonUtil.SetCleanDialogStyle(dialog);
                break;
            case R.id.rl_group_findFile:
                Intent searchIntent = new Intent(mContext, SearchChattingDetailActivity.class);
                ArrayList<Message> arrayList = new ArrayList<>();
                searchIntent.putParcelableArrayListExtra("filterMessages", arrayList);
                mResult = new SealSearchConversationResult();
                Conversation conversation = new Conversation();
                conversation.setTargetId(fromConversationId);
                conversation.setConversationType(mConversationType);
                mResult.setConversation(conversation);
//                Log.e("打印用户信息", "id：" + oneGroupBean.getText().getGID() + "---:名字：" + oneGroupBean.getText().getGID() + "---头像:" +ConstantValue.ImageFile + oneGroupBean.getText().getLogo().toString());
                if (oneGroupBean != null) {
                    String portraitUri = ConstantValue.ImageFile + oneGroupBean.getText().getLogo().toString();
                    mResult.setId(oneGroupBean.getText().getGID());
                    if (!TextUtils.isEmpty(portraitUri)) {
                        mResult.setPortraitUri(portraitUri);
                    }
                    if (!TextUtils.isEmpty(oneGroupBean.getText().getName())) {
                        mResult.setTitle(oneGroupBean.getText().getName());
                    }
                } else {
                    UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(conversation.getTargetId());
                    if (userInfo != null) {
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
                }
                searchIntent.putExtra("searchConversationResult", mResult);
                searchIntent.putExtra("flag", SEARCH_TYPE_FLAG);
                startActivity(searchIntent);
                break;
            case R.id.rl_movegroup:
                ArrayList<GroupBean> listdata = new ArrayList<>();
                for (int i = 0; i < GroupBeanList.size(); i++) {
                    if (!RongIM.getInstance().getCurrentUserId().equals(GroupBeanList.get(i).getId())) {
                        listdata.add(GroupBeanList.get(i));
                    }
                }
                Intent MoveIntent = new Intent(mContext, MoveGroupUserActivity.class);
                Bundle movebundle = new Bundle();
                movebundle.putSerializable("GroupBeanList", listdata);
                movebundle.putString("fromConversationId", fromConversationId);
                MoveIntent.putExtras(movebundle);
                startActivity(MoveIntent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String change01 = data.getStringExtra("change01");
            tv_group_groupname.setText(change01);
            RongIM.getInstance().refreshGroupInfoCache(new Group(oneGroupBean.getText().getGID(),change01,Uri.parse(ConstantValue.ImageFile+oneGroupBean.getText().getLogo())));
            Log.e("打印数据：","---ID:"+oneGroupBean.getText().getGID()+"--name:"+oneGroupBean.getText().getName()+"头像:"+Uri.parse(ConstantValue.ImageFile+oneGroupBean.getText().getLogo()));

        } else {
            return;
        }
    }

    @Override
    public void AddclickListener(View v) {
        Intent intent = new Intent(mContext, AddGroupActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("GroupId", fromConversationId);
        intent.putExtras(bundle);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void DelclickListener(View v) {
        ArrayList<GroupBean> listdata = new ArrayList<>();
        for (int i = 0; i < GroupBeanList.size(); i++) {
            if (!RongIM.getInstance().getCurrentUserId().equals(GroupBeanList.get(i).getId())) {
                listdata.add(GroupBeanList.get(i));
            }
        }
        Intent intent = new Intent(GroupDetailActivity.this, DeleteGropUserActivity.class);
        Bundle delbundle = new Bundle();
        delbundle.putSerializable("GroupBeanList", listdata);
        delbundle.putString("fromConversationId", fromConversationId);
        intent.putExtras(delbundle);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent detailintent = new Intent(mContext, FriendPersonInfoActivity.class);
        Bundle detailbundle = new Bundle();
        detailbundle.putString("userId", GroupBeanList.get(position).getId());
        detailintent.putExtras(detailbundle);
        detailintent.putExtra("conversationType", Conversation.ConversationType.PRIVATE);
        startActivity(detailintent);
        this.finish();
    }
}

