package com.organ.service.appinfoconfig.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.organ.dao.appinfoconfig.AppInfoConfigDao;
import com.organ.dao.auth.UserValidDao;
import com.organ.dao.limit.LimitDao;
import com.organ.dao.limit.RoleAppSecretDao;
import com.organ.service.appinfoconfig.AppInfoConfigService;
import com.organ.utils.LogUtils;
import com.organ.utils.PasswordGenerator;
import com.organ.utils.SecretUtils;
import com.organ.utils.StringUtils;

public class AppInfoConfigServiceImpl implements AppInfoConfigService {

	public AppInfoConfigDao appInfoConfigDao;
	public RoleAppSecretDao roleappsecretDao;
	public UserValidDao userValidDao;
	public LimitDao limitDao;
	
	public void setUserValidDao(UserValidDao userValidDao) {
		this.userValidDao = userValidDao;
	}

	public LimitDao getLimitDao() {
		return limitDao;
	}

	public void setLimitDao(LimitDao limitDao) {
		this.limitDao = limitDao;
	}

	public AppInfoConfigDao getAppInfoConfigDao() {
		return appInfoConfigDao;
	}

	public RoleAppSecretDao getRoleappsecretDao() {
		return roleappsecretDao;
	}

	public void setRoleappsecretDao(RoleAppSecretDao roleappsecretDao) {
		this.roleappsecretDao = roleappsecretDao;
	}

	public void setAppInfoConfigDao(AppInfoConfigDao appInfoConfigDao) {
		this.appInfoConfigDao = appInfoConfigDao;
	}

	private String isBlank(Object o) {
		return o == null ? "" : o + "";
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getAppInfo(int userId, int organId, int pagesize, int pageindex) {
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		
		try {
			List appList = appInfoConfigDao.getAppInfo(userId, organId, pagesize,
					pageindex);
			int count = appInfoConfigDao.getCount(organId);
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
			e.printStackTrace();
		}
		return jsonObject.toString();
	}

	@Override
	public String updatePriv(String callbackurl,
			String appname, int isopen, int organId) {
		ArrayList<String> idsecret = makeAppId();
		return appInfoConfigDao.updatePriv(idsecret.get(0), idsecret.get(1), callbackurl, appname, isopen, organId) + "";
	}

	private ArrayList<String> makeAppId() {
		ArrayList<String> as = new ArrayList<String>();

		try {
			String id = PasswordGenerator.getInstance().createId(18);
			as.add(id);
			as.add(new SecretUtils().encrypt(id));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return as;
	}

	@Override
	public String DelApp(int id) {
		String appnameString = appInfoConfigDao.getAppNameByID(id);
		System.err.println(appnameString);
		appInfoConfigDao.delete("delete AppSecret where id =" + id);
		roleappsecretDao.delete("delete from TRoleAppSecret where appsecretId = " + id);
		limitDao.delete("delete from TPriv where app = '" + appnameString+"'");
		userValidDao.delete("delete from UserValid where asid=" + id);
		return id + "";
	}

	@Override
	public String EditApp(int id, String appId, String secert,
			String callbackurl, String appname, int isopen) {
		return appInfoConfigDao.editApp(id, appId, secert, callbackurl,
				appname, isopen)
				+ "";
	}

	@Override 
	public String SearchApp(String name, int organId, String AppName, int pagesize,
			int pageindex) {
		JSONArray ja = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		try {
			List appinfos = appInfoConfigDao.SearchAppInfo(organId, AppName,
					pagesize, pageindex);
			int count = appInfoConfigDao.getSearchCount(AppName, organId);
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
					jo.put("fullname", name);
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
	public String SearchAppInfoName(int organId) {
		JSONArray ja = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		try {
			List names = appInfoConfigDao.SearchAppInfoName(organId);
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
