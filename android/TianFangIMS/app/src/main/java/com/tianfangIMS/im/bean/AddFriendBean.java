package com.tianfangIMS.im.bean;

import java.io.Serializable;

/**
 * Created by LianMengYu on 2017/1/23.
 */

public class AddFriendBean implements Serializable {
    private String id;
    private String account;
    private String name;
    private String logo;
    private String telephone;
    private String email;
    private String address;
    private String birthday;
    private String workno;
    private String mobile;
    private String groupmax;
    private String groupuse;
    private String intro;
    private String branchname;
    private String positionname;
    private String organname;
    private String sex;
    private boolean isChecked;

    public AddFriendBean(String account, String address, String birthday, String branchname, String email, String groupmax, String groupuse, String id, String intro, String logo, String mobile, String name, String organname, String positionname, String sex, String telephone, String workno,boolean isChecked) {
        this.account = account;
        this.address = address;
        this.birthday = birthday;
        this.branchname = branchname;
        this.email = email;
        this.groupmax = groupmax;
        this.groupuse = groupuse;
        this.id = id;
        this.intro = intro;
        this.logo = logo;
        this.mobile = mobile;
        this.name = name;
        this.organname = organname;
        this.positionname = positionname;
        this.sex = sex;
        this.telephone = telephone;
        this.workno = workno;
        this.isChecked = isChecked;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getBranchname() {
        return branchname;
    }

    public void setBranchname(String branchname) {
        this.branchname = branchname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganname() {
        return organname;
    }

    public void setOrganname(String organname) {
        this.organname = organname;
    }

    public String getPositionname() {
        return positionname;
    }

    public void setPositionname(String positionname) {
        this.positionname = positionname;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getWorkno() {
        return workno;
    }

    public void setWorkno(String workno) {
        this.workno = workno;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public String toString() {
        return "AddFriendBean{" +
                "account='" + account + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", logo='" + logo + '\'' +
                ", telephone='" + telephone + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", birthday='" + birthday + '\'' +
                ", workno='" + workno + '\'' +
                ", mobile='" + mobile + '\'' +
                ", groupmax='" + groupmax + '\'' +
                ", groupuse='" + groupuse + '\'' +
                ", intro='" + intro + '\'' +
                ", branchname='" + branchname + '\'' +
                ", positionname='" + positionname + '\'' +
                ", organname='" + organname + '\'' +
                ", sex='" + sex + '\'' +
                '}';
    }
}
