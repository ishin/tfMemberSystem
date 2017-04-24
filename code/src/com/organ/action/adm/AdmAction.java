package com.organ.action.adm;

import com.googlecode.sslplugin.annotation.Secured;
import com.organ.common.BaseAction;
import com.organ.model.SessionUser;
import com.organ.model.TMember;
import com.organ.service.adm.BranchService;
import com.organ.service.adm.PrivService;

import net.sf.json.JSONObject;

@Secured
public class AdmAction extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5652521060219528842L;

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

		returnToClient(js.toString());

		return "text";
	}
}