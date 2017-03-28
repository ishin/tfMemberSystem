package com.organ.action.appinfoconfig;

import org.json.JSONObject;

import com.organ.common.BaseAction;
import com.organ.service.appinfoconfig.AppInfoConfigService;

/**
 * 应用信息配置
 * 
 * @author Lmy
 * 
 */
public class AppInfoConfigAction extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3357825063940018758L;

	private AppInfoConfigService appInfoConfigService;

	public AppInfoConfigService getAppInfoConfigService() {
		return appInfoConfigService;
	}

	public void setAppInfoConfigService(
			AppInfoConfigService appInfoConfigService) {
		this.appInfoConfigService = appInfoConfigService;
	}

	public String getAppInfo() {
		String pagesize = this.request.getParameter("pagesize");
		String pageindex = this.request.getParameter("pageindex");
		Integer intpagesize = pagesize == null ? null : Integer
				.parseInt(pagesize);
		Integer intpageindex = pageindex == null ? null : Integer
				.parseInt(pageindex);
		boolean flag = false;
		String result = null;
		try {
			result = appInfoConfigService.getAppInfo(intpagesize,
					intpageindex);
			if ("".equals(result) && null == result) {
				flag = false;
			} else {
				flag = true;
			}
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		returnToClient(result);
		return "text";
	}

}
