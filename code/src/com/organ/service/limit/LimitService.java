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
	 * 
	 * @return
	 */
	public String AddLimit(int parentId, String name,
			String app);

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
	 * 
	 * @param Name
	 * @return
	 */
	public String searchPriv(String Name,int pagesize,int pageindex);
	
	public int getCount();
	
	public List getLimitbyRole(Integer roleId, String appName);
	
	public String getRoleList(String appname);
	
	public String getPrivNamebytwo(String appName);//获取父id为2的所有权限名称
	
	public String saveRolebyApp(Integer roleId,Integer appsecretId, String roleName, String privs);
	
	void delRole(Integer roleId);//删除应用时，同时删除角色

}
