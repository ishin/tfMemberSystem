package com.organ.common;

import net.sf.json.JSONObject;

public enum  AuthTips {
	WORNGMAKEAS("AppId或secret生成错误", "00001"),
	WORNGAPPID("AppId或secret生成错误", "00002"),
	WORNGPARAM("参数错误", "00003"),
	INVALIDAPPID("AppId无效", "00004"),
	INVALTOKEN("令牌无效", "00005"),
	INVALUSER("账号或密码无效", "00006"),
	WORNGSECRET("Secret无效", "00007"),
	NOTLOGIN("用户未登陆", "00008"),
	INVALCOMPANYID("未指定公司", "00009"),
	TIMEOUTAPPID("AppId失效,重新申请", "00010"),
	TIMEOUTTOKEN("令牌失效,重新申请", "00010"),
	
	OK("OK", "10000"),
	ERROR("error", "3000"),
	FAIL("fail", "20000");
	
	private String name;
	private String code;
	
	private AuthTips(String name, String code) {
		this.name = name;
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getText() {
		JSONObject jo = new JSONObject();
		
		jo.put("context", getName());
		jo.put("code", getCode());
		
		return jo.toString();
	}
	
}
