package com.sealtalk.service.adm.impl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.sealtalk.auth.dao.AppSecretDao;
import com.sealtalk.auth.dao.UserSysRelationDao;
import com.sealtalk.auth.model.AppSecret;
import com.sealtalk.auth.model.UserSysRelation;
import com.sealtalk.dao.adm.BranchDao;
import com.sealtalk.dao.adm.BranchMemberDao;
import com.sealtalk.dao.adm.MemberRoleDao;
import com.sealtalk.dao.adm.PositionDao;
import com.sealtalk.dao.member.MemberDao;
import com.sealtalk.model.ImpUser;
import com.sealtalk.model.TBranch;
import com.sealtalk.model.TBranchMember;
import com.sealtalk.model.TMember;
import com.sealtalk.model.TMemberRole;
import com.sealtalk.model.TPosition;
import com.sealtalk.service.adm.BranchService;
import com.sealtalk.utils.PasswordGenerator;
import com.sealtalk.utils.PinyinGenerator;
import com.sealtalk.utils.StringUtils;
import com.sealtalk.utils.TextHttpSender;
import com.sealtalk.utils.TimeGenerator;

public class BranchServiceImpl implements BranchService {

	private BranchDao branchDao;
	private MemberDao memberDao;
	private BranchMemberDao branchMemberDao;
	private MemberRoleDao memberRoleDao;
	private PositionDao positionDao;
	private UserSysRelationDao userSysRelationDao;
	private AppSecretDao appSecretDao;
	
	public void setAppSecretDao(AppSecretDao appSecretDao) {
		this.appSecretDao = appSecretDao;
	}
	public void setUserSysRelationDao(UserSysRelationDao userSysRelationDao) {
		this.userSysRelationDao = userSysRelationDao;
	}
	public BranchDao getBranchDao() {
		return branchDao;
	}
	public void setBranchDao(BranchDao branchDao) {
		this.branchDao = branchDao;
	}
	public MemberDao getMemberDao() {
		return memberDao;
	}
	public void setMemberDao(MemberDao memberDao) {
		this.memberDao = memberDao;
	}
	public BranchMemberDao getBranchMemberDao() {
		return branchMemberDao;
	}
	public void setBranchMemberDao(BranchMemberDao branchMemberDao) {
		this.branchMemberDao = branchMemberDao;
	}
	public MemberRoleDao getMemberRoleDao() {
		return memberRoleDao;
	}
	public void setMemberRoleDao(MemberRoleDao memberRoleDao) {
		this.memberRoleDao = memberRoleDao;
	}
	public PositionDao getPositionDao() {
		return positionDao;
	}
	public void setPositionDao(PositionDao positionDao) {
		this.positionDao = positionDao;
	}

	/*
	 * (non-Javadoc)
	 * @see com.sealtalk.service.adm.BranchService#getOrganTree(java.lang.Integer)
	 * by alopex
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String getOrganTree(Integer organId) {
		
		ArrayList<JSONObject> jl = new ArrayList<JSONObject>();

		List list = branchDao.getOrgan(organId);
		Iterator it = list.iterator();
		
		while(it.hasNext()) {
			JSONObject jo = new JSONObject();
			Object[] br = (Object[])it.next();
			jo.put("id", br[0]);
			jo.put("pid", 0);
			jo.put("name", "<img src='images/orga.png' height='28px' style='padding-right: 10px'>" + br[1]);
			jo.put("flag", 0);
			jl.add(jo);
		}
		
		list = branchDao.getBranch(organId);
		it = list.iterator();
		
		while(it.hasNext()) {
			JSONObject jo = new JSONObject();
			Object[] br = (Object[])it.next();
			jo.put("id", br[0]);
			jo.put("pid", (Integer)br[1] == 0 ? organId : br[1]);
			jo.put("name", "<img src='images/work.png' height='28px' style='padding-right: 10px'>" + br[2]);
			jo.put("flag", 1);
			jo.put("isParent", "true");
			jl.add(jo);
		}
		
		list = branchDao.getMember(organId);
		it = list.iterator();
		
		while(it.hasNext()) {
			JSONObject jo = new JSONObject();
			Object[] br = (Object[])it.next();
			jo.put("id", br[0]);
			jo.put("pid", br[1]);
			jo.put("name", "<img src='images/memb.png' height='28px' style='padding-right: 10px'>" + br[2]);
			jo.put("flag", 2);
			jl.add(jo);
		}
		
		return jl.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getOrganOnlyTree(Integer organId) {
		
		ArrayList<JSONObject> jl = new ArrayList<JSONObject>();

		List list = branchDao.getOrgan(organId);
		Iterator it = list.iterator();
		
		while(it.hasNext()) {
			JSONObject jo = new JSONObject();
			Object[] br = (Object[])it.next();
			jo.put("id", br[0]);
			jo.put("pid", 0);
			jo.put("name", br[1]);
			jo.put("flag", 0);
			jl.add(jo);
		}
		
		list = branchDao.getBranch(organId);
		it = list.iterator();
		
		while(it.hasNext()) {
			JSONObject jo = new JSONObject();
			Object[] br = (Object[])it.next();
			jo.put("id", br[0]);
			jo.put("pid", (Integer)br[1] == 0 ? organId : br[1]);
			jo.put("name", br[2]);
			jo.put("flag", 1);
			jo.put("isParent", "true");
			jl.add(jo);
		}
		
		return jl.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see com.sealtalk.service.adm.BranchService#getBranchById(java.lang.Long)
	 * by alopex
	 */
	@Override
	public String getBranchById(Integer branchId) {

		TBranch branch = branchDao.get(branchId);
		JSONObject jo = JSONObject.fromObject(branch);
		
		TMember manager = memberDao.get(branch.getManagerId());
		jo.put("manager", manager == null ? "" : manager.getFullname());
		
		return jo.toString();
	}

	@Override
	public TBranch getBranchObjectById(Integer branchId) {
		
		return branchDao.get(branchId);
	}

	@Override
	public TMember getMemberObjectById(Integer memberId) {
		
		return memberDao.get(memberId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.sealtalk.service.adm.BranchService#getMemberById(java.lang.Integer)
	 * by alopex
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public String getMemberById(Integer memberId) {
		
		TMember member = memberDao.get(memberId);

		JSONObject jo = JSONObject.fromObject(member);
		
		/*
		 * 取职务
		 */
		List list1 = memberDao.getMemberPosition(memberId);
		Iterator it1 = list1.iterator();
		if (it1.hasNext()) {
			Object[] pos = (Object[])it1.next();
			jo.put("positionId", pos[0]);
			jo.put("branchId", pos[1]);
			jo.put("branchMemberId", pos[2]);
		}
		
		/*
		 * 取角色
		 */
		List list2 = memberDao.getMemberRole(memberId);
		Iterator it2 = list2.iterator();
		if (it2.hasNext()) {
			Object rol = (Object)it2.next();
			jo.put("roleId", rol);
		}
		
		/*
		 * 取所在部门
		 */
		ArrayList<JSONObject> js = new ArrayList<JSONObject>();
		List list3 = branchDao.getBranchMember(memberId);
		Iterator it3 = list3.iterator();
		while (it3.hasNext()) {
			JSONObject j = new JSONObject();
			Object[] bm = (Object[])it3.next();
			j.put("branchmemberid", bm[0]);
			j.put("branchname", bm[1] != null ? bm[1] : "（未分组人员）");
			j.put("positionname", bm[2] == null ? "(未知职务)" : bm[2]);
			j.put("ismaster", bm[3]);
			js.add(j);
		}
		jo.put("branchmember", js);
		
		return jo.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.sealtalk.service.adm.BranchService#getMemberByAccount(java.lang.String)
	 * by alopex
	 */
	@Override
	public TMember getMemberByAccount(String account) {
		
		return memberDao.getOneMember(account);
	}

	/*
	 * (non-Javadoc)
	 * @see com.sealtalk.service.adm.BranchService#getMemberByAccount(java.lang.String)
	 * by alopex
	 */
	@Override
	public TBranch getBranchByName(String name) {
		
		return branchDao.getOneOfBranch(name);
	}

	@Override
	public List getMemberBranchById(Integer memberId) {
		
		return branchDao.getBranchMember(memberId);
	}

	@Override
	public TBranchMember getBranchMemberByBranchPosition(Integer branchId, Integer positionId) {
		
		return branchMemberDao.getBranchMemberByBranchPosition(branchId, positionId);
	}

	@Override
	public TBranchMember getBranchMemberByBranchMember(Integer branchId, Integer memberId) {
		
		return branchMemberDao.getBranchMemberByBranchMember(branchId, memberId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.sealtalk.service.adm.BranchService#getRole()
	 * by alopex
	 */
	@Override
	public String getRole() {

		ArrayList<JSONObject> jl = new ArrayList<JSONObject>();

		List list = branchDao.getRole();
		Iterator it = list.iterator();
		
		while(it.hasNext()) {
			JSONObject jo = new JSONObject();
			Object[] br = (Object[])it.next();
			jo.put("id", br[0]);
			jo.put("name", br[1]);
			jl.add(jo);
		}

		return jl.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see com.sealtalk.service.adm.BranchService#getRole()
	 * by alopex
	 */
	@Override
	public String getSex() {

		ArrayList<JSONObject> jl = new ArrayList<JSONObject>();

		List list = branchDao.getSex();
		Iterator it = list.iterator();
		
		while(it.hasNext()) {
			JSONObject jo = new JSONObject();
			Object[] br = (Object[])it.next();
			jo.put("id", br[0]);
			jo.put("name", br[1]);
			jl.add(jo);
		}

		return jl.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see com.sealtalk.service.adm.BranchService#getRole()
	 * by alopex
	 */
	@Override
	public String getPosition() {

		ArrayList<JSONObject> jl = new ArrayList<JSONObject>();

		List list = branchDao.getPosition();
		Iterator it = list.iterator();
		
		while(it.hasNext()) {
			JSONObject jo = new JSONObject();
			Object[] br = (Object[])it.next();
			jo.put("id", br[0]);
			jo.put("name", br[1]);
			jl.add(jo);
		}

		return jl.toString();
	}

	@Override
	public Integer saveBranch(TBranch branch) {
		
		branchDao.saveOrUpdate(branch);
		return branch.getId();
	}

	@Override
	public Integer saveMember(TMember member) {
		
		memberDao.saveOrUpdate(member);
		return member.getId();
	}

	@Override
	public TBranchMember getBranchMemberById(Integer branchMemberId) {
		
		return branchMemberDao.get(branchMemberId);
	}
	@Override
	public TMemberRole getMemberRoleByMemberId(Integer memberId) {
		
		List list = memberRoleDao.find("from TMemberRole where member_id = " + memberId);
		
		if (list.isEmpty()) return null;
		return (TMemberRole)list.get(0);
	}
	@Override
	public Integer saveBranchMember(TBranchMember branchMember) {
		
		branchMemberDao.saveOrUpdate(branchMember);
		return branchMember.getId();
	}
	@Override
	public Integer saveMemberRole(TMemberRole memberRole) {
		
		memberRoleDao.saveOrUpdate(memberRole);
		return memberRole.getId();
	}
	@Override
	public Integer delBranchMember(Integer branchMemberId) {
		
		TBranchMember branchMember = branchMemberDao.get(branchMemberId);
		
		//不存在，不能删除
		if (branchMember == null) return -1;
		List list = branchMemberDao.getBranchMemberByMember(branchMember.getMemberId());
		
		//只有一个，不能删除
		if (list.size() == 1) return branchMemberId;
		
		if ("1".equals(branchMember.getIsMaster())) {
			branchMemberDao.selectMaster(branchMember.getMemberId());
		}
		branchMemberDao.delete(branchMember);
		
		return 0;
	}
	@Override
	public void setMaster(Integer branchMemberId) {
		
		TBranchMember branchMember = branchMemberDao.get(branchMemberId);
		if (branchMember == null) return;

		branchMemberDao.executeUpdate("update TBranchMember set isMaster = '0' where memberId = " + branchMember.getMemberId());
		branchMember.setIsMaster("1");
		branchMemberDao.update(branchMember);
	}
	@Override
	public void reset(Integer memberId, String password) {
		
		TMember member = memberDao.get(memberId);
		member.setPassword(password);
		memberDao.update(member);
		
		// 发短信
	}
	@Override
	public void delMember(Integer memberId) {
		
		branchMemberDao.executeUpdate("delete from TBranchMember where memberId = " + memberId);
		memberRoleDao.executeUpdate("delete from TMemberRole where memberId = " + memberId);
		memberDao.executeUpdate("delete from TMember where id = " + memberId);
	}
	@Override
	public void delBranch(Integer branchId, Integer r, Integer organId) {
		
		TBranch branch = branchDao.get(branchId);
		
		List list = branchMemberDao.getBranchMemberByBranch(branchId);
		Iterator it = list.iterator();
		while(it.hasNext()) {
			TBranchMember bm = (TBranchMember)it.next();
			List list2 = branchMemberDao.getBranchMemberByMember(bm.getMemberId());
			if (list2.size() == 1) {
				TBranchMember bm2 = (TBranchMember)list2.get(0);
				bm2.setBranchId(organId);
				branchMemberDao.saveOrUpdate(bm2);
			}
			else {
				if (bm.getIsMaster().equals("1")) {
					branchMemberDao.selectMaster(bm.getMemberId());
				}
				branchMemberDao.delete(bm);
			}
		}
		
		List list3 = branchDao.getChildren(branchId);
		Iterator it3 = list3.iterator();

		while(it3.hasNext()) {
			TBranch b = (TBranch)it3.next();
			
			if (r == 1) {
				this.delBranch(b.getId(), r, organId);
			}
			else {
				b.setParentId(branch.getParentId());
				branchDao.saveOrUpdate(b);
			}
		}
		
		branchDao.delete(branch);
	}
	@Override
	public void movMember(Integer memberId, Integer pId, Integer toId) {
		
		TBranchMember bm = this.getBranchMemberByBranchMember(pId, memberId);

		TBranchMember tobm = this.getBranchMemberByBranchMember(toId, memberId);
		if (tobm == null) {
			bm.setBranchId(toId);
			branchMemberDao.saveOrUpdate(bm);
		}
		else {
			branchMemberDao.delete(bm);
		}
	}
	@Override
	public Integer movBranch(Integer branchId, Integer toId) {
		
		if (this.isDecendant(toId, branchId)) return 0;
		
		TBranch branch = branchDao.get(branchId);
		branch.setParentId(toId);
		branchDao.saveOrUpdate(branch);
		
		return branchId;
	}
	
	private boolean isDecendant(Integer branchId, Integer pId) {
		
		if (branchId < 101) return false;
		if (branchId.intValue() == pId.intValue()) return true;
		
		TBranch branch = branchDao.get(branchId);
		
		if (branch.getParentId().intValue() == pId.intValue()) return true;
		if (branch.getParentId().intValue() == 0) return false;
		return this.isDecendant(branch.getParentId(), pId);
	}

	@Override
	public JSONObject testUsers(JSONArray ja) {
		
		return branchDao.testUsers(ja);
	}
	
	@Override
	public void saveimp(JSONArray ja, Integer organId) {
		
		ArrayList<ImpUser> ua = new ArrayList<ImpUser>();
		int i = 0;
		while(i < ja.size()) {
			JSONObject js = (JSONObject)ja.get(i);
			ImpUser user = jsonToUser(js);
			ua.add(user);
			i++;
		}
		
		// 存人员
		Iterator<ImpUser> it = ua.iterator();
		long now = TimeGenerator.getInstance().getUnixTime();
		while(it.hasNext()) {
			ImpUser user = it.next();
			TMember m = new TMember();
			m.setMobile(user.getMobile());
			m.setFullname(user.getName());
			m.setPinyin(PinyinGenerator.getPinYin(user.getName()));
			m.setWorkno(user.getWorkno());
			m.setSex(user.getSex().equals("男") ? "1" : "2");
			m.setTelephone(user.getTelephone());
			m.setEmail(user.getEmail());
			m.setAccount(pinyin2account(m.getPinyin()));
			m.setOrganId(organId);
			m.setPassword(PasswordGenerator.getInstance().getMD5Str("111111"));
			m.setGroupmax(0);
			m.setGroupuse(0);
			m.setCreatetokendate(Integer.valueOf(String.valueOf(now)));
			memberDao.save(m);
			user.setId(m.getId());

			// 发短信
			String mobile = user.getMobile();
			if (mobile.length() == 11) {
				String msg = "您的IMS产品帐号" + m.getAccount() + ", 密码111111.";
				TextHttpSender.getInstance().sendText(mobile, msg);
			}
		}

		// 存部门
		it = ua.iterator();
		while(it.hasNext()) {
			ImpUser user = it.next();
			TBranch br = this.getBranchByName(user.getBranch());
			if (br == null) {
				br = new TBranch();
				br.setName(user.getBranch());
				br.setOrganId(organId);
				br.setParentId(0);
				TMember m = memberDao.getMemberByName(user.getManager());
				if (m == null) {
					br.setManagerId(0);
				}
				else {
					br.setManagerId(m.getId());
				}
				br.setListorder(0);
				branchDao.save(br);
			}
			user.setBranchId(br.getId());
		}
		
		// 存职位
		it = ua.iterator();
		while(it.hasNext()) {
			ImpUser user = it.next();
			TPosition p = positionDao.getPositionByName(organId, user.getPosition());
			if (p == null) {
				p = new TPosition();
				p.setName(user.getPosition());
				p.setOrganId(organId);
				p.setListorder(0);
				positionDao.save(p);
			}
			user.setPositionId(p.getId());
		}		

		// 存部门人员
		it = ua.iterator();
		while(it.hasNext()) {
			ImpUser user = it.next();
			TBranchMember bm = new TBranchMember();
			bm.setBranchId(user.getBranchId());
			bm.setMemberId(user.getId());
			bm.setPositionId(user.getPositionId());
			bm.setIsMaster("1");
			bm.setListorder(0);
			branchMemberDao.save(bm);
		}		
		
	}

	private String pinyin2account(String pinyin) {
	
		TMember m = memberDao.getOneMember(pinyin);
		if (m == null) return pinyin;
		
		int i = 0;
		while (true) {
			String account = pinyin + String.valueOf(i);
			m = memberDao.getOneMember(account);
			if (m == null) return account;
			i++;
		}
	}
	
	private ImpUser jsonToUser(JSONObject j) {
		
		ImpUser user = new ImpUser();

		user.setMobile((String)j.get("mobile"));
		user.setName((String)j.get("name"));
		user.setWorkno((String)j.get("workno"));
		user.setSex((String)j.get("sex"));
		user.setBranch((String)j.get("branch"));
		user.setManager((String)j.get("manager"));
		user.setPosition((String)j.get("position"));
		user.setTelephone((String)j.get("telephone"));
		user.setEmail((String)j.get("email"));

		return user;
	}

	@Override
	public void impexcel(JSONArray ja, String path) throws IOException {
		
		String[] head = {"手机号","姓名","工号","性别","所属部门","部门领导","职位","座机号","邮箱"};
		String[] code = {"mobile","name","workno","sex","branch","manager","position","telephone","email"};

		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sh = wb.createSheet();
		
		int i = 9;
		while(i-- > 0) {
//			sh.setColumnWidth(i, 2000);
		}
		
		XSSFRow row = sh.createRow(0);
		i = 0;
		while (i < 9) {
			XSSFCell cell = row.createCell(i);
			cell.setCellValue(head[i]);
			i++;
		}
		
		i = 0;
		while (i < ja.size()) {
			XSSFRow r = sh.createRow(i + 1);
			JSONObject js = (JSONObject)ja.get(i);
			int j = 0;
			while (j < 9) {
				XSSFCell c = r.createCell(j);
				c.setCellValue((String)js.get(code[j]));
				j++;
			}
			i++;
		}
		
		FileOutputStream fo = new FileOutputStream(path);
		wb.write(fo);
		fo.close();
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getBranchTree() {
		
		List list = branchDao.getBranchTree();
		Iterator it = list.iterator();
		ArrayList<JSONObject> jl = new ArrayList<JSONObject>();
		
		while(it.hasNext()) {
			JSONObject jo = new JSONObject();
			Object[] br = (Object[])it.next();
			jo.put("id", br[0]);
			jo.put("pId", br[1]);
			jo.put("name", br[2]);
			jl.add(jo);
		}
		
		return jl.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getBranchTreeAndMember(String appId) {
		List list = branchDao.getBrancTreeAndMember();
		
		JSONArray ja = new JSONArray();
		
		ArrayList<Object> branchList = new ArrayList<Object>();
		ArrayList<Object> organList = new ArrayList<Object>();
		ArrayList<String> ids = new ArrayList<String>();
		
		try {
			if (list != null) {
				AppSecret as = appSecretDao.getAppSecretByAppId(appId);
				int appRecordId = 0;
				if (as != null) {
					appRecordId = as.getId();
				}
				List<UserSysRelation> userSysList = userSysRelationDao.getAllRelation(appRecordId);
				ArrayList<Integer> memberIds = new ArrayList<Integer>();
				
				if (userSysList != null) {
					for(int i = 0; i < userSysList.size(); i++) {
						memberIds.add(userSysList.get(i).getUserId());
					}
				}
				
				for(int i = 0; i < list.size(); i++) {
					Object[] o = (Object[])list.get(i);
					JSONObject jm = new JSONObject();
					
					if (!StringUtils.getInstance().isNull(o[0])) {
						jm.put("flag", 1);
						jm.put("pid", isBlank(o[4]));
						jm.put("id", isBlank(o[7]));
						jm.put("account", isBlank(o[8]));
						jm.put("name", isBlank(o[9]));
						jm.put("logo", isBlank(o[10]));
						jm.put("telephone", isBlank(o[11]));
						jm.put("email", isBlank(o[12]));
						jm.put("address", isBlank(o[13]));
						jm.put("token", isBlank(o[14]));
						jm.put("birthday", isBlank(o[15]));
						jm.put("workno", isBlank(o[16]));
						jm.put("mobile", isBlank(o[17]));
						jm.put("intro", isBlank(o[18]));
						jm.put("postitionid", isBlank(o[19]));
						jm.put("postitionname", isBlank(o[20]));
						jm.put("sexid", isBlank(o[21]));
						jm.put("sexname", isBlank(o[22]));
						jm.put("organid", isBlank(o[23]));
						jm.put("organname", isBlank(o[24]));
						jm.put("branchid", isBlank(o[4]));
						jm.put("branchname", isBlank(o[6]));
						boolean status = false;
						
						if (!StringUtils.getInstance().isNull(o[7])) {
							status = memberIds.contains(Integer.parseInt(isBlank(o[7])));
						}
						
						jm.put("accessStatus", status);
						
						if (!branchList.contains(o[4])) {
							String pid = isBlank(o[5]);
							JSONObject jb = new JSONObject();
							jb.put("flag", 0);
							jb.put("id", isBlank(o[4]));
							jb.put("pid", pid.equals("0") ? isBlank(o[23]) : pid);
							jb.put("name", isBlank(o[6]));
							ja.add(jb);
							branchList.add(o[4]);
						}
					} else {
						String pid = isBlank(o[5]);
						jm.put("id", isBlank(o[4]));
						jm.put("pid", pid.equals("0") ? isBlank(o[23]) : pid);
						jm.put("name", isBlank(o[6]));
						jm.put("flag", 0);  
					}
					if (!organList.contains(o[23])) {		//组织
						JSONObject jor = new JSONObject();
						jor.put("id", isBlank(o[23]));
						jor.put("pid", 0);
						jor.put("name", isBlank(o[24]));
						jor.put("flag", -1);
						ja.add(jor);
						organList.add(o[23]);
					}
					ja.add(jm);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		logger.info(ja.toString());
		
		return ja.toString();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String getBranchMember(String branchId, String appId) {
		String result = null;
		boolean status = true;
		
		if (StringUtils.getInstance().isBlank(branchId)) {
			status = false;
		} else {
			List list = branchDao.getBranchMember(branchId);
			JSONArray ja = new JSONArray();
			
			try {
				if( list != null) {
					AppSecret as = appSecretDao.getAppSecretByAppId(appId);
					int appRecordId = 0;
					if (as != null) {
						appRecordId = as.getId();
					}
					List<UserSysRelation> userSysList = userSysRelationDao.getAllRelation(appRecordId);
					ArrayList<Integer> memberIds = new ArrayList<Integer>();
					
					if (userSysList != null) {
						for(int i = 0; i < userSysList.size(); i++) {
							memberIds.add(userSysList.get(i).getUserId());
						}
					}
					for(int i = 0; i < list.size(); i++) {
						Object[] o = (Object[])list.get(i);
					
						if (!StringUtils.getInstance().isBlank((String)o[1])) {
							JSONObject jm = new JSONObject();
							jm.put("code", 1);
							jm.put("text", "ok");
							jm.put("id", isBlank(o[0]));
							jm.put("account", isBlank(o[1]));
							jm.put("name", isBlank(o[2]));
							jm.put("logo", isBlank(o[3]));
							jm.put("telephone", isBlank(o[4]));
							jm.put("email", isBlank(o[5]));
							jm.put("address", isBlank(o[6]));
							jm.put("token", isBlank(o[7]));
							jm.put("sex", isBlank(o[8]));
							jm.put("birthday", isBlank(o[9]));
							jm.put("workno", isBlank(o[10]));
							jm.put("mobile", isBlank(o[11]));
							jm.put("groupmax", isBlank(o[12]));
							jm.put("groupuse", isBlank(o[13]));
							jm.put("intro", isBlank(o[14]));
							jm.put("postitionname", isBlank(o[16]));
							jm.put("accessStatus", memberIds.contains(Integer.parseInt(isBlank(o[17]))));
							ja.add(jm); 
							jm = null;
						}
					}
				}
				result = ja.toString();
			} catch (Exception e) {
				status = false;
				e.printStackTrace();
			}
		}
		
		if (!status) {
			JSONObject jo = new JSONObject();
			 
			 jo.put("code", -1);
			 jo.put("text", "err");
			 result = jo.toString();
		}
		
		return result;
	}
	
	private String isBlank(Object o) {
		return o == null ? "" : o + "";
	}
	
}
