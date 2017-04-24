package com.sealtalk.dao.fun.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.sealtalk.common.BaseDao;
import com.sealtalk.dao.fun.DontDistrubDao;
import com.sealtalk.model.TDontDistrub;
import com.sealtalk.model.TGroup;

/**
 * 其它功能管理层
 * @author hao_dy
 * @date 2017/01/04
 * @since jdk1.7
 */
public class DontDistrubDaoImpl extends BaseDao<TDontDistrub, Long> implements DontDistrubDao {
	@Override
	public void setDontDistrub(TDontDistrub tf) {
		try {
			saveOrUpdate(tf);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TDontDistrub> getDistrubListForUserId(int userIdInt) {
		try {
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.eq("memberId", userIdInt));
			
			List<TDontDistrub> list = ctr.list();
			
			if (list.size() > 0) {
				return list;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TDontDistrub getSingleDistrubListForUserId(int userIdInt, int groupIdInt) {
		try {
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.and(Restrictions.eq("memberId", userIdInt), Restrictions.eq("groupId", groupIdInt)));
			
			List<TDontDistrub> list = ctr.list();
			
			if (list.size() > 0) {
				return list.get(0);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
