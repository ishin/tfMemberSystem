package com.sealtalk.action.fun;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.googlecode.sslplugin.annotation.Secured;
import com.sealtalk.common.BaseAction;
import com.sealtalk.common.Tips;
import com.sealtalk.service.fun.FunctionService;

/**
 * 辅助功能action 
 * @author hao_dy
 * @since jdk1.7
 * @date 2017/01/07
 */
@Secured
public class FunctionAction extends BaseAction {

	private static final long serialVersionUID = -7261604465748499252L;
	private static final Logger logger = Logger.getLogger(FunctionAction.class);
	
	/**
	 * 设置消息免打扰功能
	 * @return
	 * @throws ServletException
	 */
	public String setNotRecieveMsg() throws ServletException {
		String result = null;
		
		try {
			if (functionService != null) {
				result = functionService.setNotRecieveMsg(status, groupid, userid);
			} else {
				JSONObject jo = new JSONObject();
				jo.put("code", -1);
				jo.put("text", Tips.UNKNOWERR.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 获取消息免打扰接口
	 * @return
	 * @throws ServletException
	 */
	public String getNotRecieveMsg() throws ServletException {
		String result = functionService.getNotRecieveMsg(groupid, userid);
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 设置系统提示音功能
	 * @return
	 * @throws ServletException
	 */
	public String setSysTipVoice() throws ServletException {
		String result = null;
		
		System.out.println("-----------------systip :" + userid + " : " + status);
		try {
			if (functionService != null) {
				result = functionService.setSysTipVoice(userid, status);
			} else {
				JSONObject jo = new JSONObject();
				jo.put("code", -1);
				jo.put("text", Tips.UNKNOWERR.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		returnToClient(result);
		
		return "text";
	}
	
	/**
	 * 获取系统提示音状态
	 */
	public String getSysTipVoice() throws ServletException {
		String result = null;
		
		try {
			if (functionService != null) {
				result = functionService.getSysTipVoice(userid);
			} else {
				JSONObject jo = new JSONObject();
				jo.put("code", -1);
				jo.put("text", Tips.UNKNOWERR.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 设置消息置顶
	 * @return
	 * @throws ServletException
	 */
	public String setMsgTop() throws ServletException {
		String result = functionService.setMsgTop(userid, topid, toptype);
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 获取置顶消息
	 * @return
	 * @throws ServletException
	 */
	public String getMsgTop() throws ServletException {
		String result = functionService.getMsgTop(userid);
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 取消置顶
	 * @return
	 * @throws ServletException
	 */
	public String cancelMsgTop() throws ServletException {
		String result = functionService.cancelMsgTop(userid, topid, toptype);
		returnToClient(result);
		return "text";
	}
	
	
	private String status;
	private String groupid;
	private String userid;
	private String topid;
	private String toptype;
	
	public String getTopid() {
		return topid;
	}

	public void setTopid(String topid) {
		this.topid = topid;
	}

	public String getToptype() {
		return toptype;
	}

	public void setToptype(String toptype) {
		this.toptype = toptype;
	}

	public String getGroupid() {
		return groupid;
	}

	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	private FunctionService functionService;

	public FunctionService getFunctionService() {
		return functionService;
	}

	public void setFunctionService(FunctionService functionService) {
		this.functionService = functionService;
	}
	
}
