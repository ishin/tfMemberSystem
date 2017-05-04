package com.sealtalk.service.extra.impl;

import net.sf.json.JSONObject;

import com.sealtalk.common.Tips;
import com.sealtalk.dao.friend.FriendDao;
import com.sealtalk.dao.fun.DontDistrubDao;
import com.sealtalk.dao.fun.MsgTopDao;
import com.sealtalk.dao.group.GroupMemberDao;
import com.sealtalk.dao.map.MapDao;
import com.sealtalk.service.extra.ExtraService;

public class ExtraServiceImpl implements ExtraService {

	@Override
	public String delByMemberIds(String ids) {
		
		int ret3 = dontDistrubDao.deleteByIds(ids);
		int ret4 = friendDao.deleteRelationByIds(ids);
		int ret5 = groupMemberDao.deleteRelationByIds(ids);
		int ret6 = mapDao.deleteRelationByIds(ids);
		int ret8 = msgTopDao.deleteRelationByIds(ids);
		
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
	
	
}
