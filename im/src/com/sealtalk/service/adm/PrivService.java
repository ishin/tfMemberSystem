package com.sealtalk.service.adm;

import java.util.List;

public interface PrivService {
	/**
	 * 根据用户Id获取角色
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List getRoleIdForId(int id);
	
	/**
	 * 获取权限
	 * @param id
	 * @return
	 */
	public String getPrivStringByMember(Integer id);
	
}
