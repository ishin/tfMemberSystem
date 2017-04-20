package com.tianfangIMS.im.activity;

import android.content.Context;
import android.content.Intent;
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
import com.tianfangIMS.im.bean.TopContactsBean;
import com.tianfangIMS.im.dialog.LoadDialog;
import com.tianfangIMS.im.utils.CommonUtil;
import com.tianfangIMS.im.utils.NToast;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by LianMengYu on 2017/1/23.
 */
public class AddTopContacts_Activity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private static final String TAG = "AddTopContacts_Activity";
    private ListView mlistView;
    private EditText et_search;
    private TextView tv_search_cencal;
    private Context mContext;
    private AddTopContactsAdapter addTopContactsAdapter;
    private List<AddFriendBean> list;
    private RelativeLayout rl_selectAddContacts_background;
    private TextView tv_addfriend_submit;
    private List<TopContactsBean> listBean = new ArrayList<TopContactsBean>();
    private GridView gv_addContacts;
    private AddTopContacts_GridView_Adapter gridView_adapter;
    private List<AddFriendBean> allChecked;
    private Map<String, Boolean> checkedMap;
    private List<AddFriendRequestBean> ListRequestInfo = new ArrayList<AddFriendRequestBean>();
    HashMap<Integer, Boolean> prepare;

    //    private LoginBean loginBean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtopcpntacts_activity);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String flag = (String) bundle.get("MainPlusDialog");
        mContext = this;
        init();
        setTitle("添加常用联系人");

//        getCount();
    }

    private void init() {
        mlistView = (ListView) this.findViewById(R.id.lv_addtopcontacts);
        et_search = (EditText) this.findViewById(R.id.et_search);
        tv_search_cencal = (TextView) this.findViewById(R.id.tv_search_cencal);
        tv_addfriend_submit = (TextView) this.findViewById(R.id.tv_addfriend_submit);
        tv_addfriend_submit.setOnClickListener(this);
        tv_search_cencal.setOnClickListener(this);
        mlistView.setOnItemClickListener(this);
        gv_addContacts = (GridView) this.findViewById(R.id.gv_addContacts);

        rl_selectAddContacts_background = (RelativeLayout) this.findViewById(R.id.rl_selectAddContacts_background);
        ChangeListView(et_search.getText().toString());
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    tv_search_cencal.setVisibility(View.GONE);
                } else {
                    tv_search_cencal.setVisibility(View.VISIBLE);
                    ChangeListView(et_search.getText().toString());
                }
            }
        });

    }

    private void ChangeListView(String GetSearch) {
        OkGo.post(ConstantValue.SEARCHFRIEND)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
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
                        if (!TextUtils.isEmpty(s) && !s.equals("{}")) {
                            Type listType = new TypeToken<List<AddFriendBean>>() {
                            }.getType();
                            Gson gson = new Gson();
                            list = gson.fromJson(s, listType);
                            addTopContactsAdapter = new AddTopContactsAdapter(mContext, list);
                            mlistView.setAdapter(addTopContactsAdapter);
                            addTopContactsAdapter.notifyDataSetChanged();
                        } else {
                            NToast.longToast(mContext, "请输入搜索条件");
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
    public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {
        AddTopContactsAdapter.Holder holder = (AddTopContactsAdapter.Holder) view.getTag();

        holder.cb_addfrien.toggle();

        rl_selectAddContacts_background.setVisibility(View.VISIBLE);
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
        checkedMap = addTopContactsAdapter.getCheckedMap();//获取选中的人，true是选中的，false是没选中的
        List<String> bb = new ArrayList<>();
        allChecked = new ArrayList<AddFriendBean>();//创建一个存储选中的人的集合
        Iterator a = checkedMap.keySet().iterator();//先迭代出来
        while (a.hasNext()){
            bb.add(a.next().toString());
        }

        for (int i = 0; i < checkedMap.size(); i++) {//循环获取选中人的集合

            if (checkedMap.get(bb.get(i)) == null) {    //防止出现空指针,如果为空,证明没有被选中
                continue;
            } else if (checkedMap.get(bb.get(i))) {//判断是否有值，如果为空证明没有被选中
                Log.e("checkedMap","-----:"+checkedMap.get(bb.get(i)));
                AddFriendBean testCheckBean = list.get(i);
                allChecked.add(testCheckBean);
                tv_addfriend_submit.setText("添加（" + (allChecked.size()) + "）");
            }
        }

    }

    private void setGridView() {
        gridView_adapter = new AddTopContacts_GridView_Adapter(mContext, allChecked);
        SettingGridView(allChecked);
        gv_addContacts.setAdapter(gridView_adapter);
        gridView_adapter.notifyDataSetChanged();

        gv_addContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String,Integer> posMap = new HashMap<String, Integer>();
//                posMap.put(allChecked.get(position).getId(),position);
                Log.e("打印数据集合","posMap---:"+allChecked.size());
                /**
                 * 通过for循环筛选出想要去掉的成员，就可以解决此bug
                 */
                allChecked.remove(position);
                addTopContactsAdapter.notifyDataSetChanged();
                gridView_adapter.notifyDataSetChanged();
//                for (int i = 0; i < checkedMap.size(); i++) {//循环获取选中人的集合
//                    if (checkedMap.get(i) == null) {   //防止出现空指针,如果为空,证明没有被选中
//                        continue;
//                    } else if (checkedMap.get(i)) {//判断是否有值，如果为空证明没有被选中

//                    }
//                }
            }
        });
    }

    private void SettingAddTopContacts() {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < allChecked.size(); i++) {
            list.add(allChecked.get(i).getAccount().toString());
        }
        final Gson gson = new Gson();
        LoginBean loginBean = gson.fromJson(CommonUtil.getUserInfo(mContext), LoginBean.class);
        String UID = loginBean.getText().getAccount();
        String str = list.toString();
        Log.e(TAG, "打印好友参数：" + UID + "---:" + str);
        OkGo.post(ConstantValue.ADDTOPCONTACTS)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
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
                        Log.e(TAG, "返回json:" + s);
                        Gson gson1 = new Gson();
                        Map<String, Object> map = gson1.fromJson(s, new TypeToken<Map<String, Object>>() {
                        }.getType());
                        if ((double) map.get("code") == -1.0) {
                            NToast.shortToast(mContext, "未知错误");
                        } else {
                            if (!TextUtils.isEmpty(s) && !s.equals("{}")) {
                                Gson gson = new Gson();
                                AddFriendRequestBean bean = gson.fromJson(s, AddFriendRequestBean.class);
                                if (bean.getCode().equals("1")) {
                                    NToast.shortToast(mContext, "添加好友成功");
                                }
                                if (bean.getCode().equals("0")) {
                                    NToast.shortToast(mContext, "存在好友关系");
                                }
                                if (bean.getCode().equals("-1")) {
                                    NToast.shortToast(mContext, "好友添加失败");
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
            case R.id.tv_search_cencal:
                et_search.getText().clear();
                break;
            case R.id.tv_addfriend_submit:
                SettingAddTopContacts();
                break;
        }
    }
}
