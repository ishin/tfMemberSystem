package com.organ.service.adm.impl;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.organ.common.Tips;
import com.organ.dao.adm.BranchDao;
import com.organ.dao.adm.BranchMemberDao;
import com.organ.dao.adm.MemberRoleDao;
import com.organ.dao.adm.OrgDao;
import com.organ.dao.adm.PositionDao;
import com.organ.dao.adm.RoleDao;
import com.organ.dao.member.MemberDao;
import com.organ.model.TBranch;
import com.organ.model.TBranchMember;
import com.organ.model.TMember;
import com.organ.model.TMemberRole;
import com.organ.model.TOrgan;
import com.organ.model.TPosition;
import com.organ.model.TRole;
import com.organ.service.adm.OrgService;
import com.organ.utils.JSONUtils;
import com.organ.utils.LogUtils;
import com.organ.utils.PasswordGenerator;
import com.organ.utils.PinyinGenerator;
import com.organ.utils.PropertiesUtils;
import com.organ.utils.StringUtils;

public class OrgServiceImpl implements OrgService {

	private Logger logger = LogManager.getLogger(OrgServiceImpl.class);
	private OrgDao orgDao;
	private MemberDao memberDao;
	private BranchDao branchDao;
	private PositionDao positionDao;
	private BranchMemberDao branchMemberDao;
	private RoleDao roleDao;
	private MemberRoleDao memberRoleDao;

	public void setMemberRoleDao(MemberRoleDao memberRoleDao) {
		this.memberRoleDao = memberRoleDao;
	}

	public void setRoleDao(RoleDao roleDao) {
		this.roleDao = roleDao;
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
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
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
		String code = priv + PinyinGenerator.getPinYinHeadChar(organ.getName());
		int maxNumber =orgDao.getMaxNumber();
		maxNumber++;
		code += StringUtils.getInstance().addZero(maxNumber, 5);
		String ret = null;
		JSONObject j = new JSONObject();
		
		try {
			organ.setListorder(maxNumber);
			organ.setCode(code);
			//注册系统
			orgDao.save(organ);
			int organId = 0;
			organId = organ.getId() != null ? organ.getId() : 0;
			
			if (organId != 0) {
				//初始化成员
				//String account = organ.getTelephone();
				String account = "admin";
			
				if (memberDao.getOneMember(account, organId) != null) {
					j.put("code", 0);
					j.put("text", Tips.EXISTACCOUNT.getText());
				} else {
					String organName = organ.getName();
					String name = organName;
					String pwd = PasswordGenerator.getInstance().makePwd();
					TMember tm = new TMember();
					tm.setAccount(account);
					tm.setFullname(name);
					tm.setPinyin(PinyinGenerator.getPinYinHeadChar(name));
					tm.setWorkno("");
					tm.setSex("1");
					tm.setBirthday("");
					tm.setLogo("PersonImg.png");
					tm.setEmail("");
					tm.setMobile("");
					tm.setTelephone("");
					tm.setAddress("");
					tm.setGroupmax(0);
					tm.setGroupuse(0);
					tm.setIntro(organName);
					tm.setOrganId(organId);
					tm.setAllpinyin(PinyinGenerator.getPinYin(name));
					tm.setPassword(PasswordGenerator.getInstance().getMD5Str(pwd));
					tm.setSuperAdmin(1);
					tm.setIsDel(1);
					memberDao.save(tm);
					
					//初始化职位
					String [] posArr = {"总经理", "副总经理", "部门经理", "项目主管", "工程师", "市场", "商务", "运营", "网管", "财务", "人事", "行政", "企宣"};
					List<TPosition> tpList = new ArrayList<TPosition>();
					
					for(int i = 0; i < posArr.length; i++) {
						TPosition tpt = new TPosition();
						tpt.setName(posArr[i]);
						tpt.setOrganId(organId);
						tpt.setListorder(0);
						tpList.add(tpt);
					}
					positionDao.save(tpList);
					
					int memberId = 0;
					memberId = tm.getId() != null ? tm.getId() : 0;
					
					//初始化部门
					TBranch tb = new TBranch();
					tb.setName("未分组部门");
					tb.setOrganId(organId);
					tb.setParentId(0);
					tb.setManagerId(memberId);
					tb.setAddress("0");
					tb.setWebsite("0");
					tb.setTelephone("0");
					tb.setFax("0");
					tb.setIntro("0");
					tb.setIsDel("1");
					tb.setNoGroup("1");
					tb.setListorder(0);
					branchDao.save(tb);
					
					int bid = 0;
					//int pid = 0;
					bid = tb.getId() != null ? tb.getId() : 0;
					//pid = tp.getId() != null ? tp.getId() : 0;
					
					//初始化部门成员关系 
					TBranchMember tbm = new TBranchMember();
					tbm.setBranchId(bid);
					tbm.setMemberId(memberId);
					tbm.setPositionId(0);
					tbm.setIsMaster("1");
					tbm.setIsDel("1");
					tbm.setListorder(0);
					branchMemberDao.save(tbm);
					
					//初始化角色
					List<TRole> roleList = new ArrayList<TRole>();
					TRole role1 = new TRole();
					TRole role2 = new TRole();
					TRole role3 = new TRole();
					TRole role4 = new TRole();
					
					role1.setName("组织管理员");
					role1.setOrganId(organId);
					role1.setRoleLevel("1");
					
					role2.setName("副组织管理员");
					role2.setOrganId(organId);
					role2.setRoleLevel("2");
				
					role3.setName("部门领导");
					role3.setOrganId(organId);
					role3.setRoleLevel("3");
					
					role4.setName("普通成员");
					role4.setOrganId(organId);
					role4.setRoleLevel("4");
					
					roleList.add(role1);
					roleList.add(role2);
					roleList.add(role3);
					roleList.add(role4);
					roleDao.save(roleList);
					
					int role1Id = role1.getId();
					//int role2Id = role2.getId();
					//int role3Id = role3.getId();
					
					List<TMemberRole> memRoleList = new ArrayList<TMemberRole>();
					
					//初始化成员角色关系
					TMemberRole tmr1 = new TMemberRole();
					//TMemberRole tmr2 = new TMemberRole();
					//TMemberRole tmr3 = new TMemberRole();
					
					tmr1.setMemberId(memberId);
					tmr1.setRoleId(role1Id);
					tmr1.setListorder(0);
					tmr1.setIsDel("1");
					
					//tmr2.setMemberId(memberId);
					//tmr2.setRoleId(role2Id);
					//tmr2.setListorder(0);
					//tmr2.setIsDel("1");
					
					//tmr3.setMemberId(memberId);
					//tmr3.setRoleId(role3Id);
					//tmr3.setListorder(0);
					//tmr3.setIsDel("1");
					
					memRoleList.add(tmr1);
					//memRoleList.add(tmr2);
					//memRoleList.add(tmr3);
					
					memberRoleDao.save(memRoleList);
					
					//初始化角色权限
					
					
					
					j.put("account", tm.getAccount());
					j.put("pwd", pwd);
					j.put("organId", organId);
					j.put("organCode", organ.getCode());
					//String msg = organName + "注册成功,初始账号：" + account + ";初始密码"+pwd+";公司ID:" + organId + ";公司码:"+organ.getCode();
					//TextHttpSender.getInstance().sendText(account, msg);
				}
				ret = j.toString();
			}
			return ret;
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
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
				t.put("code", to.getCode());
				t.put("name", to.getName());
				retList.add(t);
			}
			jo.put("code", 1);
			jo.put("text", retList.toString());
		} catch(Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		return jo.toString();
	}

	@Override
	public TOrgan getOrganByCode(String organCode) {
		return orgDao.getOrganByCode(organCode);
	}

}
