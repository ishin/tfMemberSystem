/**
 * 
 */
package com.organ.action.adm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.organ.common.AuthTips;
import com.organ.common.BaseAction;
import com.organ.model.AppSecret;
import com.organ.model.TBranch;
import com.organ.model.TBranchMember;
import com.organ.model.TMember;
import com.organ.model.TMemberRole;
import com.organ.service.adm.BranchService;
import com.organ.service.msg.MessageService;
import com.organ.utils.PasswordGenerator;
import com.organ.utils.PinyinGenerator;
import com.organ.utils.StringUtils;
import com.organ.utils.TextHttpSender;
import com.organ.utils.TimeGenerator;

/**
 * @author alopex
 *
 */
	
public class BranchAction extends BaseAction {

	private static final long serialVersionUID = 1L;
		
	/*
	 * 取部门树
	 * by alopex
	 */
	public String getOrganTree() throws ServletException {
		
		String result = branchService.getOrganTree(this.getOrganId());
		returnToClient(result);
		
		return "text";
	}
	
	/*
	 * 取部门树
	 * by alopex
	 */
	public String getOrganOnlyTree() throws ServletException {
		
		String result = branchService.getOrganOnlyTree(this.getOrganId());
		returnToClient(result);
		
		return "text";
	}
	
	/*
	 * 取部门通过部门id
	 * by alopex
	 */
	public String getBranchById() throws ServletException {
		
		String branchId = this.request.getParameter("id");
		
		String result = branchService.getBranchById(Integer.valueOf(branchId));
		returnToClient(result);
		
		return "text";
	}
	
	/*
	 * 取人员通过人员id
	 * by alopex
	 */
	public String getMemberById() throws ServletException {
		
		String memberId = this.request.getParameter("id");
		
		String result = branchService.getMemberById(Integer.valueOf(memberId));
		returnToClient(result);
		
		return "text";
	}

	/*
	 * 取部门人员通过id
	 * by alopex
	 */
	public String getBranchMemberById() throws ServletException {
		
		String branchMemberId = this.request.getParameter("branchmemberid");
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
		
		String memberId = this.request.getParameter("memberid");
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
	public String saveBranchExtra() throws ServletException {
		String result = null;
		AppSecret as = msgService.validAppIdAndSecret(appId, secret);
		
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
		String id = this.request.getParameter("branchid");
		int organId = getSessionUserOrganId();
		if (id != null) {
			branch = branchService.getBranchObjectById(Integer.parseInt(id));
			if (!branch.getName().equalsIgnoreCase(this.request.getParameter("branchname"))) {
				if (branchService.getBranchByName(this.request.getParameter("branchname"), organId) != null) {
					JSONObject jo = new JSONObject();
					jo.put("branchid", 0);
					returnToClient(jo.toString());
					return "text";
				}
			}
		}
		else {
			if (branchService.getBranchByName(this.request.getParameter("branchname"), organId) != null) {
				JSONObject jo = new JSONObject();
				jo.put("branchid", 0);
				returnToClient(jo.toString());
				return "text";
			}
			branch = new TBranch();
			branch.setListorder(0);
		}
		if (this.request.getParameter("branchaddress") != null)
			branch.setAddress(this.request.getParameter("branchaddress"));
		if (this.request.getParameter("branchfax") != null)
			branch.setFax(this.request.getParameter("branchfax"));
		if (this.request.getParameter("branchintro") != null)
			branch.setIntro(this.request.getParameter("branchintro"));
		if (this.request.getParameter("branchmanagerid") != null)
			branch.setManagerId(Integer.parseInt(this.request.getParameter("branchmanagerid")));
		if (this.request.getParameter("branchname") != null)
			branch.setName(this.request.getParameter("branchname"));
		if (this.request.getParameter("branchparentid") != null)
			branch.setParentId(Integer.parseInt(this.request.getParameter("branchparentid")));
		if (this.request.getParameter("branchtelephone") != null)
			branch.setTelephone(this.request.getParameter("branchtelephone"));
		if (this.request.getParameter("branchwebsite") != null)
			branch.setWebsite(this.request.getParameter("branchwebsite"));

		branch.setOrganId(this.getOrganId());
		
		Integer branchId = branchService.saveBranch(branch);
		
		JSONObject jo = new JSONObject();
		jo.put("branchid", branchId);
		
		if (appId == null && secret == null) {
			returnToClient(jo.toString());
		}

		return "text";
	}
	
	
	public String saveMember() throws ServletException {
		TMember member = null;
		String id = this.request.getParameter("memberid");
		int organId = getSessionUserOrganId();
		boolean sms = false;
		JSONObject jo = new JSONObject();
		
		String email = this.request.getParameter("memberemail");
		
		if (id != null && !"".equals(id)) {
			member = branchService.getMemberObjectById(Integer.parseInt(id));
			if (!member.getAccount().equalsIgnoreCase(this.request.getParameter("memberaccount"))) {
				if (branchService.getMemberByAccount(this.request.getParameter("memberaccount"), organId) != null) {
					JSONObject jo1 = new JSONObject();
					jo.put("memberid", 0);
					returnToClient(jo1.toString());
					return "text";
				}
			}
			if (!member.getMobile().equalsIgnoreCase(this.request.getParameter("membermobile")) ||
					!member.getTelephone().equalsIgnoreCase(this.request.getParameter("membertelephone"))) {
				if (branchService.getMemberByMobile(this.request.getParameter("membermobile"), 
						this.request.getParameter("membertelephone")) != null) {
					JSONObject jo1 = new JSONObject();
					jo.put("memberid", -1);
					returnToClient(jo.toString());
					return "text";
				}
			}
			if (!member.getEmail().equalsIgnoreCase(this.request.getParameter("memberemail"))) {
				if (branchService.getMemberByEmail(this.request.getParameter("memberemail")) != null) {
					JSONObject jo1 = new JSONObject();
					jo.put("memberid", -2);
					returnToClient(jo.toString());
					return "text";
				}
			}
		} else {
			if (branchService.getMemberByAccount(this.request.getParameter("memberaccount"), organId) != null) {
				JSONObject jo1 = new JSONObject();
				jo.put("memberid", 0);
				returnToClient(jo1.toString());
				return "text";
			}
			if (branchService.getMemberByMobile(this.request.getParameter("membermobile"), 
					this.request.getParameter("membertelephone")) != null) {
				JSONObject jo1 = new JSONObject();
				jo.put("memberid", -1);
				returnToClient(jo1.toString());
				return "text";
			}
			if (!StringUtils.getInstance().isBlank(email) && branchService.getMemberByEmail(email) != null) {
				JSONObject jo1 = new JSONObject();
				jo.put("memberid", -2);
				returnToClient(jo1.toString());
				return "text";
			}
			member = new TMember();
			member.setGroupmax(0);
			member.setGroupuse(0);
			member.setPassword(PasswordGenerator.getInstance().getMD5Str("111111"));
			sms = true;
		}
		if (this.request.getParameter("memberaccount") != null)
			member.setAccount(this.request.getParameter("memberaccount"));
		if (this.request.getParameter("memberaddress") != null)
			member.setAddress(this.request.getParameter("memberaddress"));
		if (this.request.getParameter("memberbirthday") != null) {
			String bd = this.request.getParameter("memberbirthday");
			if (bd.length() == 10) {
				member.setBirthday(bd.substring(0,4) + bd.substring(5,7) + bd.substring(8,10));
			}
		}
		if (this.request.getParameter("memberemail") != null)
			member.setEmail(this.request.getParameter("memberemail"));
		if (this.request.getParameter("memberfullname") != null) {
			member.setFullname(this.request.getParameter("memberfullname"));
			member.setPinyin(PinyinGenerator.getPinYinHeadChar(this.request.getParameter("memberfullname")));
			member.setAllpinyin(PinyinGenerator.getPinYin(this.request.getParameter("memberfullname")));
		}
		if (this.request.getParameter("memberintro") != null)
			member.setIntro(this.request.getParameter("memberintro"));
		if (this.request.getParameter("membermobile") != null)
			member.setMobile(this.request.getParameter("membermobile"));
		if (this.request.getParameter("membersex") != null)
			member.setSex(this.request.getParameter("membersex"));
		if (this.request.getParameter("membertelephone") != null)
			member.setTelephone(this.request.getParameter("membertelephone"));
		if (this.request.getParameter("memberworkno") != null)
			member.setWorkno(this.request.getParameter("memberworkno"));
		
		member.setOrganId(this.getOrganId());

		long now = TimeGenerator.getInstance().getUnixTime();
		member.setCreatetokendate(Integer.valueOf(String.valueOf(now)));
		
		Integer memberId = branchService.saveMember(member);

		//部门职务
		TBranchMember branchMember = null;
		String branchmemberid = this.request.getParameter("branchmemberid");
		if ( branchmemberid != null && !"".equals(branchmemberid)) {
			branchMember = branchService.getBranchMemberById(Integer.parseInt(branchmemberid));
		}
		else {
			branchMember = new TBranchMember();
			branchMember.setListorder(0);
			branchMember.setIsMaster("1");
		}
		branchMember.setMemberId(memberId);
		String memberbranchid = this.request.getParameter("memberbranchid");
		if ( memberbranchid!= null && !"".equals(memberbranchid)) {
			branchMember.setBranchId(Integer.parseInt(memberbranchid));
		}
		else {
			branchMember.setBranchId(0);
		}
		String memberpositionid = this.request.getParameter("memberpositionid");
		if ( memberpositionid != null && !"".equals(memberpositionid)) {
			branchMember.setPositionId(Integer.parseInt(memberpositionid));
		}
		else {
			branchMember.setPositionId(0);
		}
		branchService.saveBranchMember(branchMember);
		
		//人员角色
		String membberroleid = this.request.getParameter("memberroleid");
		if ( membberroleid != null && !"".equals(membberroleid)) {
			TMemberRole memberRole = null;
			memberRole = branchService.getMemberRoleByMemberId(memberId);
			if (memberRole == null) {
				memberRole = new TMemberRole();
				memberRole.setMemberId(memberId);
				memberRole.setListorder(0);
			}
			memberRole.setRoleId(Integer.parseInt(membberroleid));
			branchService.saveMemberRole(memberRole);
		}
		
		//发短信
		if (sms) {
			String msg = "您的IMS产品帐号" + member.getAccount() + ", 密码111111.";
			TextHttpSender.getInstance().sendText(member.getMobile(), msg);
		}
		
		jo.put("memberid", memberId);
		
		returnToClient(jo.toString());
		
		return "text";
	}
	
	public String savePosition() throws ServletException {

		String branchmemberid = this.request.getParameter("branchmemberid");
		String memberid = this.request.getParameter("memberid");
		String branchid = this.request.getParameter("branchid");
		String positionid = this.request.getParameter("positionid");

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

		String branchmemberid = this.request.getParameter("branchmemberid");
		Integer result = branchService.delBranchMember(Integer.parseInt(branchmemberid));
		
		JSONObject jo = new JSONObject();
		jo.put("branchmemberid", result);
		returnToClient(jo.toString());
		return "text";
	}
	
	public String setMaster() throws ServletException {
		
		String branchmemberid = this.request.getParameter("branchmemberid");
		branchService.setMaster(Integer.parseInt(branchmemberid));
		
		JSONObject jo = new JSONObject();
		jo.put("branchmemberid", branchmemberid );
		returnToClient(jo.toString());
		return "text";
	}
	
	public String reset() throws ServletException {
		
		String memberid = this.request.getParameter("memberid");
		String newpassword = this.request.getParameter("newpassword");
		
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
	public String delExtra() throws ServletException {
		String result = null;
		AppSecret as = msgService.validAppIdAndSecret(appId, secret);
		
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
		
		Integer id = Integer.parseInt(this.request.getParameter("id"));
		Integer r = Integer.parseInt(this.request.getParameter("r"));
		
		// 删除组织
		if (id < 101) {
		}
		// 删除部门
		else if (id < 10001) {
			branchService.delBranch(id, r, this.getOrganId());
		}
		// 删除人员
		else {
			branchService.delMember(id);
		}
		
		JSONObject jo = new JSONObject();
		jo.put("id", id);
		if (appId == null && secret == null) {
			returnToClient(jo.toString());
		}
		
		return "text";
	}

	public String mov() throws ServletException {
		
		Integer id = Integer.parseInt(this.request.getParameter("id"));
		Integer pid = Integer.parseInt(this.request.getParameter("pid"));
		Integer toid = Integer.parseInt(this.request.getParameter("toid"));

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
		
		String jtext = this.request.getParameter("jtext");
		JSONArray ja = JSONArray.fromObject(jtext);
		
		JSONObject js = branchService.testUsers(ja);
		
		returnToClient(js.toString());
		
		return "text";
	}
	
	public String impsave() throws ServletException, FileNotFoundException, IOException {

		String jtext = this.request.getParameter("jtext");
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
		String result = branchService.getBranchTreeAndMember(appId, this.getOrganId());
			
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
		AppSecret as = msgService.validAppIdAndSecret(appId, secret);
		
		if (as != null) {
			int oid = 0;
			if (!StringUtils.getInstance().isBlank(companyId)) {
				oid = Integer.parseInt(companyId);
			}
			
			result = branchService.getBranchTreeAndMember(appId, oid);
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
		
		String result = branchService.getBranchMember(branchId, appId, this.getOrganId());
		
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
		AppSecret as = msgService.validAppIdAndSecret(appId, secret);
		
		if (as != null) {
			int oid = 0;
			if (!StringUtils.getInstance().isBlank(companyId)) {
				oid = Integer.parseInt(companyId);
			}
			result = branchService.getBranchMember(branchId, appId, oid);
		} else {
			JSONObject jo = new JSONObject();
			jo.put("code", 0);
			jo.put("text", AuthTips.WORNGAPPID.getText());
			result = jo.toString();
		}
		returnToClient(result);
		return "text";
	}
	
	private BranchService branchService;
	private MessageService msgService;

	public void setMsgService(MessageService msgService) {
		this.msgService = msgService;
	}

	public void setBranchService(BranchService branchService) {
		this.branchService = branchService;
	}
	
	private String branchId;
	private String appId;
	private String secret;
	private String companyId;
	
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
