package com.organ.action.abutment;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.organ.common.BaseAction;
import com.organ.common.Tips;
import com.organ.service.auth.AppSecretService;
import com.organ.utils.JSONUtils;
import com.organ.utils.LogUtils;

public class AbutmentAuthAction extends BaseAction {

	private static final long serialVersionUID = 6187999207496183515L;
	private static final Logger logger = Logger
			.getLogger(AbutmentAuthAction.class);
	
	
	public String validAppIdAndSecret() throws ServletException {
		String result = null;
		try {
			String params = getRequestDataByStream();
			JSONObject jo = new JSONObject();
			
			if (params == null) {
				jo.put("code", 0);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			} else {
				JSONObject p = JSONUtils.getInstance().stringToObj(params);
				String appId = p.getString("appId");
				String secret = p.getString("secret");
				result = appSecretService.getAppSecretByAppIdAndSecret(appId, secret);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		returnToClient(result);
		return "text";
	}
	
	private AppSecretService appSecretService;

	public void setAppSecretService(AppSecretService appSecretService) {
		this.appSecretService = appSecretService;
	}
	
}
