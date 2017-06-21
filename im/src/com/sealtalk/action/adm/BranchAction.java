/**
 * 
 */
package com.sealtalk.action.adm;

import javax.servlet.ServletException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sealtalk.common.BaseAction;
import com.sealtalk.common.Tips;
import com.sealtalk.common.XmlCommon;
import com.sealtalk.service.adm.BranchService;

/**
 * @author alopex
 *
 */

public class BranchAction extends BaseAction {
		
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(BranchAction.class);

	/*
	 * 取部门树
	 * by alopex
	 */
	public String getMembersByOrgan() throws ServletException {
		int organId = getSessionUserOrganId();
		String result = null;
		if (organId == 0) {
			result = failResult(Tips.TIMEOUT.getText());
		} else {
			result = branchService.getMembersByOrgan(organId);
		}
		returnToClient(result);
		return "text";
	}
	
	/*
	 * 取部门树
	 */
	public String getBranchTree() throws ServletException {
		int organId = getSessionUserOrganId();
		String result = null;
		
		if (organId == 0) {
			result = failResult(Tips.TIMEOUT.getText());
		} else {
			result = branchService.getBranchTree(organId);
		}
		
		logger.info(result);
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
		String result = null;
		if (organId == 0) {
			result = failResult(Tips.TIMEOUT.getText());
		} else {
			result = branchService.getBranchTreeAndMember(organId);
		}
		logger.info(result);
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
	
		String result = null;
		if (organId == 0) {
			result = failResult(Tips.TIMEOUT.getText());
		} else {
			result = branchService.getBranchMember(clearChar(branchId), organId);
		}
		logger.info(result);
		returnToClient(result);
		return "text";
	}
	
	public String getPosition() throws ServletException {
		int organId = getSessionUserOrganId();
		String result = null;
		if (organId != 0) {
			result = branchService.getPosition(organId);
		} else {
			result = failResult(Tips.TIMEOUT.getText());	
		}
		logger.info(result);
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
