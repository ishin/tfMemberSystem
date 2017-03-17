package com.organ.dao.adm.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.organ.common.BaseDao;
import com.organ.dao.adm.PositionDao;
import com.organ.model.TMember;
import com.organ.model.TPosition;

public class PositionDaoImpl extends BaseDao<TPosition, Integer> implements PositionDao {

	@Override
	public TPosition getPositionByName(Integer organId, String name) {
		
		Criteria ctr = getCriteria();
		ctr.add(Restrictions.eq("organId", organId));
		ctr.add(Restrictions.eq("name", name));
		
		List list = ctr.list();
		
		if (list.size() > 0) {
			return (TPosition) list.get(0);
		}
		
		return null;
	}

}
