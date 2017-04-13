package com.organ.dao.appinfoconfig.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;

import com.organ.common.BaseDao;
import com.organ.dao.appinfoconfig.AppInfoConfigDao;
import com.organ.dao.limit.LimitDao;
import com.organ.dao.limit.RoleAppSecretDao;
import com.organ.model.AppSecret;
import com.organ.model.TPriv;
import com.organ.utils.PrivUrlNameUtil;
import com.organ.utils.TimeGenerator;

public class AppInfoConfigDaoImpl extends BaseDao<AppSecret, Long> implements
		AppInfoConfigDao {
	LimitDao limitDao;
	RoleAppSecretDao roleappsecretDao;

	public RoleAppSecretDao getRoleappsecretDao() {
		return roleappsecretDao;
	}

	public void setRoleappsecretDao(RoleAppSecretDao roleappsecretDao) {
		this.roleappsecretDao = roleappsecretDao;
	}

	public LimitDao getLimitDao() {
		return limitDao;
	}

	public void setLimitDao(LimitDao limitDao) {
		this.limitDao = limitDao;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List getAppInfo(int userId, int organId, int pagesize, int pageindex) {
		// TODO Auto-generated method stub
		try {
			int start = pageindex * pagesize;
			String hql = "select " + "ta.id" + ",ta.appId" + ",ta.secert"
					+ ",ta.callbackurl" + ",ta.apptime" + ",ta.appname"
					+ ",ta.isopen" + ",tm.fullname"
					+ " from t_appsecret ta right join t_member tm on tm.id ="
					+ userId + " where ta.organ_id=" + organId + " and tm.id ="
					+ userId + " limit " + start + "," + pagesize;
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
	 * long转成data
	 */
	public Long getDate() {
		return TimeGenerator.getInstance().getUnixTime();
	}

	/**
	 * 添加应用
	 */
	@Override
	public int updatePriv(String appId, String secert, String callbackurl,
			String appname, int isopen, int organId) {
		AppSecret appSecret = new AppSecret();
		appSecret.setAppId(appId);
		appSecret.setAppName(appname);
		appSecret.setAppTime(getDate());
		appSecret.setCallBackUrl(callbackurl);
		appSecret.setIsOpen(isopen);
		appSecret.setSecert(secert);
		appSecret.setOrganId(organId);
		try {
			save(appSecret);
			// 保存以及权限
			TPriv tPriv = new TPriv();
			tPriv.setApp(appname);
			tPriv.setName(appname);
			tPriv.setParentId(0);
			tPriv.setUrl(PrivUrlNameUtil.initUrlName(appname));
			tPriv.setListorder(0); // 这个不能为空
			limitDao.save(tPriv);
			// 添加二级权限
			if (tPriv.getId() != null) {
				TPriv tPriv2 = new TPriv();
				tPriv2.setApp(appname);
				tPriv2.setName("访问权限");
				tPriv2.setParentId(tPriv.getId());
				tPriv2.setUrl(PrivUrlNameUtil.initUrlName("访问权限"));
				tPriv2.setListorder(0); // 这个不能为空
				limitDao.save(tPriv2);
				// 添加三级权限
				if (tPriv2.getId() != null) {
					TPriv tPriv3 = new TPriv();
					tPriv3.setApp(appname);
					tPriv3.setName("登陆权限");
					tPriv3.setParentId(tPriv2.getId());
					tPriv3.setUrl(PrivUrlNameUtil.initUrlName("登陆权限"));
					tPriv3.setListorder(0); // 这个不能为空
					limitDao.save(tPriv3);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return appSecret.getId();
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
	public int editApp(int id, String appId, String secert, String callBackUrl,
			String appName, int isOpen) {
		try {
			String hql = "update AppSecret ap set ap.appId= '" + appId
					+ "',ap.secert='" + secert + "',ap.callBackUrl='"
					+ callBackUrl + "',ap.appTime='" + getDate()
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
	public List SearchAppInfo(int organId, String AppName, int pagesize,
			int pageindex) {
		try {
			int start = pageindex * pagesize;
			String hql;
			if (!StringUtils.isBlank(AppName)) {
				hql = "select ap.id,ap.appId,ap.secert,ap.callBackUrl,ap.appTime,ap.appName,ap.isOpen from t_appsecret ap where ap.appName like '%"
						+ AppName
						+ "%' and ap.organ_id="
						+ organId
						+ " limit "
						+ start + "," + pagesize;
			} else {
				hql = "select ap.id,ap.appId,ap.secert,ap.callBackUrl,ap.appTime,ap.appName,ap.isOpen from t_appsecret ap where ap.organ_id="
						+ organId + " limit " + start + "," + pagesize;
			}
			System.out.println("---------" + hql);

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
	public String getAppNameByID(int id) {
		String name = null;
		try {
			String sql = "select appname from t_appsecret where id =" + id;
			name = this.getSession().createSQLQuery(sql).uniqueResult()
					.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return name;
	}

	@Override
	public int getSearchCount(String AppName, int organId) {
		String sql;
		try {
			if (!StringUtils.isBlank(AppName)) {
				sql = "select count(*) from t_appsecret ap where ap.organ_id=" + organId + " and ap.appName like '%"
						+ AppName + "%'";
			} else {
				sql = "select count(*) from t_appsecret ap where ap.organ_id=" + organId;
			}
			int SearchCount = Integer.parseInt(this.getSession()
					.createSQLQuery(sql).list().get(0).toString());
			return SearchCount;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List SearchAppInfoName() {
		try {
			String hql = "select id,appname from t_appsecret";
			return runSql(hql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
