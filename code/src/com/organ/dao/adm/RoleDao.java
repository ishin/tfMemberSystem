package com.organ.dao.adm;

import java.util.List;

import com.organ.common.IBaseDao;
import com.organ.model.TRole;

public interface RoleDao extends IBaseDao<TRole, Integer> {

	public List getMemberByRole(Integer roleId, Integer page, Integer itemsperpage);
	public int getMemberCountByRole(Integer roleId);
	/**
	 * 返回角色权限（会返回所有权限,做角色修改用的）
	 * @param roleId
	 * @return
	 */
	public List getPrivByRole(Integer roleId);
	public List getPrivByMember(Integer memberId);
	/**
	 * 返回角色权限（严格满足条件）
	 * @param roleId
	 * @return
	 */
	public List getPrivilegeById(int roleId);
	
	/**
	 * 返回权限
	 * @param roleIdList 角色id数组
	 * @return
	 */
	public List<Object[]> getPrivilegeByRoleIds(Integer[] ids);
	
}
