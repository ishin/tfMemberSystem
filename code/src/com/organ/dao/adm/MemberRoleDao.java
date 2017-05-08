package com.organ.dao.adm;

import java.util.List;

import com.organ.common.IBaseDao;
import com.organ.model.TMemberRole;

public interface MemberRoleDao extends IBaseDao<TMemberRole, Integer> {

	public List<TMemberRole> getRoleForId(int id);

	/**
	 * 获取多个成员的角色
	 * @param ids
	 * @return
	 */
	public List<TMemberRole> getMemberRolesByRoleIds(Integer[] ids);

	public List getMemberIdsByRoleIds(String string);

	public int deleteRelationByIds(String userids, String isLogic);


}
