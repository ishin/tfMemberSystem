/**
 * 
 */
package com.organ.dao.adm;

import java.util.List;

import com.organ.common.IBaseDao;
import com.organ.model.TBranch;
import com.organ.model.TBranchMember;
import com.organ.model.TMember;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author alopex
 *
 */
public interface BranchDao extends IBaseDao<TBranch, Integer> {
	
	/*
	 * 取部门关系树
	 * by alopex
	 */
	public List getOrgan(Integer organId);
	public List getBranch(Integer organId);
	public List getMember(Integer organId);

	/*
	 * 取字典
	 */
	public List getRole(Integer organId);
	public List getSex();
	public List getPosition(Integer organId);

	/*
	 * 取人员所在部门
	 */
	public List getBranchMember(Integer branchId);

	/*
	 * 取部门通过部门名称
	 */
	public TBranch getOneOfBranch(String name, int organId);
	
	/*
	 * 取子部门
	 */
	public List getChildren(Integer branchId);
	
	/*
	 * 导入
	 */
	public JSONObject testUsers(JSONArray ja);

	/*
	 * 取部门关系树
	 */
	public List getBranchTree(Integer organId);

	/**
	 * 取部门关系树与成员信息
	 * @param organId 
	 * @return
	 */
	public List getBrancTreeAndMember(Integer organId);

	/**
	 * 获取指定部门的成员
	 * @param branchId
	 * @return
	 */
	public List getBranchMember(String branchId, Integer organId);
	
	public List<TBranch> getBranchByMangerId(Integer[] ids);
	public int getNoGroupBranch(Integer organId);
	public List<TBranch> getAllBranch(int organId);
	
}
