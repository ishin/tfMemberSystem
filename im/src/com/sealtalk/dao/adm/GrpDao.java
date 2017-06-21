package com.sealtalk.dao.adm;

import java.util.List;

import com.sealtalk.common.IBaseDao;
import com.sealtalk.model.TGroup;

public interface GrpDao extends IBaseDao<TGroup, Integer> {

	public void delGroupMemberByGroup(Integer id);
	public Integer getMemberCountByGrp(Integer id);
	public void changeCreator(Integer groupId, Integer groupMemberId, Integer cid);
	public List getLimitList(Integer page, Integer itemsperpage);
	public List getLimitListById(Integer id, Integer page, Integer itemsperpage);
}
