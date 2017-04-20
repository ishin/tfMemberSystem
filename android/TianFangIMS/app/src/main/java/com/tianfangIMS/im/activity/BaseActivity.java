package com.tianfangIMS.im.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianfangIMS.im.R;

/**
 * Created by LianMengYu on 2016/12/29.
 * 所有Activity的基类
 */

public class BaseActivity extends FragmentActivity {
    protected Context mContext;
    private ImageButton btn_left_back;
    private TextView tv_title, tv_complete;
    private LinearLayout layout_head;
    private Drawable mBtnBackDrawable;
    private FrameLayout mContentView;
    private ImageButton iv_conversation_loaction;
    private ImageButton iv_conversation_contacts;
    private ImageView iv_talk_message, iv_talk_intercom, iv_talk_call;
    private LinearLayout ll_talk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.layout_base);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);// 使得音量键控制媒体声音
        SystemBarTranslucentType(this);
        mContext = this;
        init();
    }

    //    public abstract void initView();
    private void init() {
        btn_left_back = (ImageButton) super.findViewById(R.id.btn_left);
        tv_title = (TextView) super.findViewById(R.id.tv_title);
        layout_head = (LinearLayout) super.findViewById(R.id.layout_head);
        mContentView = (FrameLayout) super.findViewById(R.id.layout_container);
        iv_conversation_loaction = (ImageButton) super.findViewById(R.id.iv_conversation_location);
        iv_conversation_contacts = (ImageButton) super.findViewById(R.id.iv_conversation_contacts);
        //Viewpager滑动的tag
        iv_talk_message = (ImageView) super.findViewById(R.id.iv_talk_message);
        iv_talk_intercom = (ImageView) super.findViewById(R.id.iv_talk_intercom);
        iv_talk_call = (ImageView) super.findViewById(R.id.iv_talk_call);
        ll_talk = (LinearLayout) super.findViewById(R.id.ll_talk);
        tv_complete = (TextView) super.findViewById(R.id.tv_complete);
    }

    @Override
    public void setContentView(View view) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        mContentView.addView(view, lp);
    }

    @Override
    public void setContentView(int layoutResID) {
        View view = LayoutInflater.from(this).inflate(layoutResID, null);
        setContentView(view);

    }

    /**
     * 设置头部是否可见
     *
     * @param visibility
     */
    public void setHeadVisibility(int visibility) {
        layout_head.setVisibility(visibility);
    }


    /**
     * 设置左边是否可见
     *
     * @param visibility
     */
    public void setHeadLeftButtonVisibility(int visibility) {
        btn_left_back.setVisibility(visibility);
    }

    /**
     * 设置个人资料是否可见
     */
    public void setPersonContactButtonVisibility(int visibility) {
        iv_conversation_contacts.setVisibility(visibility);
    }

    /**
     * 设置位置Button是否可见
     */
    public void setLoactionButtonVisibility(int visibility) {
        iv_conversation_loaction.setVisibility(visibility);
    }

    public ImageButton getIv_conversation_loaction() {
        return iv_conversation_loaction;
    }

    public ImageButton getIv_conversation_contacts() {
        return iv_conversation_contacts;
    }


    /**
     * 设置Viewpager的Tag小标签是否可见
     */
    public void setViewPagerTagVisibiliy(int visibiliy) {
        ll_talk.setVisibility(visibiliy);
    }

    public LinearLayout getll_talk() {
        return ll_talk;
    }

    public ImageView getIv_talk_message() {
        return iv_talk_message;
    }

    public ImageView getIv_talk_intercom() {
        return iv_talk_intercom;
    }

    public ImageView getIv_talk_call() {
        return iv_talk_call;
    }

    public void setTv_completeVisibiliy(int visibiliy) {
        tv_complete.setVisibility(visibiliy);
    }

    public TextView getTv_title() {
        return tv_complete;
    }

//
//    /**
//     * 设置右边是否可见
//     *
//     * @param visibility
//     */
//    public void setHeadRightButtonVisibility(int visibility) {
//        mBtnRight.setVisibility(visibility);
//    }

    /**
     * 设置标题
     */
    public void setTitle(int titleId) {
        setTitle(getString(titleId), false);
    }

    /**
     * 设置标题
     */
    public void setTitle(int titleId, boolean flag) {
        setTitle(getString(titleId), flag);
    }

    /**
     * 设置标题
     */
    public void setTitle(String title) {
        setTitle(title, false);
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(String title, boolean flag) {
        tv_title.setText(title);
    }

    /**
     * 点击左按钮
     */
    public void onHeadLeftButtonClick(View v) {
        finish();
    }

    /**
     * 点击右按钮
     */
    public void onHeadRightButtonClick(View v) {

    }

    public ImageButton getHeadLeftButton() {
        return btn_left_back;
    }

    public void setHeadLeftButton(ImageButton leftButton) {
        this.btn_left_back = leftButton;
    }


    protected void onResume() {
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    //将Android状态栏改变为沉浸样式
    private void SystemBarTranslucentType(Activity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }


    }
}
