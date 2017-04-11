package com.organ.service.appinfoconfig.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.organ.dao.appinfoconfig.AppInfoConfigDao;
import com.organ.dao.limit.LimitDao;
import com.organ.service.appinfoconfig.AppInfoConfigService;

public class AppInfoConfigServiceImpl implements AppInfoConfigService {

	private AppInfoConfigDao appInfoConfigDao;

	public void setAppInfoConfigDao(AppInfoConfigDao appInfoConfigDao) {
		this.appInfoConfigDao = appInfoConfigDao;
	}

	private String isBlank(Object o) {
		return o == null ? "" : o + "";
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getAppInfo(int userId, int pagesize, int pageindex) {
		// TODO Auto-generated method stub
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		try {
			List appList = appInfoConfigDao.getAppInfo(userId, pagesize,
					pageindex);
			int count = appInfoConfigDao.getCount();
			if (appList == null) {
				JSONObject jo = new JSONObject();
				jo.put("code", 0);
				jo.put("text", "û�в�ѯ��Ӧ��");
			} else {
				for (int i = 0; i < appList.size(); i++) {
					Object[] app = (Object[]) appList.get(i);
					JSONObject jo = new JSONObject();
					String string = app[4].toString();
					String s;
					if (string.length() == 13) {
						long str = Long.parseLong(string);
						Date date = new Date(str);
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy/MM/dd");
						s = sdf.format(date);
					} else {
						s = string;
					}
					jo.put("id", isBlank(app[0]));
					jo.put("appId", isBlank(app[1]));
					jo.put("secert", isBlank(app[2]));
					jo.put("callbackurl", isBlank(app[3]));
					jo.put("apptime", s);
					jo.put("appname", isBlank(app[5]));
					jo.put("isopen", isBlank(app[6]));
					jo.put("fullname", isBlank(app[7]));
					jsonArray.add(jo);
					jsonObject.put("count", count + "");
					jsonObject.put("content", jsonArray);
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return jsonObject.toString();
	}

	@Override
	public String updatePriv(String appId, String secert, String callbackurl,
			String appname, int isopen) {
		// TODO Auto-generated method stub
		return appInfoConfigDao.updatePriv(appId, secert, callbackurl, appname,
				isopen)
				+ "";
	}

	@Override
	public String DelApp(int id) {
		return appInfoConfigDao.DeletelApp(id) + "";
	}

	@Override
	public String EditApp(int id, String appId, String secert,
			String callbackurl, String appname, int isopen) {
		return appInfoConfigDao.editApp(id, appId, secert, callbackurl,
				appname, isopen)
				+ "";
	}

	@Override
	public String SearchApp(int userId, String AppName, int pagesize,
			int pageindex) {
		JSONArray ja = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		try {
			List appinfos = appInfoConfigDao.SearchAppInfo(userId, AppName,
					pagesize, pageindex);
			int count = appInfoConfigDao.getSearchCount(AppName);
			if (appinfos == null) {
				JSONObject jo = new JSONObject();
				jo.put("code", 0);
				jo.put("text", "Ӧ�����Ϊ��");
			} else {
				for (int i = 0; i < appinfos.size(); i++) {
					Object[] appinfo = (Object[]) appinfos.get(i);
					JSONObject jo = new JSONObject();
					String string = appinfo[4].toString();
					String s;
					if (string.length() == 13) {
						long str = Long.parseLong(string);
						Date date = new Date(str);
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy/MM/dd");
						s = sdf.format(date);
					} else {
						s = string;
					}
					jo.put("id", isBlank(appinfo[0]));
					jo.put("appId", isBlank(appinfo[1]));
					jo.put("secert", isBlank(appinfo[2]));
					jo.put("callbackurl", isBlank(appinfo[3]));
					jo.put("apptime", s);
					jo.put("appname", isBlank(appinfo[5]));
					jo.put("isopen", isBlank(appinfo[6]));
					jo.put("fullname", isBlank(appinfo[7]));
					ja.add(jo);
					jsonObject.put("count", count + "");
					jsonObject.put("content", ja);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonObject.toString();
	}

	@Override
	public String SearchAppInfoName() {
		JSONArray ja = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		try {
			List names = appInfoConfigDao.SearchAppInfoName();
			System.out.println("------------" + names);
			if (names == null || "{ }".equals(names)) {
				JSONObject jo = new JSONObject();
				jo.put("code", 0);
				jo.put("text", "Ӧ�����Ϊ��");
			} else {
				for (int i = 0; i < names.size(); i++) {
					Object[] name = (Object[]) names.get(i);
					JSONObject jo = new JSONObject();
					jo.put("id",isBlank(name[0]));
					jo.put("appName",isBlank(name[1]));
					ja.add(jo);
					jsonObject.put("code", 1);
					jsonObject.put("content", ja);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonObject.toString();
	}
}
