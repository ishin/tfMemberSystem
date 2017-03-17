package com.organ.service.adm.impl;

import java.util.List;

import com.organ.dao.adm.OrgDao;
import com.organ.model.TOrgan;
import com.organ.service.adm.OrgService;

public class OrgServiceImpl implements OrgService {

	private OrgDao orgDao;
	
	public OrgDao getOrgDao() {
		return orgDao;
	}
	public void setOrgDao(OrgDao orgDao) {
		this.orgDao = orgDao;
	}

	@Override
	public List getProvince() {
		// TODO Auto-generated method stub
		return orgDao.getProvince();
	}

	@Override
	public List getCity(Integer provinceId) {
		// TODO Auto-generated method stub
		return orgDao.getCity(provinceId);
	}

	@Override
	public List getDistrict(Integer cityId) {
		// TODO Auto-generated method stub
		return orgDao.getDistrict(cityId);
	}

	@Override
	public List getInward() {
		// TODO Auto-generated method stub
		return orgDao.getInward();
	}

	@Override
	public List getIndustry() {
		// TODO Auto-generated method stub
		return orgDao.getIndustry();
	}

	@Override
	public List getSubdustry(Integer industryId) {
		// TODO Auto-generated method stub
		return orgDao.getSubdustry(industryId);
	}

	@Override
	public TOrgan getInfo(Integer orgId) {
		// TODO Auto-generated method stub
		return orgDao.getInfo(orgId);
	}
	@Override
	public void save(TOrgan organ) {
		// TODO Auto-generated method stub
		orgDao.update(organ);
	}

}
