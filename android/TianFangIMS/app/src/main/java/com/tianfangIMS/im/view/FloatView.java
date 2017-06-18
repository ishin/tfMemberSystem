package com.tianfangIMS.im.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tianfangIMS.im.R;


/**
 * Created by Titan on 2017/2/19.
 */

public class FloatView extends RelativeLayout {

    public CustomView mCustomView;
    public ImageView btn;

    Context mContext;

    public FloatView(Context context) {
        this(context, null);
    }

    public FloatView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.view_float, this);
        mCustomView = (CustomView) findViewById(R.id.view_float_cv);
        btn = (ImageView) findViewById(R.id.view_float_iv);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int add = (mContext.getResources().getDisplayMetrics().widthPixels - mCustomView.getMeasuredWidth()) + (mCustomView.getMeasuredWidth() / 2);
//        int left = add;
//        int top = (getMeasuredHeight() - mCustomView.getMeasuredHeight()) / 2;
//        int right = add + mCustomView.getMeasuredWidth();
//        int bottom = top + mCustomView.getMeasuredHeight();
//        int left = add;
//        int top = 0;
//        int right = add + mCustomView.getMeasuredWidth();
//        int bottom = top + mCustomView.getMeasuredHeight();
//        mCustomView.layout(left, top, right, bottom);
    }
}
