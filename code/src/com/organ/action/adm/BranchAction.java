package com.organ.action.adm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.organ.common.AuthTips;
import com.organ.common.BaseAction;
import com.organ.common.Tips;
import com.organ.model.AppSecret;
import com.organ.model.TBranch;
import com.organ.model.TBranchMember;
import com.organ.model.TMember;
import com.organ.model.TMemberRole;
import com.organ.model.TOrgan;
import com.organ.service.adm.BranchService;
import com.organ.service.adm.OrgService;
import com.organ.service.member.MemberService;
import com.organ.service.msg.MessageService;
import com.organ.utils.PasswordGenerator;
import com.organ.utils.PinyinGenerator;
import com.organ.utils.PropertiesUtils;
import com.organ.utils.StringUtils;
import com.organ.utils.TextHttpSender;
import com.organ.utils.TimeGenerator;

/**
 * @author alopex
 *
 */

public class BranchAction extends BaseAction {

	private static final long serialVersionUID = 1L;
	private Logger logger = LogManager.getLogger(BranchAction.class);
		
	/*
	 * 取部门树
	 * by alopex
	 */
	public String getOrganTree() throws ServletException {
		int id = this.getOrganId();
		String result = null;
		
		if (id == 0) {
			result = failResult(Tips.TIMEOUT.getText());
		} else {
			result = branchService.getOrganTree(id);
		}
		returnToClient(result);
		
		return "text";
	}
	
	/*
	 * 取部门树
	 * by alopex
	 */
	public String getOrganOnlyTree() throws ServletException {
		int id = this.getOrganId();
		String result = null;
		
		if (id == 0) {
			result = failResult(Tips.TIMEOUT.getText());
		} else {
			result = branchService.getOrganOnlyTree(id);
		}
		returnToClient(result);
		
		return "text";
	}
	
	/*
	 * 取部门通过部门id
	 * by alopex
	 */
	public String getBranchById() throws ServletException {
		
		String branchId = clearChar(this.request.getParameter("id"));
		String result = null;
		
		if (StringUtils.getInstance().isBlank(branchId)) {
			result = failResult(Tips.WRONGPARAMS.getText());
		} else {
			result = branchService.getBranchById(Integer.valueOf(branchId));
		}
		returnToClient(result);
		
		return "text";
	}
	
	public String getSuperMember() throws ServletException {
		int organId = getSessionUserOrganId();
		String result = branchService.getSuperMember(organId);
		returnToClient(result);
		
		return "text";
	}
	
	/*
	 * 取人员通过人员id
	 * by alopex
	 */
	public String getMemberById() throws ServletException {
		String id = this.request.getParameter("id");
		String result = branchService.getMemberById(id);
		returnToClient(result);
		return "text";
	}

	public String getSuperAdmin() throws ServletException {
		int organId = getSessionUserOrganId();
		returnToClient(branchService.getSuperMember(organId));
		return "text";
	}
	
	/*
	 * 取部门人员通过id
	 * by alopex
	 */
	public String getBranchMemberById() throws ServletException {
		
		String branchMemberId = clearChar(this.request.getParameter("branchmemberid"));
		TBranchMember branchMember = branchService.getBranchMemberById(Integer.parseInt(branchMemberId));
		
		JSONObject jo = new JSONObject();
		jo.put("branchid", branchMember.getBranchId());
		jo.put("positionid", branchMember.getPositionId());
		
		returnToClient(jo.toString());
		
		return "text";
	}

	/*
	 * 取人员所在部门
	 */
	public String getMemberBranchById() throws ServletException {
		
		String memberId = clearChar(this.request.getParameter("memberid"));
		List list = branchService.getMemberBranchById(Integer.parseInt(memberId));
		
		ArrayList<JSONObject> joa = new ArrayList<JSONObject>();
		Iterator it = list.iterator();
		while (it.hasNext()) {
			Object[] o = (Object[])it.next();
			JSONObject jo = new JSONObject();
			jo.put("branchmemberid", o[0]);
			jo.put("branchname", o[1]);
			jo.put("positionname", o[2] == null ? "(未知职务)" : o[2]);
			jo.put("ismaster", o[3]);
			joa.add(jo);
		}
		returnToClient(joa.toString());
		
		return "text";
	}
	
	/*
	 * 取字典
	 * by alopex
	 */
	public String getRole() throws ServletException {
		
		String result = branchService.getRole(this.getOrganId());
		returnToClient(result);
		
		return "text";
	}
	public String getSex() throws ServletException {
		
		String result = branchService.getSex();
		returnToClient(result);
		
		return "text";
	}
	
	public String getPosition() throws ServletException {
		
		String result = branchService.getPosition(this.getOrganId());
		returnToClient(result);
		
		return "text";
	}
	
	/**
	 * 保存部门,专为外部接口使用
	 * @throws SevletException
	 */
	@Deprecated
	public String saveBranchExtra() throws ServletException {
		String result = null;
		AppSecret as = msgService.validAppIdAndSecret(clearChar(appId), clearChar(secret));
		
		if (as != null) {
			result = this.saveBranch();
		} else {
			JSONObject jo = new JSONObject();
			jo.put("code", 0);
			jo.put("text", AuthTips.WORNGAPPID.getText());
			result = jo.toString();
		}
		
		returnToClient(result);
		return "text";
	}
	
	
	public String saveBranch() throws ServletException {
		
		TBranch branch = null;
		String id = clearChar(this.request.getParameter("branchid"));
		String branchName = clearChar(this.request.getParameter("branchname"));
		String branchAddress = clearChar(this.request.getParameter("branchaddress"));
		String branchFax = clearChar(this.request.getParameter("branchfax"));
		String branchIntro = clearChar(this.request.getParameter("branchintro"));
		String branchManagerId = clearChar(this.request.getParameter("branchmanagerid"));
		String branchParentId = clearChar(this.request.getParameter("branchparentid"));
		String branchTelPhone = clearChar(this.request.getParameter("branchtelephone"));
		String branchWebSite = clearChar(this.request.getParameter("branchwebsite"));
		
		int organId = getSessionUserOrganId();
		int memberId = Integer.parseInt(branchManagerId);
		
		if (id != null) {
			branch = branchService.getBranchObjectById(Integer.parseInt(id));
			if (!branch.getName().equalsIgnoreCase(branchName)) {
				if (branchService.getBranchByName(clearChar(branchName), organId) != null) {
					JSONObject jo = new JSONObject();
					jo.put("branchid", 0);
					returnToClient(jo.toString());
					return "text";
				}
			}
		} else {
			if (branchService.getBranchByName(branchName, organId) != null) {
				JSONObject jo = new JSONObject();
				jo.put("branchid", 0);
				returnToClient(jo.toString());
				return "text";
			}
			branch = new TBranch();
			branch.setListorder(0);
			branch.setIsDel("1");
			branch.setNoGroup("0");
		}
		if (branchAddress != null)
			branch.setAddress(branchAddress);
		if (branchFax != null)
			branch.setFax(branchFax);
		if (branchIntro != null)
			branch.setIntro(branchIntro);
		if (branchManagerId != null)
			branch.setManagerId(memberId);
		if (branchName != null)
			branch.setName(branchName);
		if (branchParentId != null)
			branch.setParentId(Integer.parseInt(branchParentId));
		if (branchTelPhone != null)
			branch.setTelephone(branchTelPhone);
		if (branchWebSite != null)
			branch.setWebsite(branchWebSite);

		branch.setOrganId(this.getOrganId());
		
		JSONObject jo = new JSONObject();
		int branchLeaderCount = branchService.getBranchMemberCountByMember(memberId);
		
		if (branchLeaderCount < 5) {
			Integer branchId = branchService.saveBranch(branch);
			
			//增加管理者
			TBranchMember tb = branchService.getBranchMemberByBranchMember(branchId, memberId);
			
			if (tb == null) {
				TBranchMember branchMember = new TBranchMember();
				boolean b = branchService.getMasterMemberById(memberId);
				branchMember.setMemberId(memberId);
				branchMember.setBranchId(branchId);
				
				branchMember.setIsMaster(!b ? "1" : "0");
				branchMember.setPositionId(0);
				branchMember.setListorder(0);
				branchMember.setIsDel("1");
				branchService.saveBranchMember(branchMember);
			}
			
			jo.put("branchid", branchId);
		} else {
			jo.put("branchid", -1);
		}
		
		if (appId == null && secret == null) {
			returnToClient(jo.toString());
			return "text";
		} else {
			return jo.toString();
		}
	}
	
	public String saveMember() throws ServletException {
		TMember member = null;
		String id = clearChar(this.request.getParameter("memberid"));
		String memberAccount = clearChar(this.request.getParameter("memberaccount"));
		String memberMail = clearChar(this.request.getParameter("memberemail"));
		String memberMobile = clearChar(this.request.getParameter("membermobile"));
		String memberPhone = clearChar(this.request.getParameter("membertelephone"));
		String memberAddress = clearChar(this.request.getParameter("memberaddress"));
		String memberBirthday = clearChar(this.request.getParameter("memberbirthday"));
		String memberFullName = clearChar(this.request.getParameter("memberfullname"));
		String memberIntro = clearChar(this.request.getParameter("memberintro"));
		String memberSex = clearChar(this.request.getParameter("membersex"));
		String memberWorkNo = clearChar(this.request.getParameter("memberworkno"));
		
		if (StringUtils.getInstance().isBlank(memberSex)) {
			memberSex = "1";
		}
		
		int organId = getSessionUserOrganId();
		boolean sms = false;
		
		JSONObject jo = new JSONObject();
		
		int superState = 0;
		if (id != null && !"".equals(id)) {
			member = branchService.getMemberObjectById(Integer.parseInt(id));
			String account = member.getAccount();
			account = account == null ? "" : account;
			superState = member.getSuperAdmin();
			if (!member.getAccount().equalsIgnoreCase(memberAccount)) {
				if (branchService.getMemberByAccount(memberAccount, organId) != null) {
					JSONObject jo1 = new JSONObject();
					jo1.put("memberid", 0);
					returnToClient(jo1.toString());
					return "text";
				}
			}
			String mobile = member.getMobile();
			String telPhone = member.getTelephone();
			mobile = mobile == null ? "" : mobile;
			if (!mobile.equalsIgnoreCase(memberMobile)) {
				if (branchService.getMemberByMobile(memberMobile) != null) {
					JSONObject jo1 = new JSONObject();
					jo1.put("memberid", -1);
					returnToClient(jo1.toString());
					return "text";
				}
			}
			String email = member.getEmail();
			email = email == null ? "" : email;
			if (!member.getEmail().equalsIgnoreCase(memberMail)) {
				if (branchService.getMemberByEmail(memberMail) != null) {
					JSONObject jo1 = new JSONObject();
					jo1.put("memberid", -2);
					returnToClient(jo1.toString());
					return "text";
				}
			}
			if (!member.getWorkno().equalsIgnoreCase(memberWorkNo)) {
				if (branchService.getMemberByWorkNo(memberWorkNo, organId) != null) {
					JSONObject jo1 = new JSONObject();
					jo1.put("memberid", -2);
					returnToClient(jo1.toString());
					return "text";
				}
			}
		} else {
			if (branchService.getMemberByAccount(memberAccount, organId) != null) {
				JSONObject jo1 = new JSONObject();
				jo1.put("memberid", 0);
				returnToClient(jo1.toString());
				return "text";
			}
			if (branchService.getMemberByMobile(memberMobile) != null) {
				JSONObject jo1 = new JSONObject();
				jo1.put("memberid", -1);
				returnToClient(jo1.toString());
				return "text";
			}
			if (!StringUtils.getInstance().isBlank(memberMail) && branchService.getMemberByEmail(memberMail) != null) {
				JSONObject jo1 = new JSONObject();
				jo1.put("memberid", -2);
				returnToClient(jo1.toString());
				return "text";
			}
			if (!StringUtils.getInstance().isBlank(memberWorkNo) && branchService.getMemberByWorkNo(memberWorkNo, organId) != null) {
				JSONObject jo1 = new JSONObject();
				jo1.put("memberid", -3);
				returnToClient(jo1.toString());
				return "text";
			}
			TOrgan to = orgService.getInfo(organId);
			String address = "";
			if (to != null) {
				address = to.getAddress();
			}
			
			member = new TMember();
			member.setGroupmax(0);
			member.setGroupuse(0);
			member.setAddress(address);
			member.setIsDel(1);
			member.setPassword(PasswordGenerator.getInstance().getMD5Str("111111"));
			sms = true;
		}
		if (memberAccount != null)
			member.setAccount(memberAccount);
		if (memberAddress != null)
			member.setAddress(memberAddress);
		if (memberBirthday != null) {
			String bd = memberBirthday;
			if (bd.length() == 10) {
				member.setBirthday(bd.substring(0,4) + bd.substring(5,7) + bd.substring(8,10));
			}
		}
		if (memberMail != null)
			member.setEmail(memberMail);
		if (memberFullName != null) {
			member.setFullname(memberFullName);
			member.setPinyin(PinyinGenerator.getPinYinHeadChar(memberFullName));
			member.setAllpinyin(PinyinGenerator.getPinYin(memberFullName));
		}
		if (memberIntro != null)
			member.setIntro(memberIntro);
		if (memberMobile != null)
			member.setMobile(memberMobile);
		if (memberSex != null)
			member.setSex(memberSex);
		if (memberPhone != null)
			member.setTelephone(memberPhone);
		if (memberWorkNo != null)
			member.setWorkno(memberWorkNo);
		
		member.setOrganId(this.getOrganId());

		long now = TimeGenerator.getInstance().getUnixTime();
		member.setCreatetokendate(Integer.valueOf(String.valueOf(now)));
		member.setSuperAdmin(superState);
		
		Integer memberId = branchService.saveMember(member);

		//部门职务
		TBranchMember branchMember = null;
		String branchmemberid = clearChar(this.request.getParameter("branchmemberid"));
		if ( branchmemberid != null && !"".equals(branchmemberid)) {
			branchMember = branchService.getBranchMemberById(Integer.parseInt(branchmemberid));
		}
		else {
			branchMember = new TBranchMember();
			branchMember.setListorder(0);
			branchMember.setIsMaster("0");
		}
		branchMember.setMemberId(memberId);
		String memberbranchid = clearChar(this.request.getParameter("memberbranchid"));
		if ( memberbranchid!= null && !"".equals(memberbranchid)) {
			branchMember.setBranchId(Integer.parseInt(memberbranchid));
		}
		else {
			int noGroupBranchId = branchService.getNoGroupBranch(this.getOrganId());
			branchMember.setBranchId(noGroupBranchId);
		}
		String memberpositionid = clearChar(this.request.getParameter("memberpositionid"));
		if ( memberpositionid != null && !"".equals(memberpositionid)) {
			branchMember.setPositionId(Integer.parseInt(memberpositionid));
		}
		else {
			branchMember.setPositionId(0);
		}
		branchMember.setIsDel("1");
		branchService.saveBranchMember(branchMember);
		
		//人员角色
		String membberroleid = clearChar(this.request.getParameter("memberroleid"));
		if ( membberroleid != null && !"".equals(membberroleid)) {
			TMemberRole memberRole = null;
			memberRole = branchService.getMemberRoleByMemberId(memberId);
			if (memberRole == null) {
				memberRole = new TMemberRole();
				memberRole.setMemberId(memberId);
				memberRole.setIsDel("1");
				memberRole.setListorder(0);
			}
			memberRole.setRoleId(Integer.parseInt(membberroleid));
			branchService.saveMemberRole(memberRole);
		}
		
		//发短信
		if (sms) {
			String msg = "您的IMS产品帐号" + member.getAccount() + ", 密码111111.";
			logger.info("短信验证内容： " + msg);
			TextHttpSender.getInstance().sendText(member.getMobile(), msg);
		}
		
		jo.put("memberid", memberId);
		returnToClient(jo.toString());
	
		return "text";
	}
	
	public String savePosition() throws ServletException {

		String branchmemberid = clearChar(this.request.getParameter("branchmemberid"));
		String memberid = clearChar(this.request.getParameter("memberid"));
		String branchid = clearChar(this.request.getParameter("branchid"));
		String positionid = clearChar(this.request.getParameter("positionid"));

		JSONObject jo = new JSONObject();
		Integer result = 0;
		
		TBranchMember branchMember = branchService.getBranchMemberByBranchPosition(
				Integer.parseInt(branchid),Integer.parseInt(positionid));
		if (branchMember == null) {
			branchMember = branchService.getBranchMemberById(Integer.parseInt(branchmemberid));
			//新增
			if (branchMember == null) {
				branchMember = new TBranchMember();
				branchMember.setBranchId(Integer.parseInt(branchid));
				branchMember.setMemberId(Integer.parseInt(memberid));
				branchMember.setPositionId(Integer.parseInt(positionid));
				branchMember.setIsMaster("0");
				branchMember.setListorder(0);
				branchMember.setIsDel("1");
				result = branchService.saveBranchMember(branchMember);
			}
			//编辑
			else if (branchMember.getBranchId() != Integer.parseInt(branchid)
						|| branchMember.getPositionId() != Integer.parseInt(positionid)) {
				branchMember.setBranchId(Integer.parseInt(branchid));
				branchMember.setPositionId(Integer.parseInt(positionid));
				result = branchService.saveBranchMember(branchMember);
			}
		}

		jo.put("branchmemberid", result);
		returnToClient(jo.toString());
		return "text";
	}

	public String delBranchMember() throws ServletException {

		String branchmemberid = clearChar(this.request.getParameter("branchmemberid"));
		Integer result = branchService.delBranchMember(Integer.parseInt(branchmemberid));
		
		JSONObject jo = new JSONObject();
		jo.put("branchmemberid", result);
		returnToClient(jo.toString());
		return "text";
	}
	
	public String setMaster() throws ServletException {
		
		String branchmemberid = clearChar(this.request.getParameter("branchmemberid"));
		branchService.setMaster(Integer.parseInt(branchmemberid));
		
		JSONObject jo = new JSONObject();
		jo.put("branchmemberid", branchmemberid );
		returnToClient(jo.toString());
		return "text";
	}
	
	public String reset() throws ServletException {
		
		String memberid = clearChar(this.request.getParameter("memberid"));
		String newpassword = clearChar(this.request.getParameter("newpassword"));
		
		String md5password = PasswordGenerator.getInstance().getMD5Str(newpassword);
		
		branchService.reset(Integer.parseInt(memberid), md5password);

		// 发短信
		TMember member = branchService.getMemberObjectById(Integer.parseInt(memberid));
		String msg = "您的IMS密码已重置为" + newpassword;
		TextHttpSender.getInstance().sendText(member.getMobile(), msg);
		
		JSONObject jo = new JSONObject();
		jo.put("branchmemberid", memberid );
		returnToClient(jo.toString());
		return "text";
	}
	
	/**
	 * 删除指定部门数据，专为外部接口使用
	 * @return
	 * @throws ServletException
	 */
	@Deprecated
	public String delExtra() throws ServletException {
		String result = null;
		AppSecret as = msgService.validAppIdAndSecret(clearChar(appId), clearChar(secret));
		
		if (as != null) {
			result = this.del();
		} else {
			JSONObject jo = new JSONObject();
			jo.put("code", 0);
			jo.put("text", AuthTips.WORNGAPPID.getText());
			result = jo.toString();
		}
		
		returnToClient(result);
		return "text";
	}
	
	public String del() throws ServletException {
		Integer id = Integer.parseInt(clearChar(this.request.getParameter("id")));
		JSONObject jo = new JSONObject();
		
		// 删除组织
		if (id < 101) {
		}
		// 删除部门
		else if (id < 10001) {
			String isLogic = PropertiesUtils.getStringByKey("del.logic");
			boolean ret = branchService.delBranch(id, isLogic, this.getOrganId());
			jo.put("status", ret);
		}
		// 删除人员
		else {
			//物理删除
			//branchService.delMember(id);
			//逻辑删除
			String ids = "["+id+"]";
			String ret = memberService.logicDelMemberByUserIds(ids);
			/*JSONObject j = JSONUtils.getInstance().stringToObj(ret);
			if (j.getInt("code") == 1) {
				jo.put("status", true);
			}*/
			jo.put("status", true);
		}
		
		jo.put("id", id);
		if (appId == null && secret == null) {
			returnToClient(jo.toString());
			return "text";
		} else {
			return jo.toString();
		}
	}

	public String mov() throws ServletException {
		
		Integer id = Integer.parseInt(clearChar(this.request.getParameter("id")));
		Integer pid = Integer.parseInt(clearChar(this.request.getParameter("pid")));
		Integer toid = Integer.parseInt(clearChar(this.request.getParameter("toid")));

		// 移动组织
		if (id < 101) {
		}
		// 移动部门
		else if (id < 10001) {
			id = branchService.movBranch(id, toid);
		}
		// 移动人员
		else {
			branchService.movMember(id, pid, toid);
		}
		JSONObject jo = new JSONObject();
		jo.put("id", id);
		returnToClient(jo.toString());
		return "text";
	}
	
	public String impcheck() throws ServletException {
		
		String jtext = clearChar(this.request.getParameter("jtext"));
		JSONArray ja = JSONArray.fromObject(jtext);
		
		JSONObject js = branchService.testUsers(ja);
		
		returnToClient(js.toString());
		
		return "text";
	}
	
	public String impsave() throws ServletException, FileNotFoundException, IOException {

		String jtext = clearChar(this.request.getParameter("jtext"));
		JSONArray ja = JSONArray.fromObject(jtext);

		branchService.saveimp(ja, this.getOrganId());
		
		String path = request.getSession().getServletContext().getRealPath("./upload/导入成功.xlsx");
		branchService.impexcel(ja, path);
		
		JSONObject js = new JSONObject();
		js.put("status", 0);
		js.put("succeed", ja.size());
		js.put("fail", 0);

		returnToClient(js.toString());

		return "text";
	}

	/*
	 * 取部门树
	 */
	public String getBranchTree() throws ServletException {
		
		String result = branchService.getBranchTree(this.getOrganId());
		returnToClient(result);
		
		return "text";
	}

	/**
	 * 取得部门+成员数据
	 * @return
	 * @throws ServletException
	 */
	public String getBranchTreeAndMember() throws ServletException {
		String result = branchService.getBranchTreeAndMember(clearChar(appId), this.getOrganId());
			
		returnToClient(result);
		
		return "text";
	}
	
	/**
	 * 获取组织架构及成员，专为外部接口提供
	 * @return
	 * @throws ServletException
	 */
	public String getBranchTreeAndMembers() throws ServletException {
		String result = null;
		AppSecret as = msgService.validAppIdAndSecret(clearChar(appId), clearChar(secret));
		
		if (as != null) {
			int oid = 0;
			if (!StringUtils.getInstance().isBlank(companyId)) {
				oid = Integer.parseInt(clearChar(companyId));
			}
			
			result = branchService.getBranchTreeAndMember(clearChar(appId), oid);
		} else {
			JSONObject jo = new JSONObject();
			jo.put("code", 0);
			jo.put("text", AuthTips.WORNGAPPID.getText());
			result = jo.toString();
		}
			
		returnToClient(result);
		
		return "text";
	}
	
	/**
	 * 取得指定部门的成员
	 * @returen
	 * @throws ServletException
	 */
	public String getBranchMember() throws ServletException {
		
		String result = branchService.getBranchMember(clearChar(branchId), clearChar(appId), this.getOrganId());
		
		returnToClient(result);
		return "text";
	}
	
	/**
	 * 取得指定部门的成员,专为外部接口提供
	 * @return
	 * @throws ServletException
	 */
	public String getBranchMembers() throws ServletException {
		String result = null;
		appId = clearChar(appId);
		secret = clearChar(secret);
		AppSecret as = msgService.validAppIdAndSecret(appId, secret);
		
		if (as != null) {
			int oid = 0;
			if (!StringUtils.getInstance().isBlank(companyId)) {
				oid = Integer.parseInt(clearChar(companyId));
			}
			result = branchService.getBranchMember(clearChar(branchId), appId, oid);
		} else {
			JSONObject jo = new JSONObject();
			jo.put("code", 0);
			jo.put("text", AuthTips.WORNGAPPID.getText());
			result = jo.toString();
		}
		returnToClient(result);
		return "text";
	}
	
	public String exportsBranch() throws ServletException, FileNotFoundException {
		int organId = getSessionUserOrganId();
		String realPath = request.getSession().getServletContext().getRealPath("/");  
		String downFileName = branchService.exportsBranch(organId, realPath);
		
		if (downFileName != null) {
			this.setFileName(downFileName);
			inputStream = new FileInputStream(new File(realPath + "exports/" + downFileName)); 
			return "down";
		} else {
			JSONObject jo = new JSONObject();
			jo.put("code", 0);
			jo.put("text", Tips.FAIL.getText());
			returnToClient(jo.toString());
			return "text";
		}
	}
	
	//文件下载
	public InputStream getInputStream() {
		return inputStream;
	}
	
	private BranchService branchService;
	private MessageService msgService;
	private MemberService memberService;
	private OrgService orgService;

	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}

	public void setMsgService(MessageService msgService) {
		this.msgService = msgService;
	}

	public void setBranchService(BranchService branchService) {
		this.branchService = branchService;
	}
	
	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}

	private String branchId;
	private String appId;
	private String secret;
	private String companyId;
	private String fileName;
	private InputStream inputStream;
	
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}
	
	
}
