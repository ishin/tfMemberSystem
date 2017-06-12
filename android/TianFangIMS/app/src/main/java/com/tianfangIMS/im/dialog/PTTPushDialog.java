package com.tianfangIMS.im.dialog;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tianfangIMS.im.R;

import io.rong.imkit.RongIM;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.UserInfo;
import io.rong.ptt.PTTClient;

/**
 * Created by LianMengYu on 2017/4/28.
 */

public class PTTPushDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private ImageView iv_ptt_photo;
    private TextView tv_ptt_departmentName;//名字
    private Button btn_ptt_cancel, btn_ptt_submit;
    private PTTClient pttClient;
    private int ids;
    private String sessionId;
    UserInfo userInfo;
    private Object object;
    private String type;
    Group group;
    private SharedPreferences.Editor editor;
    private SharedPreferences sp;
    DialogCallBackListener mListener;

    public PTTPushDialog(Context context, int ids, Object object) {
        super(context);
        this.mContext = context;
        this.ids = ids;
        this.object = object;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) throws NullPointerException {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(mContext).inflate(R.layout.pttpush_dialog, null);
        setContentView(view);
        pttClient = PTTClient.getInstance();
        sessionId = mContext.getSharedPreferences("CompanyCode", Activity.MODE_PRIVATE).getString("CompanyCode", "");
        sp = mContext.getSharedPreferences("pttActivity", Activity.MODE_PRIVATE);
        editor = sp.edit();
        init(view);
        type = object.toString();
        if (type.equals("private")) {
            try {
                userInfo = RongUserInfoManager.getInstance().getUserInfo(ids + "");
                tv_ptt_departmentName.setText(userInfo.getName());
                Picasso.with(mContext)
                        .load(userInfo.getPortraitUri())
                        .resize(80, 80)
                        .centerCrop()
                        .placeholder(R.mipmap.default_portrait)
                        .config(Bitmap.Config.ARGB_8888)
                        .error(R.mipmap.default_portrait)
                        .into(iv_ptt_photo);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        } else if (type.equals("group")) {
            try {
                group = RongUserInfoManager.getInstance().getGroupInfo(ids + "");
                tv_ptt_departmentName.setText(group.getName());
                Picasso.with(mContext)
                        .load(group.getPortraitUri())
                        .resize(80, 80)
                        .centerCrop()
                        .placeholder(R.mipmap.default_portrait)
                        .config(Bitmap.Config.ARGB_8888)
                        .error(R.mipmap.default_portrait)
                        .into(iv_ptt_photo);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    private void init(View view) {
        iv_ptt_photo = (ImageView) view.findViewById(R.id.iv_ptt_photo);
        tv_ptt_departmentName = (TextView) view.findViewById(R.id.tv_ptt_departmentName);
        btn_ptt_cancel = (Button) view.findViewById(R.id.btn_ptt_cancel);
        btn_ptt_submit = (Button) view.findViewById(R.id.btn_ptt_submit);

        btn_ptt_cancel.setOnClickListener(this);
        btn_ptt_submit.setOnClickListener(this);
    }

    public interface DialogCallBackListener {//通过该接口回调Dialog需要传递的值

        public int callBack(int msg);//具体方法
    }

    public void DialogCallBackListener(DialogCallBackListener mListener) {
        this.mListener = mListener;
    }

    private String getRunningActivityName() {
        ActivityManager activityManager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        String str = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        return str;
    }

    @Override
    public void onClick(View v) throws NullPointerException {
        switch (v.getId()) {
            case R.id.btn_ptt_cancel://挂断
                dismiss();
                pttClient.leaveSession();
                break;
            case R.id.btn_ptt_submit://接听
//                if (getRunningActivityName().equals("com.tianfangIMS.im.activity.ConversationActivity")) {
////                    editor.putInt("pttkey",1);
////                    editor.apply();
////                    mListener.callBack(1);
//                    NToast.shortToast(mContext, "接收到语音对讲，请滑动接听");
//                } else {
                try {
                    if (type.equals("private")) {
                        RongIM.getInstance().startPrivateChat(mContext, ids + "", userInfo.getName(), 1);

                    } else if (type.equals("group")) {
                        RongIM.getInstance().startGroupChat(mContext, ids + "", group.getName(), 1);
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
//                }
                dismiss();
                break;
        }
    }
}
