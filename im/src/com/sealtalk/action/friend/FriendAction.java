package com.sealtalk.action.friend;

import javax.servlet.ServletException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.sf.json.JSONObject;

import com.googlecode.sslplugin.annotation.Secured;
import com.sealtalk.common.BaseAction;
import com.sealtalk.common.Tips;
import com.sealtalk.model.SessionUser;
import com.sealtalk.service.friend.FriendService;
import com.sealtalk.utils.LogUtils;
import com.sealtalk.utils.PasswordGenerator;
import com.sealtalk.utils.StringUtils;
import com.sealtalk.utils.TimeGenerator;

/**
 * 联系人管理 
 * @author hao_dy
 * @since jdk1.7
 * @date 2017/01/07
 */

public class FriendAction extends BaseAction {

	private static final long serialVersionUID = -7261604465748499252L;
	private final static Logger logger = LogManager.getLogger("FriendAction.class");
	
	/**
	 * 添加联系人关系
	 * @return
	 * @throws ServletException
	 */
	public String addFriend() throws ServletException {
		JSONObject jo = new JSONObject();
		String result = null;
		
		int organId = getSessionUserOrganId();
		
		try{
			if (StringUtils.getInstance().isBlank(account)) {
				jo.put("code", -1);
				jo.put("text", Tips.NULLUSER.getText());
				result = jo.toString();
			} else if (StringUtils.getInstance().isBlank(friend)) {
				jo.put("code", 0);
				jo.put("text", Tips.NOTFRIENDID.getText());
				result = jo.toString();
			} else {
				result = friendService.addFriend(clearChar(account), clearChar(friend), organId);
			}
			
			logger.info(result);
			
			returnToClient(result);
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		
		return "text";
	}
	
	/**
	 * 扫一扫加好友
	 * @return
	 */
	public String scanAddFriend() throws ServletException {
		
		JSONObject jo = new JSONObject();
		String result = null;
		SessionUser su = getSessionUser();
		
		try{
			String ts = clearChar(this.request.getParameter("timestamp"));
			long timeStamp = StringUtils.getInstance().isBlank(ts) ? 0 : Long.parseLong(ts);
			long now = TimeGenerator.getInstance().getUnixTime();
			long max = timeStamp + 120;
			long min = timeStamp - 120;
			
			if (now < min || now > max) {
				jo.put("code", 0);
				jo.put("text", Tips.TIMEOUT.getText());
				result = jo.toString();
			} else {
				if (StringUtils.getInstance().isBlank(friend)) {
					jo.put("code", 0);
					jo.put("text", Tips.NOTFRIENDID.getText());
					result = jo.toString();
				}			
				JSONObject p = new JSONObject();
				p.put("friend", friend);
				String sign = (this.request.getParameter("sign"));
				String key = "@q3$fd12%";
				String caclSign = PasswordGenerator.getInstance().makeSign(p, key, timeStamp);
				
				if (!sign.equals(caclSign)) {
					jo.put("code", 0);
					jo.put("text", Tips.FAIL.getText());
					result = jo.toString();
				} else {
					boolean s = true;
					
					if (su != null ) {
						String account = su.getAccount();
						int organId = su.getOrganId();
						
						if (account == null || "".equals(account)) {
							s = false;
						}  else {
							result = friendService.addFriend(account, clearChar(friend), organId);
						}
					} else {
						s = false;
					}
					if (!s) {
						jo.put("code", 0);
						jo.put("text", Tips.NOTINIT.getText());
						result = jo.toString();
					}
				}
			}
			logger.info(result);
			returnToClient(result);
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		
		return "text";
	}
	
	/**
	 * 删除联系人关系
	 * @return
	 * @throws ServletException
	 */
	public String delFriend() throws ServletException {
		JSONObject jo = new JSONObject();
		String result = null;
		try {
			if (StringUtils.getInstance().isBlank(account)) {
				jo.put("code", -1);
				jo.put("text", Tips.UNKNOWERR.getName());
				result = jo.toString();
			} else if (StringUtils.getInstance().isBlank(friend)) {
				jo.put("code", 0);
				jo.put("text", Tips.NOTFRIENDID.getName());
				result = jo.toString();
			} else {
				int organId = getSessionUserOrganId();
				result = friendService.delFriend(clearChar(account), clearChar(friend), organId);
			}
			logger.info(result);
			returnToClient(result);
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		return "text";
	}
	
	/**
	 * 拉取联系人列表
	 * @return
	 * @throws ServletException
	 */
	public String getMemberFriends() throws ServletException {
		JSONObject jo = new JSONObject();
		String result = null;
		
		try {
			if (StringUtils.getInstance().isBlank(account)) {
				jo.put("code", -1);
				jo.put("text", Tips.NULLUSER.getName());
			} else {
				int organId = getSessionUserOrganId();
				result = friendService.getMemberFriends(clearChar(account), organId);
			}
			
			logger.info(result);
			returnToClient(result);
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		
		return "text";
	}
	
	/**
	 * 确认是否存在好友关系
	 * @return
	 * @throws ServletException
	 */
	public String getFriendsRelation() throws ServletException {
		String result = friendService.getFriendsRelation(clearChar(userid), clearChar(friendid));
		logger.info(result);
		returnToClient(result);
		return "text";
	}
	
	private FriendService friendService;
	
	public void setFriendService(FriendService fs) {
		this.friendService = fs;
	}
	
	private String account;
	private String friend;
	private String userid;
	private String friendid;
	
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getFriendid() {
		return friendid;
	}

	public void setFriendid(String friendid) {
		this.friendid = friendid;
	}

	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getFriend() {
		return friend;
	}
	public void setFriend(String friend) {
		this.friend = friend;
	}
	
}
