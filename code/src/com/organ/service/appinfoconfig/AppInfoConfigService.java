package com.organ.service.appinfoconfig;

public interface AppInfoConfigService {
	public String getAppInfo(int userId, int organId, int pagesize, int pageindex);
	
	public String updatePriv(String appId,String secert,String callbackurl,String appname,int isopen, int organId);
	
	
	public String DelApp(int id);
	
	public String EditApp(int id,String appId,String secert,String callbackurl,String appname,int isopen);
	
	
	public String SearchApp(String name, int organId, String AppName,int pagesize, int pageindex);
	
	public String SearchAppInfoName(int organId);
}
