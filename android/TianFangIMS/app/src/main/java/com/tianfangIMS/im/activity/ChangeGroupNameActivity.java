package com.tianfangIMS.im.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.tianfangIMS.im.ConstantValue;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.bean.OneGroupBean;
import com.tianfangIMS.im.dialog.LoadDialog;

import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by LianMengYu on 2017/2/13.
 */
public class ChangeGroupNameActivity extends BaseActivity implements View.OnClickListener {
    private ImageView iv_clean;
    private EditText et_changeName;
    private String GroupName;
    private TextView tv_complete;
    private OneGroupBean oneGroupBean;
    private Context mContext;
    private int resultCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changegroupname_layout);
        setTitle("群名称");
        setTv_completeVisibiliy(View.VISIBLE);
        mContext = this;
        init();
        Object object = getIntent().getSerializableExtra("GroupBean");
        oneGroupBean = (OneGroupBean) object;
        et_changeName.setText(oneGroupBean.getText().getName());
    }

    private void init() {
        tv_complete = getTv_title();
        iv_clean = (ImageView) this.findViewById(R.id.iv_clean);
        et_changeName = (EditText) this.findViewById(R.id.et_changeName);
        iv_clean.setOnClickListener(this);
        tv_complete.setOnClickListener(this);
        et_changeName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    iv_clean.setVisibility(View.VISIBLE);
                } else {
                    iv_clean.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void ChangeGroupName() {
        String groupname = et_changeName.getText().toString();
        String groupid = oneGroupBean.getText().getGID();
        OkGo.post(ConstantValue.CHANGEGROUPNAME)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .params("groupid", groupid)
                .params("groupname", groupname)
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
                            Gson gson = new Gson();
                            Map<String, Object> map = gson.fromJson(s, new TypeToken<Map<String, Object>>() {
                            }.getType());
                            String str = map.get("code").toString();
                            Log.e("打印数据","访问网络返回Code："+str);
                            if (str.equals("1.0")) {
                                String aa = et_changeName.getText().toString();
                                Intent mIntent = new Intent(mContext, GroupDetailActivity.class);
                                mIntent.putExtra("change01", aa);
                                setResult(resultCode, mIntent);
                                finish();
                            }
                        }
                    }
                });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_clean:
                et_changeName.getText().clear();
                break;
            case R.id.tv_complete:
                ChangeGroupName();
                break;
        }
    }
}
