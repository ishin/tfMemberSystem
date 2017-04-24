package com.sealtalk.action.group;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

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
@Secured
public class GroupAction extends BaseAction {

	private static final long serialVersionUID = 5512359170256277370L;
	private static final Logger logger = Logger.getLogger(GroupAction.class);
	
	/**
	 * 创建群组
	 * @return
	 * @throws ServletException
	 */
	public String createGroup() throws ServletException {
		String result = null;
		
	
		if (groupService != null) {
			result = groupService.createGroup(userid, groupids);
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
	 * 加入群组
	 * @return
	 * @throws ServletException
	 */
	public String joinGroup() throws ServletException {
		String result = null;

		if (groupService != null) {
			result = groupService.joinGroup(groupids, groupid);
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
	 * 离开群组
	 * @return
	 * @throws ServletException
	 */
	public String leftGroup() throws ServletException {
		String result = null;
		
		if (groupService != null) {
			result = groupService.leftGroup(groupids, groupid);
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
	 * 解散群组
	 * @return
	 * @throws ServletException
	 */
	public String disslovedGroup() throws ServletException {
		String result = null;
		
		if (groupService != null) {
			result = groupService.dissLovedGroup(userid, groupid);
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
	 * 刷新群组信息(名称)
	 * @return
	 * @throws ServletException
	 */
	public String refreshGroup() throws ServletException {
		String result = null;
		
		if (groupService != null) {
			result = groupService.refreshGroup(groupid, groupname);
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
	 * 查询群成员
	 * @return
	 * @throws SevletException
	 */
	public String listGroupMemebers () throws ServletException {
		String result = null;
		
		if (groupService != null) {
			result = groupService.listGroupMembers(groupid);
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
	 * 查询群成员
	 * @return
	 * @throws SevletException
	 */
	public String listGroupMemebersData () throws ServletException {
		String result = null;
		
		if (groupService != null) {
			int organId = getSessionUserOrganId();
			result = groupService.listGroupMemebersData(groupid, organId);
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
	 * 群主转移
	 * @return
	 * @throws ServletException
	 */
	public String transferGroup() throws ServletException {
		String result = null;
		
		if (groupService != null) {
			result = groupService.transferGroup(userid, groupid);
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
	 * 同步用户所属群组(第一次连接融云服务器时)
	 * @return
	 * @throws ServletException
	 */
	public String syncUserGroup() throws ServletException {
		String result = null;
		
		if (groupService != null) {
			result = groupService.syncUserGroup(userid);
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
	 * 返回群组列表
	 * @return
	 * @throws ServletException
	 */
	public String groupList() throws ServletException {
		
		String result = null;
		result = groupService.getGroupList(userid);
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
			result = groupService.groupListWithAction(userid);
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
	 * 管理群成员
	 * @return
	 * @throws ServletException
	 */
	public String manageGroupMem() throws ServletException {
		
		String result = null;
		
		if (groupService != null) {
			result = groupService.manageGroupMem(groupid, groupids);
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
			result = groupService.groupInfo(groupid);
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
	 * 修改群名称
	 * @return
	 * @throws ServletException
	 */
	public String changeGroupName() throws ServletException {
		String result = null;
		
		if (groupService != null) {
			result = groupService.changeGroupName(groupid, groupname);
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
	 * 群组禁言
	 * @throws ServletException
	 */
	public String shutUpGroup() throws ServletException {
		String result = groupService.shutUpGroup(userid, groupid);
		
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 群组解禁言
	 * @return
	 * @throws ServletException
	 */
	public String unShutUpGroup() throws ServletException {
		String result = groupService.unShutUpGroup(userid, groupid);
		
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 查询群禁言状态
	 * @return
	 * @throws ServletException
	 */
	public String getShutUpGroupStatus() throws ServletException {
		String result = groupService.getShutUpGroupStatus(groupid);
		
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 获取群禁言成员
	 * @return
	 * @throws ServletException
	 */
	public String getShutUpGroupMember() throws ServletException {
		String result = groupService.getShutUpGroupMember(groupid);
		
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 获取群在线人数
	 * @return
	 * @throws ServletException
	 */
	public String getGroupOnLineMember() throws ServletException {
		String result = groupService.getGroupOnLineMember(userid);
		
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
