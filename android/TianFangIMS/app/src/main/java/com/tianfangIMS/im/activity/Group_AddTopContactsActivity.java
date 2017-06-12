package com.tianfangIMS.im.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.tianfangIMS.im.ConstantValue;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.adapter.AddTopContacts_GridView_Adapter;
import com.tianfangIMS.im.adapter.GroupTopContacts_GridView_Adapter;
import com.tianfangIMS.im.adapter.Group_AddTopContactsAdapter;
import com.tianfangIMS.im.bean.AddFriendBean;
import com.tianfangIMS.im.bean.AddFriendTwoBean;
import com.tianfangIMS.im.bean.LoginBean;
import com.tianfangIMS.im.bean.TopContactsListBean;
import com.tianfangIMS.im.bean.TopContactsRequestBean;
import com.tianfangIMS.im.dialog.LoadDialog;
import com.tianfangIMS.im.dialog.SendFlieMessageDialog;
import com.tianfangIMS.im.dialog.SendImageMessageSpDialog;
import com.tianfangIMS.im.utils.CommonUtil;
import com.tianfangIMS.im.utils.NToast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.Message;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by LianMengYu on 2017/2/5.
 * 用于创建群组是添加常用联系人
 */

public class Group_AddTopContactsActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private static final String TAG = "Group_AddTopContactsActivity";
    private Context mContext;
    private List<TopContactsListBean> topContactsList = new ArrayList<TopContactsListBean>();
    private ListView lv_topContacts;
    private RelativeLayout rl_selectAddGroupContacts_background;
    private GridView gv_addContacts;
    private List<AddFriendTwoBean> allChecked;
    private Map<String, Boolean> checkedMap;
    private TextView tv_addfriend_submit;
    private List<AddFriendBean> list;
    private AddTopContacts_GridView_Adapter gridView_adapter;
    private Group_AddTopContactsAdapter group_addTopContactsAdapter;
    private GroupTopContacts_GridView_Adapter groupTopContacts_gridView_adapter;
    private TopContactsListBean TopContactsListBean;
    private EditText et_search;
    private ArrayList<Message> allmessge;
    private ArrayList<Message> allFile;
    private String GroupID;
    private String GroupName;
    String sessionId;
    List<String> liststr = new ArrayList<>();//获取选中list的id
    private String PrivateID;
    String SimpleName;//常规创建群组的方法
    private String AddUserforGroup;

    private ArrayList<AddFriendBean> resultChecked = new ArrayList<>();//接受搜索选中后的数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtopcpntacts_activity);
        sessionId = getSharedPreferences("CompanyCode", MODE_PRIVATE).getString("CompanyCode", "");
        mContext = this;
        allmessge = getIntent().getParcelableArrayListExtra("allMessage");
        allFile = getIntent().getParcelableArrayListExtra("allFile");
        GroupID = getIntent().getStringExtra("Groupid");
        GroupName = getIntent().getStringExtra("GroupName");
        PrivateID = getIntent().getStringExtra("PrivateChat");
        SimpleName = getIntent().getStringExtra("SimpleName");
        AddUserforGroup = getIntent().getStringExtra("AddUserforGroup");
        resultChecked = (ArrayList<AddFriendBean>) getIntent().getSerializableExtra("ResultChecked");
        setTitle("选择群组联系人");
        init();
        GetCheckTopContacts();
    }

    private void init() {
        lv_topContacts = (ListView) this.findViewById(R.id.lv_addtopcontacts);
        rl_selectAddGroupContacts_background = (RelativeLayout) this.findViewById(R.id.rl_selectAddContacts_background);
        gv_addContacts = (GridView) this.findViewById(R.id.gv_addContacts);
        tv_addfriend_submit = (TextView) this.findViewById(R.id.tv_addfriend_submit);
        et_search = (EditText) this.findViewById(R.id.et_search);
        et_search.setFocusable(false);
        et_search.setOnClickListener(this);
        gv_addContacts.setOnItemClickListener(this);
        tv_addfriend_submit.setOnClickListener(this);
        lv_topContacts.setOnItemClickListener(this);

        View settingfootview = View.inflate(mContext, R.layout.addcontacts_footview, null);
        lv_topContacts.addFooterView(settingfootview);
    }


    private void GetCheckTopContacts() throws NullPointerException {
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
                        Log.e(TAG, "测试返回JSON数据：" + s);
                        if ((s.trim()).startsWith("<!DOCTYPE")) {
                            NToast.shortToast(mContext, "Session过期，请重新登陆");
                            startActivity(new Intent(mContext, LoginActivity.class));
                            RongIM.getInstance().logout();
                            finish();
                        } else {
                            if (!TextUtils.isEmpty(s) && !s.equals("{}")) {
                                Gson gson = new Gson();
                                Map<String, Object> map = gson.fromJson(s, new TypeToken<Map<String, Object>>() {
                                }.getType());
                                String code = map.get("code").toString();
                                if (code.equals("0.0")) {
                                    NToast.longToast(mContext, "您还没有联系人");
                                    return;
                                }
                                if ((code.equals("1.0"))) {
                                    Gson gson1 = new Gson();
                                    try {
                                        TopContactsListBean = gson1.fromJson(s, TopContactsListBean.class);
                                        group_addTopContactsAdapter = new Group_AddTopContactsAdapter(mContext, TopContactsListBean);
                                        lv_topContacts.setAdapter(group_addTopContactsAdapter);
                                        group_addTopContactsAdapter.notifyDataSetChanged();
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                return;
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

    Group_AddTopContactsAdapter.ViewHodler hodler;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (allmessge != null && allmessge.size() > 0) {
            SendImageMessageSpDialog sendImageMessageSpDialog = new SendImageMessageSpDialog(mContext, TopContactsListBean.getText().get(position).getId(), TopContactsListBean.getText().get(position).getFullname(), allmessge, Conversation.ConversationType.PRIVATE,
                    ConstantValue.ImageFile + TopContactsListBean.getText().get(position).getLogo(), TopContactsListBean.getText().get(position).getPosition());
            sendImageMessageSpDialog.show();
        }
        if (allFile != null && allFile.size() > 0) {
            SendFlieMessageDialog sendFlieMessageDialog = new SendFlieMessageDialog(mContext, TopContactsListBean.getText().get(position).getId(), TopContactsListBean.getText().get(position).getFullname(),
                    allFile, Conversation.ConversationType.PRIVATE,
                    ConstantValue.ImageFile + TopContactsListBean.getText().get(position).getLogo(), TopContactsListBean.getText().get(position).getPosition());
            if (!isFinishing()) {
                sendFlieMessageDialog.show();
            }
        }
        hodler = (Group_AddTopContactsAdapter.ViewHodler) view.getTag();
        hodler.cb_addfrien.toggle();
        Log.e(TAG, "点击ListView后的：" + group_addTopContactsAdapter.getCheckedMap().get(position));
        getCount();
        groupTopContacts_gridView_adapter.notifyDataSetChanged();
    }

    //对GridView 显示的宽高经行设置
    private void SettingGridView(List<AddFriendTwoBean> list) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int size = list.size();//要显示数据的个数
        //gridview的layout_widht,要比每个item的宽度多出2个像素，解决不能完全显示item的问题
        int allWidth = (int) (82 * size * density);
        //int allWidth = (int) ((width / 3 ) * size + (size-1)*3);//也可以这样使用，item的总的width加上horizontalspacing
        int itemWidth = (int) (65 * density);//每个item宽度
        LinearLayout.LayoutParams params = new
                LinearLayout.LayoutParams(allWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        gv_addContacts.setLayoutParams(params);
        gv_addContacts.setColumnWidth(itemWidth);
        gv_addContacts.setHorizontalSpacing(3);
        gv_addContacts.setStretchMode(GridView.NO_STRETCH);
        gv_addContacts.setNumColumns(size);
    }

    private void getCount() {
        checkedMap = group_addTopContactsAdapter.getCheckedMap();//获取选中的人，true是选中的，false是没选中的
        AddFriendTwoBean testCheckBean = new AddFriendTwoBean();
        allChecked = new ArrayList<AddFriendTwoBean>();//创建一个存储选中的人的集合
        Iterator a = checkedMap.keySet().iterator();//先迭代出来
        while (a.hasNext()) {
            String str = (String) a.next();
            liststr.add(str);
        }
        for (int i = 0; i < checkedMap.size(); i++) {
            if (checkedMap.get(liststr.get(i)) == null) {    //防止出现空指针,如果为空,证明没有被选中
                continue;
            } else if (checkedMap.get(liststr.get(i))) {
                for (int i1 = 0; i1 < TopContactsListBean.getText().size(); i1++) {
                    if (liststr.get(i) == TopContactsListBean.getText().get(i1).getId()) {
                        testCheckBean = TopContactsListBean.getText().get(i1);
                        allChecked.add(testCheckBean);
                    }
                }
                tv_addfriend_submit.setText("添加（" + (allChecked.size()) + "）");
            }
        }
        if (allChecked.size() <= 0) {
            rl_selectAddGroupContacts_background.setVisibility(View.GONE);
        } else {
            rl_selectAddGroupContacts_background.setVisibility(View.VISIBLE);
        }
//        if (resultChecked != null) {
//            //TODO
//            for (int i = 0; i < resultChecked.size(); i++) {
//            }
//        }
        groupTopContacts_gridView_adapter = new GroupTopContacts_GridView_Adapter(mContext, allChecked);
        SettingGridView(allChecked);
        gv_addContacts.setAdapter(groupTopContacts_gridView_adapter);
        groupTopContacts_gridView_adapter.notifyDataSetChanged();
        gv_addContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkedMap.put(allChecked.remove(position).getId(), false);
                tv_addfriend_submit.setText("添加（" + (allChecked.size()) + "）");
                if (allChecked.size() <= 0) {
                    rl_selectAddGroupContacts_background.setVisibility(View.GONE);
                }
                group_addTopContactsAdapter.notifyDataSetChanged();
                groupTopContacts_gridView_adapter.notifyDataSetChanged();
            }
        });
    }

    private void AddGroup() {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < allChecked.size(); i++) {
            list.add(allChecked.get(i).getId());
        }
        Gson gson = new Gson();
        LoginBean loginBean = gson.fromJson(CommonUtil.getUserInfo(mContext), LoginBean.class);
        list.add(loginBean.getText().getId());
        String UID = loginBean.getText().getId();
        String aa = list.toString();
        OkGo.post(ConstantValue.CREATEGROUP)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .headers("cookie", sessionId)
                .params("userid", UID)
                .params("groupids", aa)
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
                            }
                            Gson gson = new Gson();
                            TopContactsRequestBean bean = gson.fromJson(s, TopContactsRequestBean.class);
                            Log.e("创建群组", "---:" + s);
                            if (bean.getCode().equals("200")) {
                                NToast.shortToast(mContext, "创建成功");
                                RongIM.getInstance().refreshGroupInfoCache(new Group(bean.getText().getId(), bean.getText().getName(), null));
                                RongIM.getInstance().startGroupChat(mContext, bean.getText().getId(), bean.getText().getName());
                            } else {
                                NToast.shortToast(mContext, "创建失败");
                            }
                        }
                    }
                });
    }

    /**
     * 添加群组联系人
     */
    private void AddGroupUser() {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < allChecked.size(); i++) {
            String id = String.valueOf(allChecked.get(i).getId());
            list.add(id.toString().trim());
        }
        String ids = "[" + CommonUtil.listToString(list) + "]";
        Log.e("打印群组", "ids" + ids);
        OkGo.post(ConstantValue.ADDGROUPUSRT)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .headers("cookie", sessionId)
                .params("groupids", ids)
                .params("groupid", GroupID)
                .execute(new StringCallback() {
                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.e("Info", "meiyou" + s);
                        if (!TextUtils.isEmpty(s) && !s.equals("{}")) {
                            if ((s.trim()).startsWith("<!DOCTYPE")) {
                                NToast.shortToast(mContext, "Session过期，请重新登陆");
                                startActivity(new Intent(mContext, LoginActivity.class));
                                finish();
                            } else {
                                Gson gson = new Gson();
                                Map<String, Object> map = gson.fromJson(s, new TypeToken<Map<String, Object>>() {
                                }.getType());
                                if ((Double) map.get("code") == 1.0) {
                                    finish();
                                }
                            }
                        } else {
                            Log.e("Info", "没有数据");
                        }
                    }
                });
    }

    private void CreatePrivateChatforGroup() {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < allChecked.size(); i++) {
            String id = allChecked.get(i).getId() + "";
            list.add(id);
        }
        list.add(PrivateID);
        Gson gson = new Gson();
        LoginBean loginBean = gson.fromJson(CommonUtil.getUserInfo(mContext), LoginBean.class);
        list.add(loginBean.getText().getId());
        String UID = loginBean.getText().getId();
        String aa = list.toString();
        OkGo.post(ConstantValue.CREATEGROUP)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .headers("cookie", sessionId)
                .params("userid", UID)
                .params("groupids", aa)
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
                            }
                            Gson gson = new Gson();
                            TopContactsRequestBean bean = gson.fromJson(s, TopContactsRequestBean.class);
                            if (bean.getCode().equals("200")) {
                                NToast.shortToast(mContext, "创建成功");
                                RongIM.getInstance().refreshGroupInfoCache(new Group(bean.getText().getId(), bean.getText().getName(), null));
                                RongIM.getInstance().startGroupChat(mContext, bean.getText().getId(), bean.getText().getName());
                            } else {
                                NToast.shortToast(mContext, "创建失败");
                            }
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_addfriend_submit:
                if (!TextUtils.isEmpty(GroupID)) {
                    AddGroupUser();
                    finish();
                } else if (!TextUtils.isEmpty(PrivateID)) {
                    CreatePrivateChatforGroup();
                    finish();
                } else {
                    AddGroup();
                    finish();
                }
                break;
            case R.id.et_search:
                if (!TextUtils.isEmpty(SimpleName)) {
                    if (TextUtils.isEmpty(AddUserforGroup)) {
                        Intent intentSimpleName = new Intent(mContext, AddTopContacts_Activity.class);
                        intentSimpleName.putExtra("AddGroupforTopContacts", "AddGroupforTopContacts");
                        startActivity(intentSimpleName);
                        finish();
                    } else {
                        Intent intentSimpleName = new Intent(mContext, AddTopContacts_Activity.class);
                        intentSimpleName.putExtra("AddUserforGroup", "AddUserforGroup");
                        intentSimpleName.putExtra("GroupID", GroupID);
                        startActivity(intentSimpleName);
                        finish();
                    }
                } else {
                    startActivity(new Intent(mContext, SearchActivity.class));
                }
                break;
        }
    }
}
