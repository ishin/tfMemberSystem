package com.organ.service.adm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.organ.dao.adm.PrivDao;
import com.organ.dao.adm.RoleDao;
import com.organ.dao.adm.RolePrivDao;
import com.organ.dao.appinfoconfig.AppInfoConfigDao;
import com.organ.dao.limit.RoleAppSecretDao;
import com.organ.dao.member.MemberDao;
import com.organ.model.TBranch;
import com.organ.model.TBranchMember;
import com.organ.model.TMember;
import com.organ.model.TMemberRole;
import com.organ.model.TOrgan;
import com.organ.model.TPosition;
import com.organ.model.TPriv;
import com.organ.model.TRole;
import com.organ.model.TRoleAppSecret;
import com.organ.model.TRolePriv;
import com.organ.service.adm.OrgService;
import com.organ.utils.JSONUtils;
import com.organ.utils.LogUtils;
import com.organ.utils.PasswordGenerator;
import com.organ.utils.PinyinGenerator;
import com.organ.utils.PropertiesUtils;
import com.organ.utils.SecretUtils;
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
	private AppInfoConfigDao appinfoconfigDao;
	private RoleAppSecretDao roleappsecretDao;
	private PrivDao privDao;
	private RolePrivDao rolePrivDao;

	public void setRolePrivDao(RolePrivDao rolePrivDao) {
		this.rolePrivDao = rolePrivDao;
	}

	public void setPrivDao(PrivDao privDao) {
		this.privDao = privDao;
	}

	public void setAppinfoconfigDao(AppInfoConfigDao appinfoconfigDao) {
		this.appinfoconfigDao = appinfoconfigDao;
	}

	public void setRoleappsecretDao(RoleAppSecretDao roleappsecretDao) {
		this.roleappsecretDao = roleappsecretDao;
	}

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
		int maxNumber = orgDao.getMaxNumber();
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
					int role2Id = role2.getId();
					int role3Id = role3.getId();
					int role4Id = role4.getId();
					
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
					
					//初始化IM应用
					ArrayList<String> idsecret = makeAppId();
					String callbackurl = "";
					String appname = "IM";
					int isOpen = 1;
					int appId = appinfoconfigDao.updatePriv(idsecret.get(0), idsecret.get(1), callbackurl, appname, isOpen, organId);
					//初始化角色应用关联
					List<TRoleAppSecret> tas = new ArrayList<TRoleAppSecret>();
					tas.add(new TRoleAppSecret(role1Id, appId));
					tas.add(new TRoleAppSecret(role2Id, appId));
					tas.add(new TRoleAppSecret(role3Id, appId));
					tas.add(new TRoleAppSecret(role4Id, appId));
					roleappsecretDao.save(tas);
					
					//初始化IM权限
					initIMPriv(organId, role1Id, role2Id);
					
					j.put("account", tm.getAccount());
					j.put("pwd", pwd);
					j.put("organId", organId);
					j.put("organCode", organ.getCode());
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
	
	//初始化IM权限
	private void initIMPriv(int organId, int role1Id, int role2Id) {
		List<TPriv> privList = new ArrayList<TPriv>();
		
		privList.add(new TPriv(0, "应用APP/PC端", "1", "1", "yyapppcd", "IM", organId, 2));
		privList.add(new TPriv(0, "层级限制", "2", "1", "cjxz", "IM", organId, 3));
		privList.add(new TPriv(1, "个人设置", "1", "0", "grsz", "IM", organId, 9));
		privList.add(new TPriv(1, "聊天设置", "1", "0", "stsz", "IM", organId, 10));
		privList.add(new TPriv(1, "群组", "1", "0", "qz", "IM", organId, 11));
		privList.add(new TPriv(1, "对讲机", "1", "0", "djj", "IM", organId, 0));
		privList.add(new TPriv(1, "其他", "1", "0", "qt", "IM", organId, 0));
		privList.add(new TPriv(2, "对平级或者上级部门开放", "2", "1", "dpjhzsjbmkf", "IM", organId, 0));
		privList.add(new TPriv(3, "使用工作签名", "3", "0", "grszsygzqm", "IM", organId, 1));
		privList.add(new TPriv(3, "修改用户名", "1", "0", "grszxgyhm", "IM", organId, 2));
		privList.add(new TPriv(3, "修改姓名", "1", "0", "grszxgxm", "IM", organId, 3));
		privList.add(new TPriv(4, "修改职务", "1", "0", "grszxgzw", "IM", organId, 4));
		privList.add(new TPriv(4, "发起个人聊天", "1", "0", "ltszfqgrlt", "IM", organId, 1));
		privList.add(new TPriv(4, "群组聊天", "1", "0", "ltszqzlt", "IM", organId, 2));
		privList.add(new TPriv(4, "文件上传", "1", "0", "ltszwjsc", "IM", organId, 3));
		privList.add(new TPriv(5, "创建群", "1", "0", "qzcjq", "IM", organId, 1));
		privList.add(new TPriv(5, "解散群", "1", "0", "qzjsq", "IM", organId, 2));
		privList.add(new TPriv(5, "修改群创建者", "1", "0", "qzxgqcjz", "IM", organId, 3));
		privList.add(new TPriv(6, "开启", "1", "0", "djjkq", "IM", organId, 1));
		privList.add(new TPriv(6, "发言时其他人禁言", "1", "0", "djjfysqtrjy", "IM", organId, 2));
		privList.add(new TPriv(6, "紧急呼叫", "1", "0", "djjjjhj", "IM", organId, 3));
		privList.add(new TPriv(7, "查看地理位置", "1", "0", "qtckdlwz", "IM", organId, 1));
		privList.add(new TPriv(8, "发起个人聊天", "2", "0", "dpjhzsjbmkffqgrlt", "IM", organId, 1));
		privList.add(new TPriv(8, "文件上传", "2", "0", "dpjhzsjbmkfwjsc", "IM", organId, 2));
		privList.add(new TPriv(8, "创建群", "1", "0", "dpjhzsjbmkfcjq", "IM", organId, 3));
		privList.add(new TPriv(8, "查看地理位置", "2", "0", "dpjhzsjbmkfckdlwz", "IM", organId, 4));
		privList.add(new TPriv(8, "紧急通知", "2", "0", "dpjhzsjbmkfjjtz", "IM", organId, 4));
		privList.add(new TPriv(1, "群组管理", "2", "0", "qzgl", "IM", organId, 9));
		privList.add(new TPriv(9, "查看", "1", "0", "qzglck", "IM", organId, 0));
		privList.add(new TPriv(9, "解散", "1", "0", "qzgljs", "IM", organId, 0));
		privList.add(new TPriv(9, "修改", "1", "0", "qzglxg", "IM", organId, 0));
		privDao.save(privList);
		privList.clear();
		privList = null;
		//为了获取id
		privList = privDao.getPrivByOrganAndApp("IM", organId);
		
		if (privList != null) {
			Map<Integer, Integer> idMap = new HashMap<Integer, Integer>();
			int len = privList.size();
			
			for(int i = 0; i < len; i++) {
				TPriv t = privList.get(i);
				int id = t.getId();
				String url = t.getUrl();
				if (url.equals("yyapppcd")) {
					idMap.put(1, id);
				} else if (url.equals("cjxz")) {
					idMap.put(2, id);
				} else if (url.equals("grsz")) {
					idMap.put(3, id);
				} else if (url.equals("stsz")) {
					idMap.put(4, id);
				} else if (url.equals("qz")) {
					idMap.put(5, id);
				} else if (url.equals("djj")) {
					idMap.put(6, id);
				} else if (url.equals("qt")) {
					idMap.put(7, id);
				} else if (url.equals("dpjhzsjbmkf")) {
					idMap.put(8, id);
				} else if (url.equals("qzgl")) {
					idMap.put(9, id);
				}
			}
			ArrayList<Integer> idList = new ArrayList<Integer>();
			
			for(int i = 0; i < len; i++) {
				TPriv t = privList.get(i);
				int pid = t.getParentId();
				if (pid == 0) continue;
				if (idMap.get(pid) == null) continue;
				t.setParentId(idMap.get(pid));
				idList.add(t.getId());
			}
			privDao.saveOrUpdate(privList);
			
			//查找登陆权限
			TPriv tp = privDao.getSimilarityPrivByUrl("DLQX", organId);
			int dlqxid = 0;
			if(tp != null) {
				dlqxid = tp.getId();
			}
			//保存角色权限
			List<TRolePriv> tmrList = new ArrayList<TRolePriv>();
			
			for(int j = 0; j < 2; j++) {
				int roleId = (j == 0) ? role1Id : role2Id;
				for(int i = 0; i < idList.size(); i++) {
					tmrList.add(new TRolePriv(roleId, idList.get(i)));
				}
				if (dlqxid != 0) {
					tmrList.add(new TRolePriv(roleId, dlqxid));
				}
				rolePrivDao.save(tmrList);
				tmrList.clear();
			}
		}
		
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
	
	private ArrayList<String> makeAppId() {
		ArrayList<String> as = new ArrayList<String>();

		try {
			String id = PasswordGenerator.getInstance().createId(18);
			as.add(id);
			as.add(new SecretUtils().encrypt(id));
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}

		return as;
	}

}
