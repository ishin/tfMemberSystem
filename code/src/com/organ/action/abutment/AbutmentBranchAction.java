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
	 *  
	 * @return
	 * @throws ServletException
	 */
	public String getInfos() throws ServletException {
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
	 * @return
	 * @throws ServletException
	 */
	public String getBranchMemberByMemberIds() throws ServletException {
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
	
	private BranchService branchService;
	private OrgService orgService;

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}

	public void setBranchService(BranchService branchService) {
		this.branchService = branchService;
	}
	
}
