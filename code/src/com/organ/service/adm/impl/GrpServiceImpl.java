package com.organ.service.adm.impl;

import java.util.List;

import com.organ.dao.adm.GrpDao;
import com.organ.service.adm.GrpService;

public class GrpServiceImpl implements GrpService {

	private GrpDao grpDao;

	public GrpDao getGrpDao() {
		return grpDao;
	}
	public void setGrpDao(GrpDao grpDao) {
		this.grpDao = grpDao;
	}
	
	@Override
	public Integer getCount() {
		
		return grpDao.count("from TGroup");
	}
	@Override
	public List getList(Integer page, Integer itemsperpage) {
		
		return grpDao.getList(page, itemsperpage);
	}
	@Override
	public void dismiss(Integer id) {
		
		grpDao.delGroupMemberByGroup(id);
		grpDao.deleteById(id);
	}
	@Override
	public Integer getMemberCountByGrp(Integer id) {
		
		return grpDao.getMemberCountByGrp(id);
	}
	@Override
	public List getMemberByGrp(Integer id, Integer page, Integer itemsperpage) {
		
		return grpDao.getMemberByGrp(id, page, itemsperpage);
	}
	@Override
	public void changeCreator(Integer groupId, Integer groupMemberId) {
		
		grpDao.changeCreator(groupId, groupMemberId);
	}
	
}
