package com.tianfangIMS.im.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.tianfangIMS.im.dialog.LoadDialog;
import com.tianfangIMS.im.dialog.SendFlieMessageDialog;
import com.tianfangIMS.im.dialog.SendImageMessageSpDialog;
import com.tianfangIMS.im.utils.CommonUtil;
import com.tianfangIMS.im.utils.NToast;

import java.util.ArrayList;
import java.util.Map;

import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by LianMengYu on 2017/2/3.
 * 常用联系人
 */

public class MineTopContactsActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener, View.OnFocusChangeListener {
    private static final String TAG = "MineTopContactsActivity";
    private Context mContext;
    private ListView lv_topContacts;
    private TopContactsAdapter topContactsAdapter;
    private TopContactsListBean bean;
    private EditText et_search;
    private ArrayList<String> ImgMsgList;
    String sessionId;
    private ArrayList<Message> AllMessage;
    private ArrayList<Message> allFile;
    private LinearLayout ll_search_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_topcontacts_activity);
        setTitle("选择常用联系人");
        setHeadRightButtonVisibility(View.VISIBLE);
        sessionId = getSharedPreferences("CompanyCode", MODE_PRIVATE).getString("CompanyCode", "");
        mContext = this;
        initView();
        AllMessage = getIntent().getParcelableArrayListExtra("allMessage");
        allFile = getIntent().getParcelableArrayListExtra("allFile");
        GetTopContacts();
        ImgMsgList = getIntent().getStringArrayListExtra("ListUri");
        ImageView imageView = getMain_plus();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, QRCodeActivity.class));
            }
        });
    }

    private void initView() {
        lv_topContacts = (ListView) this.findViewById(R.id.lv_group_addtopcontacts);
        et_search = (EditText) this.findViewById(R.id.et_search);
        ll_search_view = (LinearLayout) this.findViewById(R.id.ll_minecontacts_search);
        et_search.setOnClickListener(this);
        et_search.setFocusable(false);
        lv_topContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (AllMessage != null && AllMessage.size() > 0) {
                    SendImageMessageSpDialog sendImageMessageSpDialog = new SendImageMessageSpDialog(mContext, bean.getText().get(position).getId(), bean.getText().get(position).getFullname(), AllMessage, Conversation.ConversationType.PRIVATE,
                            ConstantValue.ImageFile + bean.getText().get(position).getLogo(), bean.getText().get(position).getPosition());
                    if (!isFinishing()) {
                        sendImageMessageSpDialog.show();
                    }
                } else if (allFile != null && allFile.size() > 0) {
                    SendFlieMessageDialog sendFlieMessageDialog = new SendFlieMessageDialog(mContext, bean.getText().get(position).getId(), bean.getText().get(position).getFullname(),
                            allFile, Conversation.ConversationType.PRIVATE,
                            ConstantValue.ImageFile + bean.getText().get(position).getLogo(), bean.getText().get(position).getPosition());
                    if (!isFinishing()) {
                        sendFlieMessageDialog.show();
                    }
                } else {
                    Intent intent = new Intent(mContext, FriendPersonInfoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("userId", bean.getText().get(position).getId());
                    intent.putExtras(bundle);
                    intent.putExtra("conversationType", Conversation.ConversationType.PRIVATE);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    protected void onStart() {

        GetTopContacts();
        super.onStart();
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
                .headers("cookie", sessionId)
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
                            if ((s.trim()).startsWith("<!DOCTYPE")) {
                                NToast.shortToast(mContext, "Session过期，请重新登陆");
                                startActivity(new Intent(mContext, LoginActivity.class));
                                finish();
                            } else {
                                Gson gson = new Gson();
                                Map<String, Object> map = gson.fromJson(s, new TypeToken<Map<String, Object>>() {
                                }.getType());
                                if ((double) map.get("code") == 0.0) {
                                    NToast.longToast(mContext, "您还没有联系人");
//                                    bean.getText().clear();
//                                    topContactsAdapter = new TopContactsAdapter(bean, mContext);
//                                    lv_topContacts.setAdapter(topContactsAdapter);
//                                    topContactsAdapter.notifyDataSetChanged();
                                    lv_topContacts.setAdapter(null);
//                                    topContactsAdapter.notifyDataSetChanged();
                                }
                                if ((double) map.get("code") == 1.0) {
                                    if ((map.get("text").toString()).equals("{}")) {
                                        return;
                                    } else {
                                        Log.e("asd123asd", "sdasd" + bean);
                                        bean = gson.fromJson(s, TopContactsListBean.class);
                                        topContactsAdapter = new TopContactsAdapter(bean, mContext);
                                        lv_topContacts.setAdapter(topContactsAdapter);
                                        topContactsAdapter.notifyDataSetChanged();
                                    }
                                }
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
            case R.id.tv_search_cencal:
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.et_search:
                ll_search_view.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }
}
