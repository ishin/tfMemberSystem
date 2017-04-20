package com.organ.service.adm;

import java.util.List;

import com.organ.model.TOrgan;

public interface OrgService {

	public List getProvince(); 
	public List getCity(Integer provinceId); 
	public List getDistrict(Integer cityId);
	public List getInward();
	public List getIndustry();
	public List getSubdustry(Integer industryId);
	public TOrgan getInfo(Integer orgId);
	public void save(TOrgan organ);
	public String getInfos(String ids);
	public String registOrgan(TOrgan organ);
	/**
	 * 获取组织列表
	 * @return
	 */
	public String getList();
	
	public TOrgan getOrganByCode(String organCode);
}
