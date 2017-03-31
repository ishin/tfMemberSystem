package com.organ.service.appinfoconfig;

public interface AppInfoConfigService {
	public String getAppInfo(int pagesize, int pageindex);
	
	public String updatePriv(int appId,String secert,String callbackurl,String appname,int isopen);//添加权限接口
	
	
	public String DelApp(int id);
	
	public String EditApp(int id,int appId,String secert,String callbackurl,long apptime,String appname,int isopen);
	
	
	public String SearchApp(String AppName,int pagesize, int pageindex);
	
}
