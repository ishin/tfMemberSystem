/**
 * 
 */
package com.sealtalk.action.adm;

import javax.servlet.ServletException;

import com.googlecode.sslplugin.annotation.Secured;
import com.sealtalk.common.BaseAction;
import com.sealtalk.service.adm.BranchService;

/**
 * @author alopex
 *
 */

@Secured
public class BranchAction extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	/*
	 * 取部门树
	 */
	public String getBranchTree() throws ServletException {
		int organId = getSessionUserOrganId();
		String result = branchService.getBranchTree(organId);
		returnToClient(result);
		
		return "text";
	}

	/**
	 * 取得部门+成员数据
	 * @return
	 * @throws ServletException
	 */
	public String getBranchTreeAndMember() throws ServletException {
		int organId = getSessionUserOrganId();
		String result = branchService.getBranchTreeAndMember(organId);
			
		returnToClient(result);
		
		return "text";
	}
	
	/**
	 * 取得指定部门的成员
	 * @return
	 * @throws ServletException
	 */
	public String getBranchMember() throws ServletException {
		
		int organId = getSessionUserOrganId();
		String result = branchService.getBranchMember(branchId, organId);
		
		returnToClient(result);
		return "text";
	}
	
	public String getPosition() throws ServletException {
		int organId = getSessionUserOrganId();
		String result = branchService.getPosition(organId);
		returnToClient(result);
		
		return "text";
	}
	
	private BranchService branchService;

	public void setBranchService(BranchService branchService) {
		this.branchService = branchService;
	}
	
	private String branchId;

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}
	
	
}
