package com.sealtalk.service.auth;

public interface AppSecretService {
	/**
	 * 场景一获取临时令牌
	 * @param appId
	 * @param companyId 
	 * @return
	 */
	public String getTempTokenSceneOne(String appId);

	/**
	 * 场景一验证登陆，授权
	 * @param unAuthToken
	 * @param userName
	 * @param userPwd
	 * @param info 
	 * @param companyId 
	 * @return
	 */
	public String reqAuthorizeOne(String unAuthToken, String userName, String userPwd, String appId, String info);

	/**
	 * 获取真实访问令牌
	 * @param secret
	 * @param authToken
	 * @param companyId 
	 * @return
	 */
	public String getRealToken(String secret, String authToken, String companyId);
	
	/**
	 * 获取授权资源
	 * @param visitiToken
	 * @param companyId 
	 * @return
	 */
	public String getAuthResource(String visitiToken);

	/**
	 * 场景二授权,web端
	 * @param sessionUser 
	 * @param appId
	 * @return
	 */
	public String reqAuthorizeTwo(Integer id, String appId, String unAuthToken);

	/**
	 * 场景二授权,手机端
	 * @param appId
	 * @param unAuthToken
	 * @param unAuthToken2 
	 * @return
	 */
	public String reqAuthorizeTwoForApp(String userId, String appId, String unAuthToken);

	public boolean checkAppIdOfOrgan(String appId, int organId);


}
