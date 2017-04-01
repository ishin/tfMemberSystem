package com.organ.dao.appinfoconfig.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;

import com.organ.common.BaseDao;
import com.organ.dao.appinfoconfig.AppInfoConfigDao;
import com.organ.model.AppSecret;

public class AppInfoConfigDaoImpl extends BaseDao<AppSecret, Long> implements
		AppInfoConfigDao {

	@SuppressWarnings("unchecked")
	@Override
	public List getAppInfo(int userId,int pagesize, int pageindex) {
		// TODO Auto-generated method stub
		try {
			int start = pageindex * pagesize;
			String hql = "select " + "ta.id" + ",ta.appId" + ",ta.secert"
					+ ",ta.callbackurl" + ",ta.apptime" + ",ta.appname"
					+ ",ta.isopen" + " from t_appsecret ta limit " + start
					+ "," + pagesize;
			System.out.println(hql);
			SQLQuery query = this.getSession().createSQLQuery(hql);
			List list = query.list();
			if (list.size() > 0) {
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * ��ȡ��ǰ����
	 */
	public Long getDate() {
		Date d = new Date();
		return d.getTime();
	}

	/**
	 * ���Ӧ��
	 */
	@Override
	public int updatePriv(int appId, String secert, String callbackurl,
			String appname, int isopen) {
		AppSecret appSecret = new AppSecret();
		appSecret.setAppId(appId + "");
		appSecret.setAppName(appname);
		appSecret.setAppTime(getDate());
		appSecret.setCallBackUrl(callbackurl);
		appSecret.setIsOpen(isopen);
		appSecret.setSecert(secert);
		try {
			save(appSecret);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return appSecret.getId();
	}

	@Override
	public int DeletelApp(int id) {
		try {
			String hql = "delete AppSecret where id =" + id;
			int result = delete(hql);
			return result;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		try {
			int count = count("from AppSecret");
			return count;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int editApp(int id, int appId, String secert, String callBackUrl,
			long appTime, String appName, int isOpen) {
		try {
			String hql = "update AppSecret ap set ap.appId= '" + appId
					+ "',ap.secert='" + secert + "',ap.callBackUrl='"
					+ callBackUrl + "',ap.appTime='" + appTime
					+ "',ap.appName='" + appName + "',ap.isOpen='" + isOpen
					+ "' where ap.id=" + id;
			int result = update(hql);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List SearchAppInfo(String AppName, int pagesize, int pageindex) {
		try {
			int start = pageindex * pagesize;
			String hql;
			if (!StringUtils.isBlank(AppName)) {
				hql = "select ap.id,ap.appId,ap.secert,ap.callBackUrl,ap.appTime,ap.appName,ap.isOpen from t_appsecret ap where ap.appName like '%"
						+ AppName + "%' limit " + start + "," + pagesize;
			} else {
				hql = "select ap.id,ap.appId,ap.secert,ap.callBackUrl,ap.appTime,ap.appName,ap.isOpen from t_appsecret ap limit "
						+ start + "," + pagesize;
			}
			SQLQuery query = this.getSession().createSQLQuery(hql);
			List list = query.list();
			if (list.size() > 0) {
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int getSearchCount(String AppName) {
		String sql;
		try {
			if (!StringUtils.isBlank(AppName)) {
				sql = "select count(*) from (select ap.id,ap.appId,ap.secert,ap.callBackUrl,ap.appTime,ap.appName,ap.isOpen from t_appsecret ap where ap.appName like '%"
						+ AppName + "%') as aa";
			} else {
				sql = "select count(*) from (select ap.id,ap.appId,ap.secert,ap.callBackUrl,ap.appTime,ap.appName,ap.isOpen from t_appsecret ap) as aa";
			}
			int SearchCount = Integer.parseInt(this.getSession()
					.createSQLQuery(sql).list().get(0).toString());
			return SearchCount;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

}
