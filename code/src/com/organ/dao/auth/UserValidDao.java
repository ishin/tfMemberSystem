package com.organ.dao.auth;

import com.organ.model.UserValid;
import com.organ.common.IBaseDao;

public interface UserValidDao extends IBaseDao<UserValid, Integer> {

	public void setUnAuthToken(UserValid uv);

	public UserValid getUserValidByUnAuthToken(String unAuthToken);

	public UserValid getUserValidByAuthToken(String authToken);

	public UserValid getUserValidByRealToken(String visitToken);
}
