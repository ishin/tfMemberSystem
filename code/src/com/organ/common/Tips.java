package com.organ.common;

import net.sf.json.JSONObject;

public enum  Tips {
	NULLUSER("账号为空", "00001"),
	NULLID("ID为空", "00002"),
	ERRORUSERORPWD("账号或密码错误", "00003"),
	FALSECOMPAREPWD("密码不一致", "00004"),
	CHANGEPWDSUC("密码修改成功", "00005"),
	CHANGEPWDFAIL("密码修改失败", "00006"),
	SENDTEXTS("短信验证码已发送", "00007"),
	NULLTEXTS("短信验证码为空", "00008"),
	ERRORTEXTS("短信验证码不正确", "00009"),
	TRUETEXTS("短信验证通过", "00010"),
	SENDERR("短信验证码发送未成功", "00011"),
	UNKNOWERR("未知错误", "00011"),
	NOTFRIENDID("未选取好友", "00012"),
	FAILADDFRIEND("好友添加失败", "00013"),
	SUCADDFRIEND("好友添加成功", "00014"),
	HAVEFRIENDRELATION("已存在好友关系", "00015"),
	FAILDELFRIEND("好友删除失败", "00016"),
	SUCDELFRIEND("好友删除成功", "00017"),
	HAVEZEROFRIEND("没有好友", "00018"),
	NOHAVEFRIENDRELATION("不存在好友关系", "00019"),
	NOSENDPERSON("消息发送或接收主体不存在", "00020"),
	NULLGROUPMEMBER("没有成员", "00021"),
	NULLGROUPNAME("没有群组名称", "00022"),
	NOSECGROUP("未选取群组", "00023"),
	NOTCLEARALLMEMBER("有成员未删除，重试或手动删除", "00024"),
	GROUPMOREVOLUME("成员超出上限", "00025"),
	WRONGOLDPWD("旧密码错误", "00026"),
	NOTSETFUN("未设置功能", "00027"),
	NULLPHONE("手机号为空", "00028"),
	WRONGPARAMS("参数不正确", "00029"),
	NOLOGOERR("没有头像", "00030"),
	USEDLOGO("头像正在被使用", "00031"),
	WRONGTOKEN("Token无效","00032"),
	NULLGROUP("没有群组", "00033"),
	SEARCHFAIL("查询失败", "00034"),
	NOTINIT("请使用正确的账号登陆", "00035"),
	VALIDFAIL("检验不通过", "00036"),
	NULLORGAN("未指定公司", "00037"),
	EXISTACCOUNT("账号已存在", "00038"),
	OK("OK", "10000"),
	FAIL("fail", "20000"),
	NOTUPDATE("更新成功","1");
	private String name;
	private String code;
	
	private Tips(String name, String code) {
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
