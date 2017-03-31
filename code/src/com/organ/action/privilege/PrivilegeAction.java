package com.organ.action.privilege;

import javax.servlet.ServletException;

import com.organ.common.BaseAction;
import com.organ.service.privilege.PrivilegeService;

/**
 * 权限action
 * @author hao_dy
 *
 */

public class PrivilegeAction extends BaseAction {
	private static final long serialVersionUID = 482342287337610866L;
	
	/**
	 * 获取权限类别
	 * @return
	 * @throws ServletException
	 */
	public String getPrivilegeCatary() throws ServletException {
		String ret = privilegeService.getPrivilegeCatary();
		
		returnToClient(ret);
		return "text";
	}

	private PrivilegeService privilegeService;

	public void setPrivilegeService(PrivilegeService privilegeService) {
		this.privilegeService = privilegeService;
	}
	
}
