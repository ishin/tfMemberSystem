package com.organ.model;

/**
 * oauth2登陆数据模型
 * @author hao_dy
 * @date 2017/03/08
 */
public class UserValid {
	private int id;
	private int asid;						//应用id
	private int userId;						//用户id
	private int info;						//用户信息类型(1:姓名，头像，性别，职位.2:联系方式，公司，邮箱)
	private String unAuthToken;
	private String authToken;				//已授权临时令牌
	private String visitToken;				//访问令牌
	private long unAuthTokenTime;			//未授权临时令牌生成时间
	private long authTokenTime;				//已授权临时令牌生成时间
	private long visitTokenTime;			//访问令牌生成时间
	private String isDel;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAsid() {
		return asid;
	}
	public void setAsid(int asid) {
		this.asid = asid;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getInfo() {
		return info;
	}
	public void setInfo(int info) {
		this.info = info;
	}
	public String getUnAuthToken() {
		return unAuthToken;
	}
	public void setUnAuthToken(String unAuthToken) {
		this.unAuthToken = unAuthToken;
	}
	public String getAuthToken() {
		return authToken;
	}
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}
	public String getVisitToken() {
		return visitToken;
	}
	public void setVisitToken(String visitToken) {
		this.visitToken = visitToken;
	}
	public long getUnAuthTokenTime() {
		return unAuthTokenTime;
	}
	public void setUnAuthTokenTime(long unAuthTokenTime) {
		this.unAuthTokenTime = unAuthTokenTime;
	}
	public long getAuthTokenTime() {
		return authTokenTime;
	}
	public void setAuthTokenTime(long authTokenTime) {
		this.authTokenTime = authTokenTime;
	}
	public long getVisitTokenTime() {
		return visitTokenTime;
	}
	public void setVisitTokenTime(long visitTokenTime) {
		this.visitTokenTime = visitTokenTime;
	}
	public String getIsDel() {
		return isDel;
	}
	public void setIsDel(String isDel) {
		this.isDel = isDel;
	}
	
}
