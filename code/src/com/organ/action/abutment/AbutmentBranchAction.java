package com.organ.action.abutment;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.organ.common.BaseAction;
import com.organ.common.Tips;
import com.organ.service.adm.BranchService;
import com.organ.service.adm.OrgService;
import com.organ.utils.JSONUtils;
import com.organ.utils.LogUtils;

public class AbutmentBranchAction extends BaseAction {

	private static final long serialVersionUID = 5967807252950908349L;
	private static final Logger logger = Logger
			.getLogger(AbutmentBranchAction.class);

	/**
	 * 获取部门下的成员
	 * @return
	 * @throws ServletException
	 */
	public String getBranchMemberAb() throws ServletException {
		String result = null;
		try {
			String params = getRequestDataByStream();
			JSONObject jo = new JSONObject();

			if (params == null) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				String branchId = p.getString("branchId");
				int organId = p.getInt("organId");
				result = branchService.getBranchMember(branchId, null, organId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		returnToClient(result);
		return "text"; 
	}
	
	/**
	 * 获取组织结构及成员
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String getBranchTreeAndMemberAb() throws ServletException {
		String result = null;
		try {
			String params = getRequestDataByStream();
			JSONObject jo = new JSONObject();

			if (params == null) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				int organId = p.getInt("organId");
				result = branchService.getBranchTreeAndMember(null, organId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 获取组织树
	 * @return
	 * @throws ServletException
	 */
	public String getBranchTreeAb() throws ServletException {
		String result = null;
		try {
			String params = getRequestDataByStream();
			JSONObject jo = new JSONObject();

			if (params == null) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				int organId = p.getInt("organId");
				result = branchService.getBranchTree(organId);
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		returnToClient(result);
		return "text";
	}

	/**
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String getInfosAb() throws ServletException {
		String result = null;
		try {
			String params = getRequestDataByStream();
			JSONObject jo = new JSONObject();

			if (params == null) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				String ids = p.getString("ids");
				result = orgService.getInfos(ids);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		returnToClient(result);
		return "text";
	}

	/**
	 * 获取部门成员
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String getBranchMemberByMemberIdsAb() throws ServletException {
		String result = null;
		
		try {
			String params = getRequestDataByStream();
			JSONObject jo = new JSONObject();

			if (params == null) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				String ids = p.getString("ids");
				result = branchService.getBranchMemberByMemberIds(ids);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		returnToClient(result);
		return "text";
	}

	/**
	 * 获取职位
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String getPositionAb() throws ServletException {
		String result = null;
		try {
			String params = getRequestDataByStream();
			JSONObject jo = new JSONObject();

			if (params == null) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				int organId = p.getInt("organId");
				result = branchService.getPosition(organId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		returnToClient(result);
		return "text";
	}

	private BranchService branchService;
	private OrgService orgService;

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}

	public void setBranchService(BranchService branchService) {
		this.branchService = branchService;
	}

}
