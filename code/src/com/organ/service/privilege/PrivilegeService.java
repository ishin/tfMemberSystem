package com.organ.service.privilege;

import com.organ.model.SessionPrivilege;

public interface PrivilegeService {

	/**
	 * 生成权限session
	 * @param id
	 * @return
	 */
	public SessionPrivilege setPrivilege(int id);
	
}