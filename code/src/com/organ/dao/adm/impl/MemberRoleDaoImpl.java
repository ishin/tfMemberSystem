package com.organ.dao.adm.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.organ.common.BaseDao;
import com.organ.dao.adm.MemberRoleDao;
import com.organ.model.TMemberRole;
import com.organ.utils.LogUtils;

public class MemberRoleDaoImpl extends BaseDao<TMemberRole, Integer> implements MemberRoleDao {

	private static final Logger logger = LogManager.getLogger(MemberRoleDaoImpl.class);
	
	@SuppressWarnings("unchecked")
	@Override
	public List<TMemberRole> getRoleForId(int id) {
		try {
			
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.eq("memberId", id));
			
			List<TMemberRole> list = ctr.list();
			
			if (list.size() > 0) {
				return list;
			}
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TMemberRole> getMemberRolesByRoleIds(Integer[] ids) {
		try {
			
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.in("memberId", ids));
			
			List<TMemberRole> list = ctr.list();
			
			if (list.size() > 0) {
				return list;
			}
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public List getMemberIdsByRoleIds(String ids) {
		try {
			String hql = "select member_id from t_member_role where role_id in (" + ids + ")";
			return runSql(hql);
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public int deleteRelationByIds(String userids, String isLogic) {
		try {
			if (isLogic.equals("1")) {
				String hql = (new StringBuilder("update TMemberRole set isDel=0 where memberId in (").append(userids).append(")")).toString();
				return update(hql);
			} else {
				String hql = (new StringBuilder("delete from TMemberRole where memberId in (").append(userids).append(")")).toString();
				return delete(hql);
			}
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		return 0;
	}

}
