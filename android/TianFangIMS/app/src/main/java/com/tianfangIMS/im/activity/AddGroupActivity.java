package com.tianfangIMS.im.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    ArrayList<TreeInfo> mTreeInfos, clickHistory;

    HashMap<Integer, TreeInfo> map;
    HashMap<Integer, HashMap<Integer, TreeInfo>> maps;
    private boolean flag = true;
    InfoAdapter mAdapter;
    int workerCount;

    int currentLevel;

    Intent mIntent;

    HashMap<Integer, Boolean> prepare;
    private String GroupID;
    private String PrivateId;
    private EditText et_search;
    private ArrayList<String> ImageMessageList;//接收转发的图片消息
    private String SimpleName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addgroup_layout);
        mContext = this;
        GroupID = getIntent().getStringExtra("GroupId");
        PrivateId = getIntent().getStringExtra("PrivateChat");
        ImageMessageList = getIntent().getStringArrayListExtra("ListUri");
        SimpleName = getIntent().getStringExtra("SimpleName");
        Log.e("打印图片消息：", "---:" + SimpleName);
        setTitle("选择联系人");
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


    private void GetData() {
        OkGo.post(ConstantValue.DEPARTMENTPERSON)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .execute(new StringCallback() {
                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        LoadDialog.show(mContext);
                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        LoadDialog.dismiss(mContext);
                        mGson = new Gson();
                        mTreeInfos = mGson.fromJson(s, new TypeToken<List<TreeInfo>>() {
                        }.getType());
                        //根据PID进行平行节点排序 如果PID相同 则根据自身ID进行前后排序
                        Collections.sort(mTreeInfos, new Comparator<TreeInfo>() {
                            public int compare(TreeInfo o1, TreeInfo o2) {
                                if (o1.getPid() < o2.getPid()) {
                                    return -1;
                                } else if (o1.getPid() > o2.getPid()) {
                                    return 1;
                                }
                                return o1.getId() < o2.getId() ? -1 : 1;
                            }
                        });
                        currentLevel = mTreeInfos.get(0).getPid();
                        maps = new HashMap<Integer, HashMap<Integer, TreeInfo>>();
                        //规定最小PID为0 保证与最小PID不相同
                        int pid = -1;
                        for (TreeInfo treeInfo : mTreeInfos) {
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
                                mTreeInfos = new ArrayList<TreeInfo>();
                                childCount = new ArrayList<Integer>();
                                mAdapter = new InfoAdapter(mContext, mTreeInfos, childCount, ViewMode.NORMAL, prepare);
                                lv_addGroup_company.setAdapter(mAdapter);
                                transfer();
                            }
                        });
                    }
                });
    }

    private void transfer() {
        //清除适配数据集合
        mTreeInfos.clear();
        childCount.clear();
        //得到下一级部门的数据集合
        map = maps.get(currentLevel);
        //如果没有子部门 直接进行提示
        if (map == null) {
            return;
        }
        //将Map数据集合转换为List
        for (TreeInfo treeInfo : map.values()) {
            mTreeInfos.add(treeInfo);
        }
        //根据部门编号 进行排序
        Collections.sort(mTreeInfos, new Comparator<TreeInfo>() {
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
                startActivity(intent);
                finish();
                break;
            case R.id.rl_allContacts:
//                Toast.makeText(getActivity(), mTreeInfos.get(position).getId() + " / " + mTreeInfos.get(position).getName(), Toast.LENGTH_SHORT).show();
                mIntent = new Intent(mContext, InfoActivity.class);
                if (!TextUtils.isEmpty(GroupID)) {
                    mIntent.putExtra("Groupid", GroupID);
                }
                if (!TextUtils.isEmpty(PrivateId)) {
                    mIntent.putExtra("PrivateChat", PrivateId);
                }
                if (ImageMessageList != null && ImageMessageList.size() > 0) {
                    mIntent.putStringArrayListExtra("ImageUri", ImageMessageList);
                }
                if(!TextUtils.isEmpty(SimpleName)){
                    mIntent.putExtra("SimpleName",SimpleName);
                }
                mIntent.putExtra("maps", maps);
                mIntent.putExtra("IsBoolean", flag);
                mIntent.putExtra("viewMode", ViewMode.CHECK);
                mIntent.putExtra("currentLevel", mTreeInfos.get(0).getId());
                mIntent.putExtra("parentLevel", mTreeInfos.get(0).getPid());
                startActivity(mIntent);
                finish();
                break;
            case R.id.et_search:
                startActivity(new Intent(mContext, SearchActivity.class));
                break;
        }
    }

}
