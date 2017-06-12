package com.tianfangIMS.im.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.tianfangIMS.im.ConstantValue;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.dialog.LoadDialog;
import com.tianfangIMS.im.dialog.QRDialog;
import com.tianfangIMS.im.utils.NToast;
import com.tianfangIMS.im.utils.StringUtils;

import net.sf.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by LianMengYu on 2017/4/26.
 */

public class QRCodeActivity extends BaseActivity implements View.OnClickListener {
    private Context mContext;
    private RelativeLayout rl_qr_icon_crame, rl_qr_icon_image;
    private String Name, Position, Sex, Logo, account, Uid;
    String sessionId;
    boolean flag = false;
    String filePath;
    String TimeMillis;
    long TimeMillislong;
    private EditText et_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcode_layout);
        setTitle("我的二维码");
        mContext = this;
        init();
        getuserInfo();
    }

    private void init() {
        rl_qr_icon_crame = (RelativeLayout) this.findViewById(R.id.rl_qr_icon_crame);
        rl_qr_icon_image = (RelativeLayout) this.findViewById(R.id.rl_qr_icon_image);
        et_search = (EditText) this.findViewById(R.id.et_search);

        et_search.setFocusable(false);
        et_search.setOnClickListener(this);
        rl_qr_icon_image.setOnClickListener(this);
        rl_qr_icon_crame.setOnClickListener(this);
    }

    //回调获取扫描得到的条码值
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        TimeMillis = (System.currentTimeMillis() / 1000) + "";
        TimeMillislong = (System.currentTimeMillis() / 1000);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        String Account = "";
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "扫码取消！", Toast.LENGTH_LONG).show();
            } else {
                if (result.getContents().indexOf("&") == -1) {
                    Account = result.getContents();
                } else {
                    Account = result.getContents().substring(0, result.getContents().indexOf("&"));
                }
                JSONObject jo = new JSONObject();
                jo.put("friend", Account);
                String sign = makeSign(jo, "@q3$fd12%", TimeMillislong);
                OkGo.post(ConstantValue.SCANADDCONTACTS)
                        .tag(this)
                        .headers("cookie", sessionId)
                        .params("friend", Account)
                        .params("timestamp", TimeMillis)
                        .params("sign", sign)
                        .params("imkey", "@q3$fd12%")
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
                                        startActivity(new Intent(mContext, LoginActivity.class));
                                        finish();
                                    }
                                    Gson gson = new Gson();
                                    Map<String, Object> map = gson.fromJson(s, new TypeToken<Map<String, Object>>() {
                                    }.getType());
                                    String str = map.get("code").toString();
                                    if (str.equals("1.0")) {
                                        Toast.makeText(mContext, "添加好友成功", Toast.LENGTH_LONG).show();
                                        if (!TextUtils.isEmpty(Uid) && !TextUtils.isEmpty(Name)) {
                                            RongIM.getInstance().refreshUserInfoCache(new UserInfo(Uid, Name, Uri.parse(ConstantValue.ImageFile + Logo)));
                                        }
                                        startActivity(new Intent(mContext, MineTopContactsActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(mContext, "添加好友失败", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(mContext, "数据异常，请检查网络", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void getuserInfo() {
        sessionId = getSharedPreferences("CompanyCode", MODE_PRIVATE).getString("CompanyCode", "");
        String id = RongIMClient.getInstance().getCurrentUserId();
        OkGo.post(ConstantValue.AFTERLOGINUSERINFO)
                .tag(this)
                .headers("cookie", sessionId)
                .params("userid", id)
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
                                startActivity(new Intent(mContext, LoginActivity.class));
                                finish();
                            } else {
                                Gson gson1 = new Gson();
                                Map<String, String> map = gson1.fromJson(s, new TypeToken<Map<String, String>>() {
                                }.getType());
//                                if ((map.get("code").toString()).equals("0.0")) {
//                                    NToast.shortToast(mContext,"用户名为空，请重新获取");
//                                    return;
//                                }else{
                                Name = map.get("name");
                                Position = map.get("positionname");
                                Sex = map.get("sex");
                                Logo = map.get("logo");
                                account = map.get("account");
                                Uid = map.get("id");
//                                }
                            }
                        }
                    }
                });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_qr_icon_image:
                QRDialog qrDialog = new QRDialog(mContext, Logo, Sex, Position, Name, account);
                qrDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
                qrDialog.show();
                // 设置宽度为屏宽、靠近屏幕底部。
                Window window = qrDialog.getWindow();
                //设置dialog弹出的动画，从屏幕底部弹出     window.setWindowAnimations(R.style.take_photo_anim);
                //最重要的一句话，一定要加上！要不然怎么设置都不行!window.setBackgroundDrawableResource(android.R.color.transparent);
                WindowManager.LayoutParams wlp = window.getAttributes();
                Display d = window.getWindowManager().getDefaultDisplay();
                //获取屏幕宽
                wlp.width = (int) (d.getWidth() * 0.8);
                wlp.height = (int) (d.getHeight() * 0.65);
                //宽度按屏幕大小的百分比设置，这里我设置的是全屏显示
                wlp.gravity = Gravity.CENTER;
                if (wlp.gravity == Gravity.CENTER)
//                    wlp.y = 0;
                    //如果是底部显示，则距离底部的距离是0
                    window.setAttributes(wlp);

                break;
            case R.id.rl_qr_icon_crame:
                IntentIntegrator integrator = new IntentIntegrator(QRCodeActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setCaptureActivity(ScanActivity.class);
                integrator.setPrompt("将二维码放置框内，即开始扫描"); //底部的提示文字，设为""可以置空
                integrator.setCameraId(0); //前置或者后置摄像头
                integrator.setBeepEnabled(true); //扫描成功的「哔哔」声，默认开启
                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();
                break;
            case R.id.et_search:
                startActivity(new Intent(mContext, AddTopContacts_Activity.class));
                break;
        }
    }

    public String makeSign(JSONObject param, String key, long timeStamp) {
        StringBuilder sbp = new StringBuilder();
        Iterator<String> it = param.keys();

        while (it.hasNext()) {
            String jsonKey = it.next();
            String jsonValue = param.getString(jsonKey);
            sbp.append(jsonKey).append("=").append(jsonValue);
        }
        String pStr = sbp.toString();
        pStr = StringUtils.getInstance().sortByChars(pStr);
        pStr = key + pStr + timeStamp;
        String caclSign = StringUtils.getInstance().getMD5Str(pStr.toString());
        return caclSign;
    }
}
