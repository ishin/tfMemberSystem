package com.organ.action.limit;

import javax.servlet.ServletException;

import net.sf.json.JSONArray;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.hp.hpl.sparta.Text;
import com.organ.action.member.MemberAction;
import com.organ.common.BaseAction;
import com.organ.common.Tips;
import com.organ.dao.limit.LimitDao;
import com.organ.service.limit.LimitService;

/**
 * action
 * 
 * @author Lmy
 * 
 */
public class LiMitAction extends BaseAction {

	private static final long serialVersionUID = -8882273369530974698L;
	private static final Logger logger = Logger.getLogger(LiMitAction.class);
	private LimitService limitService;
	private LimitDao limitDao;

	public LimitDao getLimitDao() {
		return limitDao;
	}

	public void setLimitDao(LimitDao limitDao) {
		this.limitDao = limitDao;
	}

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
	 */
	public String AddPriv() throws ServletException, JSONException {
		String name = this.request.getParameter("name");
		String pid = this.request.getParameter("parentId");
		String app = this.request.getParameter("app");
		Integer intPid = pid == null ? null : Integer.parseInt(pid);
		boolean falg = false;
		try {
			String resString = limitService.AddLimit(intPid, name,
					app);
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
		String id = this.request.getParameter("privId");
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
			// TODO: handle exception
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
	 */
	public String EditPriv() throws ServletException, JSONException {
		String id = this.request.getParameter("privId");
		String pid = this.request.getParameter("parentId");
		String name = this.request.getParameter("name");
		String app = this.request.getParameter("app");
		Integer intid = id == null ? null : Integer.parseInt(id);
		boolean flag = false;
		try {
			String result = limitService.EditLimit(intid, pid, name,
					app);
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

	public String SearchPriv() throws ServletException, JSONException {
		String name = this.request.getParameter("name");
		String pagesize = this.request.getParameter("pagesize");
		String pageindex = this.request.getParameter("pageindex");
		Integer intpagesize = pagesize == null ? null : Integer
				.parseInt(pagesize);
		Integer intpageindex = pageindex == null ? null : Integer
				.parseInt(pageindex);
		String result = null;
		result = limitService.searchPriv(name, intpagesize,intpageindex);

		returnToClient(result);
		return "text";
	}
	
	public String getCount() throws ServletException,JSONException{
		boolean falg = false;
		String result = "";
		try {
			result = limitService.getCount()+"";
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

}
