package com.organ.service.adm;

import java.util.List;

import net.sf.json.JSONObject;

import com.organ.model.TPriv;

public interface PrivService {

	List getRoleList();
	int getMemberCountByRole(Integer roleId);
	List getMemberByRole(Integer roleId, Integer page, Integer itemsperpage);
	void delMemberRole(Integer id);
	List getPrivByRole(Integer roleId);
	Integer saveRole(Integer roleId, String roleName, String privs);
	void delRole(Integer roleId);
	void saveRoleMember(Integer roleId, String memberlist);
	String getPrivStringByMember(Integer memberId);
	
	/**
	 * 根据用户Id获取角色
	 * @param id
	 * @return
	 */
	public List getRoleIdForId(int id);
	
	/**
	 * 获取初始化权限
	 * @return
	 */
	public List<JSONObject> getInitLoginPriv();
	
	/**
	 * 根据url获取权限
	 * @param strToArray
	 * @return
	 */
	public String getPrivByUrl(String[] strToArray);
	
	public String getRolePrivsByPrivs(String[] strToArray);
	
	public String getRolesForIds(String[] strToArray);
	
}
