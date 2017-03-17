package com.organ.dao.adm;

import java.util.List;

import com.organ.common.IBaseDao;
import com.organ.model.TPriv;

public interface PrivDao extends IBaseDao<TPriv, Integer> {

	/**
	 * 根据url获取权限
	 * @param url
	 * @return
	 */
	public List<TPriv> getPrivByUrl(String[] url);

	/**
	 * 根据权限id获取用户属性
	 * @param privId
	 * @return
	 */
	public List getMemberByPrivId(int[] privId);

	/**
	 * 获取全部权限
	 * @return
	 */
	public List<TPriv> getAllPriv();

}
