package com.sealtalk.action.fun;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.googlecode.sslplugin.annotation.Secured;
import com.sealtalk.common.BaseAction;
import com.sealtalk.common.Tips;
import com.sealtalk.service.fun.FunctionService;
import com.sealtalk.utils.LogUtils;

/**
 * 辅助功能action 
 * @author hao_dy
 * @since jdk1.7
 * @date 2017/01/07
 */

public class FunctionAction extends BaseAction {

	private static final long serialVersionUID = -7261604465748499252L;
	private final static Logger logger = LogManager.getLogger("FunctionAction.class");
	
	/**
	 * 设置消息免打扰功能
	 * @return
	 * @throws ServletException
	 */
	public String setNotRecieveMsg() throws ServletException {
		String result = null;
		
		try {
			if (functionService != null) {
				result = functionService.setNotRecieveMsg(clearChar(status), clearChar(groupid), clearChar(userid));
			} else {
				JSONObject jo = new JSONObject();
				jo.put("code", -1);
				jo.put("text", Tips.UNKNOWERR.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		
		logger.info(result);
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 获取消息免打扰接口
	 * @return
	 * @throws ServletException
	 */
	public String getNotRecieveMsg() throws ServletException {
		String result = functionService.getNotRecieveMsg(clearChar(groupid), clearChar(userid));
		logger.info(result);
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
		
		try {
			if (functionService != null) {
				result = functionService.setSysTipVoice(clearChar(userid), clearChar(status));
			} else {
				JSONObject jo = new JSONObject();
				jo.put("code", -1);
				jo.put("text", Tips.UNKNOWERR.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		
		logger.info(result);
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
				result = functionService.getSysTipVoice(clearChar(userid));
			} else {
				JSONObject jo = new JSONObject();
				jo.put("code", -1);
				jo.put("text", Tips.UNKNOWERR.getText());
				result = jo.toString();
			}
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		logger.info(result);
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 设置消息置顶
	 * @return
	 * @throws ServletException
	 */
	public String setMsgTop() throws ServletException {
		String result = functionService.setMsgTop(clearChar(userid), clearChar(topid), clearChar(toptype));
		logger.info(result);
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 获取置顶消息
	 * @return
	 * @throws ServletException
	 */
	public String getMsgTop() throws ServletException {
		String result = functionService.getMsgTop(clearChar(userid));
		logger.info(result);
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 取消置顶
	 * @return
	 * @throws ServletException
	 */
	public String cancelMsgTop() throws ServletException {
		String result = functionService.cancelMsgTop(clearChar(userid), clearChar(topid), clearChar(toptype));
		logger.info(result);
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
