package com.tianfangIMS.im.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.squareup.picasso.Picasso;
import com.tianfangIMS.im.ConstantValue;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.bean.LoginBean;
import com.tianfangIMS.im.dialog.LoadDialog;
import com.tianfangIMS.im.dialog.UserInfo_Phone_Dialog;
import com.tianfangIMS.im.utils.CommonUtil;
import com.tianfangIMS.im.utils.NToast;
import com.tianfangIMS.im.utils.PicassoImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by LianMengYu on 2017/1/7.
 */

public class UserInfo_Activity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_useinfo_phone, ly_useinfo_photo;
    private Context mContext;
    private TextView friendinfo_email, tx_frienduserinfo_phonenumber, iv_friendinfo_phone,
            friendinfo_company, friendinfo_address, friendinfo_chanpin, friendinfo_jingli;
    private ImageView iv_userinfo_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfo_fragment);
        mContext = this;
        setTitle("个人信息");
        init();
        GetUserInfo();
        settingImagePicker();
    }

    private void settingImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new PicassoImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setMultiMode(false);
        imagePicker.setCrop(true);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setSelectLimit(9);    //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(1500);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(1500);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素
    }

    private void init() {
        rl_useinfo_phone = (RelativeLayout) this.findViewById(R.id.rl_useinfo_telephone);
        ly_useinfo_photo = (RelativeLayout) this.findViewById(R.id.ly_useinfofr1_photo);

        friendinfo_email = (TextView) this.findViewById(R.id.tv_userinfo_email);
        tx_frienduserinfo_phonenumber = (TextView) this.findViewById(R.id.tv_userinfo_phonenumber);
        iv_friendinfo_phone = (TextView) this.findViewById(R.id.tv_userinfo1_telephone);
        friendinfo_company = (TextView) this.findViewById(R.id.tv_userinfo_company);
        friendinfo_address = (TextView) this.findViewById(R.id.tv_userinfo_address);
        friendinfo_chanpin = (TextView) this.findViewById(R.id.tv_userinfo_department);
        friendinfo_jingli = (TextView) this.findViewById(R.id.tv_userinfo_position);
        iv_userinfo_photo = (ImageView) this.findViewById(R.id.iv_userinfo_photo);
        rl_useinfo_phone.setOnClickListener(this);
        ly_useinfo_photo.setOnClickListener(this);
        iv_userinfo_photo.setOnClickListener(this);
    }

    private void SetUserInfo(String eMail, String phone, String telephone, String company, String address, String chanpin, String jingli) {
        friendinfo_email.setText(eMail);
        tx_frienduserinfo_phonenumber.setText(phone);
        iv_friendinfo_phone.setText(telephone);
        friendinfo_company.setText(company);
        friendinfo_address.setText(address);
        friendinfo_chanpin.setText(chanpin);
        friendinfo_jingli.setText(jingli);
    }

    private void GetUserInfo() {
        final Gson gson = new Gson();
        LoginBean loginBean = gson.fromJson(CommonUtil.getUserInfo(mContext), LoginBean.class);
        String ids = loginBean.getText().getId();
        OkGo.post(ConstantValue.GETONEPERSONINFO)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .params("userid", ids)
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
                            Gson gson = new Gson();
                            Map<String, String> map = gson.fromJson(s, new TypeToken<Map<String, Object>>() {
                            }.getType());
                            SetUserInfo(map.get("email"), map.get("mobile"), map.get("telephone"), map.get("organname"), map.get("address"),
                                    map.get("branchname"), map.get("positionname"));
                            Picasso.with(mContext)
                                    .load(ConstantValue.ImageFile + map.get("logo"))
                                    .resize(50, 50)
                                    .error(R.mipmap.default_portrait)
                                    .into(iv_userinfo_photo);
                        }
                    }
                });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_useinfo_telephone:
                String phonenumber = (tx_frienduserinfo_phonenumber.getText().toString()).replaceAll(" ", "").trim();
                UserInfo_Phone_Dialog userInfo_phone_dialog = new UserInfo_Phone_Dialog(this, R.style.dialog, phonenumber);
                userInfo_phone_dialog.getWindow().setBackgroundDrawable(new ColorDrawable());
                userInfo_phone_dialog.show();
                break;
            case R.id.iv_userinfo_photo:
                Intent intent = new Intent(this, ImageGridActivity.class);
                startActivityForResult(intent, 0);
                break;
        }
    }

    private void UpdateUserPhoto(File imageItem) {
        final Gson gson = new Gson();
        final LoginBean loginBean = gson.fromJson(CommonUtil.getUserInfo(mContext), LoginBean.class);
        String uid = loginBean.getText().getId();
        OkGo.post(ConstantValue.UPDATEUSERPHOTONOTCUT)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .params("userid", uid)
                .params("file", imageItem)
                .execute(new StringCallback() {
                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        LoadDialog.show(mContext);

                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (!TextUtils.isEmpty(s)) {
                            Gson gson1 = new Gson();
                            Map<String, Object> map = gson1.fromJson(s, new TypeToken<Map<String, Object>>() {
                            }.getType());
                            if ((Double) map.get("code") == 1.0) {
                                String logo = map.get("text").toString();
                                SharedPreferences mySharedPreferences = getSharedPreferences("user_login",
                                        Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = mySharedPreferences.edit();
                                editor.putString("logo", logo);
                                String name =mySharedPreferences.getString("logo", "");
                                Log.e("PhotoName", "---:" + name);
                                editor.commit();
                            }
                        } else {
                            NToast.longToast(mContext, "上传失败" + s);
                        }

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Log.e("是否执行成功", "OnErro");
                    }

                    @Override
                    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        super.upProgress(currentSize, totalSize, progress, networkSpeed);

                        if (currentSize == totalSize) {
                            LoadDialog.dismiss(mContext);
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == 0) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                Log.e("aaaaaaa", "打印出什么东西：" + images.get(0).path);
                UpdateUserPhoto(new File(images.get(0).path));
//                ImagePicker.getInstance().getImageLoader().displayImage(UserInfo_Activity.this, images.get(0).path, iv_userinfo_photo, 800, 800);
                Picasso.with(mContext)
                        .load(new File(images.get(0).path))
                        .resize(500, 500)
                        .placeholder(R.mipmap.default_photo)
                        .error(R.mipmap.default_photo)
                        .into(iv_userinfo_photo);
            } else {
                Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
