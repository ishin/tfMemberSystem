package com.organ.action.auth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Locale;

import javax.servlet.ServletException;

import org.json.JSONException;
import org.json.JSONObject;

import com.organ.service.auth.AppSecretService;
import com.organ.common.BaseAction;
import com.organ.common.Tips;

/**
 * appid, secret action
 * @author hao_dy
 * @date 2017/03/08
 */
public class AppSecretAction extends BaseAction {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 获取appid和secret
	 * @return
	 * @throws ServletException
	 */
	public String getAppIDAndSecret() throws ServletException {
		String result = appSecretService.getAppIDAndSecret();
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 设置auth2登陆基本信息
	 * @return
	 * @throws ServletException
	 */
	public String setAppIDAndSecretAndUrl() throws ServletException {
		String result = appSecretService.setAppIDAndSecretAndUrl(appName, appId, secret, url, isOpen);
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 场景一取临时令牌
	 * @return
	 * @throws ServletException
	 */
	public String getTempTokenSceneOne() throws ServletException,IOException, JSONException {
		JSONObject result = appSecretService.getTempTokenSceneOne(appId);
		//服务器主动跳转(前端异步提交时，不能正常跳转，会把页面返回去)
		//如果使用服务器跳转，前端请使用form表单
		/*if (result.getString("code").equals("500")) {
			returnToClient(result.toString());
			return "text";
		} else {
			setUnAuthToken(result.getString("text"));
			return "redirectLogin";
			//response.sendRedirect("auth!redirectLogin?unAuthToken="+result.getString("text"));
		}
		*/
		returnToClient(result.toString());
		return "text";
	}
	
	/**
	 * 场景二取临时令牌
	 * @return
	 * @throws ServletException
	 */
	public String getTempTokenSceneTwo() throws ServletException,IOException, JSONException {
		JSONObject result = appSecretService.getTempTokenSceneOne(appId);
		returnToClient(result.toString());
		return "text";
	}
	
	/**
	 * 获取未授权临时令牌并跳转登陆
	 * @return
	 * @throws ServletException
	 */
	public String redirectLogin() throws ServletException {
		return "login";
	}
	
	/**
	 * 场景一登陆并授权，返回授权临时令牌
	 * @return
	 * @throws ServletException
	 * @throws JSONException 
	 * @throws IOException 
	 */
	public String reqAuthorizeOne() throws ServletException, JSONException, IOException {
		JSONObject result = appSecretService.reqAuthorizeOne(unAuthToken, userName, userPwd, appId, info);
		String code = result.getString("code");
		
		if (code.equals("500")) {
			String url = getUrl();
			response.sendRedirect(url + "auth!redirectLogin?unAuthToken=" + unAuthToken + "&err=1");
			//return "login";
			//returnToClient(result.toString());
			//return "text";
		} else {
			String url = result.getString("url") + "?authToken=" + result.getString("text");
			response.sendRedirect(url);
		}
		return null;
	} 
	
	/**
	 * 场景二授权
	 * @return
	 * @throws ServletException
	 * @throws JSONException 
	 * @throws IOException 
	 */
	public String reqAuthorizeTwo() throws ServletException, IOException {
		String result = appSecretService.reqAuthorizeTwo(getSessionUser(), appId, unAuthToken);
		returnToClient(result);
		return "text";
	}
	
	
	/**
	 * 获取访问令牌
	 * @return
	 * @throws ServletException
	 */
	public String getRealToken() throws ServletException {
		String result = appSecretService.getRealToken(secret, authToken);
		returnToClient(result.toString());
		return "text";
	}
	
	/**
	 * 获取授权用户数据
	 * @return
	 * @throws ServletException
	 */
	public String getAuthResource() throws ServletException {
		System.out.println("getAuthResource() visitToken: " + visitToken);
		String result = appSecretService.getAuthResource(visitToken);
		returnToClient(result.toString());
		return "text";
	}
	
	/** 以下接口为oa测试使用 **/
	public String oaLogin() throws ServletException {
		return "oaLogin";
	}
	
	
	public String visitToken;	//访问令牌
	public String authToken;	//授权令牌
	public String unAuthToken;	//未授权令牌
	private String info;		//要获取的用户信息
	private String appId;		//appId
	private String appName;		//应用名称
	private String isOpen;		//是否启用
	private String secret;		//secret
	private String url;			//回调url
	private String userName;	//用户名
	private String userPwd;		//用户密码
	
	public void setIsOpen(String isOpen) {
		this.isOpen = isOpen;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}
	
	public void setSecret(String secret) {
		this.secret = secret;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getUnAuthToken() {
		return unAuthToken;
	}
	
	public void setUnAuthToken(String unAuthToken) {
		this.unAuthToken = unAuthToken;
	}
	
	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public String getVisitiToken() {
		return visitToken;
	}

	public void setVisitToken(String visitToken) {
		this.visitToken = visitToken;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPwd() {
		return userPwd;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	private AppSecretService appSecretService;

	public void setAppSecretService(AppSecretService appSecretService) {
		this.appSecretService = appSecretService;
	}
	
}
