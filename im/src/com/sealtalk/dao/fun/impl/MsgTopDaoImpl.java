package com.sealtalk.dao.fun.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sealtalk.common.BaseDao;
import com.sealtalk.dao.fun.MsgTopDao;
import com.sealtalk.model.TMsgtop;
import com.sealtalk.utils.LogUtils;

/**
 * 置顶
 * @author hao_dy
 * @date 2017/01/04
 * @since jdk1.7
 */
public class MsgTopDaoImpl extends BaseDao<TMsgtop, Integer> implements MsgTopDao {

	private static final Logger logger = LogManager.getLogger(MsgTopDaoImpl.class);
	
	@SuppressWarnings("unchecked")
	@Override
	public List<TMsgtop> getMsgTop(Integer userId) {
		String hql = (new StringBuilder("from TMsgtop tm where userId=").append(userId).append(" order by listorder asc")).toString();
		
		logger.info("getMsgTop sql: " + hql);
	
		try {
			List<TMsgtop> list = getSession().createQuery(hql).list();
			
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
	public void setMsgTop(TMsgtop tm) {
		try {
			saveOrUpdate(tm);
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
	}

	@Override
	public int getCountMsgTopForUserId(int userIdInt) {
		String sql = (new StringBuilder(" from TMsgtop where userId=").append(userIdInt)).toString();
		logger.info("getCountMsgTopForUserId sql: " + sql);
		return count(sql);
	}

	@Override
	public void cancelMsgTop(int userIdInt, int topIdInt, String topType) {
		String hql = (new StringBuilder("delete TMsgtop where userId=").append(userIdInt).append(" and topId=").append(topIdInt).append(" and msgType='").append(topType).append("'")).toString();
		
		logger.info("cancelMsgTop sql: " + hql);
		try {
			delete(hql);
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
	}

	@Override
	public int deleteRelationByIds(String ids) {
		try {
			String hql = (new StringBuilder("delete from TMsgtop where userId in (").append(ids).append(")")).toString();
			
			logger.info("deleteRelationByIds sql: " + hql);
			return delete(hql);
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		return 0;
	}

}
