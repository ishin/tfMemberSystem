package com.organ.dao.adm.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

import com.organ.common.BaseDao;
import com.organ.dao.adm.PrivDao;
import com.organ.model.TPriv;

public class PrivDaoImpl extends BaseDao<TPriv, Integer> implements PrivDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<TPriv> getPrivByUrl(String[] url, int organId) {
		try {
			
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.in("url", url));
			ctr.add(Restrictions.eq("organId", organId));
			
			List<TPriv> list = ctr.list();
			
			if (list.size() > 0) {
				return list;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<TPriv> getPrivByOrganAndApp(String app, int organId) {
		try {
			
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.eq("app", app));
			ctr.add(Restrictions.eq("organId", organId));
			
			List<TPriv> list = ctr.list();
			
			if (list != null && list.size() > 0) {
				return list;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public int getMaxPrivId() {
		String sql = "from t_priv";
		return getMax("id", sql);
	}
}
