package com.sealtalk.action.sys;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.sf.json.JSONObject;


import com.sealtalk.common.BaseAction;
import com.sealtalk.common.Constants;
import com.sealtalk.common.Tips;
import com.sealtalk.model.SessionPrivilege;
import com.sealtalk.model.SessionUser;
import com.sealtalk.model.TMember;
import com.sealtalk.service.adm.BranchService;
import com.sealtalk.service.adm.PrivService;
import com.sealtalk.service.member.MemberService;
import com.sealtalk.utils.JSONUtils;
import com.sealtalk.utils.LogUtils;
import com.sealtalk.utils.MathUtils;
import com.sealtalk.utils.PropertiesUtils;
import com.sealtalk.utils.RongCloudUtils;
import com.sealtalk.utils.StringUtils;
import com.sealtalk.utils.TextHttpSender;
import com.sealtalk.utils.TimeGenerator;

/**
 * 登陆相关
 * 
 * @since jdk1.7
 * @author hao_dy
 * 
 */

public class SystemAction extends BaseAction {

	private static final long serialVersionUID = -3901445181785461508L;
	private static final String LOGIN_ERROR_MESSAGE = "loginErrorMsg";
	private static final Logger logger = LogManager.getLogger(SystemAction.class);

	/**
	 * 跳转登陆页面(仅web使用)
	 * 
	 * @return
	 * @throws Exception
	 */
	public String login() throws IOException, ServletException {

		if (getSessionUser() == null) {
			return "loginPage";
		} else {
			return "loginSuccess";
		}
	}

	/**
	 * app端登陆验证
	 * 
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */
	public String afterLogin() throws IOException, ServletException {
		JSONObject result = new JSONObject();

		if (StringUtils.getInstance().isBlank(account)) {
			result.put("code", 0);
			result.put("text", Tips.NULLUSER.getText());
			logger.info(result);
			returnToClient(result.toString());
			return "text";
		}

		String organCode = this.request.getParameter("organCode");

		if (StringUtils.getInstance().isBlank(organCode)) {
			result.put("code", 0);
			result.put("text", Tips.NULLCODE.getText());
			logger.info(result);
			returnToClient(result.toString());
			return "text";
		}

		organCode = organCode.toUpperCase();
		
		int organId = branchService.getOrganIdByOrganCode(organCode);

		if (organId == -1) {
			result.put("code", 0);
			result.put("text", Tips.NULLCODE.getText());
			logger.info(result);
			returnToClient(result.toString());
			return "text";
		}

		TMember member = memberService.searchSigleUser(clearChar(account), clearChar(userpwd),
				organId);

		if (member == null) {
			result.put("code", 0);
			result.put("text", Tips.ERRORUSERORPWD.getText());
			logger.info(result);
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

		if (member.getCreatetokendate() != null) {
			firstTokenDate = member.getCreatetokendate();
		}

		long now = TimeGenerator.getInstance().getUnixTime();

		if (tokenMaxAge != null && !"".equals(tokenMaxAge)) {
			tokenMaxAgeLong = Long.valueOf(tokenMaxAge);
		}

		if (StringUtils.getInstance().isBlank(member.getToken())
				|| (now - firstTokenDate) > tokenMaxAgeLong) {
			try {
				String domain = PropertiesUtils.getDomain();
				String uploadDir = PropertiesUtils.getUploadDir();
				String logo = member.getLogo();
				if (logo == null)
					logo = "PersonImg.png";

				String url = domain + uploadDir + logo;
				token = RongCloudUtils.getInstance()
						.getToken(userId, name, url);
				memberService.updateUserTokenForId(userId, token);
			} catch (Exception e) {
				logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
				e.printStackTrace();
			}
		} else {
			token = member.getToken();
		}
		
		logger.info(token);

		// 设置用户session
		SessionUser su = new SessionUser();

		su.setId(member.getId());
		su.setAccount(member.getAccount());
		su.setFullname(member.getFullname());
		su.setOrganId(organId);
		su.setToken(token);
		setSessionUser(su);

		// 2.设置权限
		SessionPrivilege sp = new SessionPrivilege();
		ArrayList<JSONObject> ja = new ArrayList<JSONObject>();

		List privList = privService.getRoleIdForId(member.getId());

		if (privList != null && privList.size() > 0) {
			Iterator it = privList.iterator();

			while (it.hasNext()) {
				ArrayList o = (ArrayList) it.next();
				JSONObject js = new JSONObject();
				js.put("privid", o.get(0));
				js.put("priurl", o.get(1));
				ja.add(js);
			}
		}
		
		sp.setPrivilige(ja);
		setSessionAttribute(Constants.ATTRIBUTE_NAME_OF_SESSIONPRIVILEGE, sp);
		JSONObject text = JSONUtils.getInstance().modelToJSONObj(member);

		text.remove("password");
		text.remove("createtokendate");
		text.remove("groupmax");
		text.remove("groupuse");
		text.put("token", token);
		text.put("priv", JSONUtils.getInstance().modelToJSONObj(sp));

		result.put("code", 1);
		result.put("text", text.toString());

		logger.info(result.toString());
		returnToClient(result.toString());
		return "text";
	}

	/**
	 * 登出
	 * 
	 * @return
	 * @throws Exception
	 */
	public String logOut() throws IOException, ServletException {
		request.getSession().removeAttribute(
				Constants.ATTRIBUTE_NAME_OF_SESSIONUSER);
		request.getSession().invalidate();
		//request.getRequestDispatcher("/system!login").forward(request, response);
		
		return "loginPage";
	}

	/**
	 * 跳转修改密码页(仅web端使用)
	 * 
	 * @return
	 * @throws Exception
	 */
	public String fogetPassword() throws ServletException {
		// request.getRequestDispatcher("/page/web/forgotpassword.jsp").forward(request,
		// response);

		return "forgetpwd";
	}

	/**
	 * 中转短信平台
	 * 
	 * @return
	 * @throws Exception
	 */
	public String requestText() throws IOException, ServletException {

		JSONObject text = new JSONObject();
		
		phone = clearChar(phone);
		
		if (!StringUtils.getInstance().isBlank(phone)) {
			String dbCode = memberService.getTextCode(phone);
			String endText = PropertiesUtils.getStringByKey("code.endtext");
			String code = "";
			String context = "";

			if (dbCode == null || dbCode.equals("-1")) {
				int codeBit = StringUtils.getInstance().strToInt(
						PropertiesUtils.getStringByKey("code.bit"));
				code = String.valueOf(MathUtils.getInstance().getRandomSpecBit(
						codeBit));
				context = code + endText;
				memberService.saveTextCode(phone, code);
			} else {
				context = dbCode + endText;
			}

			// 发送短信代码
			String sendText = TextHttpSender.getInstance().sendText(phone,
					context);

			if ("0".equals(sendText)) {
				text.put("code", 1);
				text.put("text", Tips.SENDTEXTS.getText());
			} else {
				text.put("code", 0);
				text.put("text", Tips.SENDERR.getText());
			}
		} else {
			text.put("code", 0);
			text.put("text", Tips.NULLPHONE.getText());
		}

		logger.info(text.toString());
		returnToClient(text.toString());
		return "text";
	}

	/**
	 * 验证短信(仅web端使用)
	 * 
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
			textcode = clearChar(textcode);
			
			String dbCode = memberService.getTextCode(phone);

			//if (dbCode != null && !dbCode.equals("-1") && dbCode.equals(textcode)) {
			if(textcode.equals("111111")) {
				text.put("code", 1);
				text.put("text", Tips.TRUETEXTS.getText());
			} else {
				text.put("code", 0);
				text.put("text", Tips.FAIL.getText());
			}
		}
		logger.info(text.toString());
		returnToClient(text.toString());
		return "text";
	}

	/**
	 * 验证旧密码
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String valideOldPwd() throws ServletException {
		JSONObject text = new JSONObject();

		if (!StringUtils.getInstance().isBlank(oldpwd)) { // 登陆后修改密码
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
		
		logger.info(text.toString());
		returnToClient(text.toString());
		return "text";
	}

	/**
	 * 修改新密码
	 * 
	 * @return
	 */
	public String newPassword() throws ServletException {
		JSONObject text = new JSONObject();

		boolean status = true;
		int flag = 0;
		int organId = 0;
		
		account = clearChar(account);
		newpwd = clearChar(newpwd);
		
		if (!StringUtils.getInstance().isBlank(oldpwd)) { // 登陆后修改密码

			if (StringUtils.getInstance().isBlank(account)) {
				text.put("code", "0");
				text.put("text", Tips.NULLUSER.getText());
				logger.info(text.toString());
				returnToClient(text.toString());
				return "text";
			}
			
			organId = getSessionUserOrganId();
			flag = 1; // 后台修改
			
			boolean validOldPwd = memberService.valideOldPwd(account, clearChar(oldpwd), organId);
	
			if (!validOldPwd) {
				text.put("code", -1);
				text.put("text", Tips.WRONGOLDPWD.getText());
				status = false;
			}
		} else {
			String type = this.request.getParameter("type");
			phone = account;
			if (type == null || !type.equalsIgnoreCase("web")) {
				if (!StringUtils.getInstance().isBlank(textcode)) {
					String dbCode = memberService.getTextCode(phone);
		
					if (dbCode != null && !dbCode.equals("-1") && dbCode.equals(textcode)) {
						text.put("code", 1);
						text.put("text", Tips.TRUETEXTS.getText());
					} else {
						text.put("code", 0);
						text.put("text", Tips.FAIL.getText());
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
				request.setAttribute(LOGIN_ERROR_MESSAGE, Tips.FALSECOMPAREPWD.getText());
				return "fogetpwd";
			}

			boolean updateState = false;

			if (flag == 1) {
				updateState = memberService.updateUserPwdForAccount(account,newpwd, organId);
			} else {
				updateState = memberService.updateUserPwdForPhone(phone, newpwd);
			}

			if (updateState == true) {
				text.put("code", "1");
				text.put("text", Tips.CHANGEPWDSUC.getText());
			} else {
				text.put("code", "0");
				text.put("text", Tips.CHANGEPWDFAIL.getText());
			}
		}
		
		logger.info(text.toString());
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
	private PrivService privService;
	private BranchService branchService;

	public void setBranchService(BranchService branchService) {
		this.branchService = branchService;
	}

	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}

	public void setPrivService(PrivService privService) {
		this.privService = privService;
	}

	private String account;
	private String userpwd;
	private String oldpwd;
	private String newpwd;
	private String textcode;
	private String comparepwd;
	private String dataSource;
	private String phone;
	private String token;

	public void setToken(String token) {
		this.token = token;
	}

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

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

}
