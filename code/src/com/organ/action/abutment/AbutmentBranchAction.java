package com.organ.action.abutment;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.googlecode.sslplugin.annotation.Secured;
import com.organ.common.BaseAction;
import com.organ.common.Tips;
import com.organ.model.TOrgan;
import com.organ.service.adm.BranchService;
import com.organ.service.adm.OrgService;
import com.organ.utils.JSONUtils;
import com.organ.utils.LogUtils;


public class AbutmentBranchAction extends BaseAction {

	private static final long serialVersionUID = 5967807252950908349L;
	private static final Logger logger = LogManager.getLogger(AbutmentBranchAction.class);

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
			boolean s = true;
			
			logger.info(params);
			
			if (params == null) {
				s = false;
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				if (!validParams(p)) {
					s = false;
				} else {
					String branchId = p.getString("branchId");
					int organId = p.getInt("organId");
					result = branchService.getBranchMember(branchId, null, organId);
				}
			}
			
			if (!s) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		logger.info(result);
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
			boolean s = true;
			
			logger.info(params);
			if (params == null) {
				s = false;
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				if (!validParams(p)) {
					s = false;
				} else {
					int organId = p.getInt("organId");
					result = branchService.getBranchTreeAndMember(null, organId);
				}
			}
			if (!s) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		
		logger.info(result);
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
			boolean s = true;
			
			logger.info(params);
			if (params == null) {
				s = false;
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				if (!validParams(p)) {
					s = false;
				} else {
					int organId = p.getInt("organId");
					result = branchService.getBranchTree(organId);
				}
			}
			if (!s) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		
		logger.info(result);
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
			boolean s = true;
			
			logger.info(params);
			if (params == null) {
				s = false;
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				if (!validParams(p)) {
					s = false;
				} else {
					String ids = p.getString("ids");
					result = orgService.getInfos(ids);
				}
			}
			if (!s) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		logger.info(result);
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
			boolean s = true;
			
			logger.info(params);
			if (params == null) {
				s = false;
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				if (!validParams(p)) {
					s = false;
				} else {
					String ids = p.getString("ids");
					result = branchService.getBranchMemberByMemberIds(ids);
				}
			}
			if (!s) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		logger.info(result);
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
			boolean s = true;
			
			logger.info(params);
			if (params == null) {
				s = false;
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				if (!validParams(p)) {
					s = false;
				} else {
					int organId = p.getInt("organId");
					jo.put("code", 1);
					jo.put("text", branchService.getPosition(organId));
				}
			}
			if (!s) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
			}
			result = jo.toString();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		logger.info(result);
		returnToClient(result);
		return "text";
	}

	public String getOrganCodeAb() throws ServletException {
		String result = "-1";
		
		try {
			String params = getRequestDataByStream();
			
			logger.info(params);
			if (params != null) {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				if (validParams(p)) {
					String organCode = p.getString("organCode");
					TOrgan to = orgService.getOrganByCode(organCode);
					if (to != null) {
						result = to.getId() + "";
					}
				}
			}
		} catch(Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		
		logger.info(result);
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
