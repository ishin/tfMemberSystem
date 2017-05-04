package com.organ.action.adm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.googlecode.sslplugin.annotation.Secured;
import com.organ.common.BaseAction;
import com.organ.model.TMember;
import com.organ.model.TPosition;
import com.organ.service.adm.PositionService;

import net.sf.json.JSONObject;


public class PosAction extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private PositionService positionService;
	
	public void setPositionService(PositionService positionService) {
		this.positionService = positionService;
	}

	public String getList() {
		
		List list = positionService.getByOrgan(this.getOrganId());
		
		ArrayList<JSONObject> js = new ArrayList<JSONObject>(); 
		Iterator it = list.iterator();
		while (it.hasNext()) {
			TPosition p = (TPosition)it.next();
			JSONObject jo = new JSONObject();
			jo.put("id", p.getId());
			jo.put("name", p.getName());
			js.add(jo);
		}
		
		returnToClient(js.toString());
		return "text";
	}
	
	public String del() {
		
		Integer id = Integer.parseInt(clearChar(this.request.getParameter("id")));
		
		String ret = positionService.del(id);
		
		return returnajaxid(id);
	}
	
	public String save() {

		String name = clearChar(this.request.getParameter("name"));
		
		TPosition p = positionService.save(name, this.getOrganId());
		
		if (p == null) return this.returnajaxid(0);
		
		JSONObject js = new JSONObject();
		js.put("id", p.getId());
		js.put("name", p.getName());
		returnToClient(js.toString());
		
		return "text";
	}
}