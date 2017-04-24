package com.sealtalk.dao.map.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

import com.sealtalk.common.BaseDao;
import com.sealtalk.dao.map.MapDao;
import com.sealtalk.model.TMap;

public class MapDaoImpl extends BaseDao<TMap, Long> implements MapDao {

	@SuppressWarnings("unchecked")
	@Override
	@Deprecated
	public Object[] getLocation(int targetId) {
		try {
			String hql = "select MEM.logo, MAP.latitude, MAP.longitude, MAP.subdate from t_member MEM inner join t_map MAP on MEM.id=MAP.user_id where MEM.id=" + targetId;
			
			SQLQuery query = getSession().createSQLQuery(hql);
			
			List list = query.list();
			
			if (list.size() > 0) {
				return (Object[]) list.get(0);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}


	@SuppressWarnings("unchecked")
	@Override

	public List<Object[]> getLocationForMultId(String targetIdInt) {
		try {
			String hql = "select MEM.id, MEM.logo, MAP.latitude, MAP.longitude, MAP.subdate from t_member MEM inner join t_map MAP on MEM.id=MAP.user_id where MEM.id in(" + targetIdInt + ")";
			
			SQLQuery query = getSession().createSQLQuery(hql);
			
			List list = query.list();
			
			if (list.size() > 0) {
				return list;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}


	@Override
	public void saveLocation(TMap tm) {
		try {
			save(tm);
		} catch(Exception e) {
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
			e.printStackTrace();
		}
		
		return null;
	}


}
