package com.sealtalk.model;

/**
 * TRolePriv entity. @author MyEclipse Persistence Tools
 */

public class TRolePriv implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -1474932465388189063L;
	private Integer id;
	private Integer roleId;
	private Integer privId;

	// Constructors

	/** default constructor */
	public TRolePriv() {
	}

	/** full constructor */
	public TRolePriv(Integer roleId, Integer privId) {
		this.roleId = roleId;
		this.privId = privId;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getRoleId() {
		return this.roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public Integer getPrivId() {
		return this.privId;
	}

	public void setPrivId(Integer privId) {
		this.privId = privId;
	}

}