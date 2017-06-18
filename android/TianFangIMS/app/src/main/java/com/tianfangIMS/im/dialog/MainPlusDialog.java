package com.tianfangIMS.im.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.google.zxing.integration.android.IntentIntegrator;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.activity.AddGroupActivity;
import com.tianfangIMS.im.activity.QRCodeActivity;
import com.tianfangIMS.im.activity.ScanActivity;

/**
 * Created by LianMengYu on 2017/1/19.
 */

public class MainPlusDialog extends PopupWindow implements View.OnClickListener {
    private static final String TAG = "MainPlusDialog";
    private Context mContext;
    private RelativeLayout rl_mainplus_chatroom, rl_mainplus_topcontacts,rl_mainplus_qr;

    public MainPlusDialog(Context context) {
        super(context);
        this.mContext = context;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.mianplus_dialog, null);
        setContentView(view);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        init(view);
    }

    private void jumpContacts(){
        Intent intent = new Intent(mContext,QRCodeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("SimpleName",TAG);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
        this.dismiss();

    }
    private void jumpGroup(){
        Intent intent = new Intent(mContext,AddGroupActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("SimpleName",TAG);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
        this.dismiss();
    }
    private void jumpQR(){
//        Intent intent = new Intent(mContext,QRCodeActivity.class);
//        mContext.startActivity(intent);
//        this.dismiss();
        IntentIntegrator integrator = new IntentIntegrator((Activity) mContext);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setCaptureActivity(ScanActivity.class);
        integrator.setPrompt("将二维码放置框内，即开始扫描"); //底部的提示文字，设为""可以置空
        integrator.setCameraId(0); //前置或者后置摄像头
        integrator.setBeepEnabled(true); //扫描成功的「哔哔」声，默认开启
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }
    private void init(View view) {
        rl_mainplus_chatroom = (RelativeLayout) view.findViewById(R.id.rl_mainplus_chatroom);
        rl_mainplus_topcontacts = (RelativeLayout) view.findViewById(R.id.rl_mainplus_topcontacts);
        rl_mainplus_qr = (RelativeLayout)view.findViewById(R.id.rl_mainplus_qr);

        rl_mainplus_topcontacts.setOnClickListener(this);
        rl_mainplus_chatroom.setOnClickListener(this);
        rl_mainplus_qr.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_mainplus_chatroom:
                jumpGroup();
                break;
            case R.id.rl_mainplus_topcontacts:
                jumpContacts();
                break;
            case R.id.rl_mainplus_qr:
                jumpQR();
                this.dismiss();
                break;
        }
    }
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAsDropDown(parent);
        } else {
            this.dismiss();
        }
    }
}
