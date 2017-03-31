package com.organ.dao.appinfoconfig;

import java.util.List;

import com.organ.model.AppSecret;

public interface AppInfoConfigDao {

	public List<AppSecret> getAppInfo(int pagesize,int pageindex);
	
	public int updatePriv(int appId,String secert,String callbackurl,String appname,int isopen);//添加权限接口
	
	public int DeletelApp(int id);
	
	public int getCount();//获取总条目
	
	public int editApp(int id,int appId,String secert,String callbackurl,long apptime,String appname,int isopen);
	
	public List SearchAppInfo(String AppName,int pagesize, int pageindex);
	
	public int getSearchCount(String AppName);
}
