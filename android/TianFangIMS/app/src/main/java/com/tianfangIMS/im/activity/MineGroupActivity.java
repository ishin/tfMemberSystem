package com.tianfangIMS.im.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.tianfangIMS.im.ConstantValue;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.adapter.GroupAdapter;
import com.tianfangIMS.im.bean.GroupBean;
import com.tianfangIMS.im.bean.LoginBean;
import com.tianfangIMS.im.bean.MineGroupBean;
import com.tianfangIMS.im.bean.MineGroupChildBean;
import com.tianfangIMS.im.dialog.LoadDialog;
import com.tianfangIMS.im.dialog.SendFlieMessageDialog;
import com.tianfangIMS.im.dialog.SendImageMessageSpDialog;
import com.tianfangIMS.im.utils.CommonUtil;
import com.tianfangIMS.im.utils.NToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by LianMengYu on 2017/1/11.
 * 我的群组
 */

public class MineGroupActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private static final String TAG = "MineGroupActivity";
    private Context mContext;
    private List<MineGroupChildBean> mList = new ArrayList<MineGroupChildBean>();
    private List<Map<String, String>> ListGroup = new ArrayList<Map<String, String>>();
    private TextView tv_groupIsNull;
    ListView activity_group_lv_data;

    MineGroupBean mMineGroupBean;
    List<GroupBean> mGroupBeen;

    GroupAdapter mGroupAdapter;
    private ArrayList<String> uriList;
    Gson mGson;
    private EditText et_search;
    private ArrayList<Message> AllMessage;
    private ArrayList<Message> allFile;
    private LinearLayout ll_minegroup_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        mContext = this;
        AllMessage = getIntent().getParcelableArrayListExtra("allMessage");
        allFile = getIntent().getParcelableArrayListExtra("allFile");
        setHeadRightButtonVisibility(View.VISIBLE);
        setTitle("我的群组");
        initView();
        GetGroupList();
        uriList = getIntent().getStringArrayListExtra("ListUri");
        getMain_plus().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AddGroupActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("SimpleName", TAG);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    public void initView() {
        activity_group_lv_data = (ListView) findViewById(R.id.activity_group_lv_data);
        activity_group_lv_data.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (AllMessage != null && AllMessage.size() > 0) {
                    SendImageMessageSpDialog sendImageMessageSpDialog = new SendImageMessageSpDialog(mContext, mGroupBeen.get(position).getGID(), mGroupBeen.get(position).getName(), AllMessage, Conversation.ConversationType.GROUP,
                            ConstantValue.ImageFile + mGroupBeen.get(position).getLogo(), null);
                    if (!isFinishing()) {
                        Window window = sendImageMessageSpDialog.getWindow();
                        window.setBackgroundDrawableResource(android.R.color.transparent);
                        sendImageMessageSpDialog.show();
                    }
                } else if (allFile != null && allFile.size() > 0) {
                    SendFlieMessageDialog sendFlieMessageDialog = new SendFlieMessageDialog(mContext, mGroupBeen.get(position).getGID(), mGroupBeen.get(position).getName(),
                            allFile, Conversation.ConversationType.GROUP,
                            ConstantValue.ImageFile + mGroupBeen.get(position).getLogo(), null);
                    if (!isFinishing()) {
                        Window window = sendFlieMessageDialog.getWindow();
                        window.setBackgroundDrawableResource(android.R.color.transparent);
                        sendFlieMessageDialog.show();
                    }
                } else {
                    if (TextUtils.isEmpty(mGroupBeen.get(position).getName())) {
                        RongIM.getInstance().startGroupChat(mContext, mGroupBeen.get(position).getGID(), " ");
                    } else {
                        RongIM.getInstance().startGroupChat(mContext, mGroupBeen.get(position).getGID(), mGroupBeen.get(position).getName());
                    }
                }
            }
        });
        et_search = (EditText) findViewById(R.id.et_search);
        et_search.setFocusable(false);
        ll_minegroup_search = (LinearLayout) this.findViewById(R.id.ll_minegroup_search);
        et_search.setOnClickListener(this);
    }

    private void GetGroupList() {
        String sessionId = getSharedPreferences("CompanyCode", MODE_PRIVATE).getString("CompanyCode", "");
        Gson gson = new Gson();
        final LoginBean loginBean = gson.fromJson(CommonUtil.getUserInfo(mContext), LoginBean.class);
        String UID = loginBean.getText().getId();
        OkGo.post(ConstantValue.MINEGROUP)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .headers("cookie", sessionId)
                .params("userid", UID)
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
                            if ((s.trim()).startsWith("<!DOCTYPE")) {
                                NToast.shortToast(mContext, "Session过期，请重新登陆");
                                startActivity(new Intent(mContext, LoginActivity.class));
                                finish();
                            } else {
                                if (!TextUtils.isEmpty(s) && !s.equals("{}")) {
                                    Gson gson1 = new Gson();
                                    Map<String, Object> map = gson1.fromJson(s, new TypeToken<Map<String, Object>>() {
                                    }.getType());
                                    if ((map.get("code").toString()).equals("1.0")) {
                                        if ((map.get("text").toString()).equals("{}")){
                                            NToast.shortToast(mContext, "请先加入/创建群组");
                                        }else{
                                            mGson = new Gson();
                                            mGroupBeen = new ArrayList<GroupBean>();
                                            mMineGroupBean = mGson.fromJson(s, MineGroupBean.class);
                                            GroupBean tmp = new GroupBean();
                                            tmp.setName("我建的组");
                                            tmp.setGID(String.valueOf(-1));
                                            mGroupBeen.add(tmp);
                                            mGroupBeen.addAll(mMineGroupBean.getText().getICreate());
                                            tmp = new GroupBean();
                                            tmp.setName("我加入的");
                                            tmp.setGID(String.valueOf(-1));
                                            mGroupBeen.add(tmp);
                                            mGroupBeen.addAll(mMineGroupBean.getText().getIJoin());
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mGroupAdapter = new GroupAdapter(mGroupBeen, mContext);
                                                    activity_group_lv_data.setAdapter(mGroupAdapter);
                                                }
                                            });
                                        }
                                    } else if ((map.get("code").toString()).equals("0.0")) {
                                    NToast.shortToast(mContext, "请先加入/创建群组");
                                }
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext, "请求失败");
                        return;
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_search:
                startActivity(new Intent(mContext, SearchGroupActivity.class));
//                ll_minegroup_search.setVisibility(View.VISIBLE);
//                et_search.setFocusable(true);
                break;
            case R.id.tv_search_cencal:
//                ll_minegroup_search.setVisibility(View.GONE);
//                searchData.clear();
//                et_search.getText().clear();
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        GetGroupList();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
