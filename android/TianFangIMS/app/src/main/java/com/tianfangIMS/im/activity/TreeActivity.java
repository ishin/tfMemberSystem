package com.tianfangIMS.im.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianfangIMS.im.R;
import com.tianfangIMS.im.bean.TreeInfo;
import com.tianfangIMS.im.utils.NToast;
import com.tianfangIMS.im.view.CustomChildLinearLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Titan on 2017/2/10.
 */

public class TreeActivity extends BaseActivity {

    HashMap<Integer, HashMap<Integer, TreeInfo>> maps;
    HashMap<Integer, TreeInfo> map;

    ArrayList<TreeInfo> mTreeInfos;
    Context mContext;
    List<Integer> keys, tmp;

    int nodeWidth;

    LinearLayout activity_tree_ll_view;

    CustomChildLinearLayout child;

    LayoutInflater mInflater;

    Map<Integer, TreeInfo> mInfoMap, dictionary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree);
        mInflater = LayoutInflater.from(this);
        mContext = this;
        keys = new ArrayList<>();
        activity_tree_ll_view = (LinearLayout) findViewById(R.id.activity_tree_ll_view);
        //屏幕宽度的一半
        nodeWidth = getResources().getDisplayMetrics().widthPixels / 2;
        maps = (HashMap<Integer, HashMap<Integer, TreeInfo>>) getIntent().getSerializableExtra("maps");
        Log.e("查看树状图：","---:"+maps);
        //获取首节点 测试环境下的Json数据首节点ID为0
        keys.add(0);
        mInfoMap = new HashMap<>();
        dictionary = new HashMap<>();
        //以ID为Key 实体自身为Value 存入Map中
        try {
            if(maps != null){
                for (HashMap<Integer, TreeInfo> hashMap : maps.values()) {
                    for (TreeInfo info : hashMap.values()) {
                        mInfoMap.put(info.getId(), info);
                        dictionary.put(info.getPid(), info);
                    }
                }
            }else{
                NToast.shortToast(mContext,"没有获取到数据");
                return;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        transfer();
    }

    private void transfer() {
        tmp = new ArrayList<>();
        tmp.addAll(keys);
        keys.clear();
        //创建新的列容器
        child = new CustomChildLinearLayout(this);
        float density = getResources().getDisplayMetrics().density;
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        ll.leftMargin = (int) (64 * density);
        child.setLayoutParams(ll);
        child.setOrientation(LinearLayout.VERTICAL);
        child.setGravity(Gravity.CENTER);
        //列容器的内的单个节点Item
        TextView mTextView = null;
        //循环列节点组
        mTreeInfos = new ArrayList<>();
        for (Integer integer : tmp) {
            map = maps.get(integer);
            if (map != null) {
                mTreeInfos.clear();
                //将Map数据添加到List中
                mTreeInfos.addAll(map.values());
                //将List中的数据根据PID>ID的顺序 进行自小到大排列
                Collections.sort(mTreeInfos, new Comparator<TreeInfo>() {
                    @Override
                    public int compare(TreeInfo o1, TreeInfo o2) {
                        if (o1.getPid() < o2.getPid()) {
                            return -1;
                        } else if (o1.getPid() > o2.getPid()) {
                            return 1;
                        } else {
                            return o1.getId() < o2.getId() ? -1 : 1;
                        }
                    }
                });
                for (TreeInfo info : mTreeInfos) {
                    //如果该节点为员工类型 则直接跳过
                    if (info.getFlag() == 1) {
                        continue;
                    }
                    keys.add(info.getId());
                    mTextView = new TextView(this);
                    mTextView.setText(info.getName());
                    mTextView.setGravity(Gravity.CENTER);
//                    mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 24);
                    mTextView.setBackgroundResource(R.drawable.tree_item_background);
                    mTextView.setPadding((int) (32 * density), (int) (16 * density), (int) (32 * density), (int) (16 * density));
                    mTextView.setTag(info);
                    //包含有子部门
                    if (dictionary.containsKey(info.getId())) {
                        mTextView.setTextColor(Color.parseColor("#373E61"));
                        mTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mTreeInfos.clear();
                                TreeInfo mInfo = (TreeInfo) v.getTag();
                                int currentLevel = mInfo.getId();
                                int parentLevel = mInfo.getPid();
                                mTreeInfos.add(mInfo);
                                //倒溯父部门
                                while (true) {
                                    mInfo = mInfoMap.get(mInfo.getPid());
                                    if (mInfo == null) {
                                        break;
                                    } else {
                                        mTreeInfos.add(mInfo);
                                    }
                                }
                                Collections.reverse(mTreeInfos);
                                Intent mIntent = new Intent();
                                mIntent.putExtra("parentLevel", parentLevel);

                                mIntent.putExtra("clickHistory", mTreeInfos);
                                mIntent.putExtra("currentLevel", currentLevel);
                                setResult(RESULT_OK, mIntent);
                                finish();
                            }
                        });
                    } else {
                        mTextView.setEnabled(false);
                        mTextView.setTextColor(getResources().getColor(android.R.color.darker_gray));
                    }
                    child.addView(mTextView);
                }
            }
        }
        if (child.getChildCount() != 0) {
            activity_tree_ll_view.addView(child);
        }
        if (keys.size() != 0) {
            transfer();
        }
    }
}
