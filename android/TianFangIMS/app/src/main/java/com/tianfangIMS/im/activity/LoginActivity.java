package com.tianfangIMS.im.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.tianfangIMS.im.ConstantValue;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.bean.LoginBean;
import com.tianfangIMS.im.bean.SetSyncUserBean;
import com.tianfangIMS.im.dialog.LoadDialog;
import com.tianfangIMS.im.utils.CommonUtil;
import com.tianfangIMS.im.utils.MD5;
import com.tianfangIMS.im.utils.NToast;

import java.util.List;
import java.util.Map;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by LianMengYu on 2016/12/29.
 * 登录页
 */

public class LoginActivity extends Activity implements View.OnClickListener, RongIM.UserInfoProvider {
    private final static String TAG = "LoginActivity";
    private static final int LOGIN = 5;
    private static final int GET_TOKEN = 6;
    private static final int SYNC_USER_INFO = 9;

    private TextView tx_forgetpassword;
    private String phoneString;
    private String passwordString;
    private ImageView img_login_clean_user, img_login_clean_password;
    private EditText et_login_user, et_login_password;
    private Button btn_login;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private Context mContext;
    private String loginToken;
    private String connectResultId;
    private LoginBean user;
    private List<LoginBean> mLoginBeanList;
    private ImageView img_login_clean_daima_et;
    private EditText et_login_daima;// 企业代码
    private String CompanyCode;
    private SharedPreferences daima_sp;
    private SharedPreferences.Editor daima_editor;
    private String sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mContext = this;
        SystemBarTranslucentType();//将Android状态栏改变为沉浸样式
        sp = getSharedPreferences("config", MODE_PRIVATE);
        daima_sp = getSharedPreferences("CompanyCode", MODE_PRIVATE);
        editor = sp.edit();
        daima_editor = daima_sp.edit();
        SharedPreferences sharedPreferences = getSharedPreferences("config",
                Activity.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        String CompanyCode = getSharedPreferences("CompanyCode", Activity.MODE_PRIVATE).getString("CompanyCode1", "");
        init();//初始化控件
        et_login_user.setText(username);
        et_login_daima.setText(CompanyCode);
    }

    private void SetSyncUserGroup(LoginBean bean) {
        String id = bean.getText().getId();
        OkGo.post(ConstantValue.SYNCUSERGROUP)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .headers("cookie", sessionId)
                .params("userid", id)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (!TextUtils.isEmpty(s) && !s.equals("{}")) {
                            if ((s.trim()).startsWith("<!DOCTYPE")) {
                                NToast.shortToast(mContext, "Session过期，请重新登陆");
                                startActivity(new Intent(mContext, LoginActivity.class));
                                finish();
                            }
                            Gson gson = new Gson();
                            SetSyncUserBean syncUserBean = gson.fromJson(s, SetSyncUserBean.class);
                            if (syncUserBean.getCode().equals("200")) {
                            } else {
                                NToast.shortToast(mContext, "同步群组失败");
                            }
                        } else {
                            return;
                        }
                    }
                });
    }

    //初始化控件
    private void init() {
        tx_forgetpassword = (TextView) this.findViewById(R.id.tx_forgetpassword);
        img_login_clean_user = (ImageView) this.findViewById(R.id.img_login_clean_user_et);
        img_login_clean_password = (ImageView) this.findViewById(R.id.img_login_clean_password);
        et_login_user = (EditText) this.findViewById(R.id.et_login_user);
        et_login_password = (EditText) this.findViewById(R.id.et_login_password);
        btn_login = (Button) this.findViewById(R.id.btn_login);
        img_login_clean_daima_et = (ImageView) this.findViewById(R.id.img_login_clean_daima_et);
        et_login_daima = (EditText) this.findViewById(R.id.et_login_daima);

        et_login_daima.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    img_login_clean_daima_et.setVisibility(View.VISIBLE);
                } else {
                    img_login_clean_daima_et.setVisibility(View.INVISIBLE);
                }
            }
        });
        //根据Edittext输入的改变，隐藏或者显示输入框右边的清空button
        et_login_user.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    img_login_clean_user.setVisibility(View.VISIBLE);
                } else {
                    img_login_clean_user.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_login_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                if (!TextUtils.isEmpty(str)) {
                    btn_login.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_login_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() > 0) {
                    img_login_clean_password.setVisibility(View.VISIBLE);
                } else {
                    img_login_clean_password.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    img_login_clean_password.setVisibility(View.VISIBLE);
                } else {
                    img_login_clean_password.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //设置单机事件
        tx_forgetpassword.setOnClickListener(this);
        img_login_clean_user.setOnClickListener(this);
        img_login_clean_password.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        et_login_password.setOnClickListener(this);
        et_login_user.setOnClickListener(this);
        img_login_clean_daima_et.setOnClickListener(this);
        CommonUtil.FilePath();//创建项目文件
    }

    private void setLogin() {
//        if (TextUtils.isEmpty(CompanyCode)){
//            NToast.shortToast(getApplicationContext(), "企业码不能为空");
//            return;
//        }
        if (TextUtils.isEmpty(CompanyCode)) {
            NToast.shortToast(getApplicationContext(), "企业码不能为空");
            return;
        }
        if (TextUtils.isEmpty(phoneString)) {
            NToast.shortToast(getApplicationContext(), R.string.phone_number_is_null);
            return;
        }
        if (TextUtils.isEmpty(passwordString)) {
            NToast.shortToast(getApplicationContext(), R.string.password_is_null);
            return;
        }
        if (passwordString.contains(" ")) {
            NToast.shortToast(getApplicationContext(), R.string.password_cannot_contain_spaces);
            return;
        }
        OkGo.post(ConstantValue.AFTERLOGIN)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .headers("cookie", sessionId)
                .params("account", et_login_user.getText().toString().trim())
                .params("userpwd", MD5.encrypt(et_login_password.getText().toString().trim()))
                .params("organCode", CompanyCode)
                .execute(new StringCallback() {
                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        LoadDialog.show(mContext);
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);
                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (!TextUtils.isEmpty(s) && !s.equals("{}")) {
                            if ((s.trim()).startsWith("<!DOCTYPE")) {
                                NToast.shortToast(mContext, "Session过期，请重新登陆");
                                startActivity(new Intent(mContext, LoginActivity.class));
                                finish();
                            }
                            Gson gson = new Gson();
                            user = gson.fromJson(s, LoginBean.class);
                            CommonUtil.saveUserInfo(mContext, gson.toJson(user));
                            loginToken = user.getText().getToken();
                            if (user.getCode() == 1) {
                                if (!TextUtils.isEmpty(loginToken)) {
                                    RongIM.connect(loginToken, new RongIMClient.ConnectCallback() {
                                        @Override
                                        public void onTokenIncorrect() {
                                            Log.e("RongIM", "onTokenIncorrect");
                                        }

                                        @Override
                                        public void onSuccess(String s) {
                                            LoadDialog.dismiss(mContext);
                                            SetSyncUserGroup(user);
                                            if (user != null){
                                                RongIM.getInstance().refreshUserInfoCache(new UserInfo(user.getText().getId(), user.getText().getFullname(),
                                                        Uri.parse(ConstantValue.ImageFile + user.getText().getLogo())));
                                            }
                                            connectResultId = s;
                                            editor.putString("username", phoneString);
                                            editor.putString("userpass", passwordString);
                                            editor.putString("token", loginToken);
                                            editor.apply();
                                            String str = OkGo.getInstance().getCookieJar().getCookieStore().getAllCookie().subList(0, 1).toString();
                                            String aa = str.substring(2, str.indexOf(";"));
                                            sessionId = aa.substring(aa.indexOf("=") + 1, aa.length());
                                            Log.e("sessionid", "----:" + sessionId);
                                            daima_editor.putString("CompanyCode1", CompanyCode);
                                            daima_editor.putString("username", phoneString);
                                            daima_editor.putString("CompanyCode", sessionId);
                                            daima_editor.apply();
                                            Toast.makeText(getApplicationContext(), "登陆成功", Toast.LENGTH_SHORT).show();
                                            Intent intent_login = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent_login);
                                            finish();
                                        }

                                        @Override
                                        public void onError(RongIMClient.ErrorCode errorCode) {
                                            Log.e("RongIM", "onError");
                                            return;
                                        }
                                    });
                                } else {
                                    LoadDialog.dismiss(mContext);
                                    NToast.shortToast(mContext, "连接服务器失败");
                                    return;
                                }
                            }
                            if (user.getCode() == 0) {
                                LoadDialog.dismiss(mContext);
                                Gson gson1 = new Gson();
                                Map<String, Object> map = gson1.fromJson(s, new TypeToken<Map<String, Object>>() {
                                }.getType());
                                Map<String, Object> map1 = gson1.fromJson(map.get("text").toString(), new TypeToken<Map<String, Object>>() {
                                }.getType());
                                String str = map1.get("code").toString();
                                if (str.equals("00036")) {
                                    Toast.makeText(getApplicationContext(), "企业码填写错误", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "账号密码错误", Toast.LENGTH_SHORT).show();
                                }
                                return;
                            }
                        }
                        if (TextUtils.isEmpty(s)) {
                            LoadDialog.dismiss(mContext);
                            Toast.makeText(getApplicationContext(), "请求超时", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        LoadDialog.dismiss(mContext);
                        Toast.makeText(getApplicationContext(), "访问网络失败，请重新登陆", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tx_forgetpassword://跳转至找回密码Activity
                Intent intent_forget = new Intent();
                intent_forget.setClass(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent_forget);
                break;
            case R.id.img_login_clean_user_et:
                et_login_user.getText().clear();
                break;
            case R.id.img_login_clean_password:
                et_login_password.getText().clear();
                break;
            case R.id.img_login_clean_daima_et:
                et_login_daima.getText().clear();
                break;
            case R.id.btn_login:
                phoneString = et_login_user.getText().toString().trim();
                passwordString = et_login_password.getText().toString().trim();
                CompanyCode = et_login_daima.getText().toString().trim();
                setLogin();
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

    private LoginBean GetUesrBean() {
        Gson gson = new Gson();
        LoginBean bean = gson.fromJson(CommonUtil.getUserInfo(mContext), LoginBean.class);
        return bean;
    }

    @Override
    public UserInfo getUserInfo(String s) {
        mLoginBeanList.add(GetUesrBean());
        if (mLoginBeanList != null && mLoginBeanList.size() > 0) {
            for (LoginBean i :
                    mLoginBeanList) {
                if (i.getText().getId().equals(s)) {
                    return new UserInfo(i.getText().getId(), i.getText().getFullname(), Uri.parse(ConstantValue.ImageFile + i.getText().getLogo()));
                }
            }
        }
        return null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            phoneString = et_login_user.getText().toString().trim();
            passwordString = et_login_password.getText().toString().trim();
            CompanyCode = et_login_daima.getText().toString().trim();
//            editor.putString("username", phoneString);
//            editor.putString("userpass", passwordString);
//            editor.apply();
            setLogin();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
