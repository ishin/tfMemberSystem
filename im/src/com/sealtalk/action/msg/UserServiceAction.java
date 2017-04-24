package com.sealtalk.action.msg;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.googlecode.sslplugin.annotation.Secured;
import com.sealtalk.common.BaseAction;
import com.sealtalk.common.Tips;
import com.sealtalk.service.msg.UserServiceService;
import com.sealtalk.utils.StringUtils;

/**
 * 融云用户服务action
 * @author hao_dy
 * @since jdk1.7
 * @date 2017/01/11
 *
 */
@Secured
public class UserServiceAction extends BaseAction {

	private static final long serialVersionUID = 7812442221327984861L;
	private static final Logger logger = Logger.getLogger(UserServiceAction.class);
	
	/**
	 * 获取token
	 * @return
	 * @throws ServletException
	 */
	public String getToken() throws ServletException {
		JSONObject jo = new JSONObject();
		String result = null;
		
		if (StringUtils.getInstance().isBlank(userid)) {
			jo.put("code", -1);
			jo.put("text", Tips.NULLID.getName());
			result = jo.toString();
		} else {
			result = userService.getToken(userid);
		}
		
		return result;
	}
	
	/**
	 * 刷新用户
	 * @return
	 * @throws ServletException
	 */
	public String refreshUser() throws ServletException {
		JSONObject jo = new JSONObject();
		String result = null;
		
		if (StringUtils.getInstance().isBlank(userid)) {
			jo.put("code", -1);
			jo.put("text", Tips.NULLID.getName());
			result = jo.toString();
		} else {
			result = userService.refreshUser(userid);
		}
		
		return result;
	}
	
	/**
	 * 检测用户在线
	 * @return
	 * @throws ServletException
	 */
	public String checkOnline() throws ServletException {
		return userService.checkOnline(userid);
	}
	
	private UserServiceService userService;
	
	public UserServiceService getMsgService() {
		return userService;
	}

	public void setMsgService(UserServiceService userService) {
		this.userService = userService;
	}

	private String userid;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}
	
}
