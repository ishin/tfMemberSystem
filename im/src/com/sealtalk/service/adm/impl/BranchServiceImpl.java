package com.sealtalk.service.adm.impl;

import net.sf.json.JSONObject;

import com.sealtalk.common.SysInterface;
import com.sealtalk.model.TMember;
import com.sealtalk.service.adm.BranchService;
import com.sealtalk.utils.HttpRequest;
import com.sealtalk.utils.JSONUtils;

public class BranchServiceImpl implements BranchService {

	@Override
	public String getBranchTreeAndMember(int organId) {
		String result = null;
		try {
			JSONObject jo = new JSONObject();
			jo.put("organId", organId);
			result = HttpRequest.getInstance().sendPost(
					SysInterface.BRANCHMEMBER.getName(), jo);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
			e.printStackTrace();
		}
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
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public String getPosition(int organId) {
		try {
			JSONObject jo = new JSONObject();
			jo.put("organId", organId);
			String result = HttpRequest.getInstance().sendPost(
					SysInterface.GETPOSITION.getName(), jo);
			JSONObject ret = JSONUtils.getInstance().stringToObj(result);
			return ret.getString("text");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public TMember getMemberByAccount(String account, int organId) {
		try {
			JSONObject p = new JSONObject();
			p.put("account", account);
			p.put("organId", organId);
			String result = HttpRequest.getInstance().sendPost(
					SysInterface.GETMEMBERBYACCOUNT.getName(), p);
			JSONObject ret = JSONUtils.getInstance().stringToObj(result);
			
			if (ret.getInt("code") == 1) {
				JSONObject text = JSONUtils.getInstance().stringToObj(ret.getString("text"));
				TMember tm = JSONUtils.getInstance().jsonObjToBean(text, TMember.class);
				
				return tm;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public int getOrganIdByOrganCode(String organCode) {
		try {
			JSONObject jo = new JSONObject();
			jo.put("organCode", organCode);
			String result = HttpRequest.getInstance().sendPost(
					SysInterface.GETORGANCODE.getName(), jo);
			
			return Integer.parseInt(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return -1;
	}
	
}
