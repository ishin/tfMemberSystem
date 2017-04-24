package com.tianfangIMS.im.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.BitmapCallback;
import com.tianfangIMS.im.ConstantValue;
import com.tianfangIMS.im.TianFangIMSApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by LianMengYu on 2017/1/6.
 */

public class CommonUtil {

    public static final String path = File.separator + File.separator + "sdcard" + File.separator + "TianFangIMS";
    public static final String PathIMGES = File.separator;

    public static void FilePath() {

        File file = new File(path);
        if (!file.exists()) {
            try {
                file.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        File fileImage = new File(path + File.separator + "Images");
        if (!fileImage.exists()) {
            try {
                fileImage.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // dialog改变大小方法
    public static void changeDialogUI(Dialog dialog, WindowManager m, double h, double w) {
        Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
        WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); // 获取对话框当前的参数值
        p.height = (int) h; //
        p.width = (int) w; //
        dialog.getWindow().setAttributes(
                (android.view.WindowManager.LayoutParams) p); // 设置生效
    }

    // 改变dialog位置
    public static void changeDialogPosition(Dialog dialog, int gravity, int xOffset, int yOffset) {
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = gravity;
        wlp.x = xOffset;
        wlp.y = yOffset;
        // wlp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
    }

    public static Bitmap GetImage(final Context context, final String filename) {
        Bitmap mbitmap = null;
        OkGo.post(ConstantValue.ImageFile + filename)
                .tag(context)
                .cacheKey("imagecache")
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)
                .execute(new BitmapCallback() {
                    @Override
                    public void onSuccess(Bitmap bitmap, Call call, Response response) {
                        Bitmap mbitmap = bitmap;
                    }
                });
        return mbitmap;

    }

    public static Bitmap GetImages(final Context context, final String filename, final ImageView view) {
        Bitmap mbitmap = null;
        OkGo.post(ConstantValue.ImageFile + filename)
                .tag(context)
                .cacheKey("imagecache")
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)
                .execute(new BitmapCallback() {
                    @Override
                    public void onSuccess(Bitmap bitmap, Call call, Response response) {
                        view.setImageBitmap(bitmap);
                    }
                });
        return mbitmap;
    }

    public static Bitmap getImageBitmap(Context context, final String filename, final ImageView view) {
        Bitmap mbitmap = null;
        OkGo.post(filename)
                .tag(context)
                .cacheKey("imagecache")
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)
                .execute(new BitmapCallback() {
                    @Override
                    public void onSuccess(Bitmap bitmap, Call call, Response response) {
                        view.setImageBitmap(bitmap);
                    }
                });
        return mbitmap;
    }

    /**
     * @Description:把list转换为一个用逗号分隔的字符串
     */
    public static String listToString(List list) {
        StringBuilder sb = new StringBuilder();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (i < list.size() - 1) {
                    sb.append(list.get(i) + ",");
                } else {
                    sb.append(list.get(i));
                }
            }
        }
        return sb.toString();
    }

    public static String getUserInfo(Context mContext) {
        SharedPreferences pref = mContext.getSharedPreferences(
                "user_login", 0);
        return pref.getString("user_login", "");
    }

    public static boolean saveUserInfo(Context mContext, String info) {
        SharedPreferences pref = mContext.getSharedPreferences(
                "user_login", 0);
        return pref.edit().putString("user_login", info).commit();
    }

    public static boolean saveFrientUserInfo(Context mContext, String info) {
        SharedPreferences pref = mContext.getSharedPreferences(
                "friend_info", 0);
        return pref.edit().putString("friend_info", info).commit();
    }

    public static String getFrientUserInfo(Context mContext) {
        SharedPreferences pref = mContext.getSharedPreferences(
                "friend_info", 0);
        return pref.getString("friend_info", "");
    }

    public static boolean saveGroupUserInfo(Context mContext, String info) {
        SharedPreferences pref = mContext.getSharedPreferences(
                "group_info", Activity.MODE_PRIVATE);
        return pref.edit().putString("group_info", info).commit();
    }

    public static String getGroupUserInfo(Context mContext) {
        SharedPreferences pref = mContext.getSharedPreferences(
                "group_info", Activity.MODE_PRIVATE);
        return pref.getString("group_info", "");
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * md5加密
     */
    public static String md5(Object object) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(toByteArray(object));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    public static byte[] toByteArray(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
            oos.close();
            bos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bytes;
    }


    /**
     * 获取存储路径
     */
    public static String getDataPath() {
        String path;
        if (isExistSDcard())
            path = Environment.getExternalStorageDirectory().getPath() + "/albumSelect";
        else
            path = TianFangIMSApplication.getInstance().getFilesDir().getPath();
        if (!path.endsWith("/"))
            path = path + "/";
        return path;
    }


    /**
     * 检测SDcard是否存在
     *
     * @return
     */
    public static boolean isExistSDcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED))
            return true;
        else {
            return false;
        }
    }

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     *
     * @param context
     * @return
     */
    public static final boolean isOPen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

    public static final void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    public static final Double StringToDouble(String str) {
        return Double.parseDouble(str);
    }

    public static final void SetDialogStyle(Dialog dialog) {
        // 设置宽度为屏宽、靠近屏幕底部。
        Window window = dialog.getWindow();
        //设置dialog弹出的动画，从屏幕底部弹出     window.setWindowAnimations(R.style.take_photo_anim);
        //最重要的一句话，一定要加上！要不然怎么设置都不行!window.setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams wlp = window.getAttributes();
        Display d = window.getWindowManager().getDefaultDisplay();
        //获取屏幕宽
        wlp.width = (int) (d.getWidth());
        wlp.height = (int) (d.getWidth());
        //宽度按屏幕大小的百分比设置，这里我设置的是全屏显示
        wlp.gravity = Gravity.CENTER;
        if (wlp.gravity == Gravity.CENTER)
//                    wlp.y = 0;
            //如果是底部显示，则距离底部的距离是0
            window.setAttributes(wlp);
    }

    public static final void SetCleanDialogStyle(Dialog dialog) {
        // 设置宽度为屏宽、靠近屏幕底部。
        Window window = dialog.getWindow();
        //设置dialog弹出的动画，从屏幕底部弹出     window.setWindowAnimations(R.style.take_photo_anim);
        //最重要的一句话，一定要加上！要不然怎么设置都不行!window.setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams wlp = window.getAttributes();
        Display d = window.getWindowManager().getDefaultDisplay();
        //获取屏幕宽
        wlp.width = 700;
        //宽度按屏幕大小的百分比设置，这里我设置的是全屏显示
        wlp.gravity = Gravity.CENTER;
        if (wlp.gravity == Gravity.CENTER)
//                    wlp.y = 0;
            //如果是底部显示，则距离底部的距离是0
            window.setAttributes(wlp);
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
}
