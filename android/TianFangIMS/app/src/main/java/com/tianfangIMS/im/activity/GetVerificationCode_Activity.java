package com.tianfangIMS.im.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.bean.SubmitCodeBean;
import com.tianfangIMS.im.bean.VerificationCodeBean;
import com.tianfangIMS.im.dialog.LoadDialog;
import com.tianfangIMS.im.utils.CountDownTimerUtils;
import com.tianfangIMS.im.utils.NToast;

import okhttp3.Call;
import okhttp3.Response;

import static com.tianfangIMS.im.ConstantValue.NEWPASSWORD;
import static com.tianfangIMS.im.ConstantValue.REQUESTTEXT;

/**
 * Created by LianMengYu on 2017/1/13.
 */

public class GetVerificationCode_Activity extends Activity implements View.OnClickListener {
    private EditText et_getcode_newpwd, et_getcode_renewpwd, et_getcode_input;
    private ImageView img_getcode_clean_yanzhengma1, img_getcode_clean_yanzhengma;
    private Button btn_getcode_sumbit;
    private Context mContext;
    private TextView tv_getcode_txt;
    private CountDownTimerUtils countDownTimerUtils;
    private ImageView imgbtn_back;
    String PhoneNuber;
    String newpwd;
    String renewpwd;
    String textcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.getcode_activity);
        mContext = this;
        init();
        Intent intent = getIntent();
        PhoneNuber = intent.getStringExtra("PhoneNumber");
        newpwd = et_getcode_newpwd.getText().toString().trim();
        renewpwd = et_getcode_renewpwd.getText().toString().trim();
        textcode = et_getcode_input.getText().toString().trim();
    }

    private void init() {
        et_getcode_newpwd = (EditText) this.findViewById(R.id.et_getcode_newpwd);
        et_getcode_renewpwd = (EditText) this.findViewById(R.id.et_getcode_renewpwd);
        img_getcode_clean_yanzhengma = (ImageView) this.findViewById(R.id.img_getcode_clean_yanzhengma);
        img_getcode_clean_yanzhengma1 = (ImageView) this.findViewById(R.id.img_getcode_clean_yanzhengma1);
        btn_getcode_sumbit = (Button) this.findViewById(R.id.btn_getcode_sumbit);
        et_getcode_input = (EditText) this.findViewById(R.id.et_getcode_input);
        tv_getcode_txt = (TextView) this.findViewById(R.id.tv_getcode_txt);
        imgbtn_back = (ImageView) this.findViewById(R.id.imgbtn_back);


        countDownTimerUtils = new CountDownTimerUtils(tv_getcode_txt, 60000, 1000);
        countDownTimerUtils.start();

        imgbtn_back.setOnClickListener(this);
        tv_getcode_txt.setOnClickListener(this);
        et_getcode_newpwd.setOnClickListener(this);
        et_getcode_renewpwd.setOnClickListener(this);
        img_getcode_clean_yanzhengma.setOnClickListener(this);
        img_getcode_clean_yanzhengma1.setOnClickListener(this);
        btn_getcode_sumbit.setOnClickListener(this);

        et_getcode_newpwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() > 0) {
                    img_getcode_clean_yanzhengma1.setVisibility(View.VISIBLE);
                } else {
                    img_getcode_clean_yanzhengma1.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    img_getcode_clean_yanzhengma1.setVisibility(View.VISIBLE);
                } else {
                    img_getcode_clean_yanzhengma1.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_getcode_renewpwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() > 0) {
                    img_getcode_clean_yanzhengma.setVisibility(View.VISIBLE);
                } else {
                    img_getcode_clean_yanzhengma.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    img_getcode_clean_yanzhengma.setVisibility(View.VISIBLE);
                } else {
                    img_getcode_clean_yanzhengma.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void GetVerification() {
        OkGo.post(NEWPASSWORD)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .params("account", PhoneNuber)
                .params("newpwd", newpwd)
                .params("comparepwd", renewpwd)
                .params("textcode", textcode)
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
                            VerificationCodeBean Verification = gson.fromJson(s, VerificationCodeBean.class);
                            if (Verification.getCode() == 1) {
                                NToast.shortToast(mContext, "密码修改成功");
                            }
                            startActivity(new Intent(mContext, LoginActivity.class));
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext, "访问失败");
                        return;
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_getcode_clean_yanzhengma1:
                et_getcode_newpwd.getText().clear();
                break;
            case R.id.img_getcode_clean_yanzhengma:
                et_getcode_renewpwd.getText().clear();
                break;
            case R.id.btn_getcode_sumbit:
                GetVerification();
                break;
            case R.id.tv_getcode_txt:
                SubmitCode();
                countDownTimerUtils = new CountDownTimerUtils(tv_getcode_txt, 60000, 1000);
                countDownTimerUtils.start();
                break;
            case R.id.imgbtn_back:
                finish();
                break;
        }
    }

    private void SubmitCode() {

        OkGo.post(REQUESTTEXT)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .params("phone",PhoneNuber)
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
                            SubmitCodeBean submitCodeBean = gson.fromJson(s, SubmitCodeBean.class);
                            if (submitCodeBean.getCode() == 1) {

                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext, "访问网络失败");
                        return;
                    }
                });
    }
}
