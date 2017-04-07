package com.organ.dao.auth;

import com.organ.common.IBaseDao;
import com.organ.model.UserSysRelation;

public interface UserSysRelationDao extends IBaseDao<UserSysRelation, Integer> {
	
	/**
	 * 获取用户应用关系 
	 * @param appId
	 * @param userId
	 * @return
	 */
	public UserSysRelation getRelation(int appId, int userId);
}
