package com.tianfangIMS.im.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.tianfangIMS.im.activity.InfoActivity;
import com.tianfangIMS.im.activity.MainActivity;
import com.tianfangIMS.im.activity.MineGroupActivity;
import com.tianfangIMS.im.activity.MineTopContactsActivity;
import com.tianfangIMS.im.activity.SearchAllContactsActivity;
import com.tianfangIMS.im.activity.TreeActivity;
import com.tianfangIMS.im.adapter.InfoAdapter;
import com.tianfangIMS.im.adapter.SearchAdapter;
import com.tianfangIMS.im.bean.SearchUserBean;
import com.tianfangIMS.im.bean.TopContactsListBean;
import com.tianfangIMS.im.bean.TreeInfo;
import com.tianfangIMS.im.bean.ViewMode;
import com.tianfangIMS.im.dialog.LoadDialog;
import com.tianfangIMS.im.utils.JSONUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by LianMengYu on 2017/1/4.
 */


public class Contacts_Fragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private RelativeLayout rl_mine_contacts;
    private RelativeLayout rl_mine_topcontacts;
    public static JSONUtils jsonUtils;
    private String name;
    private int pid;
    public static Contacts_Fragment contacts_fragment;
    private ListView fragment_contacts_search;
    private LinearLayout search_layout;
    ListView fragment_contacts_lv_departments;
    private Boolean flag = false;
    private EditText et_search;
    Gson mGson;

    ArrayList<Integer> childCount;
    ArrayList<TreeInfo> mTreeInfos, clickHistory;

    HashMap<Integer, TreeInfo> map;
    HashMap<Integer, HashMap<Integer, TreeInfo>> maps;
    private ImageView MainTree;
    InfoAdapter mAdapter;
    int workerCount;

    int currentLevel;

    Intent mIntent;

    HashMap<Integer, Boolean> prepare;
    private EditText rl_Search;
    private TextView tv_clean;

    private List<SearchUserBean> searchList;//获取所搜源
    private List<SearchUserBean> searchData = new ArrayList<>();//得到搜索后的集合
    private TopContactsListBean listbean;
    SearchAdapter searchAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contacts_fragment, container, false);
        initView(view);
        GetData();
//        SearchUserInfo();
        contacts_fragment = this;
        jsonUtils = new JSONUtils();
        MainTree = ((MainActivity) getActivity()).getIv_MainTree();
        MainTree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getActivity(), TreeActivity.class);
                mIntent.putExtra("maps", maps);
                startActivityForResult(mIntent, 100);
            }
        });
        return view;
    }

    public static Contacts_Fragment getInstance() {
        return contacts_fragment;
    }

    private void initView(View view) {
        rl_mine_contacts = (RelativeLayout) view.findViewById(R.id.rl_mine_contacts);
        rl_mine_topcontacts = (RelativeLayout) view.findViewById(R.id.rl_mine_topcontacts);
        et_search = (EditText) view.findViewById(R.id.et_search);
        fragment_contacts_lv_departments = (ListView) view.findViewById(R.id.fragment_contacts_lv_departments);
        search_layout = (LinearLayout)view.findViewById(R.id.search_layout);
        fragment_contacts_lv_departments.setOnItemClickListener(this);
        et_search.setFocusable(false);
        et_search.setOnClickListener(this);
        rl_mine_topcontacts.setOnClickListener(this);
        rl_mine_contacts.setOnClickListener(this);

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
                        LoadDialog.show(getActivity());
                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        LoadDialog.dismiss(getActivity());
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
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    clickHistory = new ArrayList<TreeInfo>();
                                    mTreeInfos = new ArrayList<TreeInfo>();
                                    childCount = new ArrayList<Integer>();
                                    prepare = new HashMap<Integer, Boolean>();
                                    mAdapter = new InfoAdapter(getActivity(), mTreeInfos, childCount, ViewMode.NORMAL, null);
                                    fragment_contacts_lv_departments.setAdapter(mAdapter);
                                    transfer();
                                }catch (NullPointerException e){
                                    e.printStackTrace();
                                }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 100) {
                mIntent = new Intent(getActivity(), InfoActivity.class);
                mIntent.putExtra("maps", maps);
                mIntent.putExtra("viewMode", ViewMode.NORMAL);
                mIntent.putExtra("currentLevel", data.getIntExtra("currentLevel", -1));
                mIntent.putExtra("parentLevel", data.getIntExtra("parentLevel", -1));
                startActivity(mIntent);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_mine_contacts:
                startActivity(new Intent(getActivity(), MineGroupActivity.class));
                break;
            case R.id.rl_mine_topcontacts:
                startActivity(new Intent(getActivity(), MineTopContactsActivity.class));
                break;
            case R.id.et_search:
                startActivity(new Intent(getActivity(), SearchAllContactsActivity.class));
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Toast.makeText(getActivity(), mTreeInfos.get(position).getId() + " / " + mTreeInfos.get(position).getName(), Toast.LENGTH_SHORT).show();
        mIntent = new Intent(getActivity(), InfoActivity.class);
        mIntent.putExtra("maps", maps);
        mIntent.putExtra("viewMode", ViewMode.NORMAL);
        mIntent.putExtra("currentLevel", mTreeInfos.get(position).getId());
        mIntent.putExtra("parentLevel", mTreeInfos.get(position).getPid());
        Log.e("打印传递的数据:", "getContacts：" + mTreeInfos.get(position).getId() + "--Pid:" + mTreeInfos.get(position).getPid() + "---pos:" + position);
        startActivity(mIntent);
    }
}
