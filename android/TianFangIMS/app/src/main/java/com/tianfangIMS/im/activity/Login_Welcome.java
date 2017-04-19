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

import com.tianfangIMS.im.R;


/**
 * Created by LianMengYu on 2016/12/29.
 * 加载页
 */

public class Login_Welcome extends Activity {
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        mContext = this;
        //获取当前窗体
        Window window = Login_Welcome.this.getWindow();
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);
        setContentView(R.layout.activity_welcome);
        //启动一个handler来限定3秒，然后调整Activity
        new Handler().postDelayed(runnable, 7000);

    if (Build.VERSION.SDK_INT >= 23) {
        if(!Settings.canDrawOverlays(Login_Welcome.this)){
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 10);
        }
    }
}

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            SharedPreferences sharedPreferences = getSharedPreferences("config",
                    Activity.MODE_PRIVATE);
            String username = sharedPreferences.getString("username", "");
            String userPwd = sharedPreferences.getString("userpass", "");
//            String token = sharedPreferences.getString("token", "");
            if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(userPwd)) {
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
