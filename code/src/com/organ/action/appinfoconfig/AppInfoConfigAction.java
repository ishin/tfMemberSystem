package com.organ.action.appinfoconfig;

import com.organ.common.BaseAction;
import com.organ.service.appinfoconfig.AppInfoConfigService;

/**
 * 应用信息配置
 * @author Lmy
 *
 */
public class AppInfoConfigAction extends BaseAction{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3357825063940018758L;

	
	private AppInfoConfigService appInfoConfigService;
	
	
	public AppInfoConfigService getAppInfoConfigService() {
		return appInfoConfigService;
	}


	public void setAppInfoConfigService(AppInfoConfigService appInfoConfigService) {
		this.appInfoConfigService = appInfoConfigService;
	}


	public String getAppInfo() {
		returnToClient("11111111");
		return "text";
	}

}
