package com.organ.action.adm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.organ.common.BaseAction;
import com.organ.service.adm.GrpService;

import net.sf.json.JSONObject;

public class GrpAction extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8960513237252698874L;

	private GrpService grpService;
	
	public GrpService getGrpService() {
		return grpService;
	}
	public void setGrpService(GrpService grpService) {
		this.grpService = grpService;
	}

	public String getCount() {
		
		return returnajaxid(grpService.getCount());
	}

	public String getList() {
		
		String p = this.request.getParameter("page");
		Integer page = p == null ? null : Integer.parseInt(p);
		String s = this.request.getParameter("itemsperpage");
		Integer itemsperpage = s == null ? null : Integer.parseInt(s);

		List list = grpService.getList(page, itemsperpage);
		
		ArrayList<JSONObject> js = new ArrayList<JSONObject>();
		Iterator it = list.iterator();
		while (it.hasNext()) {
			Object[] o = (Object[])it.next();
			JSONObject j = new JSONObject();
			j.put("id", o[0]);
			j.put("code", o[1]);
			j.put("name", o[2]);
			j.put("date", o[3]);
			j.put("member", o[4]);
			js.add(j);
		}
		
		returnToClient(js.toString());
		return "text";
	}

	public String dismiss() {

		Integer id = Integer.parseInt(this.request.getParameter("id"));

		grpService.dismiss(id);
		
		return returnajaxid(id);
	}

	public String getMemberCountByGrp() {
		
		Integer id = Integer.parseInt(this.request.getParameter("id"));
		
		Integer count = grpService.getMemberCountByGrp(id);
		
		return returnajaxid(count);
	}
	
	public String getMemberByGrp() {
		
		Integer id = Integer.parseInt(this.request.getParameter("id"));
		String p = this.request.getParameter("page");
		Integer page = p == null ? null : Integer.parseInt(p);
		String s = this.request.getParameter("itemsperpage");
		Integer itemsperpage = s == null ? null : Integer.parseInt(s);

		List list = grpService.getMemberByGrp(id, page, itemsperpage);
		
		ArrayList<JSONObject> js = new ArrayList<JSONObject>();
		Iterator it = list.iterator();
		while (it.hasNext()) {
			Object[] o = (Object[])it.next();
			JSONObject j = new JSONObject();
			j.put("iscreator", o[0]);
			j.put("name", o[1]);
			j.put("account", o[2]);
			j.put("gmid", o[3]);
			js.add(j);
		}
		
		returnToClient(js.toString());
		return "text";
	}
	
	public String change() {
		
		Integer groupId = Integer.parseInt(this.request.getParameter("id"));
		Integer groupMemberId = Integer.parseInt(this.request.getParameter("gmid"));

		grpService.changeCreator(groupId, groupMemberId);
		
		return returnajaxid(groupMemberId);
	}
}