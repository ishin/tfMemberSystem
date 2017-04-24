package com.sealtalk.service.msg.impl;


import net.sf.json.JSONObject;

import com.sealtalk.common.SysInterface;
import com.sealtalk.common.Tips;
import com.sealtalk.model.AppSecret;
import com.sealtalk.service.msg.MessageService;
import com.sealtalk.utils.HttpRequest;
import com.sealtalk.utils.JSONUtils;
import com.sealtalk.utils.RongCloudUtils;
import com.sealtalk.utils.StringUtils;

public class MessageServiceImpl implements MessageService {

	@Override
	public String sendSysMsg(String fromId, String targetIds,
			String targetNames, String msg, String extraMsg,
			String pushContent, String pushData, String isPersisted,
			String isCounted, int organId) {

		try {
			if (!StringUtils.getInstance().isBlank(fromId)
					&& !StringUtils.getInstance().isBlank(msg)) {

				boolean s = true;
				String[] targetIdsArr = null;

				if (!StringUtils.getInstance().isBlank(targetIds)) {
					targetIds = StringUtils.getInstance().replaceChar(
							targetIds, "\"", "");
					targetIds = StringUtils.getInstance().replaceChar(
							targetIds, "[", "");
					targetIds = StringUtils.getInstance().replaceChar(
							targetIds, "]", "");

					targetIdsArr = targetIds.split(",");
				} else if (!StringUtils.getInstance().isBlank(targetNames)) {
					targetNames = StringUtils.getInstance().replaceChar(
							targetNames, "\"", "");
					targetNames = StringUtils.getInstance().replaceChar(
							targetNames, "[", "");
					targetNames = StringUtils.getInstance().replaceChar(
							targetNames, "]", "");
					targetNames = StringUtils.getInstance().replaceChar(
							targetNames, " ", "");

					String[] accounts = targetNames.split(",");
					JSONObject p = new JSONObject();
					p.put("accounts", accounts);
					p.put("organId", organId);
					String result = HttpRequest.getInstance().sendPost(
							SysInterface.MEMBERIDSBYACCOUNT.getName(), p);

					if (result != null) {
						JSONObject r = JSONUtils.getInstance().stringToObj(result);
						if (r.getInt("code") == 1) {
							targetIdsArr = StringUtils.getInstance().strToArray(r.getString("text"));
						} else {
							s = false;
						}
					} else {
						s = false;
					}
				} else {
					s = false;
				}

				if (s) {
					for (int i = 0; i < targetIdsArr.length; i++) {
						targetIdsArr[i] = targetIdsArr[i].trim();
					}

					String msgType = "1";

					return RongCloudUtils.getInstance().sendSysMsg(fromId,
							targetIdsArr, msg, extraMsg, pushContent, pushData,
							isPersisted, isCounted, msgType);
				} else {
					JSONObject jo = new JSONObject();
					jo.put("code", 0);
					jo.put("text", Tips.WRONGPARAMS.getText());
					return jo.toString();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public String sendPrivateMsg(String fromId, String targetIds,
			String targetNames, String msg, String extraMsg,
			String pushContent, String count, String verifyBlacklist,
			String isPersisted, String isCounted, int organId) {

		try {
			if (!StringUtils.getInstance().isBlank(fromId)
					&& !StringUtils.getInstance().isBlank(msg)) {
				
				boolean s = true;
				String[] targetIdsArr = null;

				if (!StringUtils.getInstance().isBlank(targetIds)) {
					targetIds = StringUtils.getInstance().replaceChar(
							targetIds, "\"", "");
					targetIds = StringUtils.getInstance().replaceChar(
							targetIds, "[", "");
					targetIds = StringUtils.getInstance().replaceChar(
							targetIds, "]", "");

					targetIdsArr = targetIds.split(",");
				} else if (!StringUtils.getInstance().isBlank(targetNames)) {
					targetNames = StringUtils.getInstance().replaceChar(
							targetNames, "\"", "");
					targetNames = StringUtils.getInstance().replaceChar(
							targetNames, "[", "");
					targetNames = StringUtils.getInstance().replaceChar(
							targetNames, "]", "");
					targetNames = StringUtils.getInstance().replaceChar(
							targetNames, " ", "");

					String[] accounts = targetNames.split(",");
					JSONObject p = new JSONObject();
					p.put("accounts", accounts);
					p.put("organId", organId);
					String result = HttpRequest.getInstance().sendPost(
							SysInterface.MEMBERIDSBYACCOUNT.getName(), p);

					if (result != null) {
						JSONObject r = JSONUtils.getInstance().stringToObj(result);
						if (r.getInt("code") == 1) {
							targetIdsArr = StringUtils.getInstance().strToArray(r.getString("text"));
						} else {
							s = false;
						}
					} else {
						s = false;
					}
				} else {
					s = false;
				}

				if (s) {
					for (int i = 0; i < targetIdsArr.length; i++) {
						targetIdsArr[i] = targetIdsArr[i].trim();
					}
					
					String msgType = "1";

					return RongCloudUtils.getInstance().sendPrivateMsg(fromId,
							targetIdsArr, msg, extraMsg, pushContent, count,
							verifyBlacklist, isPersisted, isCounted, msgType);
				} else {
					JSONObject jo = new JSONObject();
					jo.put("code", 0);
					jo.put("text", Tips.WRONGPARAMS.getText());
					return jo.toString();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public AppSecret validAppIdAndSecret(String appId, String secret) {
		try {
			JSONObject jo = new JSONObject();
			jo.put("appId", appId);
			jo.put("secret", secret);
			String result = HttpRequest.getInstance().sendPost(
					SysInterface.VALIDAPPSECRET.getName(), jo);
			
			JSONObject ret = JSONUtils.getInstance().stringToObj(result);
			
			if (ret.getInt("code") == 1) {
				String txt = ret.getString("text");
				JSONObject asj = JSONUtils.getInstance().stringToObj(txt);
				AppSecret as = JSONUtils.getInstance().jsonObjToBean(asj, AppSecret.class);
				return as;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
