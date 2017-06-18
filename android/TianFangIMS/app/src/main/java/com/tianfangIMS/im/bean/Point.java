package com.tianfangIMS.im.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Titan on 2017/2/14.
 */

public class Point implements Serializable {

    int startY, endY;
    List<ChildPoint> mChildPoints;

    public Point(int startY, int endY, List<ChildPoint> childPoints) {
        this.startY = startY;
        this.endY = endY;
        this.mChildPoints = childPoints;
//        Log.d("Point", startY + " / " + endY);
//        for (ChildPoint point : mChildPoints) {
//            Log.d("Point", point.toString());
//        }
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getEndY() {
        return endY;
    }

    public void setEndY(int endY) {
        this.endY = endY;
    }

    public List<ChildPoint> getChildPoints() {
        return mChildPoints;
    }

    public void setChildPoints(List<ChildPoint> childPoints) {
        mChildPoints = childPoints;
    }
}
