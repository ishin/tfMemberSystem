package com.organ.service.adm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.organ.model.TBranch;
import com.organ.model.TBranchMember;
import com.organ.model.TMember;
import com.organ.model.TMemberRole;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public interface BranchService {


	/*
	 * 取部门树
	 * by alopex
	 */
	public String getOrganTree(Integer organId);
	public String getOrganOnlyTree(Integer organId);
	
	/*
	 * 取部门通过部门id
	 */
	public String getBranchById(Integer branchId);

	/*
	 * 取部门对象通过部门id
	 */
	public TBranch getBranchObjectById(Integer branchId);

	/*
	 * 取人员通过人员id
	 */
	public String getMemberById(Integer memberId);
	public List getMemberBranchById(Integer memberId);
	public TMember getMemberByAccount(String account, int organId);
	public TBranch getBranchByName(String name, int organId);	

	/*
	 * 取人员对象通过人员id
	 */
	public TMember getMemberObjectById(Integer memberId);
	/*
	 * 取字典
	 */
	public String getRole(Integer integer);
	public String getSex();
	public String getPosition(Integer integer);
	
	/*
	 * 取部门人员通过部门人员id
	 */
	public TBranchMember getBranchMemberById(Integer branchMemberId);

	public TBranchMember getBranchMemberByBranchPosition(Integer branchId, Integer positionId);	
	public TBranchMember getBranchMemberByBranchMember(Integer branchId, Integer memberId);
	
	/*
	 * 取人员角色通过人员id
	 */
	public TMemberRole getMemberRoleByMemberId(Integer memberId);
	
	public Integer saveBranch(TBranch branch);
	public Integer saveMember(TMember member);
	public Integer saveBranchMember(TBranchMember branchMember);
	public Integer saveMemberRole(TMemberRole memberRole);
	
	public Integer delBranchMember(Integer branchMemberId);
	public void setMaster(Integer branchMemberId);
	public void reset(Integer memberId, String password);
	
	public void delMember(Integer memberid);
	public boolean delBranch(Integer branchId, String isLogic, Integer organId);
	public void movMember(Integer memberId, Integer pId, Integer toId);
	public Integer movBranch(Integer branchId, Integer toId);
	
	public JSONObject testUsers(JSONArray ja);
	public void saveimp(JSONArray ja, Integer organId);
	public void impexcel(JSONArray ja, String path) throws FileNotFoundException, IOException;

	/**
	 * 获取部门数据
	 * @param integer 
	 * @return
	 */
	public String getBranchTree(Integer integer);

	/**
	 * 获取部门+成员 数据
	 * @param appId 
	 * @return
	 */
	public String getBranchTreeAndMember(String appId, Integer organId);

	/**
	 * 取部门下的成员
	 * @param branchId
	 * @return
	 */
	public String getBranchMember(String branchId, String appId, Integer organId);
	
	/**
	 * 获取部门成员
	 * @param ids
	 * @return
	 */
	public String getBranchMemberByMemberIds(String ids);
	
	/**
	 * 获取成员
	 * @param mobile  手机 
	 * @param telPhone  电话 
	 * @return
	 */
	public TMember getMemberByMobile(String mobile);
	
	public TMember getMemberByEmail(String parameter);
	public String getSuperMember(int organId);
	public boolean getMasterMemberById(int memberId);
	public String getMembersByOrgan(int organId);
	
}
