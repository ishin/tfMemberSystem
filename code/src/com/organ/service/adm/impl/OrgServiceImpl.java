package com.organ.service.adm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.organ.common.Tips;
import com.organ.dao.adm.BranchDao;
import com.organ.dao.adm.BranchMemberDao;
import com.organ.dao.adm.MemberRoleDao;
import com.organ.dao.adm.OrgDao;
import com.organ.dao.adm.PositionDao;
import com.organ.dao.member.MemberDao;
import com.organ.model.TBranch;
import com.organ.model.TBranchMember;
import com.organ.model.TMember;
import com.organ.model.TOrgan;
import com.organ.model.TPosition;
import com.organ.service.adm.OrgService;
import com.organ.utils.JSONUtils;
import com.organ.utils.PinyinGenerator;
import com.organ.utils.PropertiesUtils;

public class OrgServiceImpl implements OrgService {

	private OrgDao orgDao;
	private MemberDao memberDao;
	private BranchDao branchDao;
	private PositionDao positionDao;
	private BranchMemberDao branchMemberDao;
	private MemberRoleDao memberRoleDao;
	
	public void setMemberRoleDao(MemberRoleDao memberRoleDao) {
		this.memberRoleDao = memberRoleDao;
	}

	public void setBranchMemberDao(BranchMemberDao branchMemberDao) {
		this.branchMemberDao = branchMemberDao;
	}

	public void setPositionDao(PositionDao positionDao) {
		this.positionDao = positionDao;
	}

	public void setBranchDao(BranchDao branchDao) {
		this.branchDao = branchDao;
	}

	public void setMemberDao(MemberDao memberDao) {
		this.memberDao = memberDao;
	}

	public void setOrgDao(OrgDao orgDao) {
		this.orgDao = orgDao;
	}

	@Override
	public List getProvince() {
		return orgDao.getProvince();
	}

	@Override
	public List getCity(Integer provinceId) {
		return orgDao.getCity(provinceId);
	}

	@Override
	public List getDistrict(Integer cityId) {
		return orgDao.getDistrict(cityId);
	}

	@Override
	public List getInward() {
		return orgDao.getInward();
	}

	@Override
	public List getIndustry() {
		return orgDao.getIndustry();
	}

	@Override
	public List getSubdustry(Integer industryId) {
		return orgDao.getSubdustry(industryId);
	}

	@Override
	public TOrgan getInfo(Integer orgId) {
		return orgDao.getInfo(orgId);
	}
	@Override
	public void save(TOrgan organ) {
		orgDao.update(organ);
	}
	@Override
	public String getInfos(String ids) {
		JSONObject jo = new JSONObject();
		
		try {
			List list = orgDao.getInfos(ids);
			
			if (list != null) {
				JSONArray ja = JSONUtils.getInstance().objToJSONArray(list);
				jo.put("code", 1);
				jo.put("text", ja.toString());
			} else {
				jo.put("code", 0);
				jo.put("text", Tips.FAIL.getText());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jo.toString();
	}

	/**
	 * 注：这里都需要加入组织标记
	 */
	@Override
	public String registOrgan(TOrgan organ) {
		String priv = PropertiesUtils.getStringByKey("organ.priv");
		Random r = new Random();
		String random = r.nextInt(999) + "";
		String code = priv + random + PinyinGenerator.getPinYinHeadChar(organ.getName());
		String ret = null;
		
		try {
			organ.setCode(code);
			//注册系统
			orgDao.save(organ);
			int organId = 0;
			organId = organ.getId() != null ? organ.getId() : 0;
			
			//初始化账号
			if (organId != 0) {
				//初始化成员
				TMember tm = new TMember();
				tm.setAccount("admin");
				tm.setFullname("admin");
				tm.setPinyin("ADMIN");
				tm.setWorkno("NO00001");
				tm.setSex("1");
				tm.setBirthday("0");
				tm.setLogo("PersonImg.png");
				tm.setEmail("admin@admin.com");
				tm.setMobile("0");
				tm.setTelephone("0");
				tm.setAddress("0");
				tm.setGroupmax(0);
				tm.setGroupuse(0);
				tm.setIntro("0");
				tm.setOrganId(organId);
				tm.setAllpinyin("admin");
				tm.setPassword("21232f297a57a5a743894a0e4a801fc3");
				tm.setSuperAdmin(1);
				memberDao.save(tm);
				
				int memberId = 0;
				memberId = tm.getId() != null ? tm.getId() : 0;
				
				//初始化职位
				TPosition tp = new TPosition();
				tp.setName("初始职位");
				tp.setOrganId(organId);
				tp.setListorder(0);
				positionDao.save(tp);
				
				int pid = 0;
				
				pid = tp.getId() != null ? tp.getId() : 0;
				
				//初始化部门
				TBranch tb = new TBranch();
				tb.setName("初始部门");
				tb.setOrganId(organId);
				tb.setParentId(0);
				tb.setManagerId(memberId);
				tb.setAddress("0");
				tb.setWebsite("0");
				tb.setTelephone("0");
				tb.setFax("0");
				tb.setIntro("0");
				tb.setListorder(0);
				branchDao.save(tb);
				
				int bid = 0;
				
				bid = tb.getId() != null ? tb.getId() : 0;
				
				//初始化部门成员关系 
				TBranchMember tbm = new TBranchMember();
				tbm.setBranchId(bid);
				tbm.setMemberId(memberId);
				tbm.setPositionId(pid);
				tbm.setIsMaster("1");
				tbm.setListorder(0);
				branchMemberDao.save(tbm);
				
				/*
				//初始化角色
				TMemberRole tmr = new TMemberRole();
				tmr.setMemberId(memberId);
				tmr.setRoleId(1);
				tmr.setListorder(0);
				memberRoleDao.save(tmr);
				*/
				JSONObject j = new JSONObject();
				j.put("account", tm.getAccount());
				j.put("pwd", "admin");
				j.put("organId", organId);
				ret = j.toString();
			}
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "fail";
	}

	@Override
	public String getList() {
		JSONObject jo = new JSONObject();
		
		try {
			List<TOrgan> list = orgDao.getList();
			ArrayList<JSONObject> retList = new ArrayList<JSONObject>();
			
			for(int i = 0; i < list.size(); i++) {
				TOrgan to = list.get(i);
				JSONObject t = new JSONObject();
				t.put("id", to.getId());
				t.put("name", to.getName());
				retList.add(t);
			}
			jo.put("code", 1);
			jo.put("text", retList.toString());
		} catch(Exception e) {
			e.printStackTrace();
		}
		return jo.toString();
	}

}
