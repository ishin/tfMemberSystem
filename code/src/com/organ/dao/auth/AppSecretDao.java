package com.organ.dao.auth;

import com.organ.model.AppSecret;
import com.organ.common.IBaseDao;

public interface AppSecretDao extends IBaseDao<AppSecret, Integer> {
	/**
	 * 设置app,secret,url
	 * @param as
	 */
	public void setAppIDAndSecretAndUrl(AppSecret as);
	
	/**
	 * 依据appId获取appSecret
	 * @param appId
	 * @return
	 */
	public AppSecret getAppSecretByAppId(String appId);

	/**
	 * 更新AppSecret
	 * @param as
	 */
	public void updateAppSecret(AppSecret as);

	/**
	 * 依据secret获取appSecret
	 * @param secret
	 * @return
	 */
	public AppSecret getAppSecretBySecret(String secret);

}
