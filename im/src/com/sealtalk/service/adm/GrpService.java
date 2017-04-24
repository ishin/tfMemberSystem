package com.sealtalk.service.adm;

import java.util.List;

public interface GrpService {
	
	public Integer getCount();
	public List getList(Integer page, Integer itemsperpage);
	public void dismiss(Integer id);
	public Integer getMemberCountByGrp(Integer id);
	public List getMemberByGrp(Integer id, Integer page, Integer itemsperpage);
	public void changeCreator(Integer groupId, Integer groupMemberId);
}
