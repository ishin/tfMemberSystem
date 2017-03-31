package com.organ.model;

/**
 * oauth2应用数据模型
 * 
 * @author hao_dy
 * @date 2017/03/08
 */
public class UserSysRelation {
	private int id;
	private int userId;
	private int appId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

}
