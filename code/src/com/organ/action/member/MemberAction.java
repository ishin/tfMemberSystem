package com.organ.action.member;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.organ.common.BaseAction;
import com.organ.common.Tips;
import com.organ.service.member.MemberService;
import com.organ.utils.StringUtils;

/**
 * 成员action
 * 
 * @author hao_dy
 * @since jdk1.7
 * @date 2017/01/07
 */

public class MemberAction extends BaseAction {

	private static final long serialVersionUID = -9024506148523628104L;
	private static final Logger logger = LogManager
			.getLogger(MemberAction.class);

	/**
	 * 获取单个成员信息
	 * 
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String getOneOfMember() throws ServletException, IOException {
		String result = null;

		try {
			if (StringUtils.getInstance().isBlank(userid)) {
				JSONObject jo = new JSONObject();
				jo.put("code", 0);
				jo.put("text", Tips.NULLUSER);
			} else {
				result = memberService.getOneOfMember(clearChar(userid));
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
	 * 
	 * @return
	 * @throws Servlet
	 */
	public String searchUser() throws ServletException {
		String result = null;

		try {
			if (StringUtils.getInstance().isBlank(account)) {
				JSONObject jo = new JSONObject();
				jo.put("code", 0);
				jo.put("text", Tips.NULLUSER);
			} else {
				int organId = getSessionUserOrganId();
				result = memberService.searchUser(clearChar(account), organId);
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

		if (memberService != null) {
			result = memberService.updateMemberInfoForWeb(clearChar(userid),
					clearChar(position), clearChar(fullname), clearChar(sign));
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
		result = memberService.updateMemberForApp(clearChar(userid),
				clearChar(email), clearChar(mobile), clearChar(phone),
				clearChar(address));
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
		int organId = getSessionUserOrganId();
		String result = memberService.getAllMemberOnLineStatus(organId,
				clearChar(userids));
		returnToClient(result);
		return "text";
	}

	/**
	 * 逻辑删除成员
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String logicDelMemberByUserIds() throws ServletException {
		String result = memberService
				.logicDelMemberByUserIds(clearChar(userids));
		returnToClient(result);
		return "text";
	}

	public String exportsMember() throws ServletException, FileNotFoundException, UnsupportedEncodingException {
		int organId = getSessionUserOrganId();
		String realPath = request.getSession().getServletContext().getRealPath("/");
		String downFileName = memberService.exportsMember(organId, realPath);

		if (downFileName != null) {
			fileName = downFileName;
			inputStream = new FileInputStream(new File(realPath + "exports/" + downFileName)); 
			return "down";
		} else {
			JSONObject jo = new JSONObject();
			jo.put("code", 0);
			jo.put("text", Tips.FAIL.getText());
			returnToClient(jo.toString());
			return "text";
		}
	}

	// 文件下载
	public InputStream getInputStream() {
		return inputStream;
	}

	private MemberService memberService;

	public void setMemberService(MemberService ms) {
		this.memberService = ms;
	}

	private String userid;
	private String account;
	private String fullname;
	private String position;
	private String email;
	private String phone;
	private String mobile;
	private String sign;
	private String address;
	private String userids;
	private String fileName;
	private InputStream inputStream;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

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

	public void setPosition(String position) {
		this.position = position;
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

}
