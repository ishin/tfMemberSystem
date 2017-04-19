package com.tianfangIMS.im.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.github.promeg.pinyinhelper.Pinyin;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tianfangIMS.im.ConstantValue;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.adapter.SearchAllContactsAdapter;
import com.tianfangIMS.im.bean.GroupListBean;
import com.tianfangIMS.im.bean.SearchAllBean;
import com.tianfangIMS.im.bean.TopContactsListBean;
import com.tianfangIMS.im.dialog.SendImageMessageDialog;
import com.tianfangIMS.im.utils.CommonUtil;
import com.tianfangIMS.im.utils.NToast;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;

/**
 * Created by LianMengYu on 2017/3/11.
 */

public class SearchAllContactsActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private Context mContext;
    private ListView lv_searchAll_data;
    private EditText et_search;

    //初始化数据
    private List<SearchAllBean> PrivateChatList;
    private List<SearchAllBean> GroupChatList;
    //过滤过得到的数据
    private List<SearchAllBean> PrivateChatListData = new ArrayList<>();
    private List<SearchAllBean> GroupChatListData = new ArrayList<>();
    //过滤数据的集合
    List<SearchAllBean> AdapterData;
    private SearchAllContactsAdapter adapter;
    private ArrayList<String> uriList;

    private LinearLayout no_result_all;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_allcontacts_layout);
        mContext = this;
        initview();
        InitPrivateChatData();
        InitGroupChatData();
        uriList = getIntent().getStringArrayListExtra("ListUri");
    }

    private void initview() {
        lv_searchAll_data = (ListView) this.findViewById(R.id.lv_searchAll_data);
        et_search = (EditText) this.findViewById(R.id.et_search);
        lv_searchAll_data.setOnItemClickListener(this);
        no_result_all = (LinearLayout) this.findViewById(R.id.no_result_all);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                GetPrivateChatSearch(s);
                GetGroupChatSearch(s);
                SetAdapterInfo();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (PrivateChatListData.size() == 0 && PrivateChatListData.size() == 0) {
                    lv_searchAll_data.setVisibility(View.GONE);
                    no_result_all.setVisibility(View.VISIBLE);
                } else {
                    lv_searchAll_data.setVisibility(View.VISIBLE);
                    no_result_all.setVisibility(View.GONE);
                }
                String file = s.toString();
                if (TextUtils.isEmpty(file)) {
                    AdapterData.clear();
                }
            }
        });
    }

    private void InitPrivateChatData() {
        String str = CommonUtil.getFrientUserInfo(mContext);
        if (!TextUtils.isEmpty(str) && !str.equals("{}")) {
            PrivateChatList = new ArrayList<SearchAllBean>();
            Gson gson = new Gson();
            Type listType = new TypeToken<TopContactsListBean>() {
            }.getType();
            TopContactsListBean bean = gson.fromJson(str, listType);
            for (int i = 0; i < bean.getText().size(); i++) {
                PrivateChatList.add(new SearchAllBean(bean.getText().get(i).getId(), bean.getText().get(i).getFullname(),
                        bean.getText().get(i).getPosition()
                        , bean.getText().get(i).getLogo(), true, bean.getText().get(i).getMobile()));
            }
        }
    }

    private void InitGroupChatData() {
        String str = CommonUtil.getGroupUserInfo(mContext);
        if (!TextUtils.isEmpty(str) && !str.equals("{}")) {
            GroupChatList = new ArrayList<SearchAllBean>();
            Gson gson1 = new Gson();
            Map<String, Object> map = gson1.fromJson(str, new TypeToken<Map<String, Object>>() {
            }.getType());
            if ((Double) map.get("code") == 1.0) {
                Gson gson = new Gson();
                Type listType = new TypeToken<GroupListBean>() {
                }.getType();
                GroupListBean bean = gson.fromJson(str, listType);
                for (int i = 0; i < bean.getText().size(); i++) {
                    GroupChatList.add(new SearchAllBean(bean.getText().get(i).getGID(), bean.getText().get(i).getName(), null, bean.getText().get(i).getLogo(), false, null));
                }
            } else if ((Double) map.get("code") == 0.0) {
                NToast.shortToast(mContext, "该用户没有加入群组");
            }
        }
    }

    private void GetPrivateChatSearch(CharSequence s) {
        PrivateChatListData.clear();
        String input = s.toString();
        for (int i = 0; i < PrivateChatList.size(); i++) {
            int count = 0;
            //全部转为小写
            input = input.toLowerCase();
            for (int j = 0; j < input.length(); j++) {
                char a = input.charAt(j);
                //如果是中文 则只跟Name属性进行比对 (因为其他属性不会存在中文字符)
                if (Pinyin.isChinese(a)) {
                    if (PrivateChatList.get(i).getName().indexOf(a) >= 0) {
                        count++;
                    }
                } else {
                    //非中文 对所有属性进行比对
                    if (PrivateChatList.get(i).getName().indexOf(a) >= 0 || PrivateChatList.get(i).getId().indexOf(a) >= 0 || PrivateChatList.get(i).getMphone().indexOf(input) >=0) {
                        count++;
                    } else {
                        //对Name属性值进行拼音转换
                        String[] arr = Pinyin.toPinyin(PrivateChatList.get(i).getName(), ",").split(",");
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
                PrivateChatListData.add(PrivateChatList.get(i));
            }
        }
    }

    private void GetGroupChatSearch(CharSequence s) {
        GroupChatListData.clear();
        String input = s.toString();
        for (int i = 0; i < GroupChatList.size(); i++) {
            int count = 0;
            //全部转为小写
            input = input.toLowerCase();
            for (int j = 0; j < input.length(); j++) {
                char a = input.charAt(j);
                //如果是中文 则只跟Name属性进行比对 (因为其他属性不会存在中文字符)
                if (Pinyin.isChinese(a)) {
                    if (GroupChatList.get(i).getName().indexOf(a) >= 0) {
                        count++;
                    }
                } else {
                    //非中文 对所有属性进行比对
                    if (GroupChatList.get(i).getName().indexOf(a) >= 0 || GroupChatList.get(i).getId().indexOf(a) >= 0) {
                        count++;
                    } else {
                        //对Name属性值进行拼音转换
                        String[] arr = Pinyin.toPinyin(GroupChatList.get(i).getName(), ",").split(",");
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
                GroupChatListData.add(GroupChatList.get(i));
            }
        }
    }

    private void SetAdapterInfo() {
        AdapterData = new ArrayList<SearchAllBean>();
        SearchAllBean bean = new SearchAllBean();
        bean.setName("联系人");
        bean.setId(String.valueOf(-1));
        AdapterData.add(bean);
        AdapterData.addAll(PrivateChatListData);
        bean = new SearchAllBean();
        bean.setName("群组");
        bean.setId(String.valueOf(-1));
        AdapterData.add(bean);
        AdapterData.addAll(GroupChatListData);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter = new SearchAllContactsAdapter(mContext, AdapterData);
                lv_searchAll_data.setAdapter(adapter);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (AdapterData.get(position).isFlag()) {
            if (uriList != null && uriList.size() > 0) {
                SendImageMessageDialog sendImageMessageDialog = new SendImageMessageDialog(mContext, AdapterData.get(position).getId(),
                        position, AdapterData.get(position).getName(), uriList, Conversation.ConversationType.PRIVATE,
                        ConstantValue.ImageFile + AdapterData.get(position).getLogo(), AdapterData.get(position).getPosition());
                sendImageMessageDialog.show();
            } else {
                Intent intent = new Intent(mContext, FriendPersonInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("userId", AdapterData.get(position).getId());
                intent.putExtras(bundle);
                intent.putExtra("conversationType", Conversation.ConversationType.PRIVATE);
                startActivity(intent);
                finish();
            }
        } else {
            if (uriList != null && uriList.size() > 0) {
                SendImageMessageDialog sendImageMessageDialog = new SendImageMessageDialog(mContext, AdapterData.get(position).getId(),
                        position, AdapterData.get(position).getName(), uriList, Conversation.ConversationType.GROUP, ConstantValue.ImageFile + AdapterData.get(position).getLogo(),
                        null);
                sendImageMessageDialog.show();
            } else {
                RongIM.getInstance().startGroupChat(mContext, AdapterData.get(position).getId(), AdapterData.get(position).getName());
            }
        }
    }
}
