package com.organ.service.limit;

import java.util.List;

/**
 * 
 * @author Lmy
 * 
 */
public interface LimitService {

	/**
	 * 增加权限
	 * @param organId 
	 * 
	 * @return
	 */
	public String AddLimit(int parentId, String name,
			String app, int organId);

	/**
	 * 编辑权限
	 * 
	 * @return
	 */
	public String EditLimit(int priv_id, String pid, String name,
			 String app);

	/**
	 * 删除权限
	 * 
	 * @return
	 */
	public String DelLimit(int priv_id);

	/**
	 *根据权限名称搜索权限
	 * @param organId 
	 * 
	 * @param Name
	 * @return
	 */
	public String searchPriv(int organId, String Name,int pagesize,int pageindex);
	
	public int getCount(int organId);
	
	public List getLimitbyRole(Integer roleid2, int organId, String appName);
	
	public String getRoleList(Integer appId, int organId);
	
	public String getPrivNamebytwo(int organId, String appName);//获取父id为2的所有权限名称
	
	public String saveRolebyApp(Integer roleId,Integer appsecretId, String roleName, String privs, int organId);
	
	void delRole(Integer roleId);//删除应用时，同时删除角色

}
