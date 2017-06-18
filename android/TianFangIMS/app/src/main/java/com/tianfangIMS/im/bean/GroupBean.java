package com.tianfangIMS.im.bean;

import java.io.Serializable;

/**
 * Created by LianMengYu on 2017/2/5.
 */

public class GroupBean implements Serializable {

    private String mid;
    private String account;
    private String fullname;
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
    private String GID;
    private String code;
    private String name;
    private String createdate;
    private String volume;
    private String volumeuse;
    private String space;
    private String spaceuse;
    private String annexlong;
    private String notice;
    private String dontdistrub;
    private String id;

    public GroupBean(String account, String address, String annexlong, String birthday, String code, String createdate, String dontdistrub, String email, String fullname, String GID, String groupmax, String groupuse, String logo, String mid, String mobile, String name, String notice, String sex, String space, String spaceuse, String telephone, String token, String volume, String volumeuse, String workno,String id) {
        this.account = account;
        this.address = address;
        this.annexlong = annexlong;
        this.birthday = birthday;
        this.code = code;
        this.createdate = createdate;
        this.dontdistrub = dontdistrub;
        this.email = email;
        this.fullname = fullname;
        this.GID = GID;
        this.groupmax = groupmax;
        this.groupuse = groupuse;
        this.logo = logo;
        this.mid = mid;
        this.mobile = mobile;
        this.name = name;
        this.notice = notice;
        this.sex = sex;
        this.space = space;
        this.spaceuse = spaceuse;
        this.telephone = telephone;
        this.token = token;
        this.volume = volume;
        this.volumeuse = volumeuse;
        this.workno = workno;
        this.id = id;
    }

    public GroupBean() {
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

    public String getAnnexlong() {
        return annexlong;
    }

    public void setAnnexlong(String annexlong) {
        this.annexlong = annexlong;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCreatedate() {
        return createdate;
    }

    public void setCreatedate(String createdate) {
        this.createdate = createdate;
    }

    public String getDontdistrub() {
        return dontdistrub;
    }

    public void setDontdistrub(String dontdistrub) {
        this.dontdistrub = dontdistrub;
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

    public String getGID() {
        return GID;
    }

    public void setGID(String GID) {
        this.GID = GID;
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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
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

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSpace() {
        return space;
    }

    public void setSpace(String space) {
        this.space = space;
    }

    public String getSpaceuse() {
        return spaceuse;
    }

    public void setSpaceuse(String spaceuse) {
        this.spaceuse = spaceuse;
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

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getVolumeuse() {
        return volumeuse;
    }

    public void setVolumeuse(String volumeuse) {
        this.volumeuse = volumeuse;
    }

    public String getWorkno() {
        return workno;
    }

    public void setWorkno(String workno) {
        this.workno = workno;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "GroupBean{" +
                "account='" + account + '\'' +
                ", mid='" + mid + '\'' +
                ", fullname='" + fullname + '\'' +
                ", logo='" + logo + '\'' +
                ", telephone='" + telephone + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", token='" + token + '\'' +
                ", sex='" + sex + '\'' +
                ", birthday='" + birthday + '\'' +
                ", workno='" + workno + '\'' +
                ", mobile='" + mobile + '\'' +
                ", groupmax='" + groupmax + '\'' +
                ", groupuse='" + groupuse + '\'' +
                ", GID='" + GID + '\'' +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", createdate='" + createdate + '\'' +
                ", volume='" + volume + '\'' +
                ", volumeuse='" + volumeuse + '\'' +
                ", space='" + space + '\'' +
                ", spaceuse='" + spaceuse + '\'' +
                ", annexlong='" + annexlong + '\'' +
                ", notice='" + notice + '\'' +
                ", dontdistrub='" + dontdistrub + '\'' +
                '}';
    }
}
