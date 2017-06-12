package com.tianfangIMS.im.bean;

import java.io.Serializable;

/**
 * Created by LianMengYu on 2017/1/13.
 */

public class DepartmentAndPersonBean implements Serializable {

    private String flag;
    private String pid;
    private String account;
    private String id;
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

    public DepartmentAndPersonBean(String account, String address, String birthday, String email, String flag, String groupmax, String groupuse, String id, String intro, String logo, String mobile, String name, String pid, String postitionid, String postitionname, String sex, String sexid, String sexname, String telephone, String token, String workno) {
        this.account = account;
        this.address = address;
        this.birthday = birthday;
        this.email = email;
        this.flag = flag;
        this.groupmax = groupmax;
        this.groupuse = groupuse;
        this.id = id;
        this.intro = intro;
        this.logo = logo;
        this.mobile = mobile;
        this.name = name;
        this.pid = pid;
        this.postitionid = postitionid;
        this.postitionname = postitionname;
        this.sex = sex;
        this.sexid = sexid;
        this.sexname = sexname;
        this.telephone = telephone;
        this.token = token;
        this.workno = workno;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
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

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
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

    @Override
    public String toString() {
        return "DepartmentAndPersonBean{" +
                "account='" + account + '\'' +
                ", flag='" + flag + '\'' +
                ", pid='" + pid + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
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
                ", intro='" + intro + '\'' +
                ", postitionid='" + postitionid + '\'' +
                ", postitionname='" + postitionname + '\'' +
                ", sexid='" + sexid + '\'' +
                ", sexname='" + sexname + '\'' +
                '}';
    }
}
