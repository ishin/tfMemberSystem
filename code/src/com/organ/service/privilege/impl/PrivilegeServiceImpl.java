package com.organ.service.privilege.impl;

import com.organ.dao.member.MemberDao;
import com.organ.dao.privilege.MemberRoleDao;
import com.organ.dao.privilege.PrivilegeDao;
import com.organ.dao.privilege.RolePrivilegeDao;
import com.organ.model.SessionPrivilege;
import com.organ.model.TMemberRole;
import com.organ.service.privilege.PrivilegeService;

public class PrivilegeServiceImpl implements PrivilegeService {

	@Override
	public SessionPrivilege setPrivilege(int id) {
		try {
			//TMemberRole tmr = memberRoleDao.getMemberRole(id);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private MemberDao memberDao;					//成员
	private MemberRoleDao memberRoleDao;			//成员角色关系
	private PrivilegeDao privilegeDao;				//权限
	private RolePrivilegeDao rolePrivilegeDao;		//角色权限关系
	
	public MemberDao getMemberDao() {
		return memberDao;
	}

	public void setMemberDao(MemberDao memberDao) {
		this.memberDao = memberDao;
	}

	public MemberRoleDao getMemberRoleDao() {
		return memberRoleDao;
	}

	public void setMemberRoleDao(MemberRoleDao memberRoleDao) {
		this.memberRoleDao = memberRoleDao;
	}

	public RolePrivilegeDao getRolePrivilegeDao() {
		return rolePrivilegeDao;
	}

	public void setRolePrivilegeDao(RolePrivilegeDao rolePrivilegeDao) {
		this.rolePrivilegeDao = rolePrivilegeDao;
	}

	public PrivilegeDao getPrivilegeDao() {
		return privilegeDao;
	}

	public void setPrivilegeDao(PrivilegeDao privilegeDao) {
		this.privilegeDao = privilegeDao;
	}

}
