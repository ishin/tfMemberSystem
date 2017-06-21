package com.tianfangIMS.im.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.tianfangIMS.im.ConstantValue;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.adapter.CreateGroup_GridView_Adapter;
import com.tianfangIMS.im.adapter.InfoAdapter;
import com.tianfangIMS.im.bean.LoginBean;
import com.tianfangIMS.im.bean.TopContactsRequestBean;
import com.tianfangIMS.im.bean.TreeInfo;
import com.tianfangIMS.im.bean.ViewMode;
import com.tianfangIMS.im.dialog.LoadDialog;
import com.tianfangIMS.im.dialog.SendFlieMessageDialog;
import com.tianfangIMS.im.dialog.SendImageMessageSpDialog;
import com.tianfangIMS.im.utils.CommonUtil;
import com.tianfangIMS.im.utils.NToast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.rong.imkit.RongIM;
import io.rong.imkit.model.GroupUserInfo;
import io.rong.imlib.model.Conversation;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Titan on 2017/2/7.
 */

public class InfoActivity extends BaseActivity implements AdapterView.OnItemClickListener, InfoAdapter.OnDepartmentCheckedChangeListener, View.OnClickListener {

    Gson mGson;
    OkHttpClient mClient;
    Request mRequest;

    HashMap<Integer, HashMap<Integer, TreeInfo>> maps;
    List<TreeInfo> mTreeInfos;
    List<Integer> childCount;
    HashMap<Integer, TreeInfo> map;

    ListView activity_info_lv_part;

    InfoAdapter mAdapter;

    List<String> ImageMessageList;

    //树节点深度
    int currentLevel;

    List<TreeInfo> clickHistory;
    LinearLayout activity_info_ll_indicator;

    LinearLayout activity_info_ll_header;
    TextView activity_info_tv_header;

    int workerCount;

    TreeInfo mTreeInfo;

    Button activity_info_btn_tree;

    boolean isChecked;

    int oldLevel, selectedCount;
    private LinearLayout search_infoactivity;
    private EditText tv_search_icon;
    String sessionId;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            transfer();
        }
    };
    HashMap<Integer, Integer> mInfoMap;
    HashMap<Integer, Boolean> prepare;

    ViewMode mMode;
    String Groupid;//从群组详情页面传递过来的群组ID
    String GroupName;//从群组详情页面传递过来的群组ID
    String PrivateID;//从好友详情页面传递过来的好友IDS
    String SimpleName;//常规创建群组的方法
    int parentLevel;
    private TextView tv_creategroup_submit;
    private RelativeLayout rl_selectAddContacts_background;
    private Context mContext;
    private ArrayList<String> ListUri;
    private GridView gv_create;
    private ArrayList<io.rong.imlib.model.Message> allMessage;
    private ArrayList<io.rong.imlib.model.Message> allfile;
    CreateGroup_GridView_Adapter gridview_adapter;
    boolean tmpBool;
    private String AddUserforGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        setTitle("选择所有联系人");
        sessionId = getSharedPreferences("CompanyCode", MODE_PRIVATE).getString("CompanyCode", "");
        Groupid = getIntent().getStringExtra("Groupid");
        GroupName = getIntent().getStringExtra("GroupName");
        PrivateID = getIntent().getStringExtra("PrivateChat");
        allMessage = getIntent().getParcelableArrayListExtra("allMessage");
        allfile = getIntent().getParcelableArrayListExtra("allFile");
        SimpleName = getIntent().getStringExtra("SimpleName");
        ListUri = getIntent().getStringArrayListExtra("ListUri");
        AddUserforGroup = getIntent().getStringExtra("AddUserforGroup");
        mContext = this;
        maps = (HashMap<Integer, HashMap<Integer, TreeInfo>>) getIntent().getSerializableExtra("maps");
        currentLevel = getIntent().getIntExtra("currentLevel", -1);
        parentLevel = getIntent().getIntExtra("parentLevel", -1);
        mMode = (ViewMode) getIntent().getSerializableExtra("viewMode");
        activity_info_btn_tree = (Button) findViewById(R.id.activity_info_btn_tree);
        activity_info_btn_tree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent mIntent = new Intent(InfoActivity.this, TreeActivity.class);
//                mIntent.putExtra("map", maps);
//                startActivityForResult(mIntent, 100);
//                GetInfo();
            }
        });
        tv_search_icon = (EditText) this.findViewById(R.id.et_search);
        gv_create = (GridView) this.findViewById(R.id.gv_create);
        activity_info_lv_part = (ListView) findViewById(R.id.activity_info_lv_part);
        activity_info_ll_indicator = (LinearLayout) findViewById(R.id.activity_info_ll_indicator);
        activity_info_ll_header = (LinearLayout) findViewById(R.id.activity_info_ll_header);
        activity_info_tv_header = (TextView) findViewById(R.id.activity_info_tv_header);
        tv_creategroup_submit = (TextView) this.findViewById(R.id.tv_creategroup_submit);
        search_infoactivity = (LinearLayout) this.findViewById(R.id.search_infoactivity);
        search_infoactivity.setOnClickListener(this);
        tv_search_icon.setFocusable(false);
        tv_search_icon.setOnClickListener(this);
        rl_selectAddContacts_background = (RelativeLayout) this.findViewById(R.id.rl_selectinfo_background);
        tv_creategroup_submit.setOnClickListener(this);
        activity_info_lv_part.setOnItemClickListener(this);

        clickHistory = new ArrayList<TreeInfo>();
        mTreeInfos = new ArrayList<>();
        childCount = new ArrayList<Integer>();
        if (mMode == ViewMode.CHECK) {
            prepare = new HashMap<>();
        }
        mAdapter = new InfoAdapter(InfoActivity.this, mTreeInfos, childCount, mMode, prepare);
        activity_info_lv_part.setAdapter(mAdapter);
        mAdapter.setOnDepartmentCheckedChangeListener(this);
        for (TreeInfo info : maps.get(parentLevel).values()) {
            if (info.getId() == currentLevel) {
                clickHistory.add(info);
                break;
            }
        }
        transfer();
        View settingfootview = View.inflate(mContext, R.layout.info_footview, null);
        activity_info_lv_part.addFooterView(settingfootview);
    }

    private void transfer() {
        //清除适配数据集合
        mTreeInfos.clear();
        childCount.clear();
        //得到下一级部门的数据集合
        map = maps.get(currentLevel);
        //如果没有子部门 直接进行提示
        if (map == null) {
            Toast.makeText(this, "没有子部门", Toast.LENGTH_SHORT).show();
            onBackPressed();
            return;
        }
        //移除位置指示
        activity_info_ll_indicator.removeAllViews();
        float density = getResources().getDisplayMetrics().density;
        if (clickHistory.size() >= 2) {
            activity_info_ll_header.setVisibility(View.GONE);
        } else {
            activity_info_ll_header.setVisibility(View.VISIBLE);
        }
        //重新进行位置指示数据添加
        for (TreeInfo treeInfo : clickHistory) {
            TextView mTextView = new TextView(this);
            mTextView.setText(treeInfo.getName());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins((int) (16 * density), (int) (16 * density), (int) (16 * density), (int) (16 * density));
            mTextView.setLayoutParams(lp);
            mTextView.setTag(treeInfo);
            mTextView.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));

            mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTreeInfo = (TreeInfo) v.getTag();
                    int index = clickHistory.indexOf(mTreeInfo);
                    if (index == -1) {
                        clickHistory.clear();
                        currentLevel = 0;
                    } else {
                        clickHistory = clickHistory.subList(0, index + 1);
                        currentLevel = mTreeInfo.getId();
                    }
                    transfer();
                }
            });
            activity_info_ll_indicator.addView(mTextView);
            TextView symbol = new TextView(this);
            symbol.setText(">");
            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER_VERTICAL;
            symbol.setLayoutParams(lp);
            activity_info_ll_indicator.addView(symbol);
        }
        //移除最后的方向指示 && 改变最后(即当前部门)颜色
        if (activity_info_ll_indicator.getChildCount() > 0) {
            activity_info_ll_indicator.removeViewAt(activity_info_ll_indicator.getChildCount() - 1);
            ((TextView) activity_info_ll_indicator.getChildAt(activity_info_ll_indicator.getChildCount() - 1)).setTextColor(getResources().getColor(android
                    .R.color.darker_gray));
        }
        //将Map数据集合转换为List
        for (TreeInfo treeInfo : map.values()) {
//            if (isChecked) {
//                treeInfo.setChecked(isChecked);
//            }
            mTreeInfos.add(treeInfo);
        }
        isChecked = false;
        //根据部门编号 进行排序
        Collections.sort(mTreeInfos, new Comparator<TreeInfo>() {
            @Override
            public int compare(TreeInfo o1, TreeInfo o2) {
                return o1.getId() < o2.getId() ? -1 : 1;
            }
        });
        //显示Item后的子部门人数
        for (TreeInfo treeInfo : mTreeInfos) {
            workerCount = 0;
            //员工类型
            if (treeInfo.getFlag() == 1) {
                childCount.add(workerCount);
            } else {
                //部门类型
                calcSum(maps.get(treeInfo.getId()));
                childCount.add(workerCount);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mTreeInfo = mTreeInfos.get(position);
        //点击的选项为员工类型
        if (mTreeInfo.getFlag() == 1) {
            //Toast.makeText(this, "即将进入通信界面", Toast.LENGTH_SHORT).show();
            String ids = String.valueOf(mTreeInfo.getId());
            if (allMessage != null && allMessage.size() > 0) {
                SendImageMessageSpDialog sendImageMessageSpDialog = new SendImageMessageSpDialog(mContext, ids, mTreeInfo.getName(), allMessage, Conversation.ConversationType.PRIVATE,
                        ConstantValue.ImageFile + mTreeInfo.getLogo(), null);
                sendImageMessageSpDialog.show();
            } else if (allfile != null && allfile.size() > 0) {
                SendFlieMessageDialog sendFlieMessageDialog = new SendFlieMessageDialog(mContext, ids, mTreeInfo.getName(),
                        allfile, Conversation.ConversationType.PRIVATE,
                        ConstantValue.ImageFile + mTreeInfo.getLogo(), null);
                sendFlieMessageDialog.show();
            } else {
//                RongIM.getInstance().startPrivateChat(mContext, ids, mTreeInfo.getName());
                Intent intent = new Intent(mContext, FriendPersonInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("userId", ids);
                intent.putExtras(bundle);
                intent.putExtra("conversationType", Conversation.ConversationType.PRIVATE);
                startActivity(intent);
            }
            return;
        }
        isChecked = mTreeInfos.get(position).isChecked();
        //记录下一级部门的PID(当前部门的ID即为下一级的PID)
        currentLevel = mTreeInfos.get(position).getId();
        //将点击记录存入回退集合中
        clickHistory.add(mTreeInfo);
        //进行数据抽取
        transfer();
    }

    @Override
    public void onBackPressed() {
        try {
            if (clickHistory.size() == 1) {
                super.onBackPressed();
            } else {
                oldLevel = currentLevel;
                selectedCount = 0;
                for (TreeInfo info : mTreeInfos) {
                    if (info.isChecked()) {
                        selectedCount++;
                    }
                }
                currentLevel = clickHistory.get(clickHistory.size() - 1).getPid();
                maps.get(currentLevel).get(oldLevel).setChecked(selectedCount == mTreeInfos.size() ? true : false);
                clickHistory.remove(clickHistory.size() - 1);
                transfer();
            }
        } catch (Exception e) {
            Toast.makeText(this, "已经到达结构根节点", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }
    }

    /**
     * 迭代计算传入部门的总人数
     *
     * @param tmp
     */
    private void calcSum(Map<Integer, TreeInfo> tmp) {
        if (tmp != null) {
            for (TreeInfo treeInfo : tmp.values()) {
                workerCount++;
                if (treeInfo.getFlag() == 1) {
                    continue;
                }
                Map<Integer, TreeInfo> child = maps.get(treeInfo.getId());
                if (child != null) {
                    calcSum(child);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 100:
                    clickHistory.clear();
                    clickHistory.addAll((Collection<? extends TreeInfo>) data.getSerializableExtra("clickHistory"));
                    currentLevel = data.getIntExtra("currentLevel", -1);
                    transfer();
                    break;
            }
        }
    }

    //对GridView 显示的宽高经行设置
    private void SettingGridView(List<TreeInfo> list) {
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
        gv_create.setLayoutParams(params);
        gv_create.setColumnWidth(itemWidth);
        gv_create.setHorizontalSpacing(3);
        gv_create.setStretchMode(GridView.NO_STRETCH);
        gv_create.setNumColumns(size);
    }

    private List<TreeInfo> results = new ArrayList<>();
    private Map<Integer, Boolean> checkedMap;

    private void GetInfo() {
        results = new ArrayList<>();//这个就是数据被选中的数据源
        for (HashMap<Integer, TreeInfo> hashMap : maps.values()) {
            for (TreeInfo info : hashMap.values()) {
                if (info.isChecked() && info.getFlag() == 1) {
                    results.add(info);
                }
            }
        }
        if (results.size() <= 0) {
            rl_selectAddContacts_background.setVisibility(View.GONE);
        } else {
            rl_selectAddContacts_background.setVisibility(View.VISIBLE);
        }
        gridview_adapter = new CreateGroup_GridView_Adapter(InfoActivity.this, results);
        SettingGridView(results);
        gv_create.setAdapter(gridview_adapter);
        gridview_adapter.notifyDataSetChanged();
        tv_creategroup_submit.setText("添加（" + results.size() + "）");

        //底部GridView弹出框item事件
        gv_create.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO
                TreeInfo mInfo = results.get(position);
                maps.get(mInfo.getPid()).get(mInfo.getId()).setChecked(false);
                results.remove(position);
                if (maps.get(mInfo.getPid()).get(mInfo.getId()).isChecked() == false) {

                }
//                results.remove(position).getId();
                tv_creategroup_submit.setText("添加（" + results.size() + "）");
                gridview_adapter.notifyDataSetChanged();
                if (results.size() <= 0) {
                    rl_selectAddContacts_background.setVisibility(View.GONE);
                }
                transfer();
            }
        });
    }

    @Override
    public void onCheckedChange(int pid, int id, TreeInfo info, int position) {
        checkedMap = mAdapter.getCheckedMap();
//        TreeInfo info = mTreeInfos.get(position);
//        int pid = info.getPid();
//        int id = info.getId();
//        Toast.makeText(this, position + "," + info.getName(), Toast.LENGTH_SHORT).show();
        maps.get(pid).put(id, info);
        tmpBool = info.isChecked();
        //部门类型
//        if (info.getFlag() != 1) {
        //只要为True 就表明有子部门
        if (maps.containsKey(info.getId())) {
            int index = 0;
            GetInfo();
            for (TreeInfo treeInfo : maps.get(info.getId()).values()) {
                treeInfo.setChecked(tmpBool);
                onCheckedChange(treeInfo.getPid(), treeInfo.getId(), treeInfo, index);
                index++;
                GetInfo();
            }
        } else {
            GetInfo();
        }
//        }
    }

    /**
     * 单聊好友详情创建群组
     */
    private void CreatePrivateChatforGroup() {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < results.size(); i++) {
            String id = results.get(i).getId() + "";
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
                                RongIM.getInstance().refreshGroupUserInfoCache(new GroupUserInfo(bean.getText().getId(), bean.getText().getName(), null));
                                RongIM.getInstance().startGroupChat(mContext, bean.getText().getId(), bean.getText().getName());
                            } else {
                                NToast.shortToast(mContext, "创建失败");
                            }
                        }
                    }
                });
    }

    /**
     * 常规创建群组创建群组
     */
    private void CreateGroup() {
        List<String> list = new ArrayList<String>();

        for (int i = 0; i < results.size(); i++) {
            String id = results.get(i).getId() + "";
            list.add(id);
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
                                RongIM.getInstance().refreshGroupUserInfoCache(new GroupUserInfo(bean.getText().getId(), bean.getText().getName(), null));
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
        for (int i = 0; i < results.size(); i++) {
            String id = String.valueOf(results.get(i).getId());
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
                .params("groupid", Groupid)
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
                                if ((Double) map.get("code") == 1.0) {
                                    finish();
                                }
                            }
                        } else {
                            NToast.shortToast(mContext, "群组成员已经存在");
                            return;
                        }
                    }
                });
    }


    /**
     * Groupid = getIntent().getStringExtra("Groupid");
     * PrivateID = getIntent().getStringExtra("PrivateChat");
     * ImageMessageList = getIntent().getStringArrayListExtra("ImageUri");
     * SimpleName = getIntent().getStringExtra("SimpleName");
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_creategroup_submit:
                if (!TextUtils.isEmpty(Groupid)) {
                    AddGroupUser();
                    finish();
                } else if (!TextUtils.isEmpty(PrivateID)) {
                    CreatePrivateChatforGroup();
                    finish();
                } else if (!TextUtils.isEmpty(SimpleName)) {
                    CreateGroup();
                    finish();
                }
                break;
            case R.id.et_search:
                if (!TextUtils.isEmpty(SimpleName)) {
                    if (!TextUtils.isEmpty(AddUserforGroup)) {
                        Intent intentSimpleName = new Intent(mContext, AddTopContacts_Activity.class);
                        intentSimpleName.putExtra("AddUserforGroup", "AddUserforGroup");
                        intentSimpleName.putExtra("GroupID", Groupid);
                        startActivity(intentSimpleName);
                        finish();
                    } else {
                        Intent intentSimpleName = new Intent(mContext, AddTopContacts_Activity.class);
                        intentSimpleName.putExtra("AddUserforGroup", "AddUserforGroup");
                        intentSimpleName.putExtra("GroupID", Groupid);
                        startActivity(intentSimpleName);
                        finish();
                    }
                } else {
                    Log.e("asdasdsa123d123", "执行3");
                    startActivity(new Intent(mContext, SearchActivity.class));
                }
                break;
        }
    }
}
