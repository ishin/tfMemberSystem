package com.tianfangIMS.im.activity;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.tianfangIMS.im.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by LianMengYu on 2017/1/10.
 */

public class FloatViewBoll extends LinearLayout {

    private Context mContext;
    private WindowManager windowManager;
    private View floatView;
    public int viewWidth;
    public int viewHeight;
    private WindowManager.LayoutParams params;
    private float xInScreen;
    private float yInScreen;
    private float xInView;
    private float yInView;
    private int screenWidth;
    private int screenHeight;

    private static int historyX = 0;
    private static int historyY = 0;
    private static float historyAlpha = 1;

    private static String histotyOritation;
    private Runnable mHideHalfCallback;
    /**
     * 记录手指按下时在屏幕上的横坐标的值
     */
    private float xDownInScreen;

    /**
     * 记录手指按下时在屏幕上的纵坐标的值
     */
    private float yDownInScreen;
    /**
     * 状态栏的高度
     */
    private int statusBarHeight;

    private OnFloatViewClickListener onFloatViewClickListener;

    public FloatViewBoll(Context context) {
        this(context, null);
    }

    public FloatViewBoll(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void init() {
        viewWidth = dip2px(mContext, 55);
        viewHeight = dip2px(mContext, 55);
        windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        screenWidth = windowManager.getDefaultDisplay().getWidth();
        screenHeight = windowManager.getDefaultDisplay().getHeight();
        floatView = View.inflate(mContext, R.layout.float_layout, this);
        params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION;
        params.format = PixelFormat.TRANSLUCENT;
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        params.gravity = Gravity.RIGHT | Gravity.TOP;
        params.width = viewWidth;
        params.height = viewHeight;
        setViewAlpha(historyAlpha);
        if (historyX == 0 && historyY == 0) {
            params.x = 0;
            params.y = screenHeight / 3;
        } else {
            if (historyX > screenWidth - viewWidth / 2 || historyY > screenHeight - viewHeight / 2) {
                params.x = 0;
                params.y = screenHeight / 3;
                historyX = params.x;
                historyY = params.y;
            } else {
                params.x = historyX;
                params.y = historyY;
            }
        }
        windowManager.addView(this, params);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        screenWidth = windowManager.getDefaultDisplay().getWidth();
//        screenHeight = windowManager.getDefaultDisplay().getHeight();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setViewAlpha(1);
                removeCallbacks(mHideHalfCallback);
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY() - getStatusBarHeight();
                xInView = event.getX();
                yInView = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                updateViewPosition();

                break;
            case MotionEvent.ACTION_UP:
                //计算位置并靠边
                if (Math.abs(xDownInScreen - xInScreen) < 20 && Math.abs(yDownInScreen - yInScreen) < 20) {
                    //单击事件
                    if (null != onFloatViewClickListener) {
                        onFloatViewClickListener.onFloatViewClick();
                    }
                }
                stepAside();
                break;
        }

        return true;
    }

    private void stepAside() {
        int inWitchSide = 0;
        float viewCenterX = params.x + viewWidth / 2;
        float viewCenterY = params.y + viewHeight / 2;
//        screenWidth = windowManager.getDefaultDisplay().getWidth();
//        screenHeight = windowManager.getDefaultDisplay().getHeight();
        float dxLeft = viewCenterX;
        float dyUp = viewCenterY;
        float dxRight = screenWidth - dxLeft;
        float dyDown = screenHeight - dyUp;
        float result = getMin(dxLeft, dyUp, dxRight, dyDown);
        if (result == dxLeft) {
            params.x = 0;
            inWitchSide = 0;
        } else if (result == dxRight) {
            params.x = screenWidth - viewWidth;
            inWitchSide = 1;
        } else if (result == dyUp) {
            params.y = 0;
            inWitchSide = 2;
        } else {
            params.y = screenHeight - viewHeight - getStatusBarHeight();
            inWitchSide = 3;
        }
        windowManager.updateViewLayout(this, params);
        historyX = params.x;
        historyY = params.y;
        hideHalf(inWitchSide);
    }

    private void hideHalf(final int inWitchSide) {
        mHideHalfCallback = new HideHalfCallback(inWitchSide);
        postDelayed(mHideHalfCallback, 1000);
    }

    private void updateViewPosition() {
        params.x = (int) (xInScreen - xInView);
        params.y = (int) (yInScreen - yInView);
        if (params.x < 0) {
            params.x = 0;
        }
        if (params.y < 0) {
            params.y = 0;
        }
        if ((params.x + viewWidth) > screenWidth) {
            params.x = screenWidth - viewWidth;
        }
        if (params.y + viewHeight > screenHeight - getStatusBarHeight()) {
            params.y = screenHeight - getStatusBarHeight() - viewHeight;
        }
        windowManager.updateViewLayout(this, params);
        historyX = params.x;
        historyY = params.y;
    }

    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }

    private float getMin(float dxLeft, float dyUp, float dxRight, float dyDown) {
        float a = Math.min(dxLeft, dyUp);
        float b = Math.min(dxRight, dyDown);
        float c = Math.min(a, b);
        return c;
    }

    public int getBottomStatusHeight(Context context) {
        int totalHeight = getDpi(context);

        int contentHeight = getScreenHeight(context);

        return totalHeight - contentHeight;
    }

    //获取屏幕原始尺寸高度，包括虚拟功能键高度
    public int getDpi(Context context) {
        int dpi = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, displayMetrics);
            dpi = displayMetrics.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dpi;
    }

    public int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    public int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public void showView() {
        windowManager.addView(this, params);
    }

    public void setOnFloatViewClickListener(OnFloatViewClickListener onFloatViewClickListener) {
        this.onFloatViewClickListener = onFloatViewClickListener;
    }

    public interface OnFloatViewClickListener {
        void onFloatViewClick();
    }

    public void removeView() {
        try {
            windowManager.removeView(this);
        } catch (Exception e) {

        }
        removeCallbacks(mHideHalfCallback);
        mHideHalfCallback = null;
        windowManager = null;
        params = null;
    }

    public void setViewAlpha(float f) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setAlpha(f);
            historyAlpha = f;
        }
    }

    private class HideHalfCallback implements Runnable {
        int inWitchSide;

        public HideHalfCallback(int inWitchSide) {
            this.inWitchSide = inWitchSide;
        }

        @Override
        public void run() {
            if (null != params && null != windowManager) {
                if (inWitchSide == 0) {
                    params.x = -viewWidth / 2;
                } else if (inWitchSide == 1) {
                    params.x = screenWidth - viewWidth / 2;
                } else if (inWitchSide == 2) {
                    params.y = -viewHeight / 2;
                } else if (inWitchSide == 3) {
                    params.y = screenHeight - viewHeight / 2 - getStatusBarHeight();
                }
                windowManager.updateViewLayout(FloatViewBoll.this, params);
                setViewAlpha(0.5f);
                historyX = params.x;
                historyY = params.y;
            }
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);

    }
}
