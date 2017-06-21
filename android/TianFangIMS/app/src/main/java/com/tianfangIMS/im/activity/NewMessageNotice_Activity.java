package com.tianfangIMS.im.activity;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tianfangIMS.im.R;
import com.tianfangIMS.im.utils.NToast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

/**
 * Created by LianMengYu on 2017/1/8.
 */

public class NewMessageNotice_Activity extends BaseActivity {
    private TextView tv_OpenOr;
    private SharedPreferences.Editor editor;
    private SharedPreferences.Editor sound_editor;
    private SharedPreferences.Editor vibrator_editor;
    private SharedPreferences.Editor intercom_editor;
    private LinearLayout newMessage_tishi;
    private RelativeLayout newMessage_rl;
    private CompoundButton sw_newmessage_disturb;
    private CompoundButton sw_newmessage_vedio;
    private CompoundButton sw_newmessage_shock;
    private CompoundButton sw_newmessage_intercom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newmessage_activity);
        setTitle("新消息通知设置");
        SharedPreferences sp = getSharedPreferences("newmessage", Activity.MODE_PRIVATE);
        SharedPreferences sound_sp = getSharedPreferences("sound", Activity.MODE_PRIVATE);
        SharedPreferences vibrator_sp = getSharedPreferences("vibrator", Activity.MODE_PRIVATE);
        SharedPreferences intercom_sp = getSharedPreferences("intercom", Activity.MODE_PRIVATE);
        editor = sp.edit();
        sound_editor = sound_sp.edit();
        vibrator_editor = vibrator_sp.edit();
        intercom_editor = intercom_sp.edit();
        boolean isOpenDisturb = sp.getBoolean("isOpenDisturb", false);
        boolean isSound = sound_sp.getBoolean("sound", true);
        boolean isVibrator = vibrator_sp.getBoolean("vibrator", true);
        boolean isIntercom = intercom_sp.getBoolean("intercom", true);
        init();
        IsSound();
        IsVibrator();
        IsIntercom();
        if (isNotificationEnabled(mContext)) {
            tv_OpenOr.setText("已开启");
        } else {
            tv_OpenOr.setText("已关闭");
        }
        sw_newmessage_disturb.setChecked(isOpenDisturb);
        sw_newmessage_vedio.setChecked(isSound);
        sw_newmessage_shock.setChecked(isVibrator);
        sw_newmessage_intercom.setChecked(isIntercom);
    }

    private void init() {
        tv_OpenOr = (TextView) this.findViewById(R.id.tv_OpenOr);
        newMessage_tishi = (LinearLayout) this.findViewById(R.id.newMessage_tishi);
        newMessage_rl = (RelativeLayout) this.findViewById(R.id.newMessage_rl);
        sw_newmessage_disturb = (CompoundButton) this.findViewById(R.id.sw_newmessage_disturb);
        sw_newmessage_vedio = (CompoundButton) this.findViewById(R.id.sw_newmessage_vedio);
        sw_newmessage_shock = (CompoundButton) this.findViewById(R.id.sw_newmessage_shock);
        sw_newmessage_intercom = (CompoundButton) this.findViewById(R.id.sw_newmessage_intercom);
        sw_newmessage_disturb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    RongIM.getInstance().setNotificationQuietHours("00:00:00", 1439, new RongIMClient.OperationCallback() {
                        @Override
                        public void onSuccess() {
                            NToast.shortToast(mContext, "已开启消息免打扰");
                            newMessage_tishi.setVisibility(View.VISIBLE);
                            editor.putBoolean("isOpenDisturb", true);
                            editor.apply();
                            sound_editor.putBoolean("sound", false);
                            sound_editor.apply();
                            vibrator_editor.putBoolean("vibrator", false);
                            vibrator_editor.apply();
                            intercom_editor.putBoolean("intercom", false);
                            intercom_editor.apply();
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });
                } else {
                    RongIM.getInstance().removeNotificationQuietHours(new RongIMClient.OperationCallback() {
                        @Override
                        public void onSuccess() {
                            NToast.shortToast(mContext, "已关闭消息免打扰");
                            newMessage_tishi.setVisibility(View.VISIBLE);
                            editor.putBoolean("isOpenDisturb", false);
                            editor.apply();
                            sound_editor.putBoolean("sound", sw_newmessage_vedio.isChecked());
                            sound_editor.apply();
                            vibrator_editor.putBoolean("vibrator", sw_newmessage_shock.isChecked());
                            vibrator_editor.apply();
                            intercom_editor.putBoolean("intercom", sw_newmessage_intercom.isChecked());
                            intercom_editor.apply();
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });
                }
            }
        });

    }

    /**
     * 开启铃声
     */
    private void IsSound() {
        sw_newmessage_vedio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    sound_editor.putBoolean("sound", true);
                    sound_editor.apply();
                } else {
                    sound_editor.putBoolean("sound", false);
                    sound_editor.apply();
                }
            }
        });
    }

    /**
     * 开始震动
     */
    private void IsVibrator() {
        sw_newmessage_shock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    vibrator_editor.putBoolean("vibrator", true);
                    vibrator_editor.apply();
                } else {
                    vibrator_editor.putBoolean("vibrator", false);
                    vibrator_editor.apply();
                }
            }
        });
    }

    private void IsIntercom() {
        sw_newmessage_intercom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    intercom_editor.putBoolean("intercom", true);
                    intercom_editor.apply();
                } else {
                    intercom_editor.putBoolean("intercom", false);
                    intercom_editor.apply();
                }
            }
        });
    }

    private boolean isNotificationEnabled(Context context) {

        String CHECK_OP_NO_THROW = "checkOpNoThrow";
        String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;

        Class appOpsClass = null;
     /* Context.APP_OPS_MANAGER */
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                    String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }
}
