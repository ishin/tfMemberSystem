package com.organ.service.adm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.organ.dao.adm.MemberRoleDao;
import com.organ.dao.adm.PrivDao;
import com.organ.dao.adm.RoleDao;
import com.organ.dao.adm.RolePrivDao;
import com.organ.model.TMemberRole;
import com.organ.model.TPriv;
import com.organ.model.TRole;
import com.organ.model.TRolePriv;
import com.organ.service.adm.PrivService;

public class PrivServiceImpl implements PrivService {

	RoleDao roleDao;
	PrivDao privDao;
	RolePrivDao rolePrivDao;
	MemberRoleDao memberRoleDao;
	
	public RoleDao getRoleDao() {
		return roleDao;
	}
	public void setRoleDao(RoleDao roleDao) {
		this.roleDao = roleDao;
	}
	public PrivDao getPrivDao() {
		return privDao;
	}
	public void setPrivDao(PrivDao privDao) {
		this.privDao = privDao;
	}
	public MemberRoleDao getMemberRoleDao() {
		return memberRoleDao;
	}
	public void setMemberRoleDao(MemberRoleDao memberRoleDao) {
		this.memberRoleDao = memberRoleDao;
	}

	public RolePrivDao getRolePrivDao() {
		return rolePrivDao;
	}
	public void setRolePrivDao(RolePrivDao rolePrivDao) {
		this.rolePrivDao = rolePrivDao;
	}

	@Override
	public List getRoleList() {
		
		return roleDao.find("from TRole order by listorder desc");
	}

	@Override
	public int getMemberCountByRole(Integer roleId) {
		
		return roleDao.getMemberCountByRole(roleId);
	}

	@Override
	public List getMemberByRole(Integer roleId, Integer page, Integer itemsperpage) {
		
		return roleDao.getMemberByRole(roleId, page, itemsperpage);
	}

	@Override
	public void delMemberRole(Integer id) {

		memberRoleDao.deleteById(id);
	}

	@Override
	public List getPrivByRole(Integer roleId) {
		
		return roleDao.getPrivByRole(roleId);
	}

	@Override
	public Integer saveRole(Integer roleId, String roleName, String privs) {

		TRole role = roleDao.get(roleId);
		if (role == null) {
			role = new TRole();
			role.setName(roleName);
			role.setListorder(roleDao.getMax("listorder", "from TRole") + 1);
			roleDao.save(role);
		}
		
		rolePrivDao.delete("delete from TRolePriv where roleId = " + role.getId());
		
		String[] pa = privs.split(",");
		Integer i = pa.length;
		while(i-- > 0) {
			if (!"".equals(pa[i])) {
				TRolePriv rolePriv = new TRolePriv();
				rolePriv.setRoleId(role.getId());
				rolePriv.setPrivId(Integer.parseInt(pa[i]));
				rolePrivDao.save(rolePriv);
			}
		}
		
		return role.getId();
	}
	@Override
	public void delRole(Integer roleId) {
		
		rolePrivDao.delete("delete from TRolePriv where roleId = " + roleId);
		memberRoleDao.delete("delete from TMemberRole where roleId = " + roleId);
		roleDao.deleteById(roleId);
	}
	@Override
	public void saveRoleMember(Integer roleId, String memberlist) {
		
		memberRoleDao.delete("delete from TMemberRole where roleId = " + roleId);
		
		String[] ms = memberlist.split(",");
		Integer i = ms.length;
		while (i-- > 0) {
			if (!"".equals(ms[i])) {
				TMemberRole mr = new TMemberRole();
				mr.setMemberId(Integer.parseInt(ms[i]));
				mr.setRoleId(roleId);
				mr.setListorder(0);
				memberRoleDao.save(mr);
			}
		}
	}
	@Override
	public String getPrivStringByMember(Integer memberId) {
		
		List list = roleDao.getPrivByMember(memberId);
		Iterator it = list.iterator();
		
		StringBuffer privs = new StringBuffer(",");
		while(it.hasNext()) {
			Object[] o = (Object[])it.next();
			privs.append(o[4] + ",");
		}
		
		return privs.toString();
	}
		
	/**
	 * 根据用户id获取权限
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List getRoleIdForId(int id) {
		TMemberRole tmList = memberRoleDao.getRoleForId(id);
		/*List<TPriv> privList = privDao.getAllPriv();
		
		for(int i = 0; i < privList.size();i++) {
			System.out.println(privList.get(i).getId());
		}
		
		//处理父级权限标识返回
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		if (privList != null) {
			
			for(int i = 0; i < privList.size(); i++) {
				List<String> list = new ArrayList<String>();
				TPriv t = privList.get(i);
				
				map.put(t.getId() + "", list);
			}
		}
		
		for(Map.Entry<String, List<String>> m: map.entrySet()) {
			System.out.println(m.getKey() + ": " + m.getValue().toString());
		}
		*/
		ArrayList<String> priList = new ArrayList<String>();	
		
		priList.add("rsglck");
		priList.add("rsgltj");
		priList.add("rsgljcxx");
		priList.add("rsglxgmm");
		priList.add("rsglyd");
		priList.add("rsglsc");
		priList.add("bmglck");
		priList.add("bmgltj");
		priList.add("bmglxg");
		priList.add("bmglyd");
		priList.add("bmglsc");
		priList.add("zzxxglck");
		priList.add("zzxxglxg");
		priList.add("qzglck");
		priList.add("qzgljs");
		priList.add("qzglxg");
		priList.add("qxglck");
		priList.add("qxgltj");
		priList.add("qxglxg");
		priList.add("qxglsc");
	
		if (tmList != null) {
			int roleId = tmList.getRoleId();
			List<Object[]> list = roleDao.getPrivilegeById(roleId);
			
			if (list != null) {
				int len = list.size();
				boolean b = false;
				
				for(int i = 0; i < len; i++) {
					Object[] o = list.get(i);
					if (priList.contains(o[1])) {
						list.add(new Object[]{1,"htgl"});
						break;
					}
					//System.out.println(o[0] + "_" + o[1]);
				}
			} 
			
			return list;
		}
		
		return null;
	}
	
}
