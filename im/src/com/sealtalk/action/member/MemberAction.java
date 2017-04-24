package com.sealtalk.action.member;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.googlecode.sslplugin.annotation.Secured;
import com.sealtalk.common.BaseAction;
import com.sealtalk.common.Constants;
import com.sealtalk.common.Tips;
import com.sealtalk.model.SessionUser;
import com.sealtalk.service.member.MemberService;

/**
 * 成员action
 * 
 * @author hao_dy
 * @since jdk1.7
 * @date 2017/01/07
 */
@Secured
public class MemberAction extends BaseAction {

	private static final long serialVersionUID = -9024506148523628104L;
	private static final Logger logger = Logger.getLogger(MemberAction.class);

	/**
	 * 获取单个成员信息
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String getOneOfMember() throws ServletException {
		String result = null;
		result = memberService.getOneOfMember(userid);
		logger.info(result);
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
		int organId = getSessionUserOrganId();
		result = memberService.searchUser(account, organId);
		logger.info(result);
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

		if (memberService != null) {
			result = memberService.updateMemberInfoForWeb(userid, position,
					fullname, sign);
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
		result = memberService.updateMemberForApp(userid, email, mobile, phone,
				address);
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
		int organId = getSessionUserOrganId();
		String result = memberService.getAllMemberInfo(organId);
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
		SessionUser su = getSessionUser();
		String result = "{}";
		int organId = su.getOrganId();
		if (su != null) {
			result = memberService.getAllMemberOnLineStatus(organId, userids);
		}
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

	public String getUserids() {
		return userids;
	}

	public void setUserids(String userids) {
		this.userids = userids;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public MemberService getMemberService() {
		return memberService;
	}

}
