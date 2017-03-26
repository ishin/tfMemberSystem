package com.organ.dao.privilege;

import java.util.List;

import com.organ.common.IBaseDao;
import com.organ.model.TPriv;

public interface PrivilegeDao extends IBaseDao<TPriv, Long> {

	/**
	 * 获取所有权限
	 * @return
	 */
	public List<TPriv> getAllPrivilege();

} 
