package com.sealtalk.action.adm;

import net.sf.json.JSONObject;

import com.googlecode.sslplugin.annotation.Secured;
import com.sealtalk.common.BaseAction;
import com.sealtalk.model.SessionUser;
import com.sealtalk.model.TMember;
import com.sealtalk.service.adm.BranchService;
import com.sealtalk.service.adm.PrivService;

@Secured
public class AdmAction extends BaseAction {

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