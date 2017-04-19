package com.tianfangIMS.im.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.squareup.picasso.Picasso;
import com.tianfangIMS.im.ConstantValue;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.bean.AddFriendRequestBean;
import com.tianfangIMS.im.bean.LoginBean;
import com.tianfangIMS.im.bean.UserInfoBean;
import com.tianfangIMS.im.dialog.BigImagedialog;
import com.tianfangIMS.im.dialog.DelAddFriendDialog;
import com.tianfangIMS.im.dialog.LoadDialog;
import com.tianfangIMS.im.utils.CommonUtil;
import com.tianfangIMS.im.utils.NToast;

import java.util.Map;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by LianMengYu on 2017/2/10.
 */

public class FriendPersonInfoActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "FriendPersonInfoActivity";
    private Conversation.ConversationType mConversationType;
    private String fromConversationId;
    private ImageView iv_friendinfo_photo;
    private TextView tv_friendinfo_name;
    private String userID;
    private UserInfoBean userInfoBean;
    private TextView friendinfo_email, tx_frienduserinfo_phonenumber, iv_friendinfo_phone,
            friendinfo_company, friendinfo_address, friendinfo_chanpin, friendinfo_jingli;
    private Context mContext;
    private FrameLayout fl_friendinfo_add, fl_friendinfo_delete;
    private String BigImagePath;
    private Button btn_sendMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendpersoninfo_activity);
        setLoactionButtonVisibility(View.INVISIBLE);
        setViewPagerTagVisibiliy(View.INVISIBLE);
        setPersonContactButtonVisibility(View.INVISIBLE);
        mContext = this;
        init();
        userID = getIntent().getStringExtra("userId");
        GetUserInfoSync(userID);
        IsFriend();
    }

    private void GetUserInfoSync(String userID) {
        OkGo.post(ConstantValue.GETONEPERSONINFO)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .params("userid", userID)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (!TextUtils.isEmpty(s)) {
                            Gson gson = new Gson();
                            userInfoBean = gson.fromJson(s, UserInfoBean.class);
                            setTitle(userInfoBean.getName());
                            BigImagePath = ConstantValue.ImageFile + userInfoBean.getLogo();
                            SetUserInfo(
                                    userInfoBean.getName(),
                                    userInfoBean.getEmail(),
                                    userInfoBean.getMobile(),
                                    userInfoBean.getTelephone(),
                                    userInfoBean.getOrganname(),
                                    userInfoBean.getAddress(),
                                    userInfoBean.getBranchname(),
                                    userInfoBean.getPositionname());
                            Picasso.with(mContext)
                                    .load(ConstantValue.ImageFile + userInfoBean.getLogo())
                                    .resize(50, 50)
                                    .placeholder(R.mipmap.default_portrait)
                                    .config(Bitmap.Config.ARGB_8888)
                                    .error(R.mipmap.default_portrait)
                                    .into(iv_friendinfo_photo);
                        }
                    }
                });
    }


    private void init() {

        iv_friendinfo_photo = (ImageView) this.findViewById(R.id.iv_friendinfo_photo);
        tv_friendinfo_name = (TextView) this.findViewById(R.id.tv_friendinfo_name);
        friendinfo_email = (TextView) this.findViewById(R.id.friendinfo_email);
        tx_frienduserinfo_phonenumber = (TextView) this.findViewById(R.id.tx_frienduserinfo_phonenumber);
        iv_friendinfo_phone = (TextView) this.findViewById(R.id.iv_friendinfo_phone);
        friendinfo_company = (TextView) this.findViewById(R.id.friendinfo_company);
        friendinfo_address = (TextView) this.findViewById(R.id.friendinfo_address);
        friendinfo_chanpin = (TextView) this.findViewById(R.id.friendinfo_chanpin);
        friendinfo_jingli = (TextView) this.findViewById(R.id.friendinfo_jingli);
        fl_friendinfo_add = (FrameLayout) this.findViewById(R.id.fl_friendinfo_add);
        fl_friendinfo_delete = (FrameLayout) this.findViewById(R.id.fl_friendinfo_delete);
        btn_sendMessage = (Button) this.findViewById(R.id.btn_sendMessage);

        fl_friendinfo_add.setOnClickListener(this);
        fl_friendinfo_delete.setOnClickListener(this);
        iv_friendinfo_photo.setOnClickListener(this);
        btn_sendMessage.setOnClickListener(this);
    }

    private void SetUserInfo(String uesrname, String eMail, String phone, String telephone, String company, String address, String chanpin, String jingli) {
        tv_friendinfo_name.setText(uesrname);
        friendinfo_email.setText(eMail);
        tx_frienduserinfo_phonenumber.setText(phone);
        iv_friendinfo_phone.setText(telephone);
        friendinfo_company.setText(company);
        friendinfo_address.setText(address);
        friendinfo_chanpin.setText(chanpin);
        friendinfo_jingli.setText(jingli);
    }

    private void IsFriend() {
        Gson gson = new Gson();
        LoginBean loginBean = gson.fromJson(CommonUtil.getUserInfo(mContext), LoginBean.class);
        final String UID = loginBean.getText().getId();
        OkGo.post(ConstantValue.ISFRIEND)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .params("userid", UID)
                .params("friendid", userID)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (!TextUtils.isEmpty(s) && !s.equals("{}")) {
                            Gson gson = new Gson();
                            Map<String, Object> map = gson.fromJson(s, new TypeToken<Map<String, Object>>() {
                            }.getType());
                            double code = (Double) map.get("code");
                            if (code == 1.0) {
                                Log.e("aaaaaaaaaa", "执行删除好友:" + map.get("code"));
                                fl_friendinfo_add.setVisibility(View.GONE);
                                fl_friendinfo_delete.setVisibility(View.VISIBLE);
                            }
                            if (code == 0.0) {
                                if (RongIMClient.getInstance().getCurrentUserId().equals(UID)) {
                                    fl_friendinfo_add.setVisibility(View.GONE);
                                    fl_friendinfo_delete.setVisibility(View.GONE);
                                } else {
                                    fl_friendinfo_add.setVisibility(View.VISIBLE);
                                    fl_friendinfo_delete.setVisibility(View.GONE);
                                }
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
                            Gson gson = new Gson();
                            AddFriendRequestBean bean = gson.fromJson(s, AddFriendRequestBean.class);
                            if (bean.getCode().equals("1")) {
                                NToast.shortToast(mContext, "添加好友成功");
                            }
                            if (bean.getCode().equals("0")) {
                                NToast.shortToast(mContext, "存在好友关系");
                            }
                            if (bean.getCode().equals("-1")) {
                                NToast.shortToast(mContext, "好友添加失败");
                            }
                        }
                    }
                });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_friendinfo_delete:
                DelAddFriendDialog delDialog = new DelAddFriendDialog(mContext,userInfoBean);
                delDialog.show();
                break;
            case R.id.fl_friendinfo_add:
                AddFriend();
                break;
            case R.id.iv_friendinfo_photo:
                BigImagedialog bigImagedialog = new BigImagedialog(mContext, BigImagePath, R.style.Dialog_Fullscreen);
                bigImagedialog.getWindow().setBackgroundDrawable(new ColorDrawable());
                bigImagedialog.show();
                CommonUtil.SetDialogStyle(bigImagedialog);
                break;
            case R.id.btn_sendMessage:
                try {
                    if (!TextUtils.isEmpty(userInfoBean.getId())) {
                        RongIM.getInstance().startPrivateChat(FriendPersonInfoActivity.this, userInfoBean.getId(), userInfoBean.getName());
                        this.finish();
                    }else{
                        NToast.shortToast(mContext,"获取用户信息失败");
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
                break;
        }
    }
}
