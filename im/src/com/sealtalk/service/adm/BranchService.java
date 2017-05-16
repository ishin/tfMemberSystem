package com.sealtalk.service.adm;

import com.sealtalk.model.TMember;

public interface BranchService {

	/**
	 * 获取部门数据
	 * @return
	 */
	public String getBranchTree(int organId);

	/**
	 * 获取部门+成员 数据
	 * @return
	 */
	public String getBranchTreeAndMember(int organId);

	/**
	 * 取部门下的成员
	 * @param branchId
	 * @return
	 */
	public String getBranchMember(String branchId, int organId);

	/**
	 * 获取职位
	 * @return
	 */
	public String getPosition(int organId);

	public TMember getMemberByAccount(String account, int organId);

	public int getOrganIdByOrganCode(String organCode);

	public String getMembersByOrgan(int organId);
	
}
