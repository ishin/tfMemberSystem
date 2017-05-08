package com.organ.dao.appinfoconfig;

import java.util.List;

import com.organ.common.IBaseDao;
import com.organ.model.AppSecret;

public interface AppInfoConfigDao extends IBaseDao<AppSecret, Long>{

	public List<AppSecret> getAppInfo(int userId, int organId, int pagesize,int pageindex);
	
	public int updatePriv(String appId,String secert,String callbackurl,String appname,int isopen, int organId);
	public int getCount(int organId);
	
	public int editApp(int id,String appId,String secert,String callbackurl,String appname,int isopen);
	
	public List SearchAppInfo(int organId, String AppName,int pagesize, int pageindex);
	
	public int getSearchCount(String AppName, int organId);
	
	public List SearchAppInfoName(int organId);

	public String getAppNameByID(int id);

	public List getRoleIdsByAppId(int appRecordId);
}