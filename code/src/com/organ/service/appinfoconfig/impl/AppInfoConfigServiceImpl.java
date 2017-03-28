package com.organ.service.appinfoconfig.impl;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.organ.dao.appinfoconfig.AppInfoConfigDao;
import com.organ.service.appinfoconfig.AppInfoConfigService;

public class AppInfoConfigServiceImpl implements AppInfoConfigService {
	
	private AppInfoConfigDao appInfoConfigDao;
	
	public AppInfoConfigDao getAppInfoConfigDao() {
		return appInfoConfigDao;
	}

	public void setAppInfoConfigDao(AppInfoConfigDao appInfoConfigDao) {
		this.appInfoConfigDao = appInfoConfigDao;
	}
	private String isBlank(Object o) {
		return o == null ? "" : o + "";
	}
	@Override
	public String getAppInfo(int pagesize, int pageindex) {
		// TODO Auto-generated method stub
		JSONArray jsonArray = new JSONArray();
		try {
			List appList= appInfoConfigDao.getAppInfo(pagesize, pageindex);
			if(appList == null){
				JSONObject jo = new JSONObject();
				jo.put("code", 0);
				jo.put("text", "没有查询到应用");
			}else {
				for (int i = 0; i < appList.size(); i++) {
					Object [] app = (Object[])appList.get(i);
					JSONObject jo = new JSONObject();
					jo.put("id", isBlank(app[0]));
					jo.put("appId", isBlank(app[1]));
					jo.put("secert", isBlank(app[2]));
					jo.put("callbackurl", isBlank(app[3]));
					jo.put("apptime", isBlank(app[4]));
					jo.put("appname", isBlank(app[5]));
					jo.put("isopen", isBlank(app[6]));
					jsonArray.add(jo);
				}
			}
		
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return jsonArray.toString();
	}
}
