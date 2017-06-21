package com.tianfangIMS.im.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.tianfangIMS.im.ConstantValue;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.adapter.InfoAdapter;
import com.tianfangIMS.im.bean.TreeInfo;
import com.tianfangIMS.im.bean.ViewMode;
import com.tianfangIMS.im.dialog.LoadDialog;
import com.tianfangIMS.im.utils.NToast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Message;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by LianMengYu on 2017/2/5.
 */

public class AddGroupActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "AddTopContacts_Activity";
    private RelativeLayout rl_group_topcontacts;
    private Context mContext;
    private ListView lv_addGroup_company;
    private RelativeLayout rl_allContacts;
    Gson mGson;
    ArrayList<Integer> childCount;
    ArrayList<TreeInfo> mTreeInfos, clickHistory, mTreeInfosnull;

    HashMap<Integer, TreeInfo> map;
    HashMap<Integer, HashMap<Integer, TreeInfo>> maps;
    private boolean flag = true;
    InfoAdapter mAdapter;
    int workerCount;

    int currentLevel;

    Intent mIntent;

    HashMap<Integer, Boolean> prepare;
    private String GroupID;
    private String GroupName;
    private String PrivateId;
    private EditText et_search;
    private ArrayList<String> ImageMessageList;//接收转发的图片消息
    private ArrayList<Message> AllMessage;
    private ArrayList<Message> allFile;
    private String SimpleName;
    private String isGroupUUID;//判断是否为群组“+”号传值
    private String isfinish;//是否注销
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addgroup_layout);
        mContext = this;
        GroupID = getIntent().getStringExtra("GroupId");
        GroupName = getIntent().getStringExtra("GroupName");
        PrivateId = getIntent().getStringExtra("PrivateChat");
        AllMessage = getIntent().getParcelableArrayListExtra("allMessage");
        allFile = getIntent().getParcelableArrayListExtra("allFile");
        SimpleName = getIntent().getStringExtra("SimpleName");
        isGroupUUID = getIntent().getStringExtra("IsGroup");
        isfinish = getIntent().getStringExtra("isfinish");
        setTitle("选择群组联系人");
        init();
        GetData();
    }

    private void init() {
        rl_group_topcontacts = (RelativeLayout) this.findViewById(R.id.rl_group_topcontacts);
        rl_group_topcontacts.setOnClickListener(this);
        lv_addGroup_company = (ListView) this.findViewById(R.id.lv_addGroup_company);
        rl_allContacts = (RelativeLayout) this.findViewById(R.id.rl_allContacts);
        et_search = (EditText) this.findViewById(R.id.et_search);
        rl_allContacts.setOnClickListener(this);
        et_search.setFocusable(false);
        et_search.setOnClickListener(this);
    }


    private void GetData() throws NullPointerException {
        String sessionId = getSharedPreferences("CompanyCode", MODE_PRIVATE).getString("CompanyCode", "");
        OkGo.post(ConstantValue.DEPARTMENTPERSON)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .headers("cookie", sessionId)
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
                            mGson = new Gson();
                            mTreeInfos = mGson.fromJson(s, new TypeToken<List<TreeInfo>>() {
                            }.getType());
                            mTreeInfosnull = new ArrayList<TreeInfo>();
                            for (int i = 0; i < mTreeInfos.size(); i++) {
                                if (mTreeInfos.get(i).getId() != null) {
                                    mTreeInfosnull.add(mTreeInfos.get(i));
                                }
                            }

                            try {
                                //根据PID进行平行节点排序 如果PID相同 则根据自身ID进行前后排序
                                Collections.sort(mTreeInfosnull, new Comparator<TreeInfo>() {
                                    public int compare(TreeInfo o1, TreeInfo o2) {
                                        if (o1.getPid() != null && o2.getPid() != null) {
                                            if (o1.getPid() < o2.getPid()) {
                                                return -1;
                                            } else if (o1.getPid() > o2.getPid()) {
                                                return 1;
                                            }
                                        }
                                        return o1.getId() < o2.getId() ? -1 : 1;
                                    }
                                });
                                currentLevel = mTreeInfosnull.get(0).getPid();
                                maps = new HashMap<Integer, HashMap<Integer, TreeInfo>>();
                                //规定最小PID为0 保证与最小PID不相同
                                int pid = -1;
                                for (TreeInfo treeInfo : mTreeInfosnull) {
                                    //如果当前pid等于之前的pid 说明该平行节点组已被创建 直接将其放入当前平行节点组内即可
                                    if (pid == treeInfo.getPid()) {
                                        map.put(treeInfo.getId(), treeInfo);
                                    } else {
                                        if (map != null) {
                                            //当前平行节点已结束 填入父Map 自身置空
                                            maps.put(pid, map);
                                            map = null;
                                        }
                                        //如果不同 则说明进入了新的平行节点组
                                        pid = treeInfo.getPid();
                                        if (map == null) {
                                            map = new HashMap<Integer, TreeInfo>();
                                            map.put(treeInfo.getId(), treeInfo);
                                        }
                                    }
                                }
                                //最后的一组平行节点组进行嵌入
                                maps.put(pid, map);
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        clickHistory = new ArrayList<TreeInfo>();
                                        mTreeInfosnull = new ArrayList<TreeInfo>();
                                        childCount = new ArrayList<Integer>();
                                        mAdapter = new InfoAdapter(mContext, mTreeInfosnull, childCount, ViewMode.NORMAL, prepare);
                                        lv_addGroup_company.setAdapter(mAdapter);
                                        transfer();
                                    }
                                });
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void transfer() {
        //清除适配数据集合
        mTreeInfosnull.clear();
        childCount.clear();
        //得到下一级部门的数据集合
        map = maps.get(currentLevel);
        //如果没有子部门 直接进行提示
        if (map == null) {
            return;
        }
        //将Map数据集合转换为List
        for (TreeInfo treeInfo : map.values()) {
            mTreeInfosnull.add(treeInfo);
        }
        //根据部门编号 进行排序
        Collections.sort(mTreeInfosnull, new Comparator<TreeInfo>() {
            public int compare(TreeInfo o1, TreeInfo o2) {
                return o1.getId() < o2.getId() ? -1 : 1;
            }
        });
        //显示Item后的子部门人数
        for (TreeInfo treeInfo : mTreeInfosnull) {
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

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_group_topcontacts:
                Intent intent = new Intent(AddGroupActivity.this, Group_AddTopContactsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("MainPlusDialog", TAG);
                intent.putExtras(bundle);
                if (AllMessage != null && AllMessage.size() > 0) {
                    intent.putParcelableArrayListExtra("allMessage", AllMessage);
                }
                if (allFile != null && allFile.size() > 0) {
                    intent.putParcelableArrayListExtra("allFile", allFile);
                }
                if (!TextUtils.isEmpty(GroupID)) {
                    intent.putExtra("Groupid", GroupID);
                }
                if (!TextUtils.isEmpty(GroupName)) {
                    intent.putExtra("GroupName", GroupName);
                }
                if (!TextUtils.isEmpty(PrivateId)) {
                    intent.putExtra("PrivateChat", PrivateId);
                }
                if (!TextUtils.isEmpty(SimpleName)) {
                    intent.putExtra("SimpleName", SimpleName);
                }
                if (!TextUtils.isEmpty(isGroupUUID)) {
                    intent.putExtra("AddUserforGroup", "AddUserforGroup");
                }
                startActivity(intent);
                if (!TextUtils.isEmpty(isfinish)) {
                    finish();
                }
                break;
            case R.id.rl_allContacts:
                if (mTreeInfosnull != null && mTreeInfosnull.size() > 0) {
                    mIntent = new Intent(mContext, InfoActivity.class);
                    if (!TextUtils.isEmpty(GroupID)) {
                        mIntent.putExtra("Groupid", GroupID);
                    }
                    if (!TextUtils.isEmpty(GroupName)) {
                        mIntent.putExtra("GroupName", GroupName);
                    }
                    if (!TextUtils.isEmpty(PrivateId)) {
                        mIntent.putExtra("PrivateChat", PrivateId);
                    }
                    if (AllMessage != null && AllMessage.size() > 0) {
                        mIntent.putParcelableArrayListExtra("allMessage", AllMessage);
                    }
                    if (allFile != null && allFile.size() > 0) {
                        mIntent.putParcelableArrayListExtra("allFile", allFile);
                    }
                    if (!TextUtils.isEmpty(SimpleName)) {
                        mIntent.putExtra("SimpleName", SimpleName);
                    }
                    if (!TextUtils.isEmpty(isGroupUUID)) {
                        mIntent.putExtra("AddUserforGroup", "AddUserforGroup");
                    }
                    mIntent.putExtra("maps", maps);
                    mIntent.putExtra("IsBoolean", flag);
                    mIntent.putExtra("viewMode", ViewMode.CHECK);
                    mIntent.putExtra("currentLevel", mTreeInfosnull.get(0).getId());
                    mIntent.putExtra("parentLevel", mTreeInfosnull.get(0).getPid());
                    startActivity(mIntent);
                    if (!TextUtils.isEmpty(isfinish)) {
                        finish();
                    }
                } else {
                    return;
                }
                break;
            case R.id.et_search:
                if (TextUtils.isEmpty(SimpleName)) {
                    Intent intentsearch = new Intent(mContext, SearchAllContactsActivity.class);
                    startActivity(intentsearch);
                    finish();
                } else {
                    Intent intentSimpleName = new Intent(mContext, AddTopContacts_Activity.class);
                    if (TextUtils.isEmpty(isGroupUUID)) {
                        intentSimpleName.putExtra("AddGroupforTopContacts", "AddGroupforTopContacts");
                    } else {
                        intentSimpleName.putExtra("AddUserforGroup", "AddUserforGroup");
                        intentSimpleName.putExtra("GroupID", GroupID);
                    }
                    startActivity(intentSimpleName);
                }
                if (!TextUtils.isEmpty(isfinish)) {
                    finish();
                }
                break;
        }
    }

}
