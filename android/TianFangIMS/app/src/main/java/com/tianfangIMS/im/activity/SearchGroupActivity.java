package com.tianfangIMS.im.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.github.promeg.pinyinhelper.Pinyin;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.adapter.SearchGroupAdapter;
import com.tianfangIMS.im.bean.GroupListBean;
import com.tianfangIMS.im.bean.SearchGroupBean;
import com.tianfangIMS.im.utils.CommonUtil;
import com.tianfangIMS.im.utils.NToast;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.rong.imkit.RongIM;

/**
 * Created by LianMengYu on 2017/2/28.
 */

public class SearchGroupActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private EditText et_search;
    private List<SearchGroupBean> searchList;//获取所搜源
    private List<SearchGroupBean> searchData = new ArrayList<>();//得到搜索后的集合
    private GroupListBean listbean;
    SearchGroupAdapter searchAdapter;
    private Context mContext;
    private ListView fragment_contacts_search;
    private LinearLayout no_result_group;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searcg_activity);
        setTitle("搜索");
        mContext = this;
        init();
        SearchGroupInfo();
    }

    private void SearchGroupInfo() {
        String str = CommonUtil.getGroupUserInfo(mContext);
        Log.e("adassdasdsad", "-----:" + str);
        if (!TextUtils.isEmpty(str) && !str.equals("{}")) {
            searchList = new ArrayList<SearchGroupBean>();
            Gson gson = new Gson();
            Type listType = new TypeToken<GroupListBean>() {
            }.getType();
            Gson gson1 = new Gson();
            Map<String, Object> map = gson1.fromJson(str,new TypeToken<Map<String,Object>>(){}.getType());
            if((Double)map.get("code") == 1.0){
                GroupListBean bean = gson.fromJson(str, listType);
                for (int i = 0; i < bean.getText().size(); i++) {
                    searchList.add(new SearchGroupBean(bean.getText().get(i).getName(), bean.getText().get(i).getGID(), null, bean.getText().get(i).getLogo()));
                }
            }else if((Double)map.get("code") == 0.0){
                NToast.shortToast(mContext, "该用户没有加入群组");
            }
        } else {
            NToast.shortToast(mContext, "没有获取到群组");
        }
    }

    private void init() {
        et_search = (EditText) this.findViewById(R.id.et_search);
        fragment_contacts_search = (ListView) this.findViewById(R.id.lv_contacts_search);
        fragment_contacts_search.setOnItemClickListener(this);
        no_result_group = (LinearLayout)this.findViewById(R.id.no_result_group);

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("dayindadadasdsa","11111----:"+searchData);
                if(searchData.size() == 0){
                    no_result_group.setVisibility(View.VISIBLE);
                }else {
                    no_result_group.setVisibility(View.GONE);
                }
                if(TextUtils.isEmpty(s.toString())){
                    searchData.clear();
                }
                if (searchList != null) {
                    GetSearch(s);
                    searchAdapter = new SearchGroupAdapter(searchData, mContext);
                    fragment_contacts_search.setAdapter(searchAdapter);
                    searchAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void GetSearch(Editable s) {
        searchData.clear();
        String input = s.toString();
        for (int i = 0; i < searchList.size(); i++) {
            int count = 0;
            //全部转为小写
            input = input.toLowerCase();
            for (int j = 0; j < input.length(); j++) {
                char a = input.charAt(j);
                //如果是中文 则只跟Name属性进行比对 (因为其他属性不会存在中文字符)
                if (Pinyin.isChinese(a)) {
                    if (searchList.get(i).getName().indexOf(a) >= 0) {
                        count++;
                    }
                } else {
                    //非中文 对所有属性进行比对
                    if (searchList.get(i).getName().indexOf(a) >= 0 || searchList.get(i).getId().indexOf(a) >= 0) {
                        count++;
                    } else {
                        //对Name属性值进行拼音转换
                        String[] arr = Pinyin.toPinyin(searchList.get(i).getName(), ",").split(",");
                        //循环每个字符
                        for (String s1 : arr) {
                            //对每个字符的拼音数组进行比对
                            for (int i1 = 0; i1 < s1.length(); i1++) {
                                if (a == s1.charAt(i1)) {
                                    count++;
                                }
                            }
                        }
                    }
                }
            }
            if (count == input.length()) {
                searchData.add(searchList.get(i));
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        RongIM.getInstance().startGroupChat(mContext, searchList.get(position).getId(), searchList.get(position).getName());
        finish();
    }
}
