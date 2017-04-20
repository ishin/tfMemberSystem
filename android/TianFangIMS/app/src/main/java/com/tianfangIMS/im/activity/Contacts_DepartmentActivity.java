package com.tianfangIMS.im.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.tianfangIMS.im.ConstantValue;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.adapter.Contacts_DepartmentAdapter;
import com.tianfangIMS.im.bean.DepartmentBean;
import com.tianfangIMS.im.dialog.LoadDialog;
import com.tianfangIMS.im.utils.NToast;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by LianMengYu on 2017/1/12.
 * 联系人主界面下的所有部门界面
 */

public class Contacts_DepartmentActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG = "Contacts_DepartmentActivity";
    private Context mContext;
    private ListView lv_department_info;
    private Contacts_DepartmentAdapter adapter;
    private TextView tv_contacts_text_2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_department_activity);
        mContext = this;
        setTitle("部门");
        setAdapterList();
        initView();
        setListItemClick();
    }

    public void initView() {
        lv_department_info = (ListView) this.findViewById(R.id.lv_department_info);
        tv_contacts_text_2 = (TextView) this.findViewById(R.id.tv_contacts_text_2);

    }
    private void setListItemClick(){
        lv_department_info.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(mContext,DepartmentPerson_Activity.class));
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    private void setAdapterList() {
        OkGo.post(ConstantValue.DEPARTMENT)
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
                        if (!TextUtils.isEmpty(s)) {
                            Type listType = new TypeToken<List<DepartmentBean>>() {
                            }.getType();
                            Gson gson = new Gson();
                            List<DepartmentBean> list = gson.fromJson(s, listType);
                            for (Iterator iterator = list.iterator(); iterator.hasNext(); ) {
                                DepartmentBean user = (DepartmentBean) iterator.next();
                            }
                            adapter = new Contacts_DepartmentAdapter(mContext, list);
                            lv_department_info.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } else {
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

}
