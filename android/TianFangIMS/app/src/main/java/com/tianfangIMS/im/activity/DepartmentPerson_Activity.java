package com.tianfangIMS.im.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.tianfangIMS.im.ConstantValue;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.adapter.DepartmentAndPersonAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by LianMengYu on 2017/1/12.
 */

public class DepartmentPerson_Activity extends BaseActivity {
    private ListView lv_departmentperson_info;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.departmentperson_activity);
        mContext = this;
        initView();
        GetMap();
    }

    public void initView() {
        lv_departmentperson_info = (ListView) this.findViewById(R.id.lv_departmentperson_info);
    }

    private void setListItemClick(final List<Map<String, String>> list) {
        lv_departmentperson_info.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int flag = Integer.parseInt(list.get(position).get("flag"));
                if (flag == 0) {
                    startActivity(new Intent(mContext, LoginActivity.class));
                }
                if (flag == 1) {
                    return;
                }

            }
        });
    }

    private void GetMap() {
        OkGo.post(ConstantValue.DEPARTMENTPERSON)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (!TextUtils.isEmpty(s)) {
                            Gson gson = new Gson();
//                            DepartmentAndPersonBean departmentAndPersonBean= gson.fromJson(s,DepartmentAndPersonBean.class);
                            List<Map<String, String>> list = gson.fromJson(s, new TypeToken<ArrayList<Map<String, String>>>() {
                            }.getType());
                            DepartmentAndPersonAdapter adapter = new DepartmentAndPersonAdapter(mContext, list);
                            lv_departmentperson_info.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);
                        Gson gson = new Gson();
                        List<Map<String, String>> list = gson.fromJson(s, new TypeToken<ArrayList<Map<String, String>>>() {
                        }.getType());
                        setListItemClick(list);
                    }
                });
    }
}
