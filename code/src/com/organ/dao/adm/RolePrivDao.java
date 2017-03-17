package com.organ.dao.adm;

import java.util.List;

import com.organ.common.IBaseDao;
import com.organ.model.TRolePriv;

public interface RolePrivDao extends IBaseDao<TRolePriv, Integer> {

	/**
	 * 获取角色根据权限id
	 * @param privIds
	 * @return
	 */
	List<TRolePriv> getRolePrivsByPrivs(Integer[] privIds);

}
