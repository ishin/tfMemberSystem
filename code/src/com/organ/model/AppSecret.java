package com.organ.model;

/**
 * oauth2应用数据模型
 * @author hao_dy
 * @date 2017/03/08
 */
public class AppSecret {
	private int id;
	private int isOpen;						//是否开启应用
	private String appId;					//appid		用来获取未授权临时令牌
	private String secert;					//secret	用来获取访问令牌
	private String callBackUrl;				//第三方系统回调地址
	private String appName;					//应用名称
	private long appTime;					//appid,secret生成时间
	public AppSecret() {}
	public AppSecret(int id, int isOpen, String appId, String secert,
			String callBackUrl, String appName, long appTime) {
		super();
		this.id = id;
		this.isOpen = isOpen;
		this.appId = appId;
		this.secert = secert;
		this.callBackUrl = callBackUrl;
		this.appName = appName;
		this.appTime = appTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIsOpen() {
		return isOpen;
	}

	public void setIsOpen(int isOpen) {
		this.isOpen = isOpen;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getSecert() {
		return secert;
	}

	public void setSecert(String secert) {
		this.secert = secert;
	}

	public String getCallBackUrl() {
		return callBackUrl;
	}

	public void setCallBackUrl(String callBackUrl) {
		this.callBackUrl = callBackUrl;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public long getAppTime() {
		return appTime;
	}

	public void setAppTime(long appTime) {
		this.appTime = appTime;
	}
	
}
