package com.tianfangIMS.im.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.squareup.picasso.Picasso;
import com.tianfangIMS.im.ConstantValue;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.activity.LoginActivity;
import com.tianfangIMS.im.bean.AddFriendRequestBean;
import com.tianfangIMS.im.bean.LoginBean;
import com.tianfangIMS.im.bean.UserInfoBean;
import com.tianfangIMS.im.utils.CommonUtil;
import com.tianfangIMS.im.utils.NToast;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by LianMengYu on 2017/3/13.
 */

public class DelAddFriendDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private UserInfoBean userInfoBean;

    private Button btn_quxiao_move, btn_submit_move;
    private ImageView iv_movegroupuser_photo;
    private TextView tv_movegroupuser_departmentName;
    private TextView tv_person_departmentTxt;
    private UpdateUI updateUI;
    private String Sessionid;

    private boolean AddorDel;

    public DelAddFriendDialog(Context context, UserInfoBean userInfoBean, String Sessionid, boolean addorDel) {
        super(context);
        this.mContext = context;
        this.userInfoBean = userInfoBean;
        this.Sessionid = Sessionid;
        this.AddorDel = addorDel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(mContext).inflate(R.layout.movegroupuser_dialog, null);
        setContentView(view);
        init(view);
    }

    private void init(View view) {
        btn_quxiao_move = (Button) view.findViewById(R.id.btn_quxiao_move);
        btn_submit_move = (Button) view.findViewById(R.id.btn_submit_move);
        iv_movegroupuser_photo = (ImageView) view.findViewById(R.id.iv_movegroupuser_photo);
        tv_movegroupuser_departmentName = (TextView) view.findViewById(R.id.tv_movegroupuser_departmentName);
        tv_person_departmentTxt = (TextView) view.findViewById(R.id.tv_person_departmentTxt);
        if (AddorDel) {
            tv_movegroupuser_departmentName.setText("确认从常用名单删除 " + userInfoBean.getName());
            tv_person_departmentTxt.setText("确认后会删除与对方的聊天记录");
        } else {
            tv_movegroupuser_departmentName.setText("确认 " + userInfoBean.getName() + "为好友");
            tv_person_departmentTxt.setText("");
        }
        Picasso.with(mContext)
                .load(ConstantValue.ImageFile + userInfoBean.getLogo())
                .error(R.mipmap.default_portrait)
                .placeholder(R.mipmap.default_portrait)
                .resize(80, 80)
                .into(iv_movegroupuser_photo);
        btn_quxiao_move.setOnClickListener(this);
        btn_submit_move.setOnClickListener(this);
    }

    private void DelFriend() {
        Gson gson = new Gson();
        LoginBean loginBean = gson.fromJson(CommonUtil.getUserInfo(mContext), LoginBean.class);
        String UID = loginBean.getText().getAccount();
        OkGo.post(ConstantValue.DELTETFRIEND)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .headers("cookie", Sessionid)
                .params("account", UID)
                .params("friend", userInfoBean.getAccount())
                .execute(new StringCallback() {
                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        LoadDialog.show(mContext);
                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        LoadDialog.dismiss(mContext);
                        if (!TextUtils.isEmpty(s)) {
                            Log.e("delFRIEND", "code:" + s);
                            Gson gson = new Gson();

                            AddFriendRequestBean bean = gson.fromJson(s, AddFriendRequestBean.class);
                            if (bean.getCode().equals("1")) {
                                updateUI.onCheckedUI();
                                NToast.shortToast(mContext, "删除好友成功");
                            }
                            if (bean.getCode().equals("0")) {
                                NToast.shortToast(mContext, "存在好友关系");
                            }
                            if (bean.getCode().equals("-1")) {
                                NToast.shortToast(mContext, "删除好友失败");
                            }
                        }

                    }
                });

    }

    private void AddFriend() {
        Gson gson = new Gson();
        LoginBean loginBean = gson.fromJson(CommonUtil.getUserInfo(mContext), LoginBean.class);
        String UID = loginBean.getText().getAccount();
        OkGo.post(ConstantValue.ADDTOPCONTACTS)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .headers("cookie", Sessionid)
                .params("account", UID)
                .params("friend", userInfoBean.getAccount())
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
                            if ((s.trim()).startsWith("<!DOCTYPE")) {
                                NToast.shortToast(mContext, "Session过期，请重新登陆");
                                mContext.startActivity(new Intent(mContext, LoginActivity.class));
//                                finish();
                            } else {
                                Gson gson = new Gson();
                                AddFriendRequestBean bean = gson.fromJson(s, AddFriendRequestBean.class);
                                if (bean.getCode().equals("1")) {
                                    NToast.shortToast(mContext, "添加好友成功");
                                    updateUI.onCheckedUI();
                                    RongIM.getInstance().refreshUserInfoCache(new UserInfo(userInfoBean.getId(), userInfoBean.getName(), Uri.parse(ConstantValue.ImageFile + userInfoBean.getLogo())));
//                                    IsFriend();
                                }
                                if (bean.getCode().equals("0")) {
                                    NToast.shortToast(mContext, "存在好友关系");
                                }
                                if (bean.getCode().equals("-1")) {
                                    NToast.shortToast(mContext, "好友添加失败");
                                }
                            }
                        }
                    }
                });
    }

    public interface UpdateUI {
        void onCheckedUI();
    }

    public void setUpdateUI(UpdateUI ui) {
        this.updateUI = ui;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_quxiao_move:
                this.dismiss();
                break;
            case R.id.btn_submit_move:
                if (AddorDel) {
                    DelFriend();
                } else {
                    AddFriend();
                }
                this.dismiss();
                break;
        }
    }
}
