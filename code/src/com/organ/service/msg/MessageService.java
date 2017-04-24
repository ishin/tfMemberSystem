package com.organ.service.msg;

import com.organ.model.AppSecret;

/**
 * 融云消息发送
 * 
 * @author hao_dy
 * @date 2017/01/11
 * @since jdk1.7
 */
public interface MessageService {

	/**
	 * 发送系统消息
	 * 
	 * @param fromid
	 * @param targetids
	 * @param msg
	 * @param extraMsg
	 * @param isCounted
	 * @param isPersisted
	 * @param pushData
	 * @param organId 
	 * @return
	 */
	public String sendSysMsg(String fromid, String targetids,
			String targetNames, String msg, String extraMsg,
			String pushContent, String pushData, String isPersisted,
			String isCounted, int organId);

	/**
	 * 发送个人、多个会话
	 * 
	 * @param fromId
	 * @param targetIds
	 * @param msg
	 * @param pushContent
	 * @param count
	 * @param verifyBlacklist
	 * @param isPersisted
	 * @param isCounted
	 * @param organId 
	 * @return
	 */
	public String sendPrivateMsg(String fromId, String targetIds,
			String targetNames, String msg, String extraMsg,
			String pushContent, String count, String verifyBlacklist,
			String isPersisted, String isCounted, int organId);

	/**
	 * 验证是否有appId及secret
	 * 
	 * @param appId
	 * @param secret
	 * @return
	 */
	public AppSecret validAppIdAndSecret(String appId, String secret);

}
