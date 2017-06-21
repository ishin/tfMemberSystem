package com.tianfangIMS.im.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.tianfangIMS.im.ConstantValue;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.bean.TopFiveUserInfoBean;
import com.tianfangIMS.im.bean.TreeInfo;
import com.tianfangIMS.im.bean.UserBean;
import com.tianfangIMS.im.dialog.CleanAllChatDialog;
import com.tianfangIMS.im.dialog.LoadDialog;
import com.tianfangIMS.im.dialog.SignOutDialog;
import com.tianfangIMS.im.service.FloatService;
import com.tianfangIMS.im.utils.CommonUtil;
import com.tianfangIMS.im.utils.NToast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by LianMengYu on 2017/1/7.
 */

public class Settings_Activity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "Settings_Activity";
    private RelativeLayout rl_newMessage, rl_resetPwd, rl_setting_signout;//新消息通知
    private Context mContext;
    private CompoundButton sw_sttings_notfaction;
    private ArrayList<TreeInfo> mTreeInfos;
    private RelativeLayout settting_clear_conversation;
    Intent mIntent;
    ArrayList<String> strList;

    ReentrantLock lock = new ReentrantLock();
    private List<TopFiveUserInfoBean> data = new ArrayList<TopFiveUserInfoBean>(5);
    private int sum = 5;
    private List<TopFiveUserInfoBean> resultdata = new ArrayList<TopFiveUserInfoBean>(5);
    private SharedPreferences.Editor editor;
    private SharedPreferences sp;
    String sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_fragment);
        sessionId = getSharedPreferences("CompanyCode", Activity.MODE_PRIVATE).getString("CompanyCode", "");
        mContext = this;
        setTitle("设置");
        init();
        getNew5Data();
        setCahce();
        final Intent intent = getIntent();
        sp = getSharedPreferences("config", Activity.MODE_PRIVATE);
        editor = sp.edit();
        boolean flag = sp.getBoolean("isOpen", false);
        sw_sttings_notfaction.setChecked(flag);
    }

    private void init() {
        rl_newMessage = (RelativeLayout) this.findViewById(R.id.rl_newMessage);
        rl_resetPwd = (RelativeLayout) this.findViewById(R.id.rl_resetPwd);
        rl_setting_signout = (RelativeLayout) this.findViewById(R.id.rl_setting_signout);
        sw_sttings_notfaction = (CompoundButton) this.findViewById(R.id.sw_sttings_notfaction);
        settting_clear_conversation = (RelativeLayout) this.findViewById(R.id.settting_clear_conversation);

        rl_newMessage.setOnClickListener(this);
        rl_resetPwd.setOnClickListener(this);
        rl_setting_signout.setOnClickListener(this);
        settting_clear_conversation.setOnClickListener(this);
        sw_sttings_notfaction.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    mTreeInfos = new ArrayList<>();
                    for (int i = 0; i < data.size(); i++) {
                        TreeInfo mInfo = new TreeInfo();
                        int ids = 0;
                        if (data.get(i).getId() != null) {
                            ids = Integer.parseInt(data.get(i).getId());
                        }
                        mInfo.setId(ids);
                        mInfo.setLogo(data.get(i).getLogo());
                        mInfo.setName(data.get(i).getName());
                        if (resultdata.get(i).getConversationType() == Conversation.ConversationType.PRIVATE) {
                            mInfo.setGroup(false);
                        } else if (resultdata.get(i).getConversationType() == Conversation.ConversationType.GROUP) {
                            mInfo.setGroup(true);
                        }
                        mTreeInfos.add(mInfo);
                    }
                    if (IsConversationNull() == false) {
                        NToast.shortToast(mContext, "会话列表为空，无法开启悬浮窗体");
                        sw_sttings_notfaction.setChecked(false);
                        return;
                    }
                    //开启悬浮窗前先请求权限
                    if ("Xiaomi".equals(Build.MANUFACTURER)) {//小米手机
                        //小米手机
                        requestPermission();
                    } else if ("Meizu".equals(Build.MANUFACTURER)) {//魅族手机
                        //魅族手机
                        requestPermission();
                    } else {//其他手机
                        if (Build.VERSION.SDK_INT >= 23) {
                            if (!Settings.canDrawOverlays(mContext)) {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                                startActivityForResult(intent, 12);
                            } else {
                                switchActivity();
                            }
                        } else {
                            switchActivity();
                        }
                    }
                } else {
                    mIntent = new Intent(Settings_Activity.this, FloatService.class);
                    stopService(mIntent);
                    editor.putBoolean("isOpen", false);
                    editor.apply();
                }

            }
        });
    }

    public void getNew5Data() {
        List<Conversation> list = RongIMClient.getInstance().getConversationList();
        if (list != null && list.size() > 0) {
            if (list.size() < 5) {
                for (int i = 0; i < list.size(); i++) {
                    resultdata.add(new TopFiveUserInfoBean(list.get(i).getConversationType(), list.get(i).getTargetId(), null, null));
                }

            }
            if (list.size() > 5 || list.size() == 5) {
                for (int i = 0; i < 5; i++) {
                    resultdata.add(new TopFiveUserInfoBean(list.get(i).getConversationType(), list.get(i).getTargetId(), null, null));
                }
            }
        }
    }

    private void setCahce() {
        for (int i = 0; i < resultdata.size(); i++) {
            if (resultdata.get(i).getConversationType() == Conversation.ConversationType.PRIVATE) {
                GetPrivate(resultdata.get(i).getId());
            } else if (resultdata.get(i).getConversationType() == Conversation.ConversationType.GROUP) {
                GetGroup(resultdata.get(i).getId());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_newMessage:
                startActivity(new Intent(this, NewMessageNotice_Activity.class));
                break;
            case R.id.rl_resetPwd:
                startActivity(new Intent(this, ResetPassword_Activity.class));
                break;
            case R.id.rl_setting_signout:
                SignOutDialog signoutdialog = new SignOutDialog(mContext, sessionId);
                signoutdialog.getWindow().setBackgroundDrawable(new ColorDrawable());
                signoutdialog.show();
                CommonUtil.SetCleanDialogStyle(signoutdialog);
                break;
            case R.id.settting_clear_conversation:
                CleanAllChatDialog dialog = new CleanAllChatDialog(mContext);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable());
                dialog.show();
                CommonUtil.SetCleanDialogStyle(dialog);
                break;
        }
    }

    private void GetPrivate(String id) {
        OkGo.post(ConstantValue.GETONEPERSONINFO)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
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
                                NToast.shortToast(mContext, "请重新登陆");
                                startActivity(new Intent(mContext, LoginActivity.class));
                                RongIM.getInstance().logout();
                                finish();
                            } else {
                                Gson gson = new Gson();
                                UserBean bean = gson.fromJson(s, UserBean.class);
                                String logo;
                                if (TextUtils.isEmpty(bean.getLogo())) {
                                    logo = ConstantValue.ImageFile + "defaultlogo.png";
                                } else {
                                    logo = ConstantValue.ImageFile + bean.getLogo();
                                }
                                if (!TextUtils.isEmpty(bean.getLogo())) {
                                    data.add(new TopFiveUserInfoBean(Conversation.ConversationType.PRIVATE, bean.getId(),
                                            bean.getName(), logo));
                                } else if (TextUtils.isEmpty(bean.getLogo())) {
                                    data.add(new TopFiveUserInfoBean(Conversation.ConversationType.PRIVATE, bean.getId(),
                                            bean.getName(), logo));
                                }
                            }
                        }
                    }
                });
    }

    private void GetGroup(final String id) {
        OkGo.post(ConstantValue.GETONEGROUPINFO)
                .tag(this)
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .params("groupid", id)
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
                                NToast.shortToast(mContext, "请重新登陆");
                                startActivity(new Intent(mContext, LoginActivity.class));
                                RongIM.getInstance().logout();
                                finish();
                            } else {
                                Gson gson = new Gson();
                                final Map<String, Object> map = gson.fromJson(s, new TypeToken<Map<String, Object>>() {
                                }.getType());
                                Object object = map.get("text");
                                final Map<String, String> map1 = (Map<String, String>) object;
                                String logo;
                                if (map1 != null) {
                                    if (TextUtils.isEmpty(map1.get("logo"))) {
                                        logo = ConstantValue.ImageFile + "defaultlogo.png";
                                    } else {
                                        logo = ConstantValue.ImageFile + map1.get("logo");
                                    }
                                    data.add(new TopFiveUserInfoBean(Conversation.ConversationType.GROUP,
                                            id,
                                            map1.get("name"),
                                            logo));
                                } else {
                                    return;
                                }
                            }
                        }
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11) {
            if (isFloatWindowOpAllowed(this)) {//已经开启
//                switchActivity();

                NToast.shortToast(this, "授权成功，请开启悬浮窗体");
                sw_sttings_notfaction.setChecked(false);
                editor.putBoolean("isOpen", false);
                editor.apply();
            } else {
                sw_sttings_notfaction.setChecked(false);
                editor.putBoolean("isOpen", false);
                editor.apply();
                NToast.shortToast(this, "开启悬浮窗失败");
            }
        } else if (requestCode == 12) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(this)) {
                    sw_sttings_notfaction.setChecked(false);
                    editor.putBoolean("isOpen", false);
                    editor.apply();
                    NToast.shortToast(this, "权限授予失败,无法开启悬浮窗");
                } else {
//                    switchActivity();
                    NToast.shortToast(this, "授权成功，请开启悬浮窗体");
                    sw_sttings_notfaction.setChecked(false);
                    editor.putBoolean("isOpen", false);
                    editor.apply();
                }
            }
        }

    }

    /**
     * 判断悬浮窗权限
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean isFloatWindowOpAllowed(Context context) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            return checkOp(context, 24);  // AppOpsManager.OP_SYSTEM_ALERT_WINDOW
        } else {
            if ((context.getApplicationInfo().flags & 1 << 27) == 1 << 27) {
                return true;
            } else {
                return false;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean checkOp(Context context, int op) {
        final int version = Build.VERSION.SDK_INT;

        if (version >= 19) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Class<?> spClazz = Class.forName(manager.getClass().getName());
                Method method = manager.getClass().getDeclaredMethod("checkOp", int.class, int.class, String.class);
                int property = (Integer) method.invoke(manager, op,
                        Binder.getCallingUid(), context.getPackageName());
                Log.e("399", " property: " + property);

                if (AppOpsManager.MODE_ALLOWED == property) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
        }
        return false;
    }

    /**
     * 跳转Activity
     */
    private void switchActivity() {
        mIntent = new Intent(Settings_Activity.this, FloatService.class);
        mIntent.putExtra("data", mTreeInfos);
        startService(mIntent);
        editor.putBoolean("isOpen", true);
        editor.apply();
    }


    //判断会话列表是否为空
    private boolean IsConversationNull() {
        List<Conversation> list = RongIMClient.getInstance().getConversationList();
        if (list != null && list.size() > 0) {
            return true;//不为空返回true
        } else {
            return false;//不为空返回false
        }
    }

    /**
     * 请求用户给予悬浮窗的权限
     */
    public void requestPermission() {
        if (isFloatWindowOpAllowed(this)) {//已经开启
            switchActivity();
        } else {
            openSetting();
        }
    }


    /**
     * 打开权限设置界面
     */
    public void openSetting() {
        try {
            Intent localIntent = new Intent(
                    "miui.intent.action.APP_PERM_EDITOR");
            localIntent.setClassName("com.miui.securitycenter",
                    "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
            localIntent.putExtra("extra_pkgname", getPackageName());
            startActivityForResult(localIntent, 11);
        } catch (ActivityNotFoundException localActivityNotFoundException) {
            Intent intent1 = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent1.setData(uri);
            startActivityForResult(intent1, 11);
        }

    }

}
