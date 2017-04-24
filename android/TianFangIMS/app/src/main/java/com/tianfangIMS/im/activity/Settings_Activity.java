package com.tianfangIMS.im.activity;

import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_fragment);
        mContext = this;
        setTitle("设置");
        init();
        getNew5Data();
        setCahce();
        sp = getSharedPreferences("config", MODE_PRIVATE);
        editor = sp.edit();
        boolean flag = sp.getBoolean("isOpen", false);
        sw_sttings_notfaction.setChecked(flag);
        Log.e("Settings_Activity", "悬浮球初始状态：" + flag);
    }

    public static boolean getAppOps(Context context) {
        try {
            Object object = context.getSystemService("appops");
            if (object == null) {
                return false;
            }
            Class localClass = object.getClass();
            Class[] arrayOfClass = new Class[3];
            arrayOfClass[0] = Integer.TYPE;
            arrayOfClass[1] = Integer.TYPE;
            arrayOfClass[2] = String.class;
            Method method = localClass.getMethod("checkOp", arrayOfClass);
            if (method == null) {
                return false;
            }
            Object[] arrayOfObject1 = new Object[3];
            arrayOfObject1[0] = Integer.valueOf(24);
            arrayOfObject1[1] = Integer.valueOf(Binder.getCallingUid());
            arrayOfObject1[2] = context.getPackageName();
            int m = ((Integer) method.invoke(object, arrayOfObject1)).intValue();
            return m == AppOpsManager.MODE_ALLOWED;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
        builder.setTitle("提示");
        builder.setMessage("允许");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog simpledialog = builder.create();
        simpledialog.setCanceledOnTouchOutside(false);
        simpledialog.setCancelable(false);
        simpledialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        simpledialog.show();
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
//        if (Build.VERSION.SDK_INT >= 23) {
//            if (!Settings.canDrawOverlays(Settings_Activity.this)) {
//                NToast.longToast(mContext, "开启悬浮球权限");
//                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                        Uri.parse("package:" + getPackageName()));
//                startActivityForResult(intent, 10);
//            }
//        }
        sw_sttings_notfaction.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    editor.putBoolean("isOpen", true);
                    editor.apply();
                    mTreeInfos = new ArrayList<>();
                    for (int i = 0; i < data.size(); i++) {
                        Log.e("settingResultDataa：", "打印传递值的数量----:" + data.get(i));
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
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (!Settings.canDrawOverlays(Settings_Activity.this)) {
                            NToast.longToast(mContext, "请开启悬浮球权限");
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:" + getPackageName()));
                            startActivityForResult(intent, 10);
                        }else {
                            mIntent = new Intent(Settings_Activity.this, FloatService.class);
                            mIntent.putExtra("data", mTreeInfos);
                            startService(mIntent);
                        }
                    }else{
                        mIntent = new Intent(Settings_Activity.this, FloatService.class);
                        mIntent.putExtra("data", mTreeInfos);
                        startService(mIntent);
                    }
                } else {
                    editor.putBoolean("isOpen", false);
                    editor.apply();
                    mIntent = new Intent(Settings_Activity.this, FloatService.class);
//                    mIntent.putStringArrayListExtra("data",mTreeInfos);
                    stopService(mIntent);

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
                SignOutDialog signoutdialog = new SignOutDialog(mContext);
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
                            Gson gson = new Gson();
                            UserBean bean = gson.fromJson(s, UserBean.class);
                            if (!TextUtils.isEmpty(bean.getLogo())) {
                                data.add(new TopFiveUserInfoBean(Conversation.ConversationType.PRIVATE, bean.getId(),
                                        bean.getName(), ConstantValue.ImageFile + bean.getLogo()));
                            } else if (TextUtils.isEmpty(bean.getLogo())) {
                                data.add(new TopFiveUserInfoBean(Conversation.ConversationType.PRIVATE, bean.getId(),
                                        bean.getName(), ConstantValue.ImageFile + bean.getLogo()));
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
                            Gson gson = new Gson();
                            final Map<String, Object> map = gson.fromJson(s, new TypeToken<Map<String, Object>>() {
                            }.getType());
                            Object object = map.get("text");
                            final Map<String, String> map1 = (Map<String, String>) object;
                            if (!TextUtils.isEmpty(map1.get("logo"))) {
                                data.add(new TopFiveUserInfoBean(Conversation.ConversationType.GROUP,
                                        id,
                                        map1.get("name").toString(),
                                        ConstantValue.ImageFile + map1.get("logo")));
                            } else {
                                return;
                            }
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(Settings_Activity.this, "not granted", Toast.LENGTH_SHORT);
            }
        }
    }
}
