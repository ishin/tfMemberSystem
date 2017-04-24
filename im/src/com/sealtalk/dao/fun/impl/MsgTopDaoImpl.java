package com.sealtalk.dao.fun.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.sealtalk.common.BaseDao;
import com.sealtalk.dao.fun.MsgTopDao;
import com.sealtalk.model.TFunction;
import com.sealtalk.model.TMsgtop;

/**
 * 置顶
 * @author hao_dy
 * @date 2017/01/04
 * @since jdk1.7
 */
public class MsgTopDaoImpl extends BaseDao<TMsgtop, Integer> implements MsgTopDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<TMsgtop> getMsgTop(Integer userId) {
		String hql = (new StringBuilder("from TMsgtop tm where userId=").append(userId).append(" order by listorder asc")).toString();
		
		try {
			List<TMsgtop> list = getSession().createQuery(hql).list();
			
			if (list.size() > 0) {
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public void setMsgTop(TMsgtop tm) {
		try {
			saveOrUpdate(tm);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getCountMsgTopForUserId(int userIdInt) {
		return count((new StringBuilder(" from TMsgtop where userId=").append(userIdInt)).toString());
	}

	@Override
	public void cancelMsgTop(int userIdInt, int topIdInt, String topType) {
		String hql = (new StringBuilder("delete TMsgtop where userId=").append(userIdInt).append(" and topId=").append(topIdInt).append(" and msgType='").append(topType).append("'")).toString();
		try {
			delete(hql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
