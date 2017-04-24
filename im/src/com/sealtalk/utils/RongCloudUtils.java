package com.sealtalk.utils;

import io.rong.RongCloud;
import io.rong.messages.BaseMessage;
import io.rong.messages.InfoNtfMessage;
import io.rong.messages.TxtMessage;
import io.rong.models.CheckOnlineReslut;
import io.rong.models.CodeSuccessReslut;
import io.rong.models.GagGroupUser;
import io.rong.models.GroupInfo;
import io.rong.models.ListGagGroupUserReslut;
import io.rong.models.TokenReslut;

import java.util.List;

import com.sealtalk.common.Tips;

import net.sf.json.JSONObject;

/**
 * 融云sdk工具
 * 
 * @author hao_dy
 * @since jdk1.7
 * @date 2017/01/10
 * 
 */
public class RongCloudUtils {
	// private static final String JSONFILE =
	// RongCloudUtils.class.getClassLoader().getResource("jsonsource").getPath()+"/";
	private static RongCloud rongCloud = null;

	private RongCloudUtils() {
	}

	private static class Inner {
		private static final RongCloudUtils RCU = new RongCloudUtils();
	}

	public static RongCloudUtils getInstance() {
		return Inner.RCU;
	}

	/**
	 * 初始化
	 */
	private void init() {
		String appKey = PropertiesUtils.getStringByKey("db.appKey");
		String appSecret = PropertiesUtils.getStringByKey("db.appSecret");

		rongCloud = RongCloud.getInstance(appKey, appSecret);
	}

	/**
	 * 获取Token结果集
	 * 
	 * @param userId
	 * @param userName
	 * @param url
	 * @return
	 */
	public TokenReslut getTokenResult(String userId, String userName, String url) {
		try {
			if (rongCloud == null) {
				this.init();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		TokenReslut userGetTokenResult = null;

		userName = StringUtils.getInstance().isBlank(userName) ? "" : userName;

		try {
			if (url == null || "".equals(url)) {
				String domain = PropertiesUtils.getDomain();
				String uploadDir = PropertiesUtils.getUploadDir();
				String logo = PropertiesUtils.getDefaultLogo();

				url = domain + uploadDir + logo;
				// url = "http://www.rongcloud.cn/update/images/logo.png";
			}
			userGetTokenResult = rongCloud.user.getToken(userId, userName, url);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return userGetTokenResult;
	}

	/**
	 * 获取token
	 * 
	 * @param userId
	 * @param userName
	 * @param url
	 * @return
	 */
	public String getToken(String userId, String userName, String url) {
		TokenReslut userGetTokenResult = null;
		String token = null;

		try {
			if (rongCloud == null) {
				this.init();
			}
			userGetTokenResult = this.getTokenResult(userId, userName, url);
			// System.out.println("getToken :" +
			// userGetTokenResult.getErrorMessage());
			// System.out.println("getToken :" +
			// userGetTokenResult.getUserId());
			// System.out.println("getToken :" + userGetTokenResult.getCode());
			token = userGetTokenResult.getToken();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return token;
	}

	public int refreshUser(String userId, String userName, String url) {
		CodeSuccessReslut codeSuccessReslut = null;

		try {
			if (rongCloud == null) {
				this.init();
			}
			if (url == null || "".equals(url)) {
				String domain = PropertiesUtils.getDomain();
				String uploadDir = PropertiesUtils.getUploadDir();
				String logo = PropertiesUtils.getDefaultLogo();

				url = domain + uploadDir + logo;
			}
			codeSuccessReslut = rongCloud.user.refresh(userId, userName, url);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return codeSuccessReslut.getCode();
	}

	/**
	 * 检测在线状态(1,在线，0不在线)
	 * 
	 * @param userId
	 * @return
	 */
	public String checkOnLine(String userId) {
		JSONObject jo = new JSONObject();

		try {
			if (rongCloud == null) {
				this.init();
			}
			CheckOnlineReslut checkOnlineReslut = rongCloud.user
					.checkOnline(userId);
			return checkOnlineReslut.getStatus();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return jo.toString();
	}

	/**
	 * 发系统消息
	 * 
	 * @param fromId
	 * @param targetIds
	 * @param msg
	 * @param extraMsg
	 * @param type消息类型
	 * @return
	 */
	public String sendSysMsg(String fromId, String[] targetIds, String msg,
			String extraMsg, String pushContent, String pushData,
			String isPersisted, String isCounted, String type) {

		JSONObject jo = new JSONObject();

		try {
			if (rongCloud == null) {
				this.init();
			}

			JSONObject pushMsg = new JSONObject();
			pushMsg.put("pushData", pushData);

			BaseMessage messagePublishSystemTxtMessage = null;

			int msgType = 1;
			int isCountedInt = 0;
			int isPersistedInt = 0;

			if (pushContent == null) {
				pushContent = "thisisapush";
			}
			msg = msg == null ? "" : msg;
			extraMsg = extraMsg == null ? "" : extraMsg;

			if (!StringUtils.getInstance().isBlank(type)) {
				msgType = StringUtils.getInstance().strToInt(type);
			}
			if (!StringUtils.getInstance().isBlank(isCounted)) {
				isCountedInt = Integer.valueOf(StringUtils.getInstance()
						.strToInt(isCounted));
			}
			if (!StringUtils.getInstance().isBlank(isPersisted)) {
				isPersistedInt = Integer.valueOf(StringUtils.getInstance()
						.strToInt(isPersisted));
			}

			switch (msgType) {
			case 1:
				messagePublishSystemTxtMessage = new TxtMessage(msg, extraMsg);
				break;
			case 2:
				messagePublishSystemTxtMessage = new InfoNtfMessage(msg,
						extraMsg);
				break;
			}

			CodeSuccessReslut messagePublishSystemResult = rongCloud.message
					.PublishSystem(fromId, targetIds,
							messagePublishSystemTxtMessage, pushContent,
							pushMsg.toString(), isPersistedInt, isCountedInt);

			if (messagePublishSystemResult != null) {
				System.out.println("sendSysMsg->code: "
						+ messagePublishSystemResult.toString());
				jo.put("code", messagePublishSystemResult.getCode());
				jo.put("text", Tips.OK.getName());
			} else {
				jo.put("code", 0);
				jo.put("text", Tips.FAIL.getName());
			}
		} catch (Exception e) {
			jo.put("code", 0);
			jo.put("text", "fail");
			e.printStackTrace();
		}

		return jo.toString();
	}

	/**
	 * 发群组消息(创建群)
	 * 
	 * @param fromId
	 * @param targetIds
	 * @param msg
	 * @param extraMsg
	 * @param type
	 * @return
	 */
	public String sendGroupMsg(String fromId, String[] targetIds, String msg,
			String extraMsg, int isPersisted, int isCounted, int type) {
		JSONObject jo = new JSONObject();

		try {
			if (rongCloud == null) {
				this.init();
			}

			JSONObject pushMsg = new JSONObject();
			pushMsg.put("pushData", msg);

			BaseMessage messagePublishSystemTxtMessage = null;

			switch (type) {
			case 1:
				messagePublishSystemTxtMessage = new TxtMessage(msg, extraMsg);
				break;
			case 2:
				messagePublishSystemTxtMessage = new InfoNtfMessage(msg,
						extraMsg);
				break;
			}
			CodeSuccessReslut messagePublishSystemResult = rongCloud.message
					.publishGroup(fromId, targetIds,
							messagePublishSystemTxtMessage, "thisisapush",
							pushMsg.toString(), isPersisted, isCounted);

			if (messagePublishSystemResult != null) {
				System.out.println("sendGroupMsg->code: "
						+ messagePublishSystemResult.toString());
				jo.put("code", messagePublishSystemResult.getCode());
				jo.put("text", "ok");
			} else {
				jo.put("code", 0);
				jo.put("text", "fail");
			}
		} catch (Exception e) {
			jo.put("code", 0);
			jo.put("text", "fail");
			e.printStackTrace();
		}

		return jo.toString();
	}

	/**
	 * 发个人消息(创建群)
	 * 
	 * @param fromId
	 * @param targetIds
	 * @param msg
	 * @param extraMsg
	 * @param type
	 * @return
	 */
	public String sendPrivateMsg(String fromId, String[] targetIds, String msg,
			String extraMsg, String pushContent, String count,
			String verifyBlacklist, String isPersisted, String isCounted,
			String type) {
		JSONObject jo = new JSONObject();

		try {
			if (rongCloud == null) {
				this.init();
			}

			JSONObject pushMsg = new JSONObject();
			pushMsg.put("pushData", msg);

			BaseMessage messagePublishSystemTxtMessage = null;

			int msgType = 1;
			int isCountedInt = 0;
			int isPersistedInt = 0;
			int verifyBlacklistInt = 0;

			if (pushContent == null) {
				pushContent = "thisisapush";
			}

			if (!StringUtils.getInstance().isBlank(type)) {
				msgType = StringUtils.getInstance().strToInt(type);
			}
			if (!StringUtils.getInstance().isBlank(isCounted)) {
				isCountedInt = StringUtils.getInstance().strToInt(isCounted);
			}
			if (!StringUtils.getInstance().isBlank(isPersisted)) {
				isPersistedInt = StringUtils.getInstance()
						.strToInt(isPersisted);
			}
			if (!StringUtils.getInstance().isBlank(isPersisted)) {
				isPersistedInt = StringUtils.getInstance().strToInt(
						verifyBlacklist);
			}

			switch (msgType) {
			case 1:
				messagePublishSystemTxtMessage = new TxtMessage(msg, extraMsg);
				break;
			case 2:
				messagePublishSystemTxtMessage = new InfoNtfMessage(msg,
						extraMsg);
				break;
			}
			CodeSuccessReslut messagePublishSystemResult = rongCloud.message
					.publishPrivate(fromId, targetIds,
							messagePublishSystemTxtMessage, pushContent,
							pushMsg.toString(), count, verifyBlacklistInt,
							isPersistedInt, isCountedInt);

			if (messagePublishSystemResult != null) {
				System.out.println("sendPrivateMsg->code: "
						+ messagePublishSystemResult.toString());
				jo.put("code", messagePublishSystemResult.getCode());
				jo.put("text", "ok");
			} else {
				jo.put("code", 0);
				jo.put("text", "fail");
			}
		} catch (Exception e) {
			jo.put("code", 0);
			jo.put("text", "fail");
			e.printStackTrace();
		}

		return jo.toString();
	}

	/**
	 * 创建群组
	 * 
	 * @param userId
	 *            加入群的用户id组
	 * @param groupId
	 * @param groupName
	 * @return
	 */
	public String createGroup(String[] userIds, String groupId, String groupName) {
		String result = null;

		/*
		 * for(int j = 0; j < userIds.length;j++) {
		 * System.out.println("+++++++++++++++++++++++++++++++: " + userIds[j]);
		 * } System.out.println("++++++++++++++++++ groupId: " + groupId);
		 * System.out.println("++++++++++++++++++ groupName: " + groupName);
		 */

		try {
			if (rongCloud == null) {
				this.init();
			}
			if (!StringUtils.getInstance().isArrayBlank(userIds)
					&& !StringUtils.getInstance().isBlank(groupId)
					&& !StringUtils.getInstance().isBlank(groupName)) {

				CodeSuccessReslut groupCreateResult = rongCloud.group.create(
						userIds, groupId, groupName);

				System.out.println("-----------------------------: "
						+ groupCreateResult.toString());
				if (groupCreateResult != null) {
					result = groupCreateResult.getCode().toString();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 加入群
	 * 
	 * @param userIds
	 * @param groupId
	 * @param groupName
	 * @return
	 */
	public String joinGroup(String[] userIds, String groupId, String groupName) {
		String result = null;

		try {
			if (rongCloud == null) {
				this.init();
			}

			if (!StringUtils.getInstance().isArrayBlank(userIds)
					&& !StringUtils.getInstance().isBlank(groupId)
					&& !StringUtils.getInstance().isBlank(groupName)) {

				CodeSuccessReslut groupJoinResult = rongCloud.group.join(
						userIds, groupId, groupName);

				if (groupJoinResult != null) {
					result = groupJoinResult.getCode().toString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 退出群
	 * 
	 * @param userId
	 * @param groupId
	 * @return
	 */
	public String leftGroup(String[] userIds, String groupId) {
		String result = null;

		try {
			if (rongCloud == null) {
				this.init();
			}

			if (!StringUtils.getInstance().isArrayBlank(userIds)
					&& !StringUtils.getInstance().isBlank(groupId)) {
				CodeSuccessReslut groupQuitResult = rongCloud.group.quit(
						userIds, groupId);

				if (groupQuitResult != null) {
					result = groupQuitResult.getCode().toString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 解散群
	 * 
	 * @param userId
	 * @param groupId
	 * @return
	 */
	public String dissLoveGroup(String userId, String groupId) {
		String result = null;

		try {
			if (rongCloud == null) {
				this.init();
			}

			if (!StringUtils.getInstance().isBlank(userId)
					&& !StringUtils.getInstance().isBlank(groupId)) {
				CodeSuccessReslut groupDismissResult = rongCloud.group.dismiss(
						userId, groupId);

				if (groupDismissResult != null) {
					result = groupDismissResult.getCode().toString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 同步群信息(首次连接融云服务器)
	 * 
	 * @param groupSyncGroupInfo
	 * @param userId
	 * @return
	 */
	public String syncGroup(GroupInfo[] groupSyncGroupInfo, String userId) {
		String result = null;

		try {
			if (rongCloud == null) {
				this.init();
			}

			if (!StringUtils.getInstance().isBlank(userId)) {
				CodeSuccessReslut groupSyncResult = rongCloud.group.sync(
						userId, groupSyncGroupInfo);

				if (groupSyncResult != null) {
					result = groupSyncResult.getCode().toString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 刷新群组信息(名称)
	 * 
	 * @param groupId
	 * @param groupName
	 * @return
	 */
	public String refreshGroup(String groupId, String groupName) {
		String result = null;

		try {
			if (rongCloud == null) {
				this.init();
			}

			if (!StringUtils.getInstance().isBlank(groupId)
					&& !StringUtils.getInstance().isBlank(groupName)) {
				CodeSuccessReslut groupRefreshResult = rongCloud.group.refresh(
						groupId, groupName);

				if (groupRefreshResult != null) {
					result = groupRefreshResult.getCode().toString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 禁言群成员
	 * 
	 * @param userId
	 * @param groupId
	 * @param shutUpTime
	 * @return
	 */
	public String shutUpGroup(String userId, String groupId, String shutUpTime) {
		String result = null;

		try {
			if (rongCloud == null) {
				this.init();
			}

			if (!StringUtils.getInstance().isBlank(userId)
					&& !StringUtils.getInstance().isBlank(groupId)
					&& !StringUtils.getInstance().isBlank(shutUpTime)) {
				CodeSuccessReslut groupAddGagUserResult = rongCloud.group
						.addGagUser(userId, groupId, shutUpTime);

				if (groupAddGagUserResult != null) {
					result = groupAddGagUserResult.getCode().toString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 群成员解禁
	 * 
	 * @param userIds
	 * @param groupId
	 * @return
	 */
	public String unShutUpGroup(String[] userIds, String groupId) {
		String result = null;

		try {
			if (rongCloud == null) {
				this.init();
			}

			if (!StringUtils.getInstance().isBlank(groupId)) {
				CodeSuccessReslut groupAddGagUserResult = rongCloud.group
						.rollBackGagUser(userIds, groupId);

				if (groupAddGagUserResult != null) {
					result = groupAddGagUserResult.getCode().toString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 查询禁言群组状态
	 * 
	 * @param groupId
	 * @return
	 */
	public List<GagGroupUser> getShutUpGroupMember(String groupId) {
		List<GagGroupUser> result = null;

		try {
			if (rongCloud == null) {
				this.init();
			}

			if (!StringUtils.getInstance().isBlank(groupId)) {
				ListGagGroupUserReslut groupLisGagUserResult = rongCloud.group
						.lisGagUser(groupId);

				if (groupLisGagUserResult != null) {
					result = groupLisGagUserResult.getUsers();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

}
