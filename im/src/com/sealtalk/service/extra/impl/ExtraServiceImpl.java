package com.sealtalk.service.extra.impl;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import com.sealtalk.common.FunctionName;
import com.sealtalk.common.Tips;
import com.sealtalk.dao.friend.FriendDao;
import com.sealtalk.dao.fun.DontDistrubDao;
import com.sealtalk.dao.fun.FunctionDao;
import com.sealtalk.dao.fun.MsgTopDao;
import com.sealtalk.dao.group.GroupDao;
import com.sealtalk.dao.group.GroupMemberDao;
import com.sealtalk.dao.map.MapDao;
import com.sealtalk.model.TGroupMember;
import com.sealtalk.service.extra.ExtraService;

public class ExtraServiceImpl implements ExtraService {

	@Override
	public String delByMemberIds(String ids, String isLogic) {
		
		int ret1 = dontDistrubDao.deleteByIds(ids, isLogic);
		int ret2 = friendDao.deleteRelationByIds(ids, isLogic);
		//根据成员id查询群组
		String[] idArr = ids.split(",");
		Integer[] idsArrInt = new Integer[idArr.length];
		
		for(int i = 0; i < idArr.length; i++) {
			idsArrInt[i] = Integer.parseInt(idArr[i]);
		}
		List<TGroupMember> groupList = groupMemberDao.getGroupMemberByMembIds(idsArrInt);
		ArrayList<Integer> groupIdList = new ArrayList<Integer>();
		
		if (groupList != null && groupList.size() > 0) {
			for(int i = 0; i < groupList.size(); i++) {
				int id = groupList.get(i).getGroupId();
				if (!groupIdList.contains(id)) {
					groupIdList.add(id);
				}
			}
		}
		Integer[] groupIds = new Integer[groupIdList.size()];
		groupIdList.toArray(groupIds);
		int ret3 = groupMemberDao.deleteRelationByIds(ids, isLogic);
		
		if (ret3 > 0) {
			//重新设置群创建者
			List<TGroupMember> groupMembers = groupMemberDao.getGroupMemberByGroupIds(groupIds); 
			ArrayList<Integer> list = new ArrayList<Integer>();
			
			if (groupMembers != null && groupMembers.size() > 0) {
				for(int i = 0;i < groupMembers.size(); i++) {
					TGroupMember tgm = groupMembers.get(i);
					int groupId = tgm.getGroupId();
					if (!list.contains(groupId)) {
						list.add(groupId);
						tgm.setIsCreator("1");
						groupMemberDao.updateGroupMember(tgm);
						groupDao.updateCreateIdAndVolume(groupId, tgm.getMemberId());
					}
				}
			}
			
		}
		int ret4 = mapDao.deleteRelationByIds(ids, isLogic);
		int ret5 = msgTopDao.deleteRelationByIds(ids, isLogic);
		
		StringBuilder sb = new StringBuilder();
		int len = idArr.length;
		for(int i = 0; i < len; i++) {
			sb.append("'").append(idArr[i] + "_" + FunctionName.SYSTIPVOICE.getName()).append("'");
			if (i < len - 1) {
				sb.append(",");
			}
		}
		int ret6 = functionDao.deleteRelationByIds(sb.toString(), isLogic);
		
		JSONObject jo = new JSONObject();
		jo.put("code", 1);
		jo.put("text", Tips.OK.getText());
	
		return jo.toString(); 
		
	}
	
	private DontDistrubDao dontDistrubDao;
	private FriendDao friendDao;
	private GroupMemberDao groupMemberDao;
	private MapDao mapDao;
	private MsgTopDao msgTopDao;
	private GroupDao groupDao;
	private FunctionDao functionDao;
	
	public void setDontDistrubDao(DontDistrubDao dontDistrubDao) {
		this.dontDistrubDao = dontDistrubDao;
	}
	public void setFriendDao(FriendDao friendDao) {
		this.friendDao = friendDao;
	}
	public void setGroupMemberDao(GroupMemberDao groupMemberDao) {
		this.groupMemberDao = groupMemberDao;
	}
	public void setMapDao(MapDao mapDao) {
		this.mapDao = mapDao;
	}
	public void setMsgTopDao(MsgTopDao msgTopDao) {
		this.msgTopDao = msgTopDao;
	}
	public void setGroupDao(GroupDao groupDao) {
		this.groupDao = groupDao;
	}
	public void setFunctionDao(FunctionDao functionDao) {
		this.functionDao = functionDao;
	}
	
}
