package com.organ.action.sys;

import java.io.IOException;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.organ.common.BaseAction;
import com.organ.common.Constants;
import com.organ.common.Tips;
import com.organ.model.SessionUser;
import com.organ.model.TMember;
import com.organ.model.TOrgan;
import com.organ.service.adm.OrgService;
import com.organ.service.member.MemberService;
import com.organ.utils.JSONUtils;
import com.organ.utils.MathUtils;
import com.organ.utils.PropertiesUtils;
import com.organ.utils.RongCloudUtils;
import com.organ.utils.StringUtils;
import com.organ.utils.TextHttpSender;
import com.organ.utils.TimeGenerator;

/**
 * 系统相关
 * @since jdk1.7
 * @author hao_dy
 *
 */
public class SystemAction extends BaseAction {
	
	private static final long serialVersionUID = -3901445181785461508L;
	private static final Logger logger = LogManager.getLogger(SystemAction.class);
	
	/**
	 * 跳转登陆页面
	 * @return
	 * @throws Exception
	 */
	public String login() throws IOException, ServletException {
	
		if (getSessionUser() == null) {
			int i = 0;
			return "loginPage";
		} else {
			return "loginSuccess";
		}
	}
	
	/**
	 * 登陆验证
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */
	public String afterLogin() throws IOException, ServletException {
		JSONObject result = new JSONObject();
		
		if (StringUtils.getInstance().isBlank(account)) {
			result.put("code", 0);
			result.put("text", Tips.NULLUSER.getText());
			returnToClient(result.toString());
			return "text";
		}
	
		String organCode = clearChar(this.request.getParameter("organCode"));

		if (StringUtils.getInstance().isBlank(organCode)) {
			result.put("code", 0);
			result.put("text", Tips.NULLCODE.getText());
			returnToClient(result.toString());
			return "text";
		}
		organCode = organCode.toUpperCase();
		TOrgan organ = orgService.getOrganByCode(organCode);

		int organId = 0;
		
		if (organ == null) {
			result.put("code", 0);
			result.put("text", Tips.NULLCODE.getText());
			returnToClient(result.toString());
			return "text";
		}
		
		organId = organ.getId();
		
		TMember member = memberService.getSuperAdmin(clearChar(account), clearChar(userpwd), organId);
		
		if(member == null) {
			result.put("code", 0);
			result.put("text", Tips.ERRORUSERORPWD.getText());
			returnToClient(result.toString());
			return "text";
		}
		
		logger.info("The logining account is " + account);
		
		String userId = "" + member.getId();
		String name = member.getFullname();
		String token = null;
		String tokenMaxAge = PropertiesUtils.getStringByKey("db.tokenMaxAge");
		
		long tokenMaxAgeLong = 0;
		long firstTokenDate = 0;
		
		if (member.getCreatetokendate()!=null) {
			firstTokenDate = member.getCreatetokendate();
		}
		
		long now = TimeGenerator.getInstance().getUnixTime();
		
		if (tokenMaxAge != null && !"".equals(tokenMaxAge)) {
			tokenMaxAgeLong = Long.valueOf(tokenMaxAge);
		}
		
		if (StringUtils.getInstance().isBlank(member.getToken()) || (now - firstTokenDate) > tokenMaxAgeLong) {
			try {
				String domain = PropertiesUtils.getDomain();
				String uploadDir = PropertiesUtils.getUploadDir();
				String logo = member.getLogo();
				if(logo == null) logo = "PersonImg.png";
				
				String url = domain + uploadDir + logo;
				token = RongCloudUtils.getInstance().getToken(userId, name, url);
				memberService.updateUserTokenForId(userId, token);
			} catch (Exception e) {
				logger.error(e);
				e.printStackTrace();
			}
		} else {
			token = member.getToken();
		}
		
		logger.info(token);
		
		//设置用户session
		SessionUser su = new SessionUser();
		
		su.setId(member.getId());
		su.setAccount(member.getAccount());
		su.setFullname(member.getFullname());
		su.setOrganId(organId);
		su.setToken(token);
		setSessionUser(su);
		
		JSONObject text = JSONUtils.getInstance().modelToJSONObj(member);
		
		text.remove("password");
		text.remove("createtokendate");
		text.remove("groupmax");
		text.remove("groupuse");
		text.remove("isDel");
		text.put("token", token);
		
		result.put("code", 1);
		result.put("text", text.toString());
		
		returnToClient(result.toString());  
		
		return "text";
	}

	/**
	 * 登出
	 * @return
	 * @throws Exception
	 */
	public String logOut() throws IOException, ServletException
	{
		request.getSession().removeAttribute(Constants.ATTRIBUTE_NAME_OF_SESSIONUSER);
		request.getSession().invalidate();
		//response.sendRedirect("http://localhost:8080/organ/");
		//request.getRequestDispatcher("/system!login").forward(request, response);
		return "loginPage";
	}
	
	/**
	 * 跳转修改密码页(仅web端使用)
	 * @return
	 * @throws Exception
	 */
	public String fogetPassword() throws ServletException {
		//request.getRequestDispatcher("/page/web/forgotpassword.jsp").forward(request, response);
		
		return "forgetpwd";
	}
	
	/**
	 * 中转短信平台
	 * @return
	 * @throws Exception
	 */
	public String requestText() throws IOException, ServletException {

		JSONObject text = new JSONObject();
		
		if (!StringUtils.getInstance().isBlank(phone)) {
			phone = clearChar(phone);
			String dbCode = memberService.getTextCode(phone);
			String endText = PropertiesUtils.getStringByKey("code.endtext");
			String code = "";
			String context = "";
			
			if (dbCode == null || dbCode.equals("-1")) {
				int codeBit = StringUtils.getInstance().strToInt(PropertiesUtils.getStringByKey("code.bit"));
				code = String.valueOf(MathUtils.getInstance().getRandomSpecBit(codeBit));
				context = endText + code;
				memberService.saveTextCode(phone, code);
			} else {
				context = endText + dbCode;
			}
			
			logger.info("短信验证内容： " + context);
			
			//发送短信代码
			String sendText = TextHttpSender.getInstance().sendText(phone, context);
			
			if ("0".equals(sendText)) {
				text.put("code", 1);
				text.put("text", Tips.SENDTEXTS.getText());
			} else {
				text.put("code", 0);
				//text.put("text", Tips.SENDERR.getText());
				text.put("text", TextHttpSender.getInstance().code.get(sendText));
				text.put("textcode", sendText);
			}
		} else {
			text.put("code", 0);
			text.put("text", Tips.NULLPHONE.getText());
		}
		
		returnToClient(text.toString());
		
		return "text";
	}

	
	/**
	 * 验证短信(仅web端使用)
	 * @return
	 */
	public String testText() throws ServletException {
		
		JSONObject text = new JSONObject();
		
		if (StringUtils.getInstance().isBlank(phone)) {
			text.put("code", -1);
			text.put("text", Tips.NULLPHONE.getText());
		} else if (StringUtils.getInstance().isBlank(textcode)) {
			text.put("code", -1);
			text.put("text", Tips.NULLTEXTS.getText());
		} else {
			phone = clearChar(phone);
			String dbCode = memberService.getTextCode(phone);
			
			if (dbCode != null && !dbCode.equals("-1") && dbCode.equals(clearChar(textcode))) {
			//if(textcode.equals("111111")) {
				text.put("code", 1);
				text.put("text", Tips.TRUETEXTS.getText());
			} else {	
				text.put("code", 0);
				text.put("text", Tips.FAIL.getText());
			}
		}
		
		returnToClient(text.toString());
		
		return "text";
	}
	
	/**
	 * 验证旧密码
	 * @return
	 * @throws ServletException
	 */
	public String valideOldPwd() throws ServletException {
		JSONObject text = new JSONObject();
		
		if (!StringUtils.getInstance().isBlank(oldpwd)) {				//登陆后修改密码
			int organId = getSessionUserOrganId();
			boolean validOldPwd = memberService.valideOldPwd(clearChar(account), clearChar(oldpwd), organId);
			if (!validOldPwd) {
				text.put("code", 0);
				text.put("text", Tips.WRONGOLDPWD.getText());
			} else {
				text.put("code", 1);
				text.put("text", Tips.OK.getText());
			}
		} else { 
			text.put("code", 0);
			text.put("text", Tips.WRONGOLDPWD.getText());
		}
		
		returnToClient(text.toString());
		
		return "text";
	}
	
	/**
	 * 修改新密码
	 * @return
	 */
	public String newPassword() throws ServletException {
		JSONObject text = new JSONObject();
		boolean status = true;
		int flag = 0;
		int organId = 0;	
		
		account = clearChar(account);
		newpwd = clearChar(newpwd);
		
		if (!StringUtils.getInstance().isBlank(oldpwd)) {				//登陆后修改密码
			if (StringUtils.getInstance().isBlank(account)) {
				text.put("code", "0");
				text.put("text", Tips.NULLUSER.getText());
				returnToClient(text.toString());
				return "text";
			}
			organId = getSessionUserOrganId();
			boolean validOldPwd = memberService.valideOldPwd(clearChar(account), clearChar(oldpwd), organId);
			flag = 1;		//后台修改
			if (!validOldPwd) {
				text.put("code", -1);
				text.put("text", Tips.WRONGOLDPWD.getText());
				status = false;
			}
		} else {
			String type = this.request.getParameter("type");
			if (StringUtils.getInstance().isBlank(type) || !type.equalsIgnoreCase("web")) {
			
				if (!StringUtils.getInstance().isBlank(textcode)) {
					String dbCode = memberService.getTextCode(account);
		
					if (dbCode != null && !dbCode.equals("-1") && dbCode.equals(textcode)) {
						text.put("code", 1);
						text.put("text", Tips.TRUETEXTS.getText());
					} else {
						status = false;
						text.put("code", 0);
						text.put("text", Tips.ERRORTEXTS.getText());
					}
				} else {
					status = false;
					text.put("code", -1);
					text.put("text", Tips.NULLTEXTS.getText());
				}
			}
		}
		
		if (status) {
			if (!newpwd.equals(comparepwd)) {
				text.put("code", "0");
				text.put("text", Tips.FALSECOMPAREPWD.getText());
				//request.setAttribute(LOGIN_ERROR_MESSAGE, Tips.FALSECOMPAREPWD.getText());
				//return "fogetpwd";
			} else {
				
				boolean updateState = false;
				newpwd = clearChar(newpwd);
				
				if (flag == 1) {
					updateState = memberService.updateUserPwdForAccount(account, newpwd, organId);
				} else {
					updateState = memberService.updateUserPwdForPhone(account, newpwd);
				}
				
				if (updateState == true) {
					text.put("code", "1");
					text.put("text", Tips.CHANGEPWDSUC.getText());
				} else {
					text.put("code", "0");
					text.put("text", Tips.CHANGEPWDFAIL.getText());
				}
			}
		}
		returnToClient(text.toString());
		
		return "text";
	}
	
	/**
	 * 检测session状态，用于前端进行是否重新登陆，移动端会用到
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String attemptSession() throws ServletException {
		SessionUser su = this.getSessionUser();
		JSONObject jo = new JSONObject();
		jo.put("status", !(su == null));
		logger.info(jo.toString());
		returnToClient(jo.toString());
		return "text";
	}
	
	private MemberService memberService;
	private OrgService orgService;
	
	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}

	private String account;
	private String userpwd;
	private String oldpwd;
	private String newpwd;
	private String textcode;
	private String comparepwd;
	private String phone;
	
	public void setAccount(String account) {
		this.account = account;
	}

	public void setUserpwd(String userpwd) {
		this.userpwd = userpwd;
	}

	public void setOldpwd(String oldpwd) {
		this.oldpwd = oldpwd;
	}

	public void setNewpwd(String newpwd) {
		this.newpwd = newpwd;
	}

	public void setTextcode(String textcode) {
		this.textcode = textcode;
	}

	public void setComparepwd(String comparepwd) {
		this.comparepwd = comparepwd;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

}
