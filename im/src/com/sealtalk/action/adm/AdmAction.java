package com.sealtalk.action.adm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sealtalk.common.BaseAction;
import com.sealtalk.common.Tips;
import com.sealtalk.model.SessionUser;
import com.sealtalk.model.TMember;
import com.sealtalk.service.adm.BranchService;
import com.sealtalk.service.adm.PrivService;
import com.sealtalk.utils.StringUtils;

public class AdmAction extends BaseAction {

	private static final long serialVersionUID = 5652521060219528842L;
	private static final Logger logger = LogManager.getLogger(AdmAction.class);

	private BranchService branchService;
	private PrivService privService;

	public BranchService getBranchService() {
		return branchService;
	}

	public void setBranchService(BranchService branchService) {
		this.branchService = branchService;
	}

	public PrivService getPrivService() {
		return privService;
	}

	public void setPrivService(PrivService privService) {
		this.privService = privService;
	}

	public String getBase() {
		JSONObject js = new JSONObject();
		String privs = (String) this.getSessionAttribute("privs");
		
		if (privs == null) {
			SessionUser su = this.getSessionUser();
			if (su == null) {
				js.put("id", 0);
			} else {
				TMember m = branchService.getMemberByAccount(su.getAccount(), su.getOrganId());
				this.setSessionAttribute("member", m);
				privs = privService.getPrivStringByMember(m.getId());
				this.setSessionAttribute("privs", privs);
				js.put("id", 1);
				js.put("privs", privs);
			}
		} else {
			js.put("id", 1); 
			js.put("privs", privs);
		}
		
		logger.info(js.toString());
 		returnToClient(js.toString());

		return "text";
	}
	/**
	 * 获取指定权限
	 * @return
	 * @throws ServletException
	 */
	public String getTTTPriv() throws ServletException {
		SessionUser su = this.getSessionUser();
		int id = su.getId();
		JSONObject jo = new JSONObject();
		String p = this.request.getParameter("priv");
		
		p = p == null ? "" : p;
		
		List privList = privService.getRoleIdForId(id);
		boolean status = false;
		
		if (privList != null && privList.size() > 0) {
			Iterator it = privList.iterator();
			while (it.hasNext()) {
				ArrayList o = (ArrayList) it.next();
				if (p.equals(o.get(1))) {
					status = true;
					break;
				}
			}
		}
		jo.put("code", 1);
		jo.put("text", status);
		
		returnToClient(jo.toString());
		return "text";
	}	
}