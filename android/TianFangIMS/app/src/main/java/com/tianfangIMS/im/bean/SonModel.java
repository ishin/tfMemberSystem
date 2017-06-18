package com.tianfangIMS.im.bean;

/**
 * Created by liuzheng.
 * Date 17/2/5.
 * 这个是最低级别（员工）的实体类
 */

public class SonModel extends ParentModel{

    private String account;
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



    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
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

    @Override
    public String toString() {
        return "SonModel{" +
                ", account='" + account + '\'' +
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
