package com.tianfangIMS.im.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by LianMengYu on 2017/2/9.
 */

public class GroupListBean implements Serializable {
    private String code;
    private ArrayList<GroupBean> text = new ArrayList<GroupBean>();

    public GroupListBean(String code, ArrayList<GroupBean> text) {
        this.code = code;
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ArrayList<GroupBean> getText() {
        return text;
    }

    public void setText(ArrayList<GroupBean> text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "GroupListBean{" +
                "code='" + code + '\'' +
                ", text=" + text +
                '}';
    }
}
