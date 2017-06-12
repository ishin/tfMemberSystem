package com.tianfangIMS.im.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.tianfangIMS.im.ConstantValue;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.dialog.LoadDialog;
import com.tianfangIMS.im.utils.NToast;

import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;


/**
 * Created by LianMengYu on 2016/12/29.
 * 加载页
 */

public class Login_Welcome extends Activity {
    private Context mContext;
    private String isSession = "false";
    private String sessionId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionId = getSharedPreferences("CompanyCode", MODE_PRIVATE).getString("CompanyCode", "");
        //隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        mContext = this;
        IsSession();
        //获取当前窗体
        Window window = Login_Welcome.this.getWindow();
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);
        setContentView(R.layout.activity_welcome);
        //启动一个handler来限定3秒，然后调整Activity
        new Handler().postDelayed(runnable, 7000);

    if (Build.VERSION.SDK_INT >= 23) {
        if(!Settings.canDrawOverlays(Login_Welcome.this)){
            NToast.shortToast(getApplicationContext(), "请添加权限，否则无法使用悬浮窗体");
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 10);
        }
    }
}
    private void IsSession() {
        OkGo.post(ConstantValue.ISSESSION)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .headers("cookie", sessionId)
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
                            if((s.trim()).startsWith("<!DOCTYPE")){
                                NToast.shortToast(mContext,"Session过期，请重新登陆");
                                startActivity(new Intent(mContext, LoginActivity.class));
                                finish();
                            }
                            Gson gson = new Gson();
                            Map<String, Object> map = gson.fromJson(s, new TypeToken<Map<String, Object>>() {
                            }.getType());
                            isSession = map.get("status").toString();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        NToast.shortToast(Login_Welcome.this,"访问失败，请重试");
                        return;
                    }
                });
    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            SharedPreferences sharedPreferences = getSharedPreferences("config",
                    Activity.MODE_PRIVATE);
            String username = sharedPreferences.getString("username", "");
            String userPwd = sharedPreferences.getString("userpass", "");
//            String token = sharedPreferences.getString("token", "");
            if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(userPwd) && isSession.equals("true")) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            } else {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(Login_Welcome.this, "not granted", Toast.LENGTH_SHORT);
            }
        }
    }
}
