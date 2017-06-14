package com.organ.model;

import java.util.HashMap;
import java.util.Map;

/** 
* @ClassName: SessionUser 
* @Description: TODO(代表已登录用户的包装类,表示此用户处于会话中。) 
* @author hdy
*  
*/
public class SessionUser
{
	/** 
	* @Fields accountId : TODO(账号ID) 
	*/ 
	private int id;
	private String account; 
	private String fullname;
	private String token;
	private int organId;
	private Map<String, Long> apMap;		//用来做同接口请求频率限制

	public SessionUser() {
		super();
		apMap = new HashMap<String, Long>();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public int getOrganId() {
		return organId;
	}

	public void setOrganId(int organId) {
		this.organId = organId;
	}

	public Map<String, Long> getApMap() {
		return apMap;
	}

	public void setApMap(Map<String, Long> apMap) {
		this.apMap = apMap;
	}
}