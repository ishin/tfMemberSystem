package com.tianfangIMS.im.bean;

import java.io.Serializable;

/**
 * Created by LianMengYu on 2017/2/28.
 */

public class SearchGroupBean implements Serializable {
    private String name;
    private String id;
    private String position;
    private String logo;
    private String account;

    public SearchGroupBean(String name, String id, String position, String logo, String account) {
        this.name = name;
        this.id = id;
        this.position = position;
        this.logo = logo;
        this.account = account;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "SearchGroupBean{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", position='" + position + '\'' +
                ", logo='" + logo + '\'' +
                ", account='" + account + '\'' +
                '}';
    }
}
