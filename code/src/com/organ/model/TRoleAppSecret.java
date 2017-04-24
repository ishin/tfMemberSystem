package com.organ.model;

import java.io.Serializable;

/**
 * @author Lmy
 *
 */
public class TRoleAppSecret implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2819350693962418516L;
	
	private Integer id;
	
	private Integer roleId;
	
	private Integer appsecretId;

	
	public TRoleAppSecret() {}
	
	public TRoleAppSecret(Integer roleId,Integer appsecretId){
		this.appsecretId = appsecretId;
		this.roleId = roleId;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public Integer getAppsecretId() {
		return appsecretId;
	}

	public void setAppsecretId(Integer appsecretId) {
		this.appsecretId = appsecretId;
	}
	
	
	

}
