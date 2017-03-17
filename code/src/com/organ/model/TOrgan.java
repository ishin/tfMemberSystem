package com.organ.model;

/**
 * TOrgan entity. @author MyEclipse Persistence Tools
 */

public class TOrgan implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -3942808615123801560L;
	private Integer id;
	private String code;
	private String name;
	private String shortname;
	private String englishname;
	private String logo;
	private String domain;
	private Integer provinceId;
	private Integer cityId;
	private Integer districtId;
	private String postcode;
	private String contact;
	private String address;
	private String telephone;
	private String fax;
	private String email;
	private String website;
	private Integer inwardId;
	private Integer industryId;
	private Integer subdustryId;
	public Integer getSubdustryId() {
		return subdustryId;
	}

	public void setSubdustryId(Integer subdustryId) {
		this.subdustryId = subdustryId;
	}

	private Integer capital;
	private Integer membernumber;
	private Integer computernumber;
	private String ad;
	private String intro;
	private Integer listorder;

	// Constructors

	/** default constructor */
	public TOrgan() {
	}

	/** minimal constructor */
	public TOrgan(Integer provinceId, Integer cityId, Integer districtId,
			Integer inwardId, Integer industryId, Integer capital,
			Integer membernumber, Integer computernumber, Integer listorder) {
		this.provinceId = provinceId;
		this.cityId = cityId;
		this.districtId = districtId;
		this.inwardId = inwardId;
		this.industryId = industryId;
		this.capital = capital;
		this.membernumber = membernumber;
		this.computernumber = computernumber;
		this.listorder = listorder;
	}

	/** full constructor */
	public TOrgan(String code, String name, String shortname,
			String englishname, String logo, String domain, Integer provinceId,
			Integer cityId, Integer districtId, String postcode,
			String contact, String address, String telephone, String fax,
			String email, String website, Integer inwardId, Integer industryId,
			Integer capital, Integer membernumber, Integer computernumber,
			String ad, String intro, Integer listorder) {
		this.code = code;
		this.name = name;
		this.shortname = shortname;
		this.englishname = englishname;
		this.logo = logo;
		this.domain = domain;
		this.provinceId = provinceId;
		this.cityId = cityId;
		this.districtId = districtId;
		this.postcode = postcode;
		this.contact = contact;
		this.address = address;
		this.telephone = telephone;
		this.fax = fax;
		this.email = email;
		this.website = website;
		this.inwardId = inwardId;
		this.industryId = industryId;
		this.capital = capital;
		this.membernumber = membernumber;
		this.computernumber = computernumber;
		this.ad = ad;
		this.intro = intro;
		this.listorder = listorder;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortname() {
		return this.shortname;
	}

	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

	public String getEnglishname() {
		return this.englishname;
	}

	public void setEnglishname(String englishname) {
		this.englishname = englishname;
	}

	public String getLogo() {
		return this.logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getDomain() {
		return this.domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public Integer getProvinceId() {
		return this.provinceId;
	}

	public void setProvinceId(Integer provinceId) {
		this.provinceId = provinceId;
	}

	public Integer getCityId() {
		return this.cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public Integer getDistrictId() {
		return this.districtId;
	}

	public void setDistrictId(Integer districtId) {
		this.districtId = districtId;
	}

	public String getPostcode() {
		return this.postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getContact() {
		return this.contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTelephone() {
		return this.telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getFax() {
		return this.fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getWebsite() {
		return this.website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public Integer getInwardId() {
		return this.inwardId;
	}

	public void setInwardId(Integer inwardId) {
		this.inwardId = inwardId;
	}

	public Integer getIndustryId() {
		return this.industryId;
	}

	public void setIndustryId(Integer industryId) {
		this.industryId = industryId;
	}

	public Integer getCapital() {
		return this.capital;
	}

	public void setCapital(Integer capital) {
		this.capital = capital;
	}

	public Integer getMembernumber() {
		return this.membernumber;
	}

	public void setMembernumber(Integer membernumber) {
		this.membernumber = membernumber;
	}

	public Integer getComputernumber() {
		return this.computernumber;
	}

	public void setComputernumber(Integer computernumber) {
		this.computernumber = computernumber;
	}

	public String getAd() {
		return this.ad;
	}

	public void setAd(String ad) {
		this.ad = ad;
	}

	public String getIntro() {
		return this.intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public Integer getListorder() {
		return this.listorder;
	}

	public void setListorder(Integer listorder) {
		this.listorder = listorder;
	}

}