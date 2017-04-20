package com.tianfangIMS.im.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tianfangIMS.im.R;

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
    private LinearLayout newMessage_tishi;
    private RelativeLayout newMessage_rl;
    private CompoundButton sw_newmessage_disturb;
    private CompoundButton sw_newmessage_vedio;
    private CompoundButton sw_newmessage_shock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newmessage_activity);
        setTitle("新消息通知设置");
        SharedPreferences sp = getSharedPreferences("newmessage", MODE_PRIVATE);
        SharedPreferences sound_sp = getSharedPreferences("sound",MODE_PRIVATE);
        SharedPreferences vibrator_sp = getSharedPreferences("vibrator",MODE_PRIVATE);
        editor = sp.edit();
        sound_editor = sound_sp.edit();
        vibrator_editor = vibrator_sp.edit();
        boolean isOpenDisturb = sp.getBoolean("isOpenDisturb", true);
        boolean isSound = sound_sp.getBoolean("sound",true);
        boolean isVibrator = vibrator_sp.getBoolean("vibrator",true);
        init();
        IsSound();
        IsVibrator();
        if (isOpenDisturb) {
            tv_OpenOr.setText("已开启");
        } else {
            tv_OpenOr.setText("已关闭");
        }
        sw_newmessage_disturb.setChecked(isOpenDisturb);
        sw_newmessage_vedio.setChecked(isSound);
        sw_newmessage_shock.setChecked(isVibrator);
    }

    private void init() {
        tv_OpenOr = (TextView) this.findViewById(R.id.tv_OpenOr);
        newMessage_tishi = (LinearLayout) this.findViewById(R.id.newMessage_tishi);
        newMessage_rl = (RelativeLayout) this.findViewById(R.id.newMessage_rl);
        sw_newmessage_disturb = (CompoundButton) this.findViewById(R.id.sw_newmessage_disturb);
        sw_newmessage_vedio = (CompoundButton)this.findViewById(R.id.sw_newmessage_vedio);
        sw_newmessage_shock = (CompoundButton)this.findViewById(R.id.sw_newmessage_shock);

        sw_newmessage_disturb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                tv_OpenOr.setText("已开启");
                if (buttonView.isChecked()) {
                    RongIM.getInstance().setNotificationQuietHours("00:00:00", 1439, new RongIMClient.OperationCallback() {
                        @Override
                        public void onSuccess() {
                            newMessage_tishi.setVisibility(View.VISIBLE);
                            editor.putBoolean("isOpenDisturb", true);
                            editor.apply();
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });
                } else {
                    tv_OpenOr.setText("已关闭");
                    RongIM.getInstance().setNotificationQuietHours("00:00:00", 1439, new RongIMClient.OperationCallback() {
                        @Override
                        public void onSuccess() {
                            newMessage_tishi.setVisibility(View.VISIBLE);
                            editor.putBoolean("isOpenDisturb", false);
                            editor.apply();
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
    private void IsSound(){
        sw_newmessage_vedio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked()){
                    sound_editor.putBoolean("sound",true);
                    sound_editor.apply();
                }else{
                    sound_editor.putBoolean("sound",false);
                    sound_editor.apply();
                }
            }
        });
    }

    /**
     * 开始震动
     */
    private void IsVibrator(){
        sw_newmessage_shock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked()) {
                    vibrator_editor.putBoolean("vibrator", true);
                    vibrator_editor.apply();
                }else{
                    vibrator_editor.putBoolean("vibrator", false);
                    vibrator_editor.apply();
                }
            }
        });
    }

}
