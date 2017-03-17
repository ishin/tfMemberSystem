package com.organ.dao.adm.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.organ.common.BaseDao;
import com.organ.dao.adm.RolePrivDao;
import com.organ.model.TPriv;
import com.organ.model.TRolePriv;

public class RolePrivDaoImpl extends BaseDao<TRolePriv, Integer> implements RolePrivDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<TRolePriv> getRolePrivsByPrivs(Integer[] privIds) {
		try {
			
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.in("privId", privIds));
			
			List<TRolePriv> list = ctr.list();
			
			if (list.size() > 0) {
				return list;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
