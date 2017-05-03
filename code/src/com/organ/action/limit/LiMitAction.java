 package com.organ.action.limit;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonIOException;
import com.organ.common.BaseAction;
import com.organ.service.limit.LimitService;

/**
 * action
 * 
 * @author Lmy
 * 
 */

public class LiMitAction extends BaseAction {

	private static final long serialVersionUID = -8882273369530974698L;
	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(LiMitAction.class);
	private LimitService limitService;

	public LimitService getLimitService() {
		return limitService;
	}

	public void setLimitService(LimitService limitService) {
		this.limitService = limitService;
	}

	/**
	 * 添加权限
	 * 
	 * @return
	 * @throws ServletException
	 * @throws JSONException
	 * @throws UnsupportedEncodingException
	 */
	public String AddPriv() throws ServletException, JSONException,
			UnsupportedEncodingException {
		String pid = clearChar(this.request.getParameter("parentId"));
		String name = clearChar(this.request.getParameter("name"));
		String app = clearChar(this.request.getParameter("app"));
		int organId = getSessionUserOrganId();
		Integer intPid = pid == null ? null : Integer.parseInt(pid);
		boolean falg = false;
		try {
			String resString = limitService.AddLimit(intPid, name, app, organId);
			if ("".equals(resString) && null == resString) {
				falg = false;
			} else {
				falg = true;
			}
		} catch (Exception e) {
			falg = false;
			e.printStackTrace();
		}
		JSONObject jsonObject = new JSONObject();
		if (falg) {
			jsonObject.put("code", 1 + "");
			jsonObject.put("text", "更新成功");

		} else {
			jsonObject.put("code", 0 + "");
			jsonObject.put("text", "更新失败");

		}
		returnToClient(jsonObject.toString());
		return "text";
	}

	/**
	 * 删除权限
	 * 
	 * @return
	 * @throws ServletException
	 * @throws JSONException
	 */
	public String DelPriv() throws ServletException, JSONException {
		String id = clearChar(this.request.getParameter("privId"));
		Integer intid = id == null ? null : Integer.parseInt(id);
		boolean flag = false;
		try {
			String result = limitService.DelLimit(intid);
			if ("".equals(result) && null == result) {
				flag = false;
			} else {
				flag = true;
			}
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		JSONObject jsonObject = new JSONObject();
		if (flag) {
			jsonObject.put("code", 1 + "");
			jsonObject.put("text", "删除权限成功");
		} else {
			jsonObject.put("code", 0 + "");
			jsonObject.put("text", "删除权限失败");
		}
		returnToClient(jsonObject.toString());
		return "text";
	}

	/**
	 * 编辑权限
	 * 
	 * @return
	 * @throws ServletException
	 * @throws JSONException
	 * @throws UnsupportedEncodingException
	 */
	public String EditPriv() throws ServletException, JSONException,
			UnsupportedEncodingException {
		/*
		 * byte bufname[] = request.getParameter("name").getBytes("iso8859-1");
		 * byte bufapp[] = request.getParameter("app").getBytes("iso8859-1");
		 */
		String id = clearChar(this.request.getParameter("privId"));
		String pid = clearChar(this.request.getParameter("parentId"));
		String name = clearChar(this.request.getParameter("name"));
		String app = clearChar(this.request.getParameter("app"));
		/*
		 * String name = new String(bufname,"utf-8"); String app = new
		 * String(bufapp,"utf-8");
		 */
		Integer intid = id == null ? null : Integer.parseInt(id);
		boolean flag = false;
		try {
			String result = limitService.EditLimit(intid, pid, name, app);
			if ("".equals(result) && null == result) {
				flag = false;
			} else {
				flag = true;
			}
		} catch (Exception e) {
			// TODO: handle exception
			flag = false;
			e.printStackTrace();
		}
		JSONObject jsonObject = new JSONObject();
		if (flag) {
			jsonObject.put("code", 1 + "");
			jsonObject.put("text", "编辑权限成功");
		} else {
			jsonObject.put("code", 0 + "");
			jsonObject.put("text", "编辑权限失败");
		}
		returnToClient(jsonObject.toString());
		return "text";
	}

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String SearchPriv() throws ServletException, JSONException,
			UnsupportedEncodingException {
		String pagesize = clearChar(this.request.getParameter("pagesize"));
		String pageindex = clearChar(this.request.getParameter("pageindex"));
		String name = clearChar(this.request.getParameter("name"));
		Integer intpagesize = pagesize == null ? null : Integer.parseInt(pagesize);
		Integer intpageindex = pageindex == null ? null : Integer.parseInt(pageindex);
		int organId = getSessionUserOrganId();
		String result = limitService.searchPriv(organId, name, intpagesize, intpageindex);
		returnToClient(result);
		return "text";
	}

	public String getCount() throws ServletException, JSONException {
		boolean falg = false;
		String result = "";
		int organId = getSessionUserOrganId();
		try {
			result = limitService.getCount(organId) + "";
			if ("".equals(result) && null == result) {
				falg = false;
			} else {
				falg = true;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			falg = false;
		}
		JSONObject jsonObject = new JSONObject();
		if (falg) {
			jsonObject.put("id", result);
		} else {
			jsonObject.put("code", 0 + "");
			jsonObject.put("text", "请求失败");
		}
		returnToClient(jsonObject.toString());
		return "text";
	}

	public String getLimitByRole() throws ServletException, JSONException {
		Integer roleid = Integer.parseInt(clearChar(this.request.getParameter("roleid")));
		String appName = clearChar(this.request.getParameter("appname"));
		
		List list = limitService.getLimitbyRole(roleid, appName);
		Iterator it = list.iterator();
		ArrayList<JSONObject> ja = new ArrayList<JSONObject>();
		while (it.hasNext()) {
			Object[] o = (Object[]) it.next();
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

	public String getRoleList() throws ServletException, JsonIOException {
		String result = null;
		
		try {
			String appId = clearChar(this.request.getParameter("appId"));
			boolean b = com.organ.utils.StringUtils.getInstance().isBlank(appId);
			Integer appIdInt = !b ? Integer.parseInt(appId) : 0;
			int organId = getSessionUserOrganId();
			result = limitService.getRoleList(appIdInt, organId);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		returnToClient(result);
		return "text";
	}

	public String getPrivNamebyclass() throws ServletException, JSONException {
		String appname = clearChar(this.request.getParameter("appName"));
		int organId = getSessionUserOrganId();
		String result = limitService.getPrivNamebytwo(organId, appname);
		returnToClient(result);
		return "text";
	}

	public String saveRolebyApp() throws ServletException, JSONException {
		// roleId, roleName, privs, appName
		//String roleId = this.request.getParameter("roleid");
		//Integer introleId = (roleId == null ? 0 : Integer.parseInt(roleId));
		String roleIdStr = clearChar(this.request.getParameter("roleid"));
		boolean b = StringUtils.isBlank(roleIdStr);
		Integer roleId = (!b && !roleIdStr.equals("0")) ? Integer.parseInt(roleIdStr) : -1;
		
		String roleName = clearChar(this.request.getParameter("roleName"));
		String privs = clearChar(this.request.getParameter("privs"));
		Integer appsecretId = Integer.parseInt(clearChar(this.request.getParameter("appsecretId")));
		int organId = getSessionUserOrganId();
		boolean falg = false;
		String result = "";
		try {
			result = limitService.saveRolebyApp(roleId, appsecretId,
					roleName, privs, organId);
			if ("".equals(result) && null == result) {
				falg = false;
			} else {
				falg = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			falg = false;
		}
		JSONObject jsonObject = new JSONObject();
		if (falg) {
			jsonObject.put("code", 1 + "");
			jsonObject.put("text", "保存成功");
		} else {
			jsonObject.put("code", 0 + "");
			jsonObject.put("text", "保存失败");
		}
		returnToClient(jsonObject.toString());
		return "text";
	}

	/**
	 * 删除角色
	 * 
	 * @return
	 */
	public String delRole() {
		Integer roleId = Integer.parseInt(clearChar(this.request.getParameter("roleId")));
		limitService.delRole(roleId);
		return returnajaxid(0);
	}

}
