package com.organ.dao.appinfoconfig;

import java.util.List;

import com.organ.common.IBaseDao;
import com.organ.model.AppSecret;

public interface AppInfoConfigDao extends IBaseDao<AppSecret, Long>{

	public List<AppSecret> getAppInfo(int userId,int pagesize,int pageindex);
	
	public int updatePriv(String appId,String secert,String callbackurl,String appname,int isopen);
	public int getCount();
	
	public int editApp(int id,String appId,String secert,String callbackurl,String appname,int isopen);
	
	public List SearchAppInfo(int userId,String AppName,int pagesize, int pageindex);
	
	public int getSearchCount(String AppName);
	
	public List SearchAppInfoName();

	public String getAppNameByID(int id);
}
