package com.sealtalk.action.msg;

import javax.servlet.ServletException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.sf.json.JSONObject;

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

public class UserServiceAction extends BaseAction {

	private static final long serialVersionUID = 7812442221327984861L;
	private static final Logger logger = LogManager.getLogger(UserServiceAction.class);
	
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
			result = userService.getToken(clearChar(userid));
		}
		logger.info(result);
		returnToClient(result);
		return "text";
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
			result = userService.refreshUser(clearChar(userid));
		}
		
		logger.info(result);
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 检测用户在线
	 * @return
	 * @throws ServletException
	 */
	public String checkOnline() throws ServletException {
		String result = userService.checkOnline(clearChar(userid));
		logger.info(result);
		return "text";
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
