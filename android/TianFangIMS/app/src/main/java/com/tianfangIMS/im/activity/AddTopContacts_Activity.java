package com.tianfangIMS.im.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.tianfangIMS.im.adapter.AddTopContactsAdapter;
import com.tianfangIMS.im.adapter.AddTopContacts_GridView_Adapter;
import com.tianfangIMS.im.bean.AddFriendBean;
import com.tianfangIMS.im.bean.AddFriendRequestBean;
import com.tianfangIMS.im.bean.LoginBean;
import com.tianfangIMS.im.bean.TopContactsRequestBean;
import com.tianfangIMS.im.dialog.LoadDialog;
import com.tianfangIMS.im.utils.CommonUtil;
import com.tianfangIMS.im.utils.NToast;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by LianMengYu on 2017/1/23.
 */
public class AddTopContacts_Activity extends BaseActivity implements View.OnClickListener, AddTopContactsAdapter.setOnCheckedData {
    private static final String TAG = "AddTopContacts_Activity";
    private ListView mlistView;
    private EditText et_search;
    private TextView tv_search_cencal;
    private Context mContext;
    private AddTopContactsAdapter addTopContactsAdapter;
    private ArrayList<AddFriendBean> list = new ArrayList<>();
    private Map<String, AddFriendBean> maps = new HashMap<>();
    private RelativeLayout rl_selectAddContacts_background;
    private TextView tv_addfriend_submit;
    private GridView gv_addContacts;
    private AddTopContacts_GridView_Adapter gridView_adapter;
    private ArrayList<AddFriendBean> allChecked = new ArrayList<>();
    private Map<String, Boolean> checkedMap;
    String sessionId;
    private LinearLayout no_result_group;
    private String AddGroupforTopContacts;
    View settingfootview;
    private String AddUserforGroup;
    private String GroupID;
    private ArrayList<AddFriendBean> listData;
    ArrayList<AddFriendBean> newal = new ArrayList();//新建一个中间集合

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtopcpntacts_activity);
        mContext = this;
        sessionId = getSharedPreferences("CompanyCode", MODE_PRIVATE).getString("CompanyCode", "");
        AddGroupforTopContacts = getIntent().getStringExtra("AddGroupforTopContacts");
        AddUserforGroup = getIntent().getStringExtra("AddUserforGroup");
        GroupID = getIntent().getStringExtra("GroupID");
        init();
        setTitle("添加常用联系人");
    }

    private void init() {
        mlistView = (ListView) this.findViewById(R.id.lv_addtopcontacts);
        et_search = (EditText) this.findViewById(R.id.et_search);
        tv_search_cencal = (TextView) this.findViewById(R.id.tv_search_cencal);
        tv_addfriend_submit = (TextView) this.findViewById(R.id.tv_addfriend_submit);
        no_result_group = (LinearLayout) this.findViewById(R.id.no_result_addtop);
        tv_addfriend_submit.setOnClickListener(this);
        tv_search_cencal.setOnClickListener(this);
//        mlistView.setOnItemClickListener(this);

        gv_addContacts = (GridView) this.findViewById(R.id.gv_addContacts);
        rl_selectAddContacts_background = (RelativeLayout) this.findViewById(R.id.rl_selectAddContacts_background);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String ss = s.toString();
                if (ss.length() == 0) {
                    mlistView.setAdapter(null);
                    no_result_group.setVisibility(View.GONE);
                } else {
                    try {
                        ChangeListView(java.net.URLEncoder.encode(et_search.getText().toString(), "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        settingfootview = View.inflate(mContext, R.layout.addcontacts_footview, null);
        mlistView.addFooterView(settingfootview);
    }

    private void ChangeListView(String GetSearch) {
        OkGo.post(ConstantValue.SEARCHFRIEND)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .headers("cookie", sessionId)
                .params("account", GetSearch)
                .execute(new StringCallback() {
                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        LoadDialog.show(mContext);
                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        LoadDialog.dismiss(mContext);
                        if (!TextUtils.isEmpty(s) && !s.equals("[]")) {
                            if ((s.trim()).startsWith("<!DOCTYPE")) {
                                NToast.shortToast(mContext, "Session过期，请重新登陆");
                                startActivity(new Intent(mContext, LoginActivity.class));
                                RongIM.getInstance().logout();
                                finish();
                            } else {
                                Type listType = new TypeToken<List<AddFriendBean>>() {
                                }.getType();
                                Gson gson = new Gson();
                                newal = gson.fromJson(s, listType);
                                //去重复
                                if (list != null){
                                    list.clear();
                                }
                                for (int i = 0; i < newal.size(); i++) {
                                    if (!list.contains(newal.get(i))) {
                                        list.add(newal.get(i));
                                    }
                                }
                                addTopContactsAdapter = new AddTopContactsAdapter(mContext, list);
                                addTopContactsAdapter.setOnCheckedData(AddTopContacts_Activity.this);
                                addTopContactsAdapter.setMaps(maps);
                                mlistView.setAdapter(addTopContactsAdapter);
                                addTopContactsAdapter.notifyDataSetChanged();
                                no_result_group.setVisibility(View.GONE);
                            }
                        } else {
                            mlistView.setAdapter(null);
                            no_result_group.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
//                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext, "请求失败");
                        return;
                    }

                });
    }

    @Override
    public void OnCheckedData() {
        getCount();
        setGridView();
        gridView_adapter.notifyDataSetChanged();
    }


    //对GridView 显示的宽高经行设置
    private void SettingGridView(List<AddFriendBean> list) {
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
        allChecked.clear();
        Iterator a = maps.values().iterator();//先迭代出来
        while (a.hasNext()) {
            AddFriendBean str = (AddFriendBean) a.next();
            allChecked.add(str);
            tv_addfriend_submit.setText("添加（" + (allChecked.size()) + "）");
        }
        if (allChecked.size() <= 0) {
            rl_selectAddContacts_background.setVisibility(View.GONE);
        } else {
            rl_selectAddContacts_background.setVisibility(View.VISIBLE);
        }
        addTopContactsAdapter.notifyDataSetChanged();
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//        checkedMap = addTopContactsAdapter.getCheckedMap();//获取选中的人，true是选中的，false是没选中的
//        AddFriendBean testCheckBean = new AddFriendBean();
//        allChecked = new ArrayList<AddFriendBean>();//创建一个存储选中的人的集合
//        Iterator a = checkedMap.keySet().iterator();//先迭代出来
//        while (a.hasNext()) {
//            String str = (String) a.next();
//            liststr.add(str);
//        }
//        liststr.add(null);
//        for (int i = 0; i < checkedMap.size(); i++) {//循环获取选中人的集合
//            if (checkedMap.get(liststr.get(i)) == null) {    //防止出现空指针,如果为空,证明没有被选中
//                continue;
//            } else if (checkedMap.get(liststr.get(i))) {//判断是否有值，如果为空证明没有被选中
//                for (int i1 = 0; i1 < list.size(); i1++) {
//                    if (liststr.get(i) == list.get(i1).getId()) {
//                        testCheckBean = list.get(i1);
//                        allChecked.add(testCheckBean);
//                    }
//                }
//                tv_addfriend_submit.setText("添加（" + (allChecked.size()) + "）");
//            }
//
//        }
    }

    private void setGridView() {
        gridView_adapter = new AddTopContacts_GridView_Adapter(mContext, allChecked);
        SettingGridView(allChecked);
        gv_addContacts.setAdapter(gridView_adapter);
        gridView_adapter.notifyDataSetChanged();
        gv_addContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                maps.remove(allChecked.get(position).getId());
                allChecked.remove(position);
                tv_addfriend_submit.setText("添加（" + (allChecked.size()) + "）");

//                allChecked.remove(allChecked.get(position).getId());
//                checkedMap.put(allChecked.remove(position).getId(), false);

                if (allChecked.size() <= 0) {
                    rl_selectAddContacts_background.setVisibility(View.GONE);
//                    mlistView.removeFooterView(settingfootview);
                }
                addTopContactsAdapter.notifyDataSetChanged();
                gridView_adapter.notifyDataSetChanged();
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
//                                    RongIM.getInstance().startGroupChat(mContext, GroupID, "");
                                    finish();
                                }
                            }
                        } else {
                            NToast.shortToast(mContext, "添加成员错误");
                            Log.e("Info", "没有数据");
                        }
                    }
                });
    }


    private void SettingAddTopContacts() {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < allChecked.size(); i++) {
            list.add(allChecked.get(i).getAccount().toString());
        }
        final Gson gson = new Gson();
        final LoginBean loginBean = gson.fromJson(CommonUtil.getUserInfo(mContext), LoginBean.class);
        String UID = loginBean.getText().getAccount();
        String str = list.toString();
        OkGo.post(ConstantValue.ADDTOPCONTACTS)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .headers("cookie", sessionId)
                .params("account", UID)
                .params("friend", str)
                .execute(new StringCallback() {
                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        LoadDialog.show(mContext);
                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        LoadDialog.dismiss(mContext);
                        if ((s.trim()).startsWith("<!DOCTYPE")) {
                            NToast.shortToast(mContext, "Session过期，请重新登陆");
                            startActivity(new Intent(mContext, LoginActivity.class));
                            RongIM.getInstance().logout();
                            finish();
                        } else {
                            if (!TextUtils.isEmpty(s) && !s.equals("{}")) {
                                Log.e(TAG, "返回json:" + s);
                                Gson gson1 = new Gson();
                                Map<String, Object> map = gson1.fromJson(s, new TypeToken<Map<String, Object>>() {
                                }.getType());
                                if ((double) map.get("code") == -1.0) {
                                    NToast.shortToast(mContext, "请选取好友");
                                } else {
                                    if (!TextUtils.isEmpty(s) && !s.equals("{}")) {
                                        Gson gson = new Gson();
                                        AddFriendRequestBean bean = gson.fromJson(s, AddFriendRequestBean.class);
                                        if (bean.getCode().equals("1")) {
                                            NToast.shortToast(mContext, "添加好友成功");
                                            RongIM.getInstance().refreshUserInfoCache(new UserInfo(loginBean.getText().getId(), loginBean.getText().getFullname(),
                                                    Uri.parse(ConstantValue.ImageFile + loginBean.getText().getLogo())));
                                            startActivity(new Intent(mContext, MineTopContactsActivity.class));
                                            finish();
                                        }
                                        if (bean.getCode().equals("0")) {
                                            NToast.shortToast(mContext, "存在好友关系");
                                        }
                                        if (bean.getCode().equals("-1")) {
                                            NToast.shortToast(mContext, "好友添加失败");
                                        }
                                    }
                                }
                            } else {
                                NToast.shortToast(mContext, "存在好友关系联系人");
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

    /**
     * 常规创建群组创建群组
     */
    private void CreateGroup() {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < allChecked.size(); i++) {
            list.add(allChecked.get(i).getId().toString());
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
                            if (bean.getCode().equals("200")) {
                                NToast.shortToast(mContext, "创建成功");
                                RongIM.getInstance().startGroupChat(mContext, bean.getText().getId(), bean.getText().getName());
                                finish();
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
            case R.id.tv_search_cencal:
                if (TextUtils.isEmpty(et_search.getText().toString())) {
                    finish();
                } else {
                    et_search.getText().clear();
                }
                break;
            case R.id.tv_addfriend_submit:
                if (!TextUtils.isEmpty(AddGroupforTopContacts)) {
                    CreateGroup();
//                    Intent intent = new Intent(mContext, Group_AddTopContactsActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("ResultChecked", allChecked);
//                    intent.putExtras(bundle);
//                    startActivity(intent);
                } else if (!TextUtils.isEmpty(AddUserforGroup)) {
                    AddGroupUser();
                } else {
                    SettingAddTopContacts();
                }
                break;
        }
    }
}
