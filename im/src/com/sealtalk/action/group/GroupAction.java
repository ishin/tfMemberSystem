package com.sealtalk.action.group;

import javax.servlet.ServletException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.sf.json.JSONObject;

import com.googlecode.sslplugin.annotation.Secured;
import com.sealtalk.common.BaseAction;
import com.sealtalk.common.Tips;
import com.sealtalk.service.group.GroupService;

/**
 * 成员action
 * @author hao_dy
 * @since jdk1.7
 * @date 2017/01/07
 */

public class GroupAction extends BaseAction {

	private static final long serialVersionUID = 5512359170256277370L;
	private final static Logger logger = LogManager.getLogger("GroupAction.class");
	
	/**
	 * 创建群组
	 * @return
	 * @throws ServletException
	 */
	public String createGroup() throws ServletException {
		String result = null;
	
		if (groupService != null) {
			result = groupService.createGroup(clearChar(userid), clearChar(groupids), clearChar(groupname));
		} else {
			JSONObject jo = new JSONObject();
			jo.put("code", -1);
			jo.put("text", Tips.UNKNOWERR.getText());
			result = jo.toString();
		}
		
		logger.info(result);
		returnToClient(result);
		
		return "text";
	}
	
	/**
	 * 加入群组
	 * @return
	 * @throws ServletException
	 */
	public String joinGroup() throws ServletException {
		String result = null;

		if (groupService != null) {
			result = groupService.joinGroup(clearChar(groupids), clearChar(groupid));
		} else {
			JSONObject jo = new JSONObject();
			jo.put("code", -1);
			jo.put("text", Tips.UNKNOWERR.getText());
			result = jo.toString();
		}
		
		logger.info(result);
		returnToClient(result);
		
		return "text";
	}
	
	/**
	 * 离开群组
	 * @return
	 * @throws ServletException
	 */
	public String leftGroup() throws ServletException {
		String result = null;
		
		if (groupService != null) {
			result = groupService.leftGroup(clearChar(groupids), clearChar(groupid));
		} else {
			JSONObject jo = new JSONObject();
			jo.put("code", -1);
			jo.put("text", Tips.UNKNOWERR.getText());
			result = jo.toString();
		}
		logger.info(result);
		returnToClient(result);
		
		return "text";
	}
	
	/**
	 * 解散群组
	 * @return
	 * @throws ServletException
	 */
	public String disslovedGroup() throws ServletException {
		String result = null;
		
		if (groupService != null) {
			result = groupService.dissLovedGroup(clearChar(userid), clearChar(groupid));
		} else {
			JSONObject jo = new JSONObject();
			jo.put("code", -1);
			jo.put("text", Tips.UNKNOWERR.getText());
			result = jo.toString();
		}
		logger.info(result);
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 刷新群组信息(名称)
	 * @return
	 * @throws ServletException
	 */
	public String refreshGroup() throws ServletException {
		String result = null;
		
		if (groupService != null) {
			result = groupService.refreshGroup(clearChar(groupid), clearChar(groupname));
		} else {
			JSONObject jo = new JSONObject();
			jo.put("code", -1);
			jo.put("text", Tips.UNKNOWERR.getText());
			result = jo.toString();
		}
		
		logger.info(result);
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 查询群成员
	 * @return
	 * @throws SevletException
	 */
	public String listGroupMemebers () throws ServletException {
		String result = null;
		
		if (groupService != null) {
			result = groupService.listGroupMembers(clearChar(groupid));
		} else {
			JSONObject jo = new JSONObject();
			jo.put("code", -1);
			jo.put("text", Tips.UNKNOWERR.getText());
			result = jo.toString();
		}
		
		logger.info(result);
		returnToClient(result);
		return "text";
	} 
	
	/**
	 * 查询群成员
	 * @return
	 * @throws SevletException
	 */
	public String listGroupMemebersData () throws ServletException {
		String result = null;
		
		if (groupService != null) {
			int organId = getSessionUserOrganId();
			result = groupService.listGroupMemebersData(clearChar(groupid), organId);
		} else {
			JSONObject jo = new JSONObject();
			jo.put("code", -1);
			jo.put("text", Tips.UNKNOWERR.getText());
			result = jo.toString();
		}
		logger.info(result);
		returnToClient(result);
		return "text";
	} 
	
	/**
	 * 群主转移
	 * @return
	 * @throws ServletException
	 */
	public String transferGroup() throws ServletException {
		String result = null;
		
		if (groupService != null) {
			result = groupService.transferGroup(clearChar(userid), clearChar(groupid));
		} else {
			JSONObject jo = new JSONObject();
			jo.put("code", -1);
			jo.put("text", Tips.UNKNOWERR.getText());
			result = jo.toString();
		}
		logger.info(result);
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 同步用户所属群组(第一次连接融云服务器时)
	 * @return
	 * @throws ServletException
	 */
	public String syncUserGroup() throws ServletException {
		String result = null;
		
		if (groupService != null) {
			result = groupService.syncUserGroup(clearChar(userid));
		} else {
			JSONObject jo = new JSONObject();
			jo.put("code", -1);
			jo.put("text", Tips.UNKNOWERR.getText());
			result = jo.toString();
		}
		logger.info(result);
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 返回群组列表
	 * @return
	 * @throws ServletException
	 */
	public String groupList() throws ServletException {
		
		String result = null;
		result = groupService.getGroupList(clearChar(userid));
		logger.info(result);
		returnToClient(result);
		return "text";
	}
	
	
	/**
	 * 返回群组列表，分我建的和我加入的
	 * @return
	 * @throws ServletException
	 */
	public String groupListWithAction() throws ServletException {
		String result = null;
		
		if (groupService != null) {
			result = groupService.groupListWithAction(clearChar(userid));
		} else {
			JSONObject jo = new JSONObject();
			jo.put("code", -1);
			jo.put("text", Tips.UNKNOWERR.getText());
			result = jo.toString();
		}
		
		logger.info(result);
		returnToClient(result);
		
		return "text";	
	}
	
	/**
	 * 管理群成员
	 * @return
	 * @throws ServletException
	 */
	public String manageGroupMem() throws ServletException {
		
		String result = null;
		
		if (groupService != null) {
			String userId = String.valueOf(getSessionUser().getId());
			result = groupService.manageGroupMem(clearChar(groupid), userId, clearChar(groupids));
		} else {
			JSONObject jo = new JSONObject();
			jo.put("code", -1);
			jo.put("text", Tips.UNKNOWERR.getText());
			result = jo.toString();
		}
		
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 查看群信息
	 * @return
	 * @throws ServletException
	 */
	public String groupInfo() throws ServletException {
		String result = null;
		
		if (groupService != null) {
			result = groupService.groupInfo(clearChar(groupid));
		} else {
			JSONObject jo = new JSONObject();
			jo.put("code", -1);
			jo.put("text", Tips.UNKNOWERR.getText());
			result = jo.toString();
		}
		
		logger.info(result);
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 修改群名称
	 * @return
	 * @throws ServletException
	 */
	public String changeGroupName() throws ServletException {
		String result = null;
		
		if (groupService != null) {
			result = groupService.changeGroupName(clearChar(groupid), clearChar(groupname));
		} else {
			JSONObject jo = new JSONObject();
			jo.put("code", -1);
			jo.put("text", Tips.UNKNOWERR.getText());
			result = jo.toString();
		}
		logger.info(result);
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 群组禁言
	 * @throws ServletException
	 */
	public String shutUpGroup() throws ServletException {
		String result = groupService.shutUpGroup(clearChar(userid), clearChar(groupid));
		logger.info(result);
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 群组解禁言
	 * @return
	 * @throws ServletException
	 */
	public String unShutUpGroup() throws ServletException {
		String result = groupService.unShutUpGroup(clearChar(userid), clearChar(groupid));
		logger.info(result);
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 查询群禁言状态
	 * @return
	 * @throws ServletException
	 */
	public String getShutUpGroupStatus() throws ServletException {
		String result = groupService.getShutUpGroupStatus(clearChar(groupid));
		logger.info(result);
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 获取群禁言成员
	 * @return
	 * @throws ServletException
	 */
	public String getShutUpGroupMember() throws ServletException {
		String result = groupService.getShutUpGroupMember(clearChar(groupid));
		logger.info(result);
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 获取群在线人数
	 * @return
	 * @throws ServletException
	 */
	public String getGroupOnLineMember() throws ServletException {
		String result = groupService.getGroupOnLineMember(clearChar(userid));
		logger.info(result);
		returnToClient(result);
		return "text";
	}
	
	private GroupService groupService;
	
	public GroupService getGroupService() {
		return groupService;
	}

	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}

	private String userid;			//自己
	private String groupids;		//群成员
	private String groupid;			//群id
	private String groupname;		//群名称
	
	public String getGroupid() {
		return groupid;
	}

	public String getGroupname() {
		return groupname;
	}

	public void setGroupname(String groupname) {
		this.groupname = groupname;
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

	public String getGroupids() {
		return groupids;
	}

	public void setGroupids(String groupids) {
		this.groupids = groupids;
	}

}
