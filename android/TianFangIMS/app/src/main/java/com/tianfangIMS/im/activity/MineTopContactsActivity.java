package com.tianfangIMS.im.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.tianfangIMS.im.ConstantValue;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.adapter.TopContactsAdapter;
import com.tianfangIMS.im.bean.LoginBean;
import com.tianfangIMS.im.bean.TopContactsListBean;
import com.tianfangIMS.im.bean.UserBean;
import com.tianfangIMS.im.dialog.LoadDialog;
import com.tianfangIMS.im.dialog.SendImageMessageDialog;
import com.tianfangIMS.im.utils.CommonUtil;
import com.tianfangIMS.im.utils.NToast;

import java.util.ArrayList;
import java.util.Map;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by LianMengYu on 2017/2/3.
 * 常用联系人
 */

public class MineTopContactsActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private static final String TAG = "MineTopContactsActivity";
    private Context mContext;
    private ListView lv_topContacts;
    private TopContactsAdapter topContactsAdapter;
    private TopContactsListBean bean;
    private EditText et_search;
    private ArrayList<String> ImgMsgList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_topcontacts_activity);
        setTitle("选择常用联系人");
        mContext = this;
        initView();
        GetLoginUserInfo();
        GetTopContacts();
        ImgMsgList = getIntent().getStringArrayListExtra("ListUri");
    }

    private void initView() {
        lv_topContacts = (ListView) this.findViewById(R.id.lv_group_addtopcontacts);
        et_search = (EditText) this.findViewById(R.id.et_search);
        et_search.setFocusable(false);
        et_search.setOnClickListener(this);
        lv_topContacts.setOnItemClickListener(this);


    }

    private void GetLoginUserInfo() {
        String ids = RongIMClient.getInstance().getCurrentUserId();
        OkGo.post(ConstantValue.GETONEPERSONINFO)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .params("userid", ids)
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
                            Gson gson2 = new Gson();
                            UserBean bean = gson2.fromJson(s, UserBean.class);
                        }
                    }
                });
    }

    private void GetTopContacts() {
        Gson gson = new Gson();
        LoginBean loginBean = gson.fromJson(CommonUtil.getUserInfo(mContext), LoginBean.class);
        String UID = loginBean.getText().getAccount();
        OkGo.post(ConstantValue.GETCONTACTSLIST)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .params("account", UID)
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
//                            Type listType = new TypeToken<TopContactsListBean>() {
//                            }.getType();
                            Log.e(TAG, "aaaa:" + s);
                            Gson gson = new Gson();
                            Map<String, Object> map = gson.fromJson(s, new TypeToken<Map<String, Object>>() {
                            }.getType());
                            if ((double) map.get("code") == 0.0) {
                                NToast.longToast(mContext, "您还没有联系人");
                            }
                            if ((double) map.get("code") == 1.0) {
                                bean = gson.fromJson(s, TopContactsListBean.class);
                                topContactsAdapter = new TopContactsAdapter(bean, mContext);
                                lv_topContacts.setAdapter(topContactsAdapter);
                                topContactsAdapter.notifyDataSetChanged();
                            }
                        } else {
                            return;
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
                startActivity(new Intent(mContext, SearchActivity.class));
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (ImgMsgList != null && ImgMsgList.size() > 0) {
//            SendImageMessage(ImgMsgList, position);
            SendImageMessageDialog sendImageMessageDialog = new SendImageMessageDialog(mContext, bean.getText().get(position).getId(),
                    position, bean.getText().get(position).getFullname(), ImgMsgList, Conversation.ConversationType.PRIVATE,
                    ConstantValue.ImageFile + bean.getText().get(position).getLogo(), bean.getText().get(position).getPosition());
            sendImageMessageDialog.show();
        } else {
            Intent intent = new Intent(mContext, FriendPersonInfoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("userId", bean.getText().get(position).getId());
            intent.putExtras(bundle);
            intent.putExtra("conversationType", Conversation.ConversationType.PRIVATE);
            startActivity(intent);
//            RongIM.getInstance().startPrivateChat(mContext, bean.getText().get(position).getId(), bean.getText().get(position).getFullname());
        }
    }
}
