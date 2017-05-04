package com.sealtalk.dao.map.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.sealtalk.common.BaseDao;
import com.sealtalk.dao.map.MapDao;
import com.sealtalk.model.TMap;
import com.sealtalk.utils.LogUtils;

public class MapDaoImpl extends BaseDao<TMap, Long> implements MapDao {
	private static final Logger logger = LogManager.getLogger(MapDaoImpl.class);
	
	@Override
	public void saveLocation(TMap tm) {
		try {
			save(tm);
		} catch(Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	public TMap getLaLongtitudeForUserId(int userId) {
		try {
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.eq("userId", userId));
			
			List<TMap> list = ctr.list();
			
			if (list.size() > 0) {
				return list.get(0);
			}
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		
		return null;
	}


	@Override
	public void updateLocation(int userId, String latitude, String longtitude, long now) {
		try {
			StringBuilder sbSql = new StringBuilder("update TMap tm set tm.subDate=");
			sbSql.append(now);
			
			if (latitude != null) {
				sbSql.append(",tm.latitude='").append(latitude).append("'");
			}
			if (longtitude != null) {
				sbSql.append(",tm.longitude='").append(longtitude).append("'");
			}
			
			sbSql.append(" where userId=").append(userId);
			update(sbSql.toString());
			
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		
	}


	@Override
	public List<TMap> getMapByIds(Integer[] ids) {
		try {
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.in("userId", ids));
			
			List<TMap> list = ctr.list();
			
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
	public int deleteRelationByIds(String ids) {
		try {
			String hql = (new StringBuilder("delete from TMap where userId in (").append(ids).append(")")).toString();
			return delete(hql);
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		return 0;
	}


}
