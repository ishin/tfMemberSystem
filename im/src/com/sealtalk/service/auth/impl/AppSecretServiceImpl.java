package com.sealtalk.service.auth.impl;

import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.googlecode.jsonplugin.JSONUtil;
import com.sealtalk.common.AuthTips;
import com.sealtalk.common.SysInterface;
import com.sealtalk.service.auth.AppSecretService;
import com.sealtalk.utils.HttpRequest;
import com.sealtalk.utils.JSONUtils;
import com.sealtalk.utils.LogUtils;
import com.sealtalk.utils.StringUtils;


public class AppSecretServiceImpl implements AppSecretService {
	private static final Logger logger = LogManager.getLogger(AppSecretServiceImpl.class);

	@Override
	public String getTempTokenSceneOne(String appId) {
		String result = null;
		logger.info("getTempTokenSceneOne appId: " + appId);

		try {
			if (StringUtils.getInstance().isBlank(appId)) {
				JSONObject j = new JSONObject();
				j.put("code", 500);
				j.put("text", AuthTips.WORNGPARAM.getText());
				result = j.toString();
			}else {
				appId = appId.trim();
				
				JSONObject pa = new JSONObject();
				pa.put("appId", appId);
				
				result = HttpRequest.getInstance().sendPost(
						SysInterface.GETTEMPTOKENSCENEONE.getName(), pa);

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		return result;
	}

	@Override
	public String reqAuthorizeOne(String unAuthToken, String userName,
			String userPwd, String appId, String info) {
		String result = null;
		int s = 0;
		try {
			if (StringUtils.getInstance().isBlank(appId)) {
				s = 0;
			} else if (StringUtils.getInstance().isBlank(unAuthToken)) {
				s = 1;
			} else if (StringUtils.getInstance().isBlank(userName)
					|| StringUtils.getInstance().isBlank(userPwd)) {
				s = 2;
			} else {
				JSONObject pa = new JSONObject();
				pa.put("unAuthToken", unAuthToken);
				pa.put("userName", userName);
				pa.put("userPwd", userPwd);
				pa.put("appId", appId);
				pa.put("info", StringUtils.getInstance().isBlank(info) ? "3" : info);
				
				result = HttpRequest.getInstance().sendPost(
						SysInterface.REQAUTHORIZEONE.getName(), pa);
				s = 3;
			}
			
			if (s != 3) {
				JSONObject jo = new JSONObject();
				String code = "500";
				String text = null;
				
				if (s == 0) text = AuthTips.INVALIDAPPID.getText();
				if (s == 1) text = AuthTips.INVALTOKEN.getText();
				if (s == 2) text = AuthTips.INVALUSER.getText();
				
				result = jo.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		return result;
	}

	@Override
	public String getRealToken(String secret, String authToken, String organId) {
		boolean s = false;
		String text = null;
		String result = null;
		
		try {
			if (StringUtils.getInstance().isBlank(secret)) {
				text = AuthTips.WORNGSECRET.getText();
			} else if (StringUtils.getInstance().isBlank(authToken)) {
				text = AuthTips.INVALTOKEN.getText();
			} else if (StringUtils.getInstance().isBlank(organId)) {
				text = AuthTips.INVALCOMPANYID.getText();
			} else {
				JSONObject pa = new JSONObject();
				pa.put("secret", secret);
				pa.put("authToken", authToken);
				pa.put("organId", organId);
				s = true;
				result = HttpRequest.getInstance().sendPost(
						SysInterface.GETREALTOKEN.getName(), pa);
			}
			if (!s) {
				JSONObject jo = new JSONObject();
				jo.put("code", 500);
				jo.put("text", text);
				result = jo.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		return result;
	}

	@Override
	public String getAuthResource(String visitToken) {
		String result = null;
		
		try {
			if (StringUtils.getInstance().isBlank(visitToken)) {
				JSONObject ret = new JSONObject();
				ret.put("code", 500);
				ret.put("text", AuthTips.INVALTOKEN.getText());
				result = ret.toString();
			} else {
				JSONObject pa = new JSONObject();
				pa.put("visitToken", visitToken);
				
				result = HttpRequest.getInstance().sendPost(
						SysInterface.GETAUTHRESOURCE.getName(), pa);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		return result;
	}

	@Override
	public String reqAuthorizeTwo(Integer id, String appId, String unAuthToken) {
		String result = null;
		boolean s = false;
		String text = null;
		
		try {
			if (StringUtils.getInstance().isBlank(appId)) {
				text = AuthTips.INVALIDAPPID.getText();
			} else if (StringUtils.getInstance().isBlank(unAuthToken)) {
				 text = AuthTips.INVALTOKEN.getText();
			} else {
				JSONObject pa = new JSONObject();
				pa.put("id", id);
				pa.put("appId", appId);
				pa.put("unAuthToken", unAuthToken);
				
				result = HttpRequest.getInstance().sendPost(
						SysInterface.REQAUTHORIZTWO.getName(), pa);
				s = true;
			}	
			if (!s) {
				JSONObject jo = new JSONObject();
				String code = "500";
				jo.put("code", 500);
				jo.put("text", text);
				result = jo.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		return result;
	}

	@Override
	public String reqAuthorizeTwoForApp(String userId, String appId, String unAuthToken) {
		String text = null;
		String result = null;
		boolean s = false;
		
		try {
			if (StringUtils.getInstance().isBlank(userId)) {
				text = AuthTips.WORNGPARAM.getText();
			} else if (StringUtils.getInstance().isBlank(appId)) {
				text = AuthTips.INVALIDAPPID.getText();
			} else if (StringUtils.getInstance().isBlank(unAuthToken)) {
				text = AuthTips.INVALTOKEN.getText();
			} else {
				JSONObject pa = new JSONObject();
				pa.put("userId", userId);
				pa.put("appId", appId);
				pa.put("unAuthToken", unAuthToken);
				
				result = HttpRequest.getInstance().sendPost(
						SysInterface.REQAUTHORIZETOWFORAPP.getName(), pa);
				s = true;
			}
			
			if (!s) {
				JSONObject ret = new JSONObject();
				ret.put("code", 500);
				ret.put("text", text);
				result = ret.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}

		return result;
	}

	@Override
	public boolean checkAppIdOfOrgan(String appId, int organId) {
		boolean s = false;
		
		if (!StringUtils.getInstance().isBlank(appId)) {
			JSONObject pa = new JSONObject();
			pa.put("appId", appId);
			pa.put("organId", organId);
			
			String result = HttpRequest.getInstance().sendPost(
					SysInterface.CHECKAPPIDOFORGAN.getName(), pa);
			
			JSONObject jo = JSONUtils.getInstance().stringToObj(result);
			if (jo != null && jo.getInt("code") == 1) {
				s = jo.getBoolean("text");
			}
		}
		return s;
	}

}
