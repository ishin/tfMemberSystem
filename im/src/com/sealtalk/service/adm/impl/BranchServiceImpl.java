package com.sealtalk.service.adm.impl;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sealtalk.common.SysInterface;
import com.sealtalk.common.Tips;
import com.sealtalk.model.TMember;
import com.sealtalk.service.adm.BranchService;
import com.sealtalk.utils.HttpRequest;
import com.sealtalk.utils.JSONUtils;
import com.sealtalk.utils.LogUtils;

public class BranchServiceImpl implements BranchService {
	private static final Logger logger = LogManager.getLogger(BranchServiceImpl.class);
	
	@Override
	public String getBranchTreeAndMember(int organId) {
		String result = null;
		try {
			JSONObject jo = new JSONObject();
			jo.put("organId", organId);
			result = HttpRequest.getInstance().sendPost(
					SysInterface.BRANCHMEMBER.getName(), jo);
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		logger.info(result);
		return result;
	}
	
	@Override
	public String getBranchMember(String branchId, int organId) {
		String result = null;
		try {
			JSONObject jo = new JSONObject();
			jo.put("branchId", branchId);
			jo.put("organId", organId);
			result = HttpRequest.getInstance().sendPost(
					SysInterface.MEMBEROFBRANCH.getName(), jo);
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		logger.info(result);
		return result;
	}

	@Override
	public String getBranchTree(int organId) {
		String result = null;
		try {
			JSONObject jo = new JSONObject();
			jo.put("organId", organId);
			result = HttpRequest.getInstance().sendPost(
					SysInterface.BRANCHTREE.getName(), jo);
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		logger.info(result);
		return result;
	}

	@Override
	public String getPosition(int organId) {
		String result = null;
		
		try {
			JSONObject jo = new JSONObject();
			jo.put("organId", organId);
			String ret = HttpRequest.getInstance().sendPost(
					SysInterface.GETPOSITION.getName(), jo);
			if (ret != null) {
				result = ret;
			} else {
				JSONObject r = new JSONObject();
				r.put("code", 0);
				r.put("text", Tips.FAIL.getText());
			}
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		logger.info(result);
		return result;
	}

	@Override
	public TMember getMemberByAccount(String account, int organId) {
		
		try {
			JSONObject p = new JSONObject();
			p.put("account", account);
			p.put("organId", organId);
			String result = HttpRequest.getInstance().sendPost(
					SysInterface.GETMEMBERBYACCOUNT.getName(), p);
			JSONObject json = JSONUtils.getInstance().stringToObj(result);
			
			if (json.getInt("code") == 1) {
				JSONObject text = JSONUtils.getInstance().stringToObj(json.getString("text"));
				TMember tm = JSONUtils.getInstance().jsonObjToBean(text, TMember.class);
				logger.info(text.toString());
				return tm;
			} else {
				logger.warn("abmember!getMemberByAccountAb result is null");
			}
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public int getOrganIdByOrganCode(String organCode) {
		int oid= -1;
		
		try {
			JSONObject jo = new JSONObject();
			jo.put("organCode", organCode);
			String result = HttpRequest.getInstance().sendPost(
					SysInterface.GETORGANCODE.getName(), jo);
			
			oid = Integer.parseInt(result);
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		logger.info("oid: " + oid);
		return oid;
	}
	
}
