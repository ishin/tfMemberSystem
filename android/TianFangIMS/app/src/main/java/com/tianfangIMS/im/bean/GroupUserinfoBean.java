package com.tianfangIMS.im.bean;

import java.io.Serializable;

/**
 * Created by LianMengYu on 2017/3/12.
 * 群组中成员的信息
 */

public class GroupUserinfoBean implements Serializable{
    private String account;
    private String address;
    private String allpinyin;
    private String birthday;
    private String createtokendate;
    private String email;
    private String fullname;
    private String groupmax;
    private String groupuse;
    private String id;
    private String intro;
    private String logo;
    private String mobile;
    private String organId;
    private String pinyin;
    private String sex;
    private String telephone;
    private String token;
    private String workno;
    private String qzqx;

    public GroupUserinfoBean(String account, String address, String allpinyin, String birthday, String createtokendate, String email, String fullname, String groupmax, String groupuse, String id, String intro, String logo, String mobile, String organId, String pinyin, String sex, String telephone, String token, String workno, String qzqx) {
        this.account = account;
        this.address = address;
        this.allpinyin = allpinyin;
        this.birthday = birthday;
        this.createtokendate = createtokendate;
        this.email = email;
        this.fullname = fullname;
        this.groupmax = groupmax;
        this.groupuse = groupuse;
        this.id = id;
        this.intro = intro;
        this.logo = logo;
        this.mobile = mobile;
        this.organId = organId;
        this.pinyin = pinyin;
        this.sex = sex;
        this.telephone = telephone;
        this.token = token;
        this.workno = workno;
        this.qzqx = qzqx;
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

    public String getAllpinyin() {
        return allpinyin;
    }

    public void setAllpinyin(String allpinyin) {
        this.allpinyin = allpinyin;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCreatetokendate() {
        return createtokendate;
    }

    public void setCreatetokendate(String createtokendate) {
        this.createtokendate = createtokendate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
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

    public String getOrganId() {
        return organId;
    }

    public void setOrganId(String organId) {
        this.organId = organId;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getWorkno() {
        return workno;
    }

    public void setWorkno(String workno) {
        this.workno = workno;
    }

    public String getQzqx() {
        return qzqx;
    }

    public void setQzqx(String qzqx) {
        this.qzqx = qzqx;
    }

    @Override
    public String toString() {
        return "GroupUserinfoBean{" +
                "account='" + account + '\'' +
                ", address='" + address + '\'' +
                ", allpinyin='" + allpinyin + '\'' +
                ", birthday='" + birthday + '\'' +
                ", createtokendate='" + createtokendate + '\'' +
                ", email='" + email + '\'' +
                ", fullname='" + fullname + '\'' +
                ", groupmax='" + groupmax + '\'' +
                ", groupuse='" + groupuse + '\'' +
                ", id='" + id + '\'' +
                ", intro='" + intro + '\'' +
                ", logo='" + logo + '\'' +
                ", mobile='" + mobile + '\'' +
                ", organId='" + organId + '\'' +
                ", pinyin='" + pinyin + '\'' +
                ", sex='" + sex + '\'' +
                ", telephone='" + telephone + '\'' +
                ", token='" + token + '\'' +
                ", workno='" + workno + '\'' +
                ", qzqx='" + qzqx + '\'' +
                '}';
    }
}
