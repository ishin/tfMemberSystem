package com.tianfangIMS.im.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.tianfangIMS.im.bean.LoginBean;
import com.tianfangIMS.im.dialog.LoadDialog;
import com.tianfangIMS.im.service.FloatService;
import com.tianfangIMS.im.utils.CommonUtil;
import com.tianfangIMS.im.utils.MD5;
import com.tianfangIMS.im.utils.NToast;

import java.util.Map;

import io.rong.imkit.RongIM;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by LianMengYu on 2017/1/9.
 */

public class ResetPassword_Activity extends BaseActivity implements View.OnClickListener {

    private EditText et_oldpassword, ed_newpassword, ed_comparepwd;
    private RelativeLayout btn_submit;
    String old;
    String newpwd;
    String comparepwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resetpwd_activity);
        setTitle("重置密码");
        init();
    }

    private void init() {
        et_oldpassword = (EditText) this.findViewById(R.id.et_oldpassword);
        ed_newpassword = (EditText) this.findViewById(R.id.ed_newpassword);
        ed_comparepwd = (EditText) this.findViewById(R.id.ed_comparepwd);
        btn_submit = (RelativeLayout) this.findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);
    }

    private void resetPassWord(String old, String newpwd, String comparepwd) {
        String sessionId = getSharedPreferences("CompanyCode", MODE_PRIVATE).getString("CompanyCode", "");
        Gson gson = new Gson();
        LoginBean loginBean = gson.fromJson(CommonUtil.getUserInfo(mContext), LoginBean.class);
        String account = loginBean.getText().getAccount();
        OkGo.post(ConstantValue.NEWPASSWORD)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .headers("cookie", sessionId)
                .params("account", account)
                .params("oldpwd", MD5.encrypt(old))
                .params("newpwd", MD5.encrypt(newpwd))
                .params("comparepwd", MD5.encrypt(comparepwd))
                .execute(new StringCallback() {
                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        LoadDialog.show(mContext);
                    }
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        LoadDialog.dismiss(mContext);
                        if (!TextUtils.isEmpty(s) && !s.equals("{}")) {
                            if ((s.trim()).startsWith("<!DOCTYPE")) {
                                NToast.shortToast(mContext, "Session过期，请重新登陆");
                                startActivity(new Intent(mContext, LoginActivity.class));
                                finish();
                            } else {
                                Gson gson = new Gson();
                                Map<String, Object> map = gson.fromJson(s, new TypeToken<Map<String, Object>>() {
                                }.getType());
                                if (((map.get("code")).toString()).equals("-1.0")) {
                                    NToast.shortToast(mContext, "旧密码错误");
                                }
                                if ((map.get("code")).equals("1")) {
                                    NToast.shortToast(mContext, "密码修改成功，请重新登录");
                                    Intent mIntent = new Intent(mContext, FloatService.class);
                                    mContext.stopService(mIntent);
                                    startActivity(new Intent(mContext, LoginActivity.class));
                                    RongIM.getInstance().logout();
//                                    SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
//                                    SharedPreferences.Editor editor = sp.edit();
//                                    editor.clear();
//                                    editor.commit();
                                    finish();
                                }
                            }
                        }
                    }
                });
    }

    private void PasswordIsNull() {
        old = et_oldpassword.getText().toString();
        newpwd = ed_newpassword.getText().toString();
        comparepwd = ed_comparepwd.getText().toString();
        if (!TextUtils.isEmpty(old) && !TextUtils.isEmpty(newpwd) && !TextUtils.isEmpty(comparepwd)) {
            if (newpwd.equals(comparepwd)) {
                if (newpwd.length() < 6 || newpwd.length() > 32) {
                    NToast.shortToast(mContext, "密码不能小于6位或者大于32位");
                    return;
                }else{
                    resetPassWord(old, newpwd, comparepwd);
                }
            } else {
                NToast.shortToast(mContext, "两次密码输入不一致");
                return;
            }
        } else {
            NToast.shortToast(mContext, "密码不能为空");
            return;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                PasswordIsNull();
                break;
        }
    }
}
