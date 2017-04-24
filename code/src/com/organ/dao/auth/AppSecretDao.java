package com.organ.dao.auth;

import com.organ.common.IBaseDao;
import com.organ.model.AppSecret;


public interface AppSecretDao extends IBaseDao<AppSecret, Integer> {
	/**
	 * 设置app,secret,url
	 * @param as
	 */
	public void setAppIDAndSecretAndUrl(AppSecret as);
	
	/**
	 * 依据appId获取appSecret
	 * @param appId
	 * @param companyId 
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
	 * @param organId 
	 * @return
	 */
	public AppSecret getAppSecretBySecret(String secret, Integer organId);

	/**
	 * 根据appId及secret获取appsecret
	 * @param appId
	 * @param secret
	 * @return
	 */
	public AppSecret getAppSecretByAppIdAndSecret(String appId, String secret);

}
