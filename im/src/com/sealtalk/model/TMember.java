package com.sealtalk.model;

public class TMember implements java.io.Serializable {

	private static final long serialVersionUID = -4973676984274721166L;
	
	private Integer id;
	private String account;
	private String password;
	private String fullname;
	private String pinyin;
	private String allpinyin;
	private String workno;
	private String sex;
	private String birthday;
	private String logo;
	private String email;
	private String mobile;
	private String telephone;
	private String address;
	private Integer organId;
	private Integer groupmax;
	private Integer groupuse;
	private String intro;
	private String token;
	private Integer createtokendate;

	public TMember() {
	}
	public TMember(Integer id) {
		this.id = id;
	}

	public TMember(String account, String password, String fullname,
			String pinyin, String allpinyin, String workno, String sex, String birthday,
			String logo, String email, String mobile, String telephone,
			String address, Integer groupmax, Integer groupuse, String intro,
			String token, Integer createtokendate) {
		this.account = account;
		this.password = password;
		this.fullname = fullname;
		this.pinyin = pinyin;
		this.allpinyin = allpinyin;
		this.workno = workno;
		this.sex = sex;
		this.birthday = birthday;
		this.logo = logo;
		this.email = email;
		this.mobile = mobile;
		this.telephone = telephone;
		this.address = address;
		this.groupmax = groupmax;
		this.groupuse = groupuse;
		this.intro = intro;
		this.token = token;
		this.createtokendate = createtokendate;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAccount() {
		return this.account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFullname() {
		return this.fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getPinyin() {
		return this.pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}
	
	public String getAllpinyin() {
		return allpinyin;
	}
	public void setAllpinyin(String allpinyin) {
		this.allpinyin = allpinyin;
	}
	public String getWorkno() {
		return this.workno;
	}

	public void setWorkno(String workno) {
		this.workno = workno;
	}

	public String getSex() {
		return this.sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getBirthday() {
		return this.birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getLogo() {
		return this.logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return this.mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getTelephone() {
		return this.telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getOrganId() {
		return this.organId;
	}

	public void setOrganId(Integer organid) {
		this.organId = organid;
	}

	public Integer getGroupmax() {
		return this.groupmax;
	}

	public void setGroupmax(Integer groupmax) {
		this.groupmax = groupmax;
	}

	public Integer getGroupuse() {
		return this.groupuse;
	}

	public void setGroupuse(Integer groupuse) {
		this.groupuse = groupuse;
	}

	public String getIntro() {
		return this.intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Integer getCreatetokendate() {
		return createtokendate;
	}

	public void setCreatetokendate(Integer createtokendate) {
		this.createtokendate = createtokendate;
	}
	
}