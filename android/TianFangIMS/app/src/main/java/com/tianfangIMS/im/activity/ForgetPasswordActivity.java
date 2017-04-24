package com.tianfangIMS.im.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.bean.SubmitCodeBean;
import com.tianfangIMS.im.dialog.LoadDialog;
import com.tianfangIMS.im.utils.AMUtils;
import com.tianfangIMS.im.utils.NToast;

import okhttp3.Call;
import okhttp3.Response;

import static com.tianfangIMS.im.ConstantValue.REQUESTTEXT;

/**
 * Created by LianMengYu on 2016/12/30.
 */

public class ForgetPasswordActivity extends Activity implements View.OnClickListener {

    private Context mContext;
    private Button button;
    private EditText et_forgetpassword_phoneNumber;
    private LinearLayout ly_forget_getphonenumber;
    private ImageView img_login_clean_yanzhengma;
    private ImageView imgbtn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgetpassword);
        mContext = this;
        SystemBarTranslucentType();//将Android状态栏改变为沉浸样式
        init();


    }

    private void init() {
        button = (Button) this.findViewById(R.id.btn_forget_sumbit);
        et_forgetpassword_phoneNumber = (EditText) this.findViewById(R.id.et_forgetpassword_phoneNumber);
        ly_forget_getphonenumber = (LinearLayout) this.findViewById(R.id.ly_forget_getphonenumber);
        img_login_clean_yanzhengma = (ImageView) this.findViewById(R.id.img_login_clean_yanzhengma);
        imgbtn_back = (ImageView) this.findViewById(R.id.imgbtn_back);

        img_login_clean_yanzhengma.setOnClickListener(this);
        et_forgetpassword_phoneNumber.setOnClickListener(this);
        button.setOnClickListener(this);
        et_forgetpassword_phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() > 0) {
                    img_login_clean_yanzhengma.setVisibility(View.VISIBLE);
                } else {
                    img_login_clean_yanzhengma.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    img_login_clean_yanzhengma.setVisibility(View.VISIBLE);
                } else {
                    img_login_clean_yanzhengma.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        imgbtn_back.setOnClickListener(this);
    }

    private void SubmitCode() {

        OkGo.post(REQUESTTEXT)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .params("phone",et_forgetpassword_phoneNumber.getText().toString())
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
                                String Message = submitCodeBean.getMessagetext();
                                String phoneNumber = (et_forgetpassword_phoneNumber.getText().toString()).replaceAll(" ", "").trim();
                                NToast.shortToast(mContext, Message);
                                GetPhoneToGetCode(phoneNumber);

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

    private void BtnSubmit() {
        String phoneNumber = (et_forgetpassword_phoneNumber.getText().toString()).replaceAll(" ", "").trim();
        if (TextUtils.isEmpty(phoneNumber)) {
            NToast.shortToast(mContext, R.string.phone_number_is_null);
            return;
        }
        if (AMUtils.isMobile(phoneNumber)) {
            SubmitCode();
        } else {
            NToast.shortToast(mContext, "请输入正确的手机号");
            return;
        }
    }

    //自获取手机号跳转至获取验证码
    private void GetPhoneToGetCode(String PhoneNumber) {
        startActivity(new Intent(mContext, GetVerificationCode_Activity.class).putExtra("PhoneNumber", PhoneNumber));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_forget_sumbit:
                BtnSubmit();
                break;
            case R.id.img_login_clean_yanzhengma:
                et_forgetpassword_phoneNumber.getText().clear();
                break;
            case R.id.imgbtn_back:
                finish();
                break;
        }
    }


    //将Android状态栏改变为沉浸样式
    private void SystemBarTranslucentType() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 点击空白处隐藏键盘
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                hideKeyboard(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 点击左按钮
     */
    public void onHeadLeftButtonClick(View v) {
        finish();
    }

    private boolean isShouldHideKeyboard(View view, MotionEvent event) {
        if (view != null && (view instanceof EditText)) {
            int[] l = {0, 0};
            view.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + view.getHeight(),
                    right = left + view.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     *
     * @param token
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
