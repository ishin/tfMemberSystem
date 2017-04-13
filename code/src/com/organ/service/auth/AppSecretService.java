package com.organ.service.auth;

import org.json.JSONObject;

import com.organ.model.SessionUser;


public interface AppSecretService {
	/**
	 * 获取生成的appid和secret
	 * @return
	 */
	public String getAppIDAndSecret();
	
	/**
	 * 设置appid和secret和url
	 * @param appId
	 * @param secret
	 * @param url
	 */
	public String setAppIDAndSecretAndUrl(String appName, String appId, String secret, String url, String isOpen);

	/**
	 * 场景一获取临时令牌
	 * @param appId
	 * @param companyId 
	 * @return
	 */
	public JSONObject getTempTokenSceneOne(String appId, String companyId);

	/**
	 * 场景一验证登陆，授权
	 * @param unAuthToken
	 * @param userName
	 * @param userPwd
	 * @param info 
	 * @param companyId 
	 * @return
	 */
	public JSONObject reqAuthorizeOne(String unAuthToken, String userName, String userPwd, String appId, String info, String companyId);

	/**
	 * 获取真实访问令牌
	 * @param secret
	 * @param authToken
	 * @return
	 */
	public String getRealToken(String secret, String authToken);
	
	/**
	 * 获取授权资源
	 * @param visitiToken
	 * @param companyId 
	 * @return
	 */
	public String getAuthResource(String visitiToken, String companyId);

	/**
	 * 场景二授权,web端
	 * @param sessionUser 
	 * @param appId
	 * @return
	 */
	public JSONObject reqAuthorizeTwo(SessionUser sessionUser, String appId, String unAuthToken);

	/**
	 * 场景二授权,手机端
	 * @param appId
	 * @param unAuthToken
	 * @param unAuthToken2 
	 * @return
	 */
	public JSONObject reqAuthorizeTwoForApp(String userName, String appId, String unAuthToken, String companyId);

	/**
	 * 根据appid,secret获取appsecret
	 * @param appId
	 * @param secret
	 * @return
	 */
	public String getAppSecretByAppIdAndSecret(String appId, String secret);


}
