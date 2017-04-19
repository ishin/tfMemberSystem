package com.tianfangIMS.im.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.LinearLayout;


import com.tianfangIMS.im.bean.ChildPoint;
import com.tianfangIMS.im.bean.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Titan on 2017/2/13.
 */

public class CustomLinearLayout extends LinearLayout {

    String[] colorArr = new String[]{"#c8b6ee", "#9fb7e3", "#7be3d4", "#bde7d4", "#f4e09d", "#f7ba8a", "#865573", "#515e8a", "#87cbe2", "#d8d1b8", "#c5958a", "#f28186"};

    int margin;

    CustomChildLinearLayout mChildLinearLayout;

    Map<Integer, ChildPoint> mMap;

    List<Map<Integer, Point>> mMapList;

    Context mContext;

    public CustomLinearLayout(Context context) {
        this(context, null);
    }

    public CustomLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setWillNotDraw(false);
        margin = (int) (context.getResources().getDisplayMetrics().density * 32);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mMap = new HashMap<>();
        mMapList = new ArrayList<>();
//        super.onLayout(changed, l, t, r, b);
        int childCount = getChildCount();
        int[] widthCount = new int[childCount];
        int width = 0;
        for (int i = 0; i < childCount; i++) {
            width += (64 * mContext.getResources().getDisplayMetrics().density);
            widthCount[i] = width;
            mChildLinearLayout = (CustomChildLinearLayout) getChildAt(i);
            width += mChildLinearLayout.getMeasuredWidth();
        }
        for (int i = childCount - 1; i >= 0; i--) {
            mChildLinearLayout = (CustomChildLinearLayout) getChildAt(i);
            mChildLinearLayout.receive(mMap, mMapList);
            int startX = widthCount[i];
//            mChildLinearLayout.setBackgroundResource(R.drawable.tree_item_background);
//            mChildLinearLayout.setBackgroundColor(Color.parseColor(colorArr[(int) (Math.random() * 12)]));
            mChildLinearLayout.layout(startX, 0, startX + mChildLinearLayout.getMeasuredWidth(), mChildLinearLayout.getMeasuredHeight());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#cccccc"));
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(3);
        //首先循环垂直部门节点
        for (Map<Integer, Point> map : mMapList) {
            //循环部门节点的父节点ID
            for (Integer leftId : map.keySet()) {
                //得到左侧绘制点
                ChildPoint mLeftPoint = mMap.get(leftId);
                if (mLeftPoint != null) {
                    for (ChildPoint point : map.get(leftId).getChildPoints()) {
                        canvas.drawLine(mLeftPoint.getX(), mLeftPoint.getY(), point.getX(), point.getY(), mPaint);
                    }
                }
            }
        }
        super.onDraw(canvas);
    }
}
