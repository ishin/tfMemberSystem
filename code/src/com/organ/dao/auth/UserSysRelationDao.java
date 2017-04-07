package com.organ.dao.auth;

import java.util.List;

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

	/**
	 * 获取所有用户应用关系
	 * @param appRecordId 
	 * @return
	 */
	public List<UserSysRelation> getAllRelation(int appRecordId);

}
