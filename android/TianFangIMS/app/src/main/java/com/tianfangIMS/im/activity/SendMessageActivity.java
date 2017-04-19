package com.tianfangIMS.im.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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
 * Created by LianMengYu on 2017/3/6.
 */

public class SendMessageActivity extends BaseActivity implements View.OnClickListener {
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
    private Context mContext;
    private RelativeLayout rl_sendmessage_contacts, rl_sendmessage_topcontacts, rl_sendmessage_allContacts;
    private ArrayList<String> listUri;
    private EditText et_search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sendmessage_layout);
        mContext = this;
        init();
        GetData();
        listUri = getIntent().getStringArrayListExtra("ListUri");
    }

    private void init() {
        rl_sendmessage_contacts = (RelativeLayout) this.findViewById(R.id.rl_sendmessage_contacts);
        rl_sendmessage_topcontacts = (RelativeLayout) this.findViewById(R.id.rl_sendmessage_topcontacts);
        rl_sendmessage_allContacts = (RelativeLayout) this.findViewById(R.id.rl_sendmessage_allContacts);
        et_search = (EditText) this.findViewById(R.id.et_search);

        rl_sendmessage_contacts.setOnClickListener(this);
        rl_sendmessage_topcontacts.setOnClickListener(this);
        rl_sendmessage_allContacts.setOnClickListener(this);
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
                            @Override
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
                            @Override
                            public void run() {
                                clickHistory = new ArrayList<TreeInfo>();
                                mTreeInfos = new ArrayList<>();
                                childCount = new ArrayList<Integer>();
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_sendmessage_contacts:
                Intent inentGroup = new Intent(mContext, MineGroupActivity.class);
                if (listUri != null && listUri.size() > 0) {
                    inentGroup.putStringArrayListExtra("ListUri", listUri);
                }
                startActivity(inentGroup);
                break;
            case R.id.rl_sendmessage_topcontacts:
                Intent inentopcontacts = new Intent(mContext, MineTopContactsActivity.class);
                if (listUri != null && listUri.size() > 0) {
                    inentopcontacts.putStringArrayListExtra("ListUri", listUri);
                }
                startActivity(inentopcontacts);
                break;
            case R.id.rl_sendmessage_allContacts:
                mIntent = new Intent(mContext, InfoActivity.class);
                mIntent.putExtra("maps", maps);
                if (listUri != null && listUri.size() > 0) {
                    mIntent.putStringArrayListExtra("ListUri", listUri);
                }
                mIntent.putExtra("IsBoolean", flag);
                mIntent.putExtra("viewMode", ViewMode.NORMAL);
                mIntent.putExtra("currentLevel", mTreeInfos.get(0).getId());
                mIntent.putExtra("parentLevel", mTreeInfos.get(0).getPid());
                startActivity(mIntent);
                finish();
                break;
            case R.id.et_search:
                Intent searchIntent = new Intent(mContext, SearchAllContactsActivity.class);
                if (listUri != null && listUri.size() > 0) {
                    searchIntent.putStringArrayListExtra("ListUri", listUri);
                }
                startActivity(searchIntent);
                break;
        }
    }
}
