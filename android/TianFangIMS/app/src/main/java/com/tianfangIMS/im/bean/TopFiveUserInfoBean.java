package com.tianfangIMS.im.bean;

import java.io.Serializable;

import io.rong.imlib.model.Conversation;

/**
 * Created by LianMengYu on 2017/3/8.
 * 悬浮球bean
 */

public class TopFiveUserInfoBean implements Serializable {

    private Conversation.ConversationType conversationType;
    private String name;
    private String logo;
    private String id;

    public TopFiveUserInfoBean(Conversation.ConversationType conversationType, String id, String name, String logo) {
        this.conversationType = conversationType;
        this.id = id;
        this.name = name;
        this.logo = logo;
    }

    public Conversation.ConversationType getConversationType() {
        return conversationType;
    }

    public void setConversationType(Conversation.ConversationType conversationType) {
        this.conversationType = conversationType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "TopFiveUserInfoBean{" +
                "conversationType=" + conversationType +
                ", name='" + name + '\'' +
                ", logo='" + logo + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
