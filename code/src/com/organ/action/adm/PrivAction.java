package com.organ.action.adm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.googlecode.sslplugin.annotation.Secured;
import com.organ.common.BaseAction;
import com.organ.model.TPriv;
import com.organ.model.TRole;
import com.organ.service.adm.PrivService;

import net.sf.json.JSONObject;

public class PrivAction extends BaseAction {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 获取角色列表
	 * @return
	 */
	public String getRoleList() {
		int organId = getSessionUserOrganId();
		List list = privService.getRoleList(organId);
		ArrayList<JSONObject> ja = new ArrayList<JSONObject>();
		Iterator it = list.iterator();
		while (it.hasNext()) {
			TRole p = (TRole)it.next();
			JSONObject js = new JSONObject();
			js.put("id", p.getId());
			js.put("name", p.getName());
			ja.add(js);
		}
		
		returnToClient(ja.toString());
		return "text";
	}
	
	 /**
	  * 获取角色所属的成员总数
	  * @return
	  */
	public String getMemberCountByRole() {

		Integer roleId = Integer.parseInt(clearChar(this.request.getParameter("roleid")));

		int c = privService.getMemberCountByRole(roleId);
		
		JSONObject js = new JSONObject();
		js.put("count", c);
		returnToClient(js.toString());
		return "text";
	}
	
	/**
	 * 由角色获取成员
	 * @return
	 */
	public String getMemberByRole() {
		String roleId = clearChar(this.request.getParameter("roleid"));
		Integer roleid = Integer.parseInt(roleId);
		String p = clearChar(this.request.getParameter("page"));
		Integer page = p == null ? null : Integer.parseInt(p);
		String s = clearChar(this.request.getParameter("itemsperpage"));
		Integer itemsperpage = s == null ? null : Integer.parseInt(s);
		
		List list = privService.getMemberByRole(roleid, page, itemsperpage);
		Iterator it = list.iterator();
		
		ArrayList<JSONObject> ja = new ArrayList<JSONObject>();
		while(it.hasNext()) {
			Object[] o = (Object[])it.next();
			JSONObject js = new JSONObject();
			js.put("memberroleid", o[0]);
			js.put("membername", o[1]);
			js.put("branchname", o[2] == null ? "(未分组人员)" : o[2]);
			js.put("positionname", o[3]);
			js.put("memberid", o[4]);
			ja.add(js);
		}
		
		returnToClient(ja.toString());
		return "text";
	}

	/**
	 * 根据角色获取权限
	 * @return
	 */
	public String getPrivByRole() {
	
		Integer roleid = Integer.parseInt(clearChar(this.request.getParameter("roleid")));

		// 返回权限列表，roleid表示该角色具有的权限
		List list = privService.getPrivByRole(roleid);
		Iterator it = list.iterator();
		
		ArrayList<JSONObject> ja = new ArrayList<JSONObject>();
		while(it.hasNext()) {
			Object[] o = (Object[])it.next();
			JSONObject js = new JSONObject();
			js.put("privid", o[0]);
			js.put("privname", o[1]);
			js.put("parentid", o[2]);
			js.put("grouping", o[3]);
			js.put("roleid", o[4] == null ? "" : o[4]);
			ja.add(js);
		}
		returnToClient(ja.toString());
		return "text";
	}
	
	/**
	 * 删除成员角色关系
	 * @return
	 */
	public String delMemberRole() {
		
		Integer id = Integer.parseInt(clearChar(this.request.getParameter("id")));
		
		privService.delMemberRole(id);
		
		return returnajaxid(0);
	}

	/**
	 * 保存角色
	 * @return
	 */
	public String saveRole() {
		
		String id = clearChar(this.request.getParameter("roleId"));
		Integer roleId = (id == null ? 0 : Integer.parseInt(id));
		String roleName = clearChar(this.request.getParameter("rolename"));
		String privs = clearChar(this.request.getParameter("privs"));
		int organId = getSessionUserOrganId();
		roleId = privService.saveRole(roleId, roleName, privs, organId);
		return returnajaxid(roleId);
	}

	/**
	 * 保存成员角色关系
	 * @return
	 */
	public String saveRoleMember() {
		
		Integer roleId = Integer.parseInt(clearChar(this.request.getParameter("roleid")));
		String memberlist = clearChar(this.request.getParameter("memberlist"));
		
		privService.saveRoleMember(roleId, memberlist);
		
		return returnajaxid(0);
	}
	
	/**
	 * 删除角色
	 * @return
	 */
	public String delRole() {
		Integer roleId = Integer.parseInt(clearChar(this.request.getParameter("roleid")));
		privService.delRole(roleId);;
		return returnajaxid(0);
	}
	
	PrivService privService;

	public void setPrivService(PrivService privService) {
		this.privService = privService;
	}
	
}