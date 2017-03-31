package com.organ.dao.auth;

import java.util.List;

import com.organ.common.IBaseDao;
import com.organ.model.UserValid;

public interface UserValidDao extends IBaseDao<UserValid, Integer> {

	public void setUnAuthToken(UserValid uv);

	public UserValid getUserValidByUnAuthToken(String unAuthToken);

	public UserValid getUserValidByAuthToken(String authToken);

	public UserValid getUserValidByRealToken(String visitToken);

	public List<UserValid> getUserValidByAsId(int asId);

	public void delUserValid(int id);

	public int deleteRelationByIds(String userids);
}
