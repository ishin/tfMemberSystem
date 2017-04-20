package com.tianfangIMS.im.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianfangIMS.im.bean.ChildPoint;
import com.tianfangIMS.im.bean.Point;
import com.tianfangIMS.im.bean.TreeInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Titan on 2017/2/13.
 */

public class CustomChildLinearLayout extends LinearLayout {

    int left, top, right, bottom, width, height;
    int startY, endY;
    Point mPoint;
    TreeInfo mInfo;
    int pid = -1;
    Map<Integer, Point> mPointMap;
    List<ChildPoint> mChildPoints;
    ChildPoint mChildPoint;

    Map<Integer, ChildPoint> mMap;

    List<Map<Integer, Point>> mMapList;

    public CustomChildLinearLayout(Context context) {
        super(context);
    }

    public CustomChildLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomChildLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void receive(Map<Integer, ChildPoint> mMap, List<Map<Integer, Point>> mMapList) {
        this.mMap = mMap;
        this.mMapList = mMapList;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        mPointMap = new HashMap<>();
        int itemHeight = getChildAt(0).getMeasuredHeight();
        int emptyHeight = (getHeight() - itemHeight * childCount) / (childCount + 1);
        pid = -1;
        for (int i = 0; i < childCount; i++) {
            TextView view = (TextView) getChildAt(i);
            width = view.getMeasuredWidth();
            height = view.getMeasuredHeight();
            right = getRight();
            left = right - width;
            bottom = view.getBottom();
            top = bottom - height;
            int selfTop = (i + 1) * emptyHeight + i * itemHeight;
            view.layout(view.getLeft(), selfTop, view.getMeasuredWidth(), selfTop + view.getMeasuredHeight());
//            view.postInvalidate();
            top = selfTop;
            bottom = selfTop + height;
            mInfo = (TreeInfo) view.getTag();
            //用于存储控件右侧的连接点(连线起点)
            mMap.put(mInfo.getId(), new ChildPoint(getLeft() + width, bottom - height / 2));
            if (mInfo.getPid() != pid) {
                if (mChildPoints != null) {
                    mPoint = new Point(startY, endY, mChildPoints);
                    mPointMap.put(pid, mPoint);
                    mMapList.add(mPointMap);
                    mChildPoints = null;
                }
                pid = mInfo.getPid();
                startY = top;
                endY = bottom;
                mChildPoints = new ArrayList<>();
                mChildPoint = new ChildPoint(left, bottom - height / 2);
                mChildPoints.add(mChildPoint);
            } else {
                endY = bottom;
                mChildPoint = new ChildPoint(left, bottom - height / 2);
                mChildPoints.add(mChildPoint);
            }
        }
        mPoint = new Point(startY, endY, mChildPoints);
        mPointMap.put(pid, mPoint);
        mMapList.add(mPointMap);
        mChildPoints = null;
//        super.onLayout(changed, l, t, r, b);
//        super.onLayout(changed, l, t, r, b);
//        for (int i = 0; i < childCount; i++) {
//            View view = getChildAt(i);
//            width = view.getMeasuredWidth();
//            height = view.getMeasuredHeight();
//            right = getRight();
//            left = right - width;
//            bottom = view.getBottom();
//            top = bottom - height;
//            int selfTop = (i + 1) * emptyHeight + i * itemHeight;
//            view.layout(view.getLeft(), selfTop, right, selfTop + height);
//            view.postInvalidate();
//        }
    }
}
