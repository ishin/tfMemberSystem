package com.sealtalk.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

/** 
* @ClassName: SessionPrivilege 
* @Description: TODO(权限session) 
* @author hdy
*  
*/
public class SessionPrivilege {
	
	private static final ArrayList<String> privileges = new ArrayList<String>();
	private Map<String, String> map;
	/*
	private String htgl;			//后台管理
//	private String yyapppcd;		//应用APP/PC端
	private String cjxz;			//层级限制
	private String rsgl;			//人事管理
	private String bmgl;			//部门管理
	private String zzxxgl;			//组织信息管理
	private String qzgl;			//群组管理
	private String qxgl;			//权限管理
	private String grsz;			//个人设置
	private String stsz;			//聊天设置
	private String qz;				//群组
	private String djj;				//对讲机
	private String qt;				//其他
	private String dpjhzsjbmkf;		//对平级或者上级部门开放
	private String rsglck;			//查看
	private String rsgltj;			//添加
	private String rsgljcxx;		//基础信息
	private String rsglxgmm;		//修改密码
	private String rsglyd;			//移动
	private String rsglsc;			//删除
	private String bmglck;			//查看
	private String bmgltj;			//添加
	private String bmglxg;			//修改
	private String bmglyd;			//移动	
	private String bmglsc;			//删除
	private String zzxxglck;		//查看
	private String zzxxglxg;		//修改
	private String qzglck;			//查看
	private String qzgljs;			//解散
	private String qzglxg;			//修改
	
	qxglck			查看（权限管理）
	qxgltj			添加（权限管理）
	qxglxg			修改（权限管理）
	qxglsc			删除（权限管理）
	grszsygzqm		使用工作签名（个人设置）
	grszxgyhm		修改用户名（个人设置）
	grszxgxm		修改姓名 （个人设置）
	grszxgzw		修改职务（个人设置）
	ltszfqgrlt		发起个人聊天（聊天设置）
	ltszqzlt		群组聊天（聊天设置）
	ltszwjsc		文件上传（聊天设置）
	qzcjq			创建群（群组）
	qzjsq			解散群（群组）
	qzxgqcjz		修改群创建者（群组）
	djjkq			开启（对讲机）
	djjfysqtrjy		发言时其他人禁言（对讲机）
	djjjjhj			紧急呼叫（对讲机）
	qtckdlwz		查看地理位置(其它)
	dpjhzsjbmkffqgrlt	发起个人聊天（对平级或者上级部门开放）
	dpjhzsjbmkfwjsc		文件上传（对平级或者上级部门开放）
	dpjhzsjbmkfcjq		创建群（对平级或者上级部门开放）
	dpjhzsjbmkfckdlwz	查看地理位置（对平级或者上级部门开放）
	dpjhzsjbmkfjjtz		紧急通知（对平级或者上级部门开放）
	*/
	public SessionPrivilege(){
		addList();
	};
	
	public void setPrivilige(ArrayList<JSONObject> ja) {
		int len = ja.size();
		Map<String, String> map = new HashMap<String, String>();
		
		for(int i = 0; i < len; i++) {
			JSONObject jo = ja.get(i);
			//if (jo.containsKey("priurl") && privileges.contains(jo.getString("priurl"))) {
			if (jo.containsKey("priurl")) {
				map.put(jo.getString("priurl"), "1");
			}
		}
		
		this.map = map;
	}
	
	public Map<String, String> getMap() {
		return map;
	}

	public void setMap(Map<String, String> map) {
		this.map = map;
	}

	private void addList() {
		privileges.add("htgl");
		privileges.add("yyapppcd");
		privileges.add("cjxz");
		privileges.add("rsgl");
		privileges.add("bmgl");
		privileges.add("zzxxgl");
		privileges.add("qzgl");
		privileges.add("qxgl");
		privileges.add("grsz");
		privileges.add("stsz");
		privileges.add("qz");
		privileges.add("djj");
		privileges.add("qt");
		privileges.add("dpjhzsjbmkf");
		privileges.add("rsglck");
		privileges.add("rsgltj");
		privileges.add("rsgljcxx");
		privileges.add("rsglxgmm");
		privileges.add("rsglyd");
		privileges.add("rsglsc");
		privileges.add("bmglck");
		privileges.add("bmgltj");
		privileges.add("bmglxg");
		privileges.add("bmglyd");
		privileges.add("bmglsc");
		privileges.add("zzxxglck");
		privileges.add("zzxxglxg");
		privileges.add("qzglck");
		privileges.add("qzgljs");
		privileges.add("qzglxg");
		privileges.add("qxglck");
		privileges.add("qxgltj");
		privileges.add("qxglxg");
		privileges.add("qxglsc");	
		privileges.add("grszsygzqm");
		privileges.add("grszxgyhm");
		privileges.add("grszxgxm");
		privileges.add("grszxgzw");
		privileges.add("ltszfqgrlt");
		privileges.add("ltszqzlt");	
		privileges.add("ltszwjsc");
		privileges.add("qzcjq");
		privileges.add("qzjsq");
		privileges.add("qzxgqcjz");
		privileges.add("djjkq");
		privileges.add("djjfysqtrjy");
		privileges.add("djjjjhj");
		privileges.add("qtckdlwz");
		privileges.add("dpjhzsjbmkffqgrlt");
		privileges.add("dpjhzsjbmkfwjsc");
		privileges.add("dpjhzsjbmkfcjq");
		privileges.add("dpjhzsjbmkfckdlwz");
		privileges.add("dpjhzsjbmkfjjtz");
	}
}