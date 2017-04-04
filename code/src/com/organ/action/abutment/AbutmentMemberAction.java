package com.organ.action.abutment;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.organ.common.BaseAction;
import com.organ.common.Tips;
import com.organ.service.member.MemberService;
import com.organ.utils.JSONUtils;
import com.organ.utils.LogUtils;

/**
 * 成员action
 * 
 * @author hao_dy
 * @since jdk1.7
 * @date 2017/01/07
 */

public class AbutmentMemberAction extends BaseAction {

	private static final long serialVersionUID = -7324946068454866523L;
	private static final Logger logger = Logger
			.getLogger(AbutmentMemberAction.class);

	/**
	 * 获取单个成员信息
	 * 
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String getOneOfMember() throws ServletException, IOException {
		String result = null;
		JSONObject jo = null;

		try {
			String params = getRequestDataByStream();
			String userId = null;

			if (params != null) {
				jo = JSONUtils.getInstance().stringToObj(params);
				userId = jo.getString("userId");
				result = memberService.getOneOfMember(userId);
			} else {
				jo = new JSONObject();
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		returnToClient(result);
		return "text";
	}

	/**
	 * 搜索用户(账号、拼音)
	 * 
	 * @return
	 * @throws Servlet
	 */
	public String searchUser() throws ServletException {
		String result = null;
		JSONObject jo = null;

		try {
			String params = getRequestDataByStream();

			if (params == null) {
				JSONArray ja = new JSONArray();
				jo = new JSONObject();
				jo.put("code", 0);
				jo.put("text", Tips.NULLUSER);
				ja.add(jo);
				result = ja.toString();
			} else {
				String account = null;
				jo = JSONUtils.getInstance().stringToObj(params);
				account = jo.getString("account");
				result = memberService.searchUser(account);
			}

			logger.info(result);

		} catch (Exception e) {
			e.printStackTrace();
		}

		returnToClient(result);
		return "text";
	}

	/**
	 * 个人设置保存(web端)
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String updateMemberInfoForWeb() throws ServletException {
		String result = null;
		JSONObject jo = null;
		
		try {
			String params = getRequestDataByStream();
			String userId = null;
			String position = null;
			String fullName = null;
			String sign = null;
			
			if (params != null) {
				jo = JSONUtils.getInstance().stringToObj(params);
				userId = jo.getString("userId");
				position = jo.getString("position");
				fullName = jo.getString("fullName");
				sign = jo.getString("sign");
				result = memberService.updateMemberInfoForWeb(userId, position, fullName, sign);
			} else {
				jo = new JSONObject();
				jo.put("code", -1);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		returnToClient(result);
		return "text";
	}

	/**
	 * 个人信息保存app端
	 * @return
	 * @throws ServletException
	 */
	public String updateMemberInfoForApp() throws ServletException {
		String result = null;
		JSONObject jo = new JSONObject();
		
		try {
			String params = getRequestDataByStream();
			String userId = null;
			String email = null;
			String mobile = null;
			String phone = null;
			String address = null;
			
			if (params != null) {
				jo = JSONUtils.getInstance().stringToObj(params);
				userId = jo.getString("userId");
				email = jo.getString("email");
				mobile = jo.getString("mobile");
				phone = jo.getString("phone");
				address = jo.getString("address");
				result = memberService.updateMemberForApp(userId, email, mobile, phone, address);
			} else { 
				jo = new JSONObject();
				jo.put("code", -1);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		
		returnToClient(result);         
		return "text";
	}

	/**
	 * 获取所有成员
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String getAllMemberInfo() throws ServletException {
		String result = memberService.getAllMemberInfo();
		returnToClient(result);
		return "text";
	}

	/**
	 * 获取成员在线状态，如果不传成员id，则表示全部成员
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String getAllMemberOnLineStatus() throws ServletException {
		String result = null;
		JSONObject jo = null;
		
		try {
			String params = getRequestDataByStream();
			String userIds = null;
			
			if (!params.equals("")) {
				jo = JSONUtils.getInstance().stringToObj(params);
				userIds = jo.getString("userIds");
			}
			result = memberService.getAllMemberOnLineStatus(userIds);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		returnToClient(result);
		return "text";
	}

	/**
	 * 多账号查询成员
	 * @return
	 * @throws ServletException
	 */
	public String getMultipleMemberForAccounts() throws ServletException {
		String result = null;
		JSONObject jo = null;
		
		try {
			String params = getRequestDataByStream();
			
			if (params != null) {
				String mulMemberStr = null;
				jo = JSONUtils.getInstance().stringToObj(params);
				mulMemberStr = jo.getString("mulMemberStr");
				result = memberService.getMultipleMemberForAccounts(mulMemberStr);
			} else {
				jo = new JSONObject();
				jo.put("code", -1);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 统计成员数量
	 * @return
	 * @throws ServletException
	 */
	public String getMemberCount() throws ServletException {
		String result = null;
		JSONObject jo = null;
		
		try {
			int count = memberService.countMember();
			jo.put("code", 1);
			jo.put("text", count);
			result = jo.toString();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		
		returnToClient(result);
		return "text";
	}
	private MemberService memberService;

	public void setMemberService(MemberService ms) {
		this.memberService = ms;
	}

}
