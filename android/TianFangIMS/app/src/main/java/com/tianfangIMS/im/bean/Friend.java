package com.tianfangIMS.im.bean;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by LianMengYu on 2017/2/18.
 * 使用Xutils创建数据库实体类
 */

@Table(name="Person")
public class Friend {
    @Column(name = "account")
    private String account;
    @Column(name = "address")
    private String address;
    @Column(name = "birthday")
    private String birthday;
    @Column(name = "createtokendate")
    private String createtokendate;
    @Column(name = "email")
    private String email;
    @Column(name = "fullname")
    private String fullname;
    @Column(name = "groupmax")
    private String groupmax;
    @Column(name = "groupuse")
    private String groupuse;
    @Column(name = "id",isId = true)
    private String id;
    @Column(name = "intro")
    private String intro;
    @Column(name = "logo")
    private String logo;
    @Column(name = "mobile")
    private String mobile;
    @Column(name = "password")
    private String password;
    @Column(name = "pinyin")
    private String pinyin;
    @Column(name = "sex")
    private String sex;
    @Column(name = "telephone")
    private String telephone;
    @Column(name = "token")
    private String token;
    @Column(name = "workno")
    private String workno;
    @Column(name = "code")
    private String code;
    @Column(name = "text")
    private String text;

    public Friend(String workno, String fullname, String logo) {
        this.workno = workno;
        this.fullname = fullname;
        this.logo = logo;
    }

    //这里注意，要使用构造器初始化时，必须再提供一个无参构造器
    //我觉得是和注解有关吧
    public Friend() {}

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
        return "Friend{" +
                "account='" + account + '\'' +
                ", address='" + address + '\'' +
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
                ", password='" + password + '\'' +
                ", pinyin='" + pinyin + '\'' +
                ", sex='" + sex + '\'' +
                ", telephone='" + telephone + '\'' +
                ", token='" + token + '\'' +
                ", workno='" + workno + '\'' +
                ", code='" + code + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
