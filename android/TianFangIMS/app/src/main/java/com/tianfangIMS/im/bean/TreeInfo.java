package com.tianfangIMS.im.bean;

import java.io.Serializable;

/**
 * Created by Titan on 2017/2/5.
 */

public class TreeInfo implements Serializable {

    /**
     * 部门 or 个人
     */
    private int flag;
    /**
     * 所属上级
     */
    private Integer pid;
    private Integer id;
    private String account;
    private String name;
    private String logo;
    private String telephone;
    private String email;
    private String address;
    private String token;
    private String sex;
    private String birthday;
    private String workno;
    private String mobile;
    private String groupmax;
    private String groupuse;
    private String intro;
    private String postitionid;
    private String postitionname;
    private String sexid;
    private String sexname;
    private boolean isGroup;//判断是否为群组
    private boolean isChecked;

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getWorkno() {
        return workno;
    }

    public void setWorkno(String workno) {
        this.workno = workno;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getGroupmax() {
        return groupmax;
    }

    public void setGroupmax(String groupmax) {
        this.groupmax = groupmax;
    }

    public String getGroupuse() {
        return groupuse;
    }

    public void setGroupuse(String groupuse) {
        this.groupuse = groupuse;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getPostitionid() {
        return postitionid;
    }

    public void setPostitionid(String postitionid) {
        this.postitionid = postitionid;
    }

    public String getPostitionname() {
        return postitionname;
    }

    public void setPostitionname(String postitionname) {
        this.postitionname = postitionname;
    }

    public String getSexid() {
        return sexid;
    }

    public void setSexid(String sexid) {
        this.sexid = sexid;
    }

    public String getSexname() {
        return sexname;
    }

    public void setSexname(String sexname) {
        this.sexname = sexname;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
