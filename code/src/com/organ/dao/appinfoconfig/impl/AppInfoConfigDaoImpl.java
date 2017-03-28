package com.organ.dao.appinfoconfig.impl;

import java.util.List;

import org.hibernate.SQLQuery;

import com.organ.common.BaseDao;
import com.organ.dao.appinfoconfig.AppInfoConfigDao;
import com.organ.model.AppSecret;

public class AppInfoConfigDaoImpl extends BaseDao<AppSecret, Long> implements
		AppInfoConfigDao {

	@Override
	public List getAppInfo(int pagesize,int pageindex) {
		// TODO Auto-generated method stub
		try {
			int start = pageindex * pagesize;
			String hql = "select "+",ta.id"+",ta.appId"+
			",ta.secert"+",ta.callbackurl"+",ta.apptime"+",ta.appname"+",ta.isopen"+
			"from t_appsecret ta limit "+start+","+pagesize;
			System.out.println(hql);
			SQLQuery query = this.getSession().createSQLQuery(hql);
			List list = query.list();
			if (list.size() > 0) {
				return list;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

}
