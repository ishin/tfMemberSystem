package com.tianfangIMS.im.dialog;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tianfangIMS.im.R;

/**
 * Created by LianMengYu on 2017/1/6.
 */

public class UserInfo_Phone_Dialog extends Dialog implements View.OnClickListener {
    private Context context;
    private ClickListenerInterface clickListenerInterface;
    private RelativeLayout rl_dialog_phone, rl_dialog_copy, rl_dialog_maillist, rl_dialog_cencal;
    private String mphoneNumber;
    private ClipboardManager myClipboard;
    private ClipData myClip;

    public UserInfo_Phone_Dialog(Context context, int theme, String phoneNumber) {
        super(context);
        this.context = context;
        this.mphoneNumber = phoneNumber;
        this.setCanceledOnTouchOutside(true);
    }

    public interface ClickListenerInterface {
        public void Phone();//打电话

        public void copyText();//复制

        public void MailList();//存入通讯录
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.userinfo_phone_dialog, null);
        setContentView(view);
        init(view);

    }

    private void init(View view) {
        rl_dialog_copy = (RelativeLayout) view.findViewById(R.id.rl_dialog_copy);
        rl_dialog_phone = (RelativeLayout) view.findViewById(R.id.rl_dialog_phone);
        rl_dialog_maillist = (RelativeLayout) view.findViewById(R.id.rl_dialog_maillist);
        rl_dialog_cencal = (RelativeLayout) view.findViewById(R.id.rl_dialog_cencal);
        rl_dialog_copy.setOnClickListener(this);
        rl_dialog_phone.setOnClickListener(this);
        rl_dialog_maillist.setOnClickListener(this);
        rl_dialog_cencal.setOnClickListener(this);
    }

    private void setClickListener(ClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_dialog_phone:
                Intent intentPhone = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mphoneNumber));
                intentPhone.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentPhone);
                this.dismiss();
                break;
            case R.id.rl_dialog_copy:
                ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(mphoneNumber);
                Toast.makeText(getContext(), "复制成功。", Toast.LENGTH_LONG).show();
                this.dismiss();
                break;
            case R.id.rl_dialog_maillist:
                this.dismiss();
                break;
            case R.id.rl_dialog_cencal:
                this.dismiss();
                break;
        }
    }
}
