package com.sealtalk.model;

/**
 * TMemberRole entity. @author MyEclipse Persistence Tools
 */

public class TMemberRole implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -3137237746638835389L;
	private Integer id;
	private Integer memberId;
	private Integer roleId;
	private Integer listorder;

	// Constructors

	/** default constructor */
	public TMemberRole() {
	}

	/** full constructor */
	public TMemberRole(Integer memberId, Integer roleId, Integer listorder) {
		this.memberId = memberId;
		this.roleId = roleId;
		this.listorder = listorder;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getMemberId() {
		return this.memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public Integer getRoleId() {
		return this.roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public Integer getListorder() {
		return this.listorder;
	}

	public void setListorder(Integer listorder) {
		this.listorder = listorder;
	}

}