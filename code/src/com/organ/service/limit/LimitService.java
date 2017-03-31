package com.organ.service.limit;

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

}
