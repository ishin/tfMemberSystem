package com.organ.model;

/**
 * TBranch entity. @author MyEclipse Persistence Tools
 */

public class TBranch implements java.io.Serializable {

	// Fields

	private static final long serialVersionUID = -1894285403795785111L;
	
	private Integer id;
	private Integer organId;
	private Integer parentId;
	private String name;
	private Integer managerId;
	private String address;
	private String website;
	private String telephone;
	private String fax;
	private String intro;
	private String isDel;
	private String noGroup;
	private Integer listorder;

	// Constructors

	/** default constructor */
	public TBranch() {
	}

	/** minimal constructor */
	public TBranch(Integer organId, Integer parentId, Integer managerId,
			Integer listorder) {
		this.organId = organId;
		this.parentId = parentId;
		this.managerId = managerId;
		this.listorder = listorder;
	}

	/** full constructor */
	public TBranch(Integer organId, Integer parentId, String name,
			Integer managerId, String address, String website,
			String telephone, String fax, String intro, Integer listorder, String isDel, String noGroup) {
		this.organId = organId;
		this.parentId = parentId;
		this.name = name;
		this.managerId = managerId;
		this.address = address;
		this.website = website;
		this.telephone = telephone;
		this.fax = fax;
		this.intro = intro;
		this.listorder = listorder;
		this.isDel = isDel;
		this.noGroup = noGroup;
		
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getOrganId() {
		return this.organId;
	}

	public void setOrganId(Integer organId) {
		this.organId = organId;
	}

	public Integer getParentId() {
		return this.parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getManagerId() {
		return this.managerId;
	}

	public void setManagerId(Integer managerId) {
		this.managerId = managerId;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getWebsite() {
		return this.website;
	}

	public void setWebsite(String website) {
		this.website = website;
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

	public String getIsDel() {
		return isDel;
	}

	public void setIsDel(String isDel) {
		this.isDel = isDel;
	}

	public String getNoGroup() {
		return noGroup;
	}

	public void setNoGroup(String noGroup) {
		this.noGroup = noGroup;
	}
	
}