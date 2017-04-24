package com.tianfangIMS.im.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tianfangIMS.im.R;
import com.tianfangIMS.im.bean.TopFiveUserInfoBean;
import com.tianfangIMS.im.bean.TreeInfo;
import com.tianfangIMS.im.utils.NToast;
import com.tianfangIMS.im.view.FloatView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;

/**
 * Created by Titan on 2017/2/19.
 */

public class FloatService extends Service {

    private static final String TAG = "FloatService";

    WindowManager mWindowManager;
    FloatView mFloatView;

    List<TreeInfo> mTreeInfos;

    WindowManager.LayoutParams wl;
    private List<TopFiveUserInfoBean> data = new ArrayList<TopFiveUserInfoBean>(5);
    private int sum = 5;
    ReentrantLock lock = new ReentrantLock();
    UserInfo userinfo;
    TopFiveUserInfoBean floatbean;
    private String PrivateChatLogo;
    private String GroupLogo;
    TreeInfo mInfo;
    private Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        RongIM.setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageListener() {
            @Override
            public boolean onReceived(Message message, int i) {
                add5Date(new TopFiveUserInfoBean(message.getConversationType(), message.getTargetId(), null, null));
                return false;
            }
        });
        if (mFloatView == null) {
            final int density = (int) getResources().getDisplayMetrics().density;
            if (mTreeInfos == null) {
                try {
                    mTreeInfos = (List<TreeInfo>) intent.getSerializableExtra("data");
                    Log.e("settingResultData：", "打印接受值的数量----:" + mTreeInfos.size());
                } catch (NullPointerException e) {
                    Toast.makeText(this, "服务异常,无法启动", Toast.LENGTH_SHORT).show();
                }
            }
            mWindowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
            mFloatView = new FloatView(getApplicationContext());
            mFloatView.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mFloatView.mCustomView.getVisibility() == View.GONE) {
                        mFloatView.mCustomView.setVisibility(View.VISIBLE);
                        wl.width = WindowManager.LayoutParams.WRAP_CONTENT;
                        wl.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        wl.gravity = Gravity.TOP | Gravity.LEFT;
                        wl.dimAmount = 0.3f;
                        wl.x = getResources().getDisplayMetrics().widthPixels - 300 * density / 2;
                        wl.y = (getResources().getDisplayMetrics().heightPixels - 300 * density) / 2;
                        wl.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_DIM_BEHIND | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(50 * density, 50 * density);
                        lp.addRule(RelativeLayout.CENTER_VERTICAL);
                        lp.leftMargin = 150 * density - 70 * density;
                        mFloatView.btn.setLayoutParams(lp);
                    } else {
                        mFloatView.mCustomView.setVisibility(View.GONE);
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(50 * density, 50 * density);
                        lp.rightMargin = 25 * density;
                        mFloatView.btn.setLayoutParams(lp);
                        wl.width = WindowManager.LayoutParams.WRAP_CONTENT;
                        wl.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        wl.gravity = Gravity.TOP | Gravity.LEFT;
                        wl.dimAmount = 0.3f;
                        wl.x = getResources().getDisplayMetrics().widthPixels - 75 * density / 2;
                        wl.y = (getResources().getDisplayMetrics().heightPixels - 100 * density) / 2;
                        wl.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_DIM_BEHIND | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
                        wl.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//                        mFloatView.setBackgroundColor(Color.TRANSPARENT);
                    }
                    mWindowManager.updateViewLayout(mFloatView, wl);
                }
            });
            mFloatView.btn.setImageResource(R.mipmap.icon_float);
            if (mTreeInfos != null && mTreeInfos.size() > 0) {
                for (int i = 0; i < mTreeInfos.size(); i++) {
                    ImageView mImageView = new ImageView(this);
                    String logo = mTreeInfos.get(i).getLogo();
                    mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(50 * density, 50 * density);
                    mImageView.setLayoutParams(lp);
                    mImageView.setId(View.NO_ID);
                    Log.e("settingResultData", "----:" + mTreeInfos.get(i).getLogo());
//                    Picasso.with(FloatService.this)
//                            .load(logo)
//                            .resize(50, 50)
//                            .placeholder(R.mipmap.default_portrait)
//                            .config(Bitmap.Config.ARGB_8888)
//                            .error(R.mipmap.default_portrait)
//                            .into(mImageView);
                    Glide.with(FloatService.this)
                            .load(logo)
                            .placeholder(R.mipmap.default_portrait)
                            .dontAnimate()
                            .fallback(R.mipmap.default_portrait)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .error(R.mipmap.default_portrait)
                            .into(mImageView);
                    mImageView.setTag(mImageView.getId(), mTreeInfos.get(i));
                    mImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mInfo = (TreeInfo) v.getTag(v.getId());
                            Log.e("Float：", "------:" + mInfo.getId() + "---IsGroup:" + mInfo.isGroup());
                            if (mInfo.getId() != 0) {
                                if (mInfo.isGroup()) {
                                    RongIM.getInstance().startGroupChat(FloatService.this, mInfo.getId() + "", mInfo.getName());
                                } else {
                                    RongIM.getInstance().startPrivateChat(FloatService.this, mInfo.getId() + "", mInfo.getName());
                                }

                            } else {
                                NToast.shortToast(FloatService.this, "没有找到用户");
                            }
                        }
                    });
                    mImageView.setOnLongClickListener(new View.OnLongClickListener() {
                        //TODO
                        @Override
                        public boolean onLongClick(View v) {
                            return false;
                        }
                    });
                    if (mTreeInfos != null && mTreeInfos.size() > 0) {
                        mFloatView.mCustomView.addView(mImageView);
                    }
                }
            }
            wl = new WindowManager.LayoutParams();
            wl.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            wl.format = PixelFormat.RGBA_8888;
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(50 * density, 50 * density);
            lp.rightMargin = 25 * density;
            mFloatView.btn.setLayoutParams(lp);
            wl.width = WindowManager.LayoutParams.WRAP_CONTENT;
            wl.height = WindowManager.LayoutParams.WRAP_CONTENT;
            wl.gravity = Gravity.TOP | Gravity.LEFT;
            wl.dimAmount = 0.3f;
            wl.x = getResources().getDisplayMetrics().widthPixels - 75 * density / 2;
            wl.y = (getResources().getDisplayMetrics().heightPixels - 100 * density) / 2;
            wl.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_DIM_BEHIND | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
            wl.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            if (mTreeInfos != null && mTreeInfos.size() > 0) {
                mWindowManager.addView(mFloatView, wl);
            }
            Log.d(TAG, "添加完成");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void add5Date(TopFiveUserInfoBean da) {
        lock.lock();
        try {
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getId().equals(da.getId())) {
                    data.remove(i);
                }
            }
            //执行某些操作
            if (data.size() < sum) {
                data.add(da);
            }
            if (data.size() >= sum) {
                data.remove(0);
                data.add(da);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTreeInfos != null && mTreeInfos.size() > 0) {
            mWindowManager.removeView(mFloatView);
        }
//        if (mFloatView != null) {
//            mWindowManager.removeView(mFloatView);
//        }
    }
}
