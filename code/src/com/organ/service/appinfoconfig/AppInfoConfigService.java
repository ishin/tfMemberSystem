package com.organ.service.appinfoconfig;

public interface AppInfoConfigService {
	public String getAppInfo(int userId,int pagesize, int pageindex);
	
	public String updatePriv(String appId,String secert,String callbackurl,String appname,int isopen);//���Ȩ�޽ӿ�
	
	
	public String DelApp(int id);
	
	public String EditApp(int id,String appId,String secert,String callbackurl,String appname,int isopen);
	
	
	public String SearchApp(int userId,String AppName,int pagesize, int pageindex);
	
	public String SearchAppInfoName();
}
