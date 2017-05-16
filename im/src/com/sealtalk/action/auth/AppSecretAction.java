package com.sealtalk.action.auth;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import com.sealtalk.common.AuthTips;
import com.sealtalk.common.BaseAction;
import com.sealtalk.common.XmlCommon;
import com.sealtalk.model.SessionUser;
import com.sealtalk.service.auth.AppSecretService;
import com.sealtalk.utils.XMLUtils;

/**
 * appid, secret action
 * @author hao_dy
 * @date 2017/03/08
 */

public class AppSecretAction extends BaseAction {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 场景一取临时令牌
	 * @return
	 * @throws ServletException
	 */
	public String getTempTokenSceneOne() throws ServletException,IOException, JSONException {
		String appId = getAppId();
		//String result = appSecretService.getTempTokenSceneOne(clearChar(appId));
		String result = appSecretService.getTempTokenSceneOne(appId);
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 场景二取临时令牌,场景一服务器不主动做转发的话，就和场景二是一样的。
	 * @return
	 * @throws ServletException
	 */
	public String getTempTokenSceneTwo() throws ServletException,IOException, JSONException {
		String appId = getAppId();
		String result = appSecretService.getTempTokenSceneOne(appId);
		//String result = appSecretService.getTempTokenSceneOne(clearChar(appId));
		returnToClient(result);
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
	 * 场景一登陆并授权，返回授权临时令牌,并跳转
	 * @return
	 * @throws ServletException
	 * @throws JSONException 
	 * @throws IOException 
	 */
	public String reqAuthorizeOne() throws ServletException, JSONException, IOException {
		String appId = getAppId();
		String result = appSecretService.reqAuthorizeOne(clearChar(unAuthToken), clearChar(userName), clearChar(userPwd), appId, clearChar(info));
		//String result = appSecretService.reqAuthorizeOne(clearChar(unAuthToken), clearChar(userName), clearChar(userPwd), clearChar(appId), clearChar(info));
		returnToClient(result);
		return "text";
	} 
	
	/**
	 * 场景二授权，web端专用
	 * @return
	 * @throws ServletException
	 * @throws JSONException 
	 * @throws IOException 
	 * @throws JSONException 
	 */
	public String reqAuthorizeTwo() throws ServletException, IOException, JSONException {
		SessionUser su = getSessionUser();
		String result = null;
		
		if (su == null) {
			JSONObject jo = new JSONObject();
			jo.put("code", 500);
			jo.put("text", AuthTips.NOTLOGIN.getText());
			result = jo.toString();
		} else {
			Integer id = su.getId();
			String appId = getAppId();
			result = appSecretService.reqAuthorizeTwo(id, appId, clearChar(unAuthToken));
			//result = appSecretService.reqAuthorizeTwo(id, clearChar(appId), clearChar(unAuthToken));
		}
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 场景二授权，手机端专用
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @throws JSONException
	 */
	public String reqAuthorizeTwoForApp() throws ServletException, IOException, JSONException {
		String appId = getAppId();
		String result = appSecretService.reqAuthorizeTwoForApp(clearChar(userId), appId, clearChar(unAuthToken));
		//String result = appSecretService.reqAuthorizeTwoForApp(clearChar(userId), clearChar(appId), clearChar(unAuthToken));
		
		returnToClient(result);
		return "text";
	}

	/**
	 * 获取访问令牌
	 * @return
	 * @throws ServletException
	 */
	public String getRealToken() throws ServletException {
		String secret = getSecret();
		String result = appSecretService.getRealToken(secret, clearChar(authToken), clearChar(companyId));
		//String result = appSecretService.getRealToken(clearChar(secret), clearChar(authToken), clearChar(companyId));
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 获取授权用户数据
	 * @return
	 * @throws ServletException
	 */
	public String getAuthResource() throws ServletException {
		String result = appSecretService.getAuthResource(clearChar(visitToken));
		returnToClient(result);
		return "text";
	}
	
	private String getAppId() {
		return XMLUtils.getInstance().getDynamicData("appid");
	}
	
	private String getSecret() {
		return XMLUtils.getInstance().getDynamicData("secret");
	}
	
	public String visitToken;	//访问令牌
	public String authToken;	//授权令牌
	public String unAuthToken;	//未授权令牌
	private String info;		//要获取的用户信息
	//private String appId;		//appId
	//private String secret;		//secret
	private String userName;	//用户名
	private String userPwd;		//用户密码
	private String userId;		//成员id
	private String companyId;	//系统标识
	
	
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	/*public void setAppId(String appId) {
		this.appId = appId;
	}
	
	public void setSecret(String secret) {
		this.secret = secret;
	}*/
	
	public void setUnAuthToken(String unAuthToken) {
		this.unAuthToken = unAuthToken;
	}
	
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public void setVisitToken(String visitToken) {
		this.visitToken = visitToken;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	private AppSecretService appSecretService;

	public void setAppSecretService(AppSecretService appSecretService) {
		this.appSecretService = appSecretService;
	}
	
}
