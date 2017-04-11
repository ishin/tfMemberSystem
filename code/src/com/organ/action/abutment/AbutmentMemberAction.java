package com.organ.action.abutment;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.organ.common.BaseAction;
import com.organ.common.Tips;
import com.organ.model.TMember;
import com.organ.service.member.MemberService;
import com.organ.service.upload.UploadService;
import com.organ.utils.JSONUtils;
import com.organ.utils.LogUtils;
import com.organ.utils.StringUtils;

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
	 * 获取成员指定参数
	 * 
	 * @return
	 * @throws ServcletException
	 */
	public String getMemberParam() throws ServletException {
		String result = null;

		try {
			String params = getRequestDataByStream();

			if (params != null) {
				JSONObject jo = JSONUtils.getInstance().stringToObj(params);
				String id = jo.getString("id");
				String ps = jo.getString("params");
				result = memberService.getMemberParam(id, ps);
			} else {
				JSONObject jo = new JSONObject();
				jo = new JSONObject();
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		returnToClient(result.toString());
		return "text";
	}

	/**
	 * 根据token获取成员
	 */
	public String getMemberByToken() throws ServletException {
		String result = null;

		try {
			String params = getRequestDataByStream();

			if (params != null) {
				JSONObject jo = JSONUtils.getInstance().stringToObj(params);
				String token = jo.getString("token");
				TMember tm = memberService.getMemberByToken(token);
				if (tm != null) {
					result = JSONUtils.getInstance().modelToJSONObj(tm)
							.toString();
				}
			} else {
				JSONObject jo = new JSONObject();
				jo = new JSONObject();
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		returnToClient(result.toString());
		return "text";
	}

	/**
	 * 获取指定数量的用户id
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String getLimitMemberIds() throws ServletException {
		String result = null;

		try {
			String params = getRequestDataByStream();

			if (params != null) {
				JSONObject jo = JSONUtils.getInstance().stringToObj(params);
				String mapMax = jo.getString("mapMax");
				result = memberService.getLimitMemberIds(mapMax);
			} else {
				JSONObject jo = new JSONObject();
				jo = new JSONObject();
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		returnToClient(result.toString());
		return "text";
	}

	/**
	 * 根据id获取单个成员
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String getMemberForId() throws ServletException {
		String result = null;

		try {
			String params = getRequestDataByStream();

			if (params != null) {
				JSONObject jo = JSONUtils.getInstance().stringToObj(params);
				String userId = jo.getString("userId");
				result = memberService.getMemberForId(userId);
			} else {
				JSONObject jo = new JSONObject();
				jo = new JSONObject();
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		returnToClient(result.toString());
		return "text";
	}

	/**
	 * 根据id获取多个成员
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String getMultipleMemberForIds() throws ServletException {
		String result = null;

		try {
			String params = getRequestDataByStream();

			if (params != null) {
				JSONObject jo = JSONUtils.getInstance().stringToObj(params);
				String ids = jo.getString("ids");
				result = memberService.getMultipleMemberForIds(ids);
			} else {
				JSONObject jo = new JSONObject();
				jo = new JSONObject();
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		returnToClient(result.toString());
		return "text";
	}

	/**
	 * 根据账号获取id
	 * 
	 * @return
	 */
	public String getMemberIdForAccount() throws ServletException {
		String result = null;

		try {
			String params = getRequestDataByStream();

			if (params != null) {
				JSONObject jo = JSONUtils.getInstance().stringToObj(params);
				String account = jo.getString("account");
				result = memberService.getMemberIdForAccount(account);
			} else {
				JSONObject jo = new JSONObject();
				jo = new JSONObject();
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		returnToClient(result.toString());
		return "text";
	}

	/**
	 * 登陆验证
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String checkAccount() throws ServletException {
		JSONObject result = new JSONObject();

		try {
			String params = getRequestDataByStream();

			if (params != null) {
				JSONObject jo = JSONUtils.getInstance().stringToObj(params);
				String account = jo.getString("account");
				String pwd = jo.getString("password");
				TMember tm = memberService.searchSigleUser(account, pwd);
				result.put("code", 1);
				result.put("text", JSONUtils.getInstance().modelToJSONObj(tm)
						.toString());
			} else {
				result = new JSONObject();
				result.put("code", 0);
				result.put("text", Tips.WRONGPARAMS.getText());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		returnToClient(result.toString());
		return "text";
	}

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
				result = memberService.updateMemberInfoForWeb(userId, position,
						fullName, sign);
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
	 * 
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
				result = memberService.updateMemberForApp(userId, email,
						mobile, phone, address);
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
	 * 
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
				result = memberService
						.getMultipleMemberForAccounts(mulMemberStr);
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
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String getMemberCount() throws ServletException {
		String result = null;
		JSONObject jo = new JSONObject();

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

	/**
	 * 更新token
	 * 
	 * @return
	 * @throws SevletException
	 */
	public String updateUserTokenForId() throws ServletException {
		String result = null;
		JSONObject r = new JSONObject();

		try {
			String params = getRequestDataByStream();
			JSONObject jo = JSONUtils.getInstance().stringToObj(params);
			String userId = jo.getString("userId");
			String token = jo.getString("token");
			int count = memberService.updateUserTokenForId(userId, token);
			r.put("code", 1);
			r.put("text", count);
			result = r.toString();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		returnToClient(result);
		return "text";
	}

	/**
	 * 根据账号获取id
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String getMemberIdsByAccount() throws ServletException {
		String result = null;

		try {
			String params = getRequestDataByStream();

			if (StringUtils.getInstance().isBlank(params)) {
				JSONObject jo = new JSONObject();
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				String names = p.getString("accounts");
				result = memberService.getMemberIdsByAccount(names);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		returnToClient(result);
		return "text";
	}

	/**
	 * 获取短信码
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String getTextCode() throws ServletException {
		String result = null;
		JSONObject jo = new JSONObject();

		try {
			String params = getRequestDataByStream();
			if (StringUtils.getInstance().isBlank(params)) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				String phone = p.getString("phone");
				result = memberService.getTextCode(phone);
				jo.put("code", 1);
				jo.put("text", result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		returnToClient(jo.toString());
		return "text";
	}

	/**
	 * 保存短信验证码记录
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String saveTextCode() throws ServletException {
		JSONObject jo = new JSONObject();
		String result = null;

		try {
			String params = getRequestDataByStream();
			if (StringUtils.getInstance().isBlank(params)) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				String phone = p.getString("phone");
				String code = p.getString("code");
				memberService.saveTextCode(phone, code);
				jo.put("code", 1);
				jo.put("text", result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		returnToClient(jo.toString());
		return "text";
	}

	/**
	 * 验证旧密码
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String valideOldPwd() throws ServletException {
		JSONObject jo = new JSONObject();
		String result = null;

		try {
			String params = getRequestDataByStream();
			if (StringUtils.getInstance().isBlank(params)) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				String account = p.getString("account");
				String oldPwd = p.getString("oldPwd");
				boolean status = memberService.valideOldPwd(account, oldPwd);
				jo.put("code", 1);
				jo.put("text", status);
			}
			result = jo.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		returnToClient(result);
		return "text";
	}

	/**
	 * 依据账号更新密码
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String updateUserPwdForAccount() throws ServletException {
		JSONObject jo = new JSONObject();
		String result = null;

		try {
			String params = getRequestDataByStream();
			if (StringUtils.getInstance().isBlank(params)) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				String account = p.getString("account");
				String newPwd = p.getString("newPwd");
				boolean status = memberService.updateUserPwdForAccount(account,
						newPwd);
				jo.put("code", 1);
				jo.put("text", status);
			}
			result = jo.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		returnToClient(result);
		return "text";
	}

	/**
	 * 依据手机号更新密码
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String updateUserPwdForPhone() throws ServletException {
		JSONObject jo = new JSONObject();
		String result = null;

		try {
			String params = getRequestDataByStream();
			if (StringUtils.getInstance().isBlank(params)) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				String account = p.getString("phone");
				String newPwd = p.getString("newPwd");
				boolean status = memberService.updateUserPwdForPhone(account,
						newPwd);
				jo.put("code", 1);
				jo.put("text", status);
			}
			result = jo.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		returnToClient(result);
		return "text";
	}

	/**
	 * 保存用户选择的头像
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String saveSelectedPic() throws ServletException {
		JSONObject jo = new JSONObject();
		String result = null;

		try {
			String params = getRequestDataByStream();
			if (StringUtils.getInstance().isBlank(params)) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				String userId = p.getString("userId");
				String picName = p.getString("picName");
				result = uploadService.saveSelectedPic(userId, picName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		returnToClient(result);
		return "text";
	}

	/**
	 * 保存成员头像
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String saveTempPic() throws ServletException {
		JSONObject jo = new JSONObject();
		String result = null;

		try {
			String params = getRequestDataByStream();
			if (StringUtils.getInstance().isBlank(params)) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				String logName = p.getString("logName");
				String userId = p.getString("userId");
				result = uploadService.saveTempPic(userId, logName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		returnToClient(result);
		return "text";
	}

	/**
	 * 删除成员头像
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String delUserLogos() throws ServletException {
		JSONObject jo = new JSONObject();
		String result = null;

		try {
			String params = getRequestDataByStream();
			if (StringUtils.getInstance().isBlank(params)) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				String picName = p.getString("picName");
				String userId = p.getString("userId");
				result = uploadService.delUserLogos(userId, picName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		returnToClient(result);
		return "text";
	}

	/**
	 * 检测 是否正在使用头像
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String isUsedPic() throws ServletException {
		JSONObject jo = new JSONObject();
		String result = null;

		try {
			String params = getRequestDataByStream();
			if (StringUtils.getInstance().isBlank(params)) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				String picName = p.getString("picName");
				String userId = p.getString("userId");
				result = memberService.isUsedPic(userId, picName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		returnToClient(result);
		return "text";
	}

	/**
	 * 获取头像列表
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String getUserLogos() throws ServletException {
		JSONObject jo = new JSONObject();
		String result = null;

		try {
			String params = getRequestDataByStream();
			if (StringUtils.getInstance().isBlank(params)) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				String userId = p.getString("userId");
				result = uploadService.getUserLogos(userId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		returnToClient(result);
		return "text";
	}

	/**
	 * 通过http方式上传的图片
	 * 
	 * @return
	 * @throws ServletException
	 * @throws IOException 
	 */
	public String httpUpload() throws ServletException, IOException {
		String fileName = request.getParameter("fileName");
		InputStream input = request.getInputStream();
		String realPath = request.getSession().getServletContext().getRealPath("/");  
		String result = uploadService.httpUpload(fileName, input, realPath);
		returnToClient(result);
		return "text";
	}

	private MemberService memberService;
	private UploadService uploadService;

	public void setUploadService(UploadService uploadService) {
		this.uploadService = uploadService;
	}

	public void setMemberService(MemberService ms) {
		this.memberService = ms;
	}

}
