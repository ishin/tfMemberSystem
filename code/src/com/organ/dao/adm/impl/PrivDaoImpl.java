package com.organ.dao.adm.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

import com.organ.common.BaseDao;
import com.organ.dao.adm.PrivDao;
import com.organ.model.TMember;
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

	@Override
	public TPriv getSimilarityPrivByUrl(String url, int organId) {
		String hql = "SELECT new TPriv(t.id,t.parentId,t.name,t.url,t.app) FROM TPriv t WHERE organId="+ organId +" AND url LIKE '"+url+"%'";
		Query query = getSession().createQuery(hql);

		List<TPriv> list = query.list();

		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
}
