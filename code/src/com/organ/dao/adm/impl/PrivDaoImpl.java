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
	public List<TPriv> getPrivByUrl(String[] url) {
		try {
			
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.in("url", url));
			
			List<TPriv> list = ctr.list();
			
			if (list.size() > 0) {
				return list;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List getMemberByPrivId(int[] privId) {
		String sql = (new StringBuilder("select M.id mid,TRP.id trpid from t_member M left join t_member_role MR on M.id=MR.member_id left join t_role_priv TRP on TRP.role_id=MR.role_id and TRP.priv_id=").append(privId)).toString();
		SQLQuery query = this.getSession().createSQLQuery(sql);
		List list = query.list();
		
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TPriv> getAllPriv() {
		String sql = (new StringBuilder("from TPriv t order by t.parentId asc")).toString();
		Query query = this.getSession().createQuery(sql);
		List<TPriv> list = query.list();
		return list;
	}

}
