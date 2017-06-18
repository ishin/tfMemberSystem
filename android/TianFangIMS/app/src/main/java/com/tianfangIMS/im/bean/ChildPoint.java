package com.tianfangIMS.im.bean;

import java.io.Serializable;

/**
 * Created by Titan on 2017/2/14.
 */

public class ChildPoint implements Serializable {

    int x, y;

    public ChildPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "End点位 : x轴 " + x + " ,Y轴 " + y;
    }
}
