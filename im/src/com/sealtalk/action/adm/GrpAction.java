package com.sealtalk.action.adm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.googlecode.sslplugin.annotation.Secured;
import com.sealtalk.common.BaseAction;
import com.sealtalk.common.SysInterface;
import com.sealtalk.service.adm.GrpService;
import com.sealtalk.utils.HttpRequest;
import com.sealtalk.utils.JSONUtils;

@Secured
public class GrpAction extends BaseAction {

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
				}
			}

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
				}
			}
		}

		returnToClient(js.toString());
		return "text";
	}

	public String change() {

		Integer groupId = Integer.parseInt(this.request.getParameter("id"));
		Integer groupMemberId = Integer.parseInt(this.request
				.getParameter("gmid"));

		grpService.changeCreator(groupId, groupMemberId);

		return returnajaxid(groupMemberId);
	}
}