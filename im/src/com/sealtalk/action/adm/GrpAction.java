package com.sealtalk.action.adm;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sealtalk.common.BaseAction;
import com.sealtalk.common.SysInterface;
import com.sealtalk.common.Tips;
import com.sealtalk.service.adm.GrpService;
import com.sealtalk.utils.HttpRequest;
import com.sealtalk.utils.JSONUtils;
import com.sealtalk.utils.StringUtils;

public class GrpAction extends BaseAction {

	private static final long serialVersionUID = -8960513237252698874L;
	private static final Logger logger = LogManager.getLogger(GrpAction.class);

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

	@SuppressWarnings("unchecked")
	public String getList() {
		String p = clearChar(this.request.getParameter("page"));
		String s = clearChar(this.request.getParameter("itemsperpage"));
		String result = null;
		
		if (StringUtils.getInstance().isBlank(p) || StringUtils.getInstance().isBlank(s)) {
			result = failResult(Tips.WRONGPARAMS.getText());
		} else {
			Integer page = p == null ? null : Integer.parseInt(p);
			Integer itemsperpage = s == null ? null : Integer.parseInt(s);
	
			ArrayList<JSONObject> js = new ArrayList<JSONObject>();
	
			List list = grpService.getList(page, itemsperpage);
	
			if (list != null) {
				int len = list.size();
				ArrayList<Integer> memIds = new ArrayList<Integer>();
	
				for (int i = 0; i < len; i++) {
					Object[] tmp =  (Object[]) list.get(i);
					memIds.add((Integer) tmp[4]);
					JSONObject j = new JSONObject();
					j.put("id", tmp[0]);
					j.put("code", tmp[1]);
					j.put("name", tmp[2]);
					j.put("date", tmp[3]);
					j.put("cid", tmp[4]);
					js.add(j);
				}
	
				JSONObject param = new JSONObject();
				param.put("id", memIds.toString());
				param.put("params", new String[] { "id", "fullname" });
				String tpsStr = HttpRequest.getInstance().sendPost(
						SysInterface.GETMEMBERPARAM.getName(), param);
	
				JSONObject ret = JSONUtils.getInstance().stringToObj(tpsStr);
	
				if (ret.getInt("code") == 1) {
					JSONArray arr = JSONUtils.getInstance().stringToArrObj(
							ret.getString("text"));
	
					if (arr != null) {
						for (int i = 0; i < len; i++) {
							JSONObject json = js.get(i);
	
							for (int j = 0; j < arr.size(); j++) {
								JSONObject t = arr.getJSONObject(j);
	
								if (t.getInt("userID") == json.getInt("cid")) {
									json.put("member", t.get("fullname"));
								}
							}
						}
					} else {
						logger.warn("abmember!getMemberParamAb->result is null");
					}
				} else {
					logger.warn("abmember!getMemberParamAb->code is not 1");
				}
			} else {
				logger.warn("group list is null!");
			}
			result = js.toString();
		}
		
		logger.info(result);
		returnToClient(result);
		return "text";
	}

	public String dismiss() {
		String idStr = clearChar(this.request.getParameter("id"));
		Integer id = 0;
		
		int userId = getSessionUser().getId();
		if (!StringUtils.getInstance().isBlank(idStr)) {
			id = Integer.parseInt(idStr);
			grpService.dismiss(userId, id);
		}
		logger.info("id: " + id);
		return returnajaxid(id); 
	}

	public String getMemberCountByGrp() {
		String idStr = clearChar(this.request.getParameter("id"));
		Integer count = 0;
		
		if (!StringUtils.getInstance().isBlank(idStr)) {
			Integer id = Integer.parseInt(idStr);
			count = grpService.getMemberCountByGrp(id);
		}

		logger.info("count: " + count);
		return returnajaxid(count);
	}

	public String getMemberByGrp() {
		String idStr = clearChar(this.request.getParameter("id"));
		String p = clearChar(this.request.getParameter("page"));
		String s = clearChar(this.request.getParameter("itemsperpage"));
		String result = null;
		
		if (StringUtils.getInstance().isBlank(idStr) ||
				StringUtils.getInstance().isBlank(p) ||
				StringUtils.getInstance().isBlank(s)) {
			result = failResult(Tips.WRONGPARAMS.getText());
		} else {
			Integer id = Integer.parseInt(idStr);
			Integer page = p == null ? null : Integer.parseInt(p);
	
			Integer itemsperpage = s == null ? null : Integer.parseInt(s);
			ArrayList<JSONObject> js = new ArrayList<JSONObject>();
			List list = grpService.getMemberByGrp(id, page, itemsperpage);
	
			if (list != null) {
				int len = list.size();
				ArrayList<Integer> memIds = new ArrayList<Integer>();
	
				for (int i = 0; i < len; i++) {
					Object[] tmp =  (Object[]) list.get(i);
					memIds.add((Integer) tmp[2]);
					JSONObject j = new JSONObject();
					j.put("iscreator", tmp[0]);
					j.put("gmid", tmp[1]);
					j.put("cid", tmp[2]);
					js.add(j);
				}
	
				JSONObject param = new JSONObject();
				param.put("id", memIds.toString());
				param.put("params", new String[] { "id", "fullname", "account" });
				String tpsStr = HttpRequest.getInstance().sendPost(
						SysInterface.GETMEMBERPARAM.getName(), param);
	
				JSONObject ret = JSONUtils.getInstance().stringToObj(tpsStr);
	
				if (ret.getInt("code") == 1) {
					JSONArray arr = JSONUtils.getInstance().stringToArrObj(
							ret.getString("text"));
	
					if (arr != null) {
						for (int i = 0; i < len; i++) {
							JSONObject json = js.get(i);
	
							for (int j = 0; j < arr.size(); j++) {
								JSONObject t = arr.getJSONObject(j);
	
								if (t.getInt("userID") == json.getInt("cid")) {
									json.put("member", t.get("fullname"));
									json.put("account", t.get("account"));
								}
							}
						}
					} else {
						logger.warn("abmember!getMemberParamAb->result is null");
					}
				} else {
					logger.warn("abmember!getMemberParamAb->code is not 1");
				}
			} else {
				logger.warn("group list is null");
			}
			result = js.toString();
		}

		logger.info(result);
		returnToClient(result);
		return "text";
	}

	public String change() {

		String idStr = clearChar(this.request.getParameter("id"));
		String memberId = clearChar(this.request.getParameter("gmid"));
		Integer groupMemberId = 0;
		
		if (!StringUtils.getInstance().isBlank(idStr) && !StringUtils.getInstance().isBlank(memberId)) {
			Integer groupId = Integer.parseInt(idStr);
			groupMemberId = Integer.parseInt(memberId);
	
			grpService.changeCreator(groupId, groupMemberId);
		}
		logger.info(groupMemberId);
		return returnajaxid(groupMemberId);
	}
}