package com.organ.action.abutment;

import javax.servlet.ServletException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.sf.json.JSONObject;


import com.googlecode.sslplugin.annotation.Secured;
import com.organ.common.BaseAction;
import com.organ.common.Tips;
import com.organ.service.auth.AppSecretService;
import com.organ.utils.JSONUtils;
import com.organ.utils.LogUtils;


public class AbutmentAuthAction extends BaseAction {

	private static final long serialVersionUID = 6187999207496183515L;
	private static final Logger logger = LogManager.getLogger(AbutmentAuthAction.class);
	
	
	public String validAppIdAndSecretAb() throws ServletException {
		String result = null;
		
		try {
			String params = getRequestDataByStream();
			logger.info(params);
			
			JSONObject jo = new JSONObject();
			boolean s = true;
			
			if (params == null) {
				s = false;
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				if (!validParams(p)) {
					s = false;
				} else {
					String appId = p.getString("appId");
					String secret = p.getString("secret");
					result = appSecretService.getAppSecretByAppIdAndSecret(appId, secret);
				}
			}
			if (!s) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		logger.info(result);
		returnToClient(result);
		return "text";
	}
	
	public String getTempTokenSceneOneAb() throws ServletException {
		String result = null;
		
		try {
			String params = getRequestDataByStream();
			logger.info(params);
			
			JSONObject jo = new JSONObject();
			boolean s = true;
			
			if (params == null) {
				s = false;
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				if (!validParams(p)) {
					s = false;
				} else {
					String appId = p.getString("appId");
					result = appSecretService.getTempTokenSceneOne(appId).toString();
				}
			}
			if (!s) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		logger.info(result);
		returnToClient(result);
		return "text";
	}
	
	public String reqAuthorizeOneAb() throws ServletException {
		String result = null;
		
		try {
			String params = getRequestDataByStream();
			logger.info(params);
			
			JSONObject jo = new JSONObject();
			boolean s = true;
			
			if (params == null) {
				s = false;
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				if (!validParams(p)) {
					s = false;
				} else {
					String unAuthToken = p.getString("unAuthToken");
					String userName = p.getString("userName");
					String userPwd = p.getString("userPwd");
					String appId = p.getString("appId");
					String info = p.getString("info");
					
					result = appSecretService.reqAuthorizeOne(unAuthToken, userName, userPwd, appId, info).toString();
				}
			}
			if (!s) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		logger.info(result);
		returnToClient(result);
		return "text";
	}
	
	public String reqAuthorizeTwoAb() throws ServletException {
		String result = null;
		
		try {
			String params = getRequestDataByStream();
			logger.info(params);
			
			JSONObject jo = new JSONObject();
			boolean s = true;
			
			if (params == null) {
				s = false;
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				if (!validParams(p)) {
					s = false;
				} else {
					String id = p.getString("id");
					String appId = p.getString("appId");
					String unAuthToken = p.getString("unAuthToken");
					
					Integer idInt = id == null ? 0 : Integer.parseInt(id);
					result = appSecretService.reqAuthorizeTwo(idInt, appId, unAuthToken).toString();
				}
			}
			if (!s) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		logger.info(result);
		returnToClient(result);
		return "text";
	}
	
	public String reqAuthorizeTwoForAppAb() throws ServletException {
		String result = null;
		
		try {
			String params = getRequestDataByStream();
			logger.info(params);
			
			JSONObject jo = new JSONObject();
			boolean s = true;
			
			if (params == null) {
				s = false;
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				if (!validParams(p)) {
					s = false;
				} else {
					String id = p.getString("userId");
					String appId = p.getString("appId");
					String unAuthToken = p.getString("unAuthToken");
					
					result = appSecretService.reqAuthorizeTwoForApp(id, appId, unAuthToken).toString();
				}
			}
			if (!s) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		logger.info(result);
		returnToClient(result);
		return "text";
	}
	
	public String getRealTokenAb() throws ServletException {
		String result = null;
		
		try {
			String params = getRequestDataByStream();
			logger.info(params);
			
			JSONObject jo = new JSONObject();
			boolean s = true;
			
			if (params == null) {
				s = false;
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				if (!validParams(p)) {
					s = false;
				} else {
					String secret = p.getString("secret");
					String authToken = p.getString("authToken");
					String organId = p.getString("organId");
					
					result = appSecretService.getRealToken(secret, authToken, organId).toString();
				}
			}
			if (!s) {
				jo.put("code", 500);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		logger.info(result);
		returnToClient(result);
		return "text";
	}
	
	public String getAuthResourceAb() throws ServletException {
		String result = null;
		
		try {
			String params = getRequestDataByStream();
			logger.info(params);
			
			JSONObject jo = new JSONObject();
			boolean s = true;
			
			if (params == null) {
				s = false;
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				if (!validParams(p)) {
					s = false;
				} else {
					String visitToken = p.getString("visitToken");
					result = appSecretService.getAuthResource(visitToken).toString();
				}
			}
			if (!s) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		logger.info(result);
		returnToClient(result);
		return "text";
	}
	
	private AppSecretService appSecretService;

	public void setAppSecretService(AppSecretService appSecretService) {
		this.appSecretService = appSecretService;
	}
	
}
