package com.tianfangIMS.im.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by LianMengYu on 2017/3/11.
 */

public class SearchAllLisBean implements Serializable {
    private List<SearchAllBean> privatechat;
    private List<SearchAllBean> groupchat;

    public SearchAllLisBean(List<SearchAllBean> privatechat, List<SearchAllBean> groupchat) {
        this.privatechat = privatechat;
        this.groupchat = groupchat;
    }

    public List<SearchAllBean> getPrivatechat() {
        return privatechat;
    }

    public void setPrivatechat(List<SearchAllBean> privatechat) {
        this.privatechat = privatechat;
    }

    public List<SearchAllBean> getGroupchat() {
        return groupchat;
    }

    public void setGroupchat(List<SearchAllBean> groupchat) {
        this.groupchat = groupchat;
    }

    @Override
    public String toString() {
        return "SearchAllLisBean{" +
                "privatechat=" + privatechat +
                ", groupchat=" + groupchat +
                '}';
    }
}
