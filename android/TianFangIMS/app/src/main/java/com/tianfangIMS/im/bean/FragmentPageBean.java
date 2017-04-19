package com.tianfangIMS.im.bean;

import java.io.Serializable;

/**
 * Created by LianMengYu on 2017/1/5.
 */

public class FragmentPageBean implements Serializable {

    private String FName;

    private String FClass;//页面名

    private String FParams;//传入参数，json object结构

    public FragmentPageBean(String FClass, String FName, String FParams) {
        this.FClass = FClass;
        this.FName = FName;
        this.FParams = FParams;
    }

    @Override
    public String toString() {
        return "FragmentPageBean{" +
                "FClass='" + FClass + '\'' +
                ", FName='" + FName + '\'' +
                ", FParams='" + FParams + '\'' +
                '}';
    }

    public String getFClass() {
        return FClass;
    }

    public void setFClass(String FClass) {
        this.FClass = FClass;
    }

    public String getFParams() {
        return FParams;
    }

    public void setFParams(String FParams) {
        this.FParams = FParams;
    }

    public String getFName() {
        return FName;
    }

    public void setFName(String FName) {
        this.FName = FName;
    }
}
