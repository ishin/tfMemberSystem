package com.tianfangIMS.im.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by LianMengYu on 2017/2/13.
 * 包含list的联系人bean
 */

public class TopContactsListBean implements Serializable {

    private String code;
    private List<AddFriendTwoBean> text;

    public TopContactsListBean(String code, List<AddFriendTwoBean> text) {
        this.code = code;
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<AddFriendTwoBean> getText() {
        return text;
    }

    public void setText(List<AddFriendTwoBean> text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "TopContactsListBean{" +
                "code='" + code + '\'' +
                ", text=" + text +
                '}';
    }
}
