package com.organ.dao.adm.impl;

import java.util.List;

import com.organ.common.BaseDao;
import com.organ.dao.adm.OrgDao;
import com.organ.model.TOrgan;

public class OrgDaoImpl extends BaseDao<TOrgan, Integer> implements OrgDao {

	@Override
	public List getProvince() {
		
		String sql = "select id, name from t_province";
		return runSql(sql);
	}

	@Override
	public List getCity(Integer provinceId) {
		
		String sql = "select id, name from t_city where province_id = " + provinceId;
		return runSql(sql);
	}

	@Override
	public List getDistrict(Integer cityId) {
		
		String sql = "select id, name from t_district where city_id = " + cityId;
		return runSql(sql);
	}

	@Override
	public List getInward() {
		
		String sql = "select id, name from t_inward";
		return runSql(sql);
	}

	@Override
	public List getIndustry() {
		
		String sql = "select id, name from t_industry";
		return runSql(sql);
	}

	@Override
	public List getSubdustry(Integer industryId) {
		
		String sql = "select id, name from t_subdustry where industry_id = " + industryId;
		return runSql(sql);
	}

	@Override
	public TOrgan getInfo(Integer orgId) {
		
		return this.get(orgId);
	}

	@Override
	public List getInfos(String soStr) {
		String sql = new StringBuilder("select id, name from t_organ where id in(").append(soStr).append(")").toString();
		return runSql(sql);
	}

}
