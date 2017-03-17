package com.organ.dao.adm.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.organ.common.BaseDao;
import com.organ.dao.adm.MemberRoleDao;
import com.organ.model.TMemberRole;

public class MemberRoleDaoImpl extends BaseDao<TMemberRole, Integer> implements MemberRoleDao {

	@SuppressWarnings("unchecked")
	@Override
	public TMemberRole getRoleForId(int id) {
		try {
			
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.eq("memberId", id));
			
			List<TMemberRole> list = ctr.list();
			
			if (list.size() > 0) {
				return list.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TMemberRole> getRolesForIds(Integer[] ids) {
		try {
			
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.in("memberId", ids));
			
			List<TMemberRole> list = ctr.list();
			
			if (list.size() > 0) {
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
