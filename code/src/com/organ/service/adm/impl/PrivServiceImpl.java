package com.organ.service.adm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.organ.dao.adm.MemberRoleDao;
import com.organ.dao.adm.PrivDao;
import com.organ.dao.adm.RoleDao;
import com.organ.dao.adm.RolePrivDao;
import com.organ.model.AppSecret;
import com.organ.model.TMemberRole;
import com.organ.model.TPriv;
import com.organ.model.TRole;
import com.organ.model.TRoleAppSecret;
import com.organ.model.TRolePriv;
import com.organ.service.adm.PrivService;
import com.organ.utils.JSONUtils;

public class PrivServiceImpl implements PrivService {

	private RoleDao roleDao;
	private PrivDao privDao;
	private RolePrivDao rolePrivDao;
	private MemberRoleDao memberRoleDao;
	
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
		
		ArrayList<String> priList = new ArrayList<String>();	

		priList.add("qzglck");
		priList.add("qzgljs");
		priList.add("qzglxg");
	
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
				}
			} 
			
			return list;
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<JSONObject> getInitLoginPriv() {
		ArrayList<JSONObject> alj = new ArrayList<JSONObject>();

		List<TPriv> privList = privDao.find("from TPriv T where T.id between 15 and 53");

		if (privList != null) {
			int len = privList.size();
			
			for (int i = 0; i < len; i++) {
				JSONObject jo = new JSONObject();
				TPriv tp = privList.get(i);
				jo.put("privid", tp.getId());
				jo.put("priurl",tp.getUrl());
				alj.add(jo);
			}
			JSONObject jo = new JSONObject();
			jo.put("privid", 1);
			jo.put("priurl", "htgl");
			alj.add(jo);
		}
		
		return alj;
	}
	
	@Override
	public String getPrivByUrl(String[] strToArray) {
		try {
			List<TPriv> list = privDao.getPrivByUrl(strToArray);
			List<JSONObject> lj = new ArrayList<JSONObject>();
			
			if (list != null) {
				for(int i = 0; i < list.size(); i++) {
					lj.add(JSONUtils.getInstance().modelToJSONObj(list.get(i)));
				}
				return lj.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public String getRolePrivsByPrivs(String[] strToArray) {
		try {
			Integer[] p = new Integer[strToArray.length];
			
			for (int i = 0; i < strToArray.length; i++) {
				p[i] = Integer.parseInt(strToArray[i]);
			}
			
			List<TRolePriv> list = rolePrivDao.getRolePrivsByPrivs(p);
			List<JSONObject> lj = new ArrayList<JSONObject>();
			
			if (list != null) {
				for(int i = 0; i < list.size(); i++) {
					lj.add(JSONUtils.getInstance().modelToJSONObj(list.get(i)));
				}
				return lj.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public String getRolesForIds(String[] strToArray) {
		try {
			Integer[] p = new Integer[strToArray.length];
			
			for (int i = 0; i < strToArray.length; i++) {
				p[i] = Integer.parseInt(strToArray[i]);
			}
			
			List<TMemberRole> list = memberRoleDao.getRolesForIds(p);
			List<JSONObject> lj = new ArrayList<JSONObject>();
			
			if (list != null) {
				for(int i = 0; i < list.size(); i++) {
					lj.add(JSONUtils.getInstance().modelToJSONObj(list.get(i)));
				}
				return lj.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
