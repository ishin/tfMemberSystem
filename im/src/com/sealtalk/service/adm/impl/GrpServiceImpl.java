package com.sealtalk.service.adm.impl;

import java.util.List;

import net.sf.json.JSONObject;

import com.sealtalk.common.SysInterface;
import com.sealtalk.dao.adm.GrpDao;
import com.sealtalk.dao.group.GroupMemberDao;
import com.sealtalk.model.TGroupMember;
import com.sealtalk.model.TMember;
import com.sealtalk.service.adm.GrpService;
import com.sealtalk.utils.HttpRequest;
import com.sealtalk.utils.JSONUtils;
import com.sealtalk.utils.RongCloudUtils;

public class GrpServiceImpl implements GrpService {

	private GrpDao grpDao;
	private GroupMemberDao groupMemberDao;
	
	public void setGrpDao(GrpDao grpDao) {
		this.grpDao = grpDao;
	}
	public void setGroupMemberDao(GroupMemberDao groupMemberDao) {
		this.groupMemberDao = groupMemberDao;
	}


	@Override
	public Integer getCount() {
		
		return grpDao.count("from TGroup");
	}
	@Override
	public List getList(Integer page, Integer itemsperpage) {
		List groupList = grpDao.getLimitList(page, itemsperpage);
		return groupList;
	}
	@Override
	public void dismiss(int userId, Integer id) {
		
		grpDao.delGroupMemberByGroup(id);
		grpDao.deleteById(id);
		
		//解散群组发送小灰条
		String fromId = "FromId";
		String msg = "群组已解散";
		String extrMsg = msg;
		String[] groupIds = {id+""};
		String code = RongCloudUtils.getInstance().sendGroupMsg(fromId, groupIds, msg, extrMsg, 1, 1, 2);
		if (code.equals("200")) {
			RongCloudUtils.getInstance().dissLoveGroup(String.valueOf(userId), String.valueOf(id));
		}
		
	}
	@Override
	public Integer getMemberCountByGrp(Integer id) {
		
		return grpDao.getMemberCountByGrp(id);
	}
	@Override
	public List getMemberByGrp(Integer id, Integer page, Integer itemsperpage) {
		return grpDao.getLimitListById(id, page, itemsperpage);
		//return grpDao.getMemberByGrp(id, page, itemsperpage);
	}
	@Override
	public void changeCreator(Integer groupId, Integer groupMemberId) {
		TGroupMember tg = groupMemberDao.getGroupMemberById(groupMemberId);
		int cid = 0;
		if (tg != null) {
			cid = tg.getMemberId();
		}
		grpDao.changeCreator(groupId, groupMemberId, cid);
		String[] groupIds = { groupId+"" };
		JSONObject p = new JSONObject();
		p.put("userId", cid);
		String memberStr = HttpRequest.getInstance().sendPost(SysInterface.MEMBERFORID.getName(), p);
		JSONObject ret = JSONUtils.getInstance().stringToObj(memberStr);
		TMember tm = null;

		if (ret.getInt("code") == 1) {
			tm = JSONUtils.getInstance().jsonObjToBean(ret.getJSONObject("text"), TMember.class);
		}
		String name = tm != null ? "为" + tm.getFullname() : "";
		String msg = "管理员已变更" + name;
		String extrMsg = msg;
		String fromId = "FromId";
		RongCloudUtils.getInstance().sendGroupMsg(fromId, groupIds, msg, extrMsg, 1, 1, 2);
	}
	
}
