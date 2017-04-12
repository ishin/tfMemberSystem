package com.organ.service.limit.impl;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.google.gson.JsonObject;
import com.organ.dao.adm.MemberRoleDao;
import com.organ.dao.adm.PrivDao;
import com.organ.dao.adm.RoleDao;
import com.organ.dao.adm.RolePrivDao;
import com.organ.dao.limit.LimitDao;
import com.organ.dao.limit.RoleAppSecretDao;
import com.organ.model.TRole;
import com.organ.model.TRoleAppSecret;
import com.organ.model.TRolePriv;
import com.organ.service.limit.LimitService;

/**
 * 实现接口
 * 
 * @author Lmy
 * 
 */
public class LimitServiceImpl implements LimitService {

	private LimitDao limitDao;
	RoleDao roleDao;
	PrivDao privDao;
	RolePrivDao rolePrivDao;
	RoleAppSecretDao roleappsecretDao;
	private MemberRoleDao memberRoleDao;
	public MemberRoleDao getMemberRoleDao() {
		return memberRoleDao;
	}

	public void setMemberRoleDao(MemberRoleDao memberRoleDao) {
		this.memberRoleDao = memberRoleDao;
	}

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

	public RolePrivDao getRolePrivDao() {
		return rolePrivDao;
	}

	public void setRolePrivDao(RolePrivDao rolePrivDao) {
		this.rolePrivDao = rolePrivDao;
	}

	public RoleAppSecretDao getRoleappsecretDao() {
		return roleappsecretDao;
	}

	public void setRoleappsecretDao(RoleAppSecretDao roleappsecretDao) {
		this.roleappsecretDao = roleappsecretDao;
	}

	public void setLimitDao(LimitDao limitDao) {
		this.limitDao = limitDao;
	}

	@Override
	public String AddLimit(int parentId, String name, String app) {
		// TODO Auto-generated method stub
		return limitDao.updatePriv(parentId, name, app) + "";
	}

	@Override
	public String DelLimit(int privId) {
		// TODO Auto-generated method stub
		return limitDao.DeletePriv(privId) + "";
	}

	@Override
	public String EditLimit(int priv_id, String pid, String name, String app) {
		// TODO Auto-generated method stub
		return limitDao.editPriv(priv_id, pid, name, app) + "";
	}

	@SuppressWarnings("unchecked")
	@Override
	public String searchPriv(String Name, int pagesize, int pageindex) {
		JSONArray ja = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		try {

			List privlist = limitDao.searchPriv(Name, pagesize, pageindex);
			int count = limitDao.getSearchCount(Name);
			if (privlist == null) {
				JSONObject jo = new JSONObject();

				jo.put("code", 0);
				jo.put("text", "权限名称为空");
			} else {
				for (int i = 0; i < privlist.size(); i++) {
					Object[] priv = (Object[]) privlist.get(i);
					JSONObject jo = new JSONObject();
					jo.put("id", isBlank(priv[0]));
					jo.put("parent_id", isBlank(priv[1]));
					jo.put("name", isBlank(priv[2]));
					jo.put("category", isBlank(priv[3]));
					jo.put("url", isBlank(priv[4]));
					jo.put("app", isBlank(priv[5]));
					jo.put("parent_name", isBlank(priv[6]));
					ja.add(jo);
				}
				jsonObject.put("count", count + "");
				jsonObject.put("content", ja);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonObject.toString();
	}

	private String isBlank(Object o) {
		return o == null ? "" : o + "";
	}

	@Override
	public int getCount() {
		try {
			int count = limitDao.getCount();
			return count;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List getLimitbyRole(Integer roleId, String appName) {
		// TODO Auto-generated method stub
		return limitDao.getLimitbyRole(roleId, appName);
	}
	
	@Override
	public String getRoleList(Integer appId) {
		JSONArray ja = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		try {
			List roles = limitDao.getRoleList(appId);
			if (roles == null) {
				JSONObject jo = new JSONObject();
				jo.put("code", 0);
				jo.put("text", "权限名称为空");
			} else {
				for (int i = 0; i < roles.size(); i++) {
					Object[] role = (Object[]) roles.get(i);
					JSONObject jo = new JSONObject();
					jo.put("id", isBlank(role[0]));
					jo.put("name", isBlank(role[1]));
					ja.add(jo);
					jsonObject.put("code", 1);
					jsonObject.put("content", ja);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonObject.toString();
	}

	@Override
	public String getPrivNamebytwo(String appName) {
		JSONArray ja = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		try {
			List names = limitDao.getPrivNamebytwo(appName);
			if (names == null) {
				JSONObject jo = new JSONObject();
				jo.put("code", 0);
				jo.put("text", "名称为空");
			} else {
				for (int i = 0; i < names.size(); i++) {
					Object[] name = (Object[]) names.get(i);
					JSONObject jo = new JSONObject();
					jo.put("id", isBlank(name[0]));
					jo.put("name", isBlank(name[1]));
					ja.add(jo);
					jsonObject.put("code", 1);
					jsonObject.put("content", ja);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonObject.toString();
	}

	@Override
	public String saveRolebyApp(Integer roleId, Integer appsecretId,
			String roleName, String privs) {
		TRole role = roleDao.get(roleId);
		if (role == null) {
			role = new TRole();

			role.setName(roleName);
			role.setListorder(roleDao.getMax("listorder", "from TRole") + 1);
			roleDao.save(role);
		}
		if (roleId == -1) {
			TRoleAppSecret roleAppSecret = new TRoleAppSecret();
			roleAppSecret.setAppsecretId(appsecretId);
			roleAppSecret.setRoleId(role.getId());
			roleappsecretDao.save(roleAppSecret);
		}

		rolePrivDao.delete("delete from TRolePriv where roleId = "
				+ role.getId());
		String[] pa = privs.split(",");
		Integer i = pa.length;
		while (i-- > 0) {
			if (!"".equals(pa[i])) {
				TRolePriv rolePriv = new TRolePriv();
				rolePriv.setRoleId(role.getId());
				rolePriv.setPrivId(Integer.parseInt(pa[i]));
				rolePrivDao.save(rolePriv);
			}
		}
		return role.getId() + "";
	}

	@Override
	public void delRole(Integer roleId) {
		rolePrivDao.delete("delete from TRolePriv where roleId = " + roleId);
		memberRoleDao.delete("delete from TMemberRole where roleId = " + roleId);
		roleappsecretDao.delete("delete from TRoleAppSecret where roleId = " + roleId);
		roleDao.deleteById(roleId);
	}


}
