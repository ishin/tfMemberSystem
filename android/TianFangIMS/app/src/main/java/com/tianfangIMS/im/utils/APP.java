package com.tianfangIMS.im.utils;

import android.app.ActivityManager;
import android.content.Context;

import com.tianfangIMS.im.TianFangIMSApplication;

import io.rong.imkit.RongIM;
import io.rong.push.RongPushClient;
import io.rong.push.common.RongException;

/**
 * Created by LianMengYu on 2017/1/9.
 */

public class APP extends TianFangIMSApplication{
    @Override
    public void onCreate() {
        super.onCreate();
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {
            RongPushClient.registerHWPush(this);
//            RongPushClient.registerMiPush(this,);
            try {
                RongPushClient.registerGCM(this);
            } catch (RongException e) {
                e.printStackTrace();
            }
            RongIM.init(this);
//            NLog.setDebug(true);//Seal Module Log 开关
            SharedPreferencesContext.init(this);
            //获取对讲监听
        }
    }

    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
}
