package com.sealtalk.action.msg;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sealtalk.common.AuthTips;
import com.sealtalk.common.BaseAction;
import com.sealtalk.model.AppSecret;
import com.sealtalk.service.msg.MessageService;

/**
 * 消息管理
 * 
 * @author hao_dy
 * @since jdk1.7
 * @date 2017/01/12
 */

public class MessageAction extends BaseAction {

	private static final long serialVersionUID = -1948853366651740073L;
	private static final Logger logger = LogManager.getLogger(MessageAction.class);

	/**
	 * 发送系统消息
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String sendSysMsg() throws ServletException {
		String result = null;

		AppSecret as = msgService.validAppIdAndSecret(clearChar(appId), clearChar(secret));
		if (as != null) {
			int organId = as.getOrganId();
			result = msgService.sendSysMsg(clearChar(fromId), clearChar(targetIds), clearChar(targetNames), clearChar(msg),
					clearChar(extraMsg), clearChar(pushContent), clearChar(pushData), clearChar(isPersisted), clearChar(isCounted), organId);
		} else {
			JSONObject jo = new JSONObject();
			jo.put("code", 0);
			jo.put("text", AuthTips.WORNGAPPID.getText());
			result = jo.toString();
		}
		logger.info(result);
		returnToClient(result);
		return "text";
	}

	/**
	 * 发送单人多人会话
	 * 
	 * @return
	 * @throws ServletException
	 */
	public String sendPrivateMsg() throws ServletException {
		String result = null;
		AppSecret as = msgService.validAppIdAndSecret(clearChar(appId), clearChar(secret));
		if (as != null) {
			int organId = as.getOrganId();
			result = msgService.sendPrivateMsg(clearChar(fromId), clearChar(targetIds), clearChar(targetNames),
					clearChar(msg), clearChar(extraMsg), clearChar(pushContent), clearChar(count), clearChar(verifyBlacklist),
					clearChar(isPersisted), clearChar(isCounted), organId);
		} else {
			JSONObject jo = new JSONObject();
			jo.put("code", 0);
			jo.put("text", AuthTips.WORNGAPPID.getText());
			result = jo.toString();
		}
		logger.info(result);
		returnToClient(result);
		return "text";
	}

	private String fromId;
	private String targetIds;
	private String msg;
	private String pushContent;
	private String pushData;
	private String isPersisted;
	private String isCounted;
	private String count;
	private String verifyBlacklist;
	private String appId;
	private String secret;
	private String extraMsg;
	private String targetNames;

	private MessageService msgService;

	public void setTargetNames(String targetNames) {
		this.targetNames = targetNames;
	}

	public void setExtraMsg(String extraMsg) {
		this.extraMsg = extraMsg;
	}

	public String getFromId() {
		return fromId;
	}

	public void setFromId(String fromId) {
		this.fromId = fromId;
	}

	public String getTargetIds() {
		return targetIds;
	}

	public void setTargetIds(String targetIds) {
		this.targetIds = targetIds;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getPushContent() {
		return pushContent;
	}

	public void setPushContent(String pushContent) {
		this.pushContent = pushContent;
	}

	public String getPushData() {
		return pushData;
	}

	public void setPushData(String pushData) {
		this.pushData = pushData;
	}

	public String getIsPersisted() {
		return isPersisted;
	}

	public void setIsPersisted(String isPersisted) {
		this.isPersisted = isPersisted;
	}

	public String getIsCounted() {
		return isCounted;
	}

	public void setIsCounted(String isCounted) {
		this.isCounted = isCounted;
	}

	public MessageService getMsgService() {
		return msgService;
	}

	public void setMsgService(MessageService msgService) {
		this.msgService = msgService;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getVerifyBlacklist() {
		return verifyBlacklist;
	}

	public void setVerifyBlacklist(String verifyBlacklist) {
		this.verifyBlacklist = verifyBlacklist;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

}
