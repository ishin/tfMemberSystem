package com.tianfangIMS.im.dialog;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tianfangIMS.im.ConstantValue;
import com.tianfangIMS.im.R;

/**
 * Created by LianMengYu on 2017/5/17.
 */

public class TalkPhoneDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private String logo;
    private String phone;
    private String name;

    private ImageView iv_talkphone_photo;
    private Button btn_quxiao_talk, btn_submit_talk;
    private TextView tv_talk_Name, tv_phone_number;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;

    public TalkPhoneDialog(Context context, String logo, String phone, String name) {
        super(context);
        this.mContext = context;
        this.logo = logo;
        this.phone = phone;
        this.name = name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(mContext).inflate(R.layout.callphone_dialog, null);
        setContentView(view);
        init(view);
    }

    private void init(View view) {
        iv_talkphone_photo = (ImageView) view.findViewById(R.id.iv_talkphone_photo);
        btn_quxiao_talk = (Button) view.findViewById(R.id.btn_quxiao_talk);
        btn_submit_talk = (Button) view.findViewById(R.id.btn_submit_talk);
        tv_talk_Name = (TextView) view.findViewById(R.id.tv_talk_Name);
        tv_phone_number = (TextView) view.findViewById(R.id.tv_phone_number);
        btn_quxiao_talk.setOnClickListener(this);
        btn_submit_talk.setOnClickListener(this);

        tv_talk_Name.setText(name);
        tv_phone_number.setText(phone);
        Picasso.with(mContext)
                .load(ConstantValue.ImageFile + logo)
                .error(R.mipmap.default_portrait)
                .placeholder(R.mipmap.default_portrait)
                .resize(80, 80)
                .into(iv_talkphone_photo);
    }

    private void setPermissions() {
        // 检查是否获得了权限（Android6.0运行时权限）
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // 没有获得授权，申请授权
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext,
                    Manifest.permission.CALL_PHONE)) {
// 帮跳转到该应用的设置界面，让用户手动授权
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                intent.setData(uri);
                mContext.startActivity(intent);
            } else {
                // 不需要解释为何需要该权限，直接请求授权
                ActivityCompat.requestPermissions((Activity) mContext,
                        new String[]{Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
            }
        } else {
            // 已经获得授权，可以打电话
            CallPhone();
        }
    }

    private void CallPhone() {
        if (TextUtils.isEmpty(phone)) {
            // 提醒用户
            // 注意：在这个匿名内部类中如果用this则表示是View.OnClickListener类的对象，
            // 所以必须用MainActivity.this来指定上下文环境。
            Toast.makeText(mContext, "号码不能为空！", Toast.LENGTH_SHORT).show();
        } else {
            // 拨号：激活系统的拨号组件
            Intent intent = new Intent(); // 意图对象：动作 + 数据
            intent.setAction(Intent.ACTION_CALL); // 设置动作
            Uri data = Uri.parse("tel:" + phone); // 设置数据
            intent.setData(data);
            mContext.startActivity(intent); // 激活Activity组件
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit_talk:
                setPermissions();
                dismiss();
                break;
            case R.id.btn_quxiao_talk:
                dismiss();
                break;
        }
    }
}
