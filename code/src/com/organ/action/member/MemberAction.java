package com.organ.action.member;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.googlecode.sslplugin.annotation.Secured;
import com.organ.common.BaseAction;
import com.organ.common.Tips;
import com.organ.service.member.MemberService;

/**
 * 成员action
 * @author hao_dy
 * @since jdk1.7
 * @date 2017/01/07
 */

public class MemberAction extends BaseAction {

	private static final long serialVersionUID = -9024506148523628104L;
	private static final Logger logger = Logger.getLogger(MemberAction.class);
	
	/**
	 * 获取单个成员信息
	 * @return
	 * @throws ServletException
	 * @throws IOException 
	 */
	public String getOneOfMember() throws ServletException, IOException {
		String result = null;
		
		try {
			if (userid == null || "".equals(userid)) {
				JSONObject jo = new JSONObject();
				jo.put("code", 0);
				jo.put("text", Tips.NULLUSER);
			} else {
				result = memberService.getOneOfMember(userid);
			}
			
			logger.info(result);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 搜索用户(账号、拼音)
	 * @return
	 * @throws Servlet
	 */
	public String searchUser() throws ServletException {
		String result = null;
		
		try {
			if (account == null || "".equals(account)) {
				JSONObject jo = new JSONObject();
				jo.put("code", 0);
				jo.put("text", Tips.NULLUSER);
			} else {
				int organId = getSessionUserOrganId();
				result = memberService.searchUser(account, organId);
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
	 * @return
	 * @throws ServletException
	 */
	public String updateMemberInfoForWeb() throws ServletException {
		String result = null;
		
		if (memberService != null) {
			result = memberService.updateMemberInfoForWeb(userid, position, fullname,  sign);
		} else {
			JSONObject jo = new JSONObject();
			jo.put("code", 0);
			jo.put("text", Tips.FAIL.getText());
			result = jo.toString();
		}
		
		returnToClient(result);
		return "text";
	}
	
	public String updateMemberInfoForApp() throws ServletException {
		String result = "{}";
		result = memberService.updateMemberForApp(userid, email, mobile, phone, address);
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 获取所有成员
	 * @return
	 * @throws ServletException
	 */
	public String getAllMemberInfo() throws ServletException {
		int organId = getSessionUserOrganId();
		String result = memberService.getAllMemberInfo(organId);
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 获取成员在线状态，如果不传成员id，则表示全部成员
	 * @return
	 * @throws ServletException
	 */
	public String getAllMemberOnLineStatus() throws ServletException {
		int organId = getSessionUserOrganId();
		String result = memberService.getAllMemberOnLineStatus(organId, userids);
		returnToClient(result);
		return "text";
	}
	
	private MemberService memberService;
	
	public void setMemberService(MemberService ms) {
		this.memberService = ms;
	}
	
	private String userid;
	private String account;
	private String sex;
	private String fullname;
	private String position;
	private String branch;
	private String email;
	private String phone;
	private String mobile;
	private String sign;
	private String logo;
	private String address;
	private String userids;

	public void setUserids(String userids) {
		this.userids = userids;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public void setSign(String sign) {
		this.sign = sign;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

}
