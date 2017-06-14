package com.organ.dao.member;

import java.util.List;

import com.organ.common.IBaseDao;
import com.organ.model.TMember;

public interface MemberDao extends IBaseDao<TMember, Integer> {

	public List getMemberPosition(Integer memberId);
	public List getMemberRole(Integer memberId);
	public TMember getMemberByName(String name, Integer organId);
	
	/**
	 * 登陆验证
	 * @param name
	 * @param password
	 * @param organId 
	 * @param organId 
	 * @param organIdInt 
	 * @return
	 */
	public TMember searchSigleUser(String name, String password, int organId);

	/**
	 * 更新用户密码
	 * @param userName
	 * @param md5Pwd
	 * @param organId 
	 * @return
	 */
	public boolean updateUserPwdForAccount(String account, String md5Pwd, int organId);
	
	/**
	 * 更新用户密码
	 * @param userName
	 * @param md5Pwd
	 * @return
	 */
	public boolean updateUserPwdForPhone(String account, String md5Pwd);

	/**
	 *	获取单用户 
	 * @param id
	 * @return
	 */
	public Object[] getOneOfMember(int id);

	/**
	 * in 查询多个用户按账号
	 * @param mulMemberStr
	 * @param organId 
	 * @return
	 */
	public List<TMember> getMultipleMemberForAccounts(String[] mulMemberStr, int organId);

	/**
	 * 查询多个用户按id
	 * @param accounts
	 * @return
	 */
	public List<TMember> getMultipleMemberForIds(Integer[] ids);

	/**
	 * 更新用户token
	 * @param userId
	 * @param token
	 * @return
	 */
	public int updateUserTokenForId(String userId, String token);

	/**
	 * 查询单用户按id
	 * @param valueOf
	 * @return
	 */
	public TMember getMemberForId(int valueOf);

	/**
	 * 按账号查询id
	 * @param account
	 * @param organId 
	 * @return
	 */
	public int getMemberIdForAccount(String account, int organId);

	/**
	 * 按 账号或名称或全拼查找用户
	 * @param account
	 * @param organId 
	 * @param pinYin 
	 * @return
	 */
	public List searchUser(String account, int organId);

	/**
	 * 验证旧密码
	 * @param account
	 * @param oldPwd
	 * @param organId 
	 * @return
	 */
	public boolean valideOldPwd(String account, String oldPwd, int organId);

	/**
	 * 更新用户头像
	 * @param userId
	 * @param picName
	 * @return
	 */
	public int updateUserLogo(int userId, String picName);

	/**
	 * 查看头像是否在使用中
	 * @param userIdInt 
	 * @param userId
	 * @return
	 */
	public boolean isUsedPic(int userIdInt, String userId);

	/**
	 * 更新个人设置web端
	 * @param account
	 * @param fullname
	 * @param sex
	 * @param email
	 * @param phone
	 * @param sign2 
	 * @param phone2 
	 * @return
	 */
	public int updateMemeberInfoForWeb(int userId, String fullName, String sign);
	
	/**
	 * 更新个人设置app端
	 * @param userIdInt
	 * @param email
	 * @param mobile
	 * @param phone
	 * @param address
	 * @return
	 */
	public int updateMemeberInfoForApp(int userIdInt, String email, String mobile, String phone, String address);
	
	/**
	 * 更新用户密码
	 * @param userName
	 * @param md5Pwd
	 * @return
	 */
	public boolean updateUserPwd(String account, String md5Pwd);
	/**
	 *	获取单用户 
	 * @param account
	 * @param organId 
	 * @return
	 */
	public TMember getOneMember(String account, int organId);

	/**
	 * 获取指定数量的用户id
	 * @param mapMax
	 * @param organId 
	 * @return
	 */
	public List<TMember> getLimitMemberIds(int mapMax, int organId);
	
	/**
	 * 根据token取成员
	 * @param token
	 * @return
	 */
	public TMember getMemberByToken(String token);
	/**
	 * 获取全部成员
	 * @param organId 
	 * @return
	 */
	public List<TMember> getAllMemberInfo(int organId);
	
	/**
	 * oauth2登陆获取成员信息
	 * @param userId
	 * @param organId 
	 * @return
	 */
	public Object[] getAuthResouce(int userId);
	
	/**
	 * 获取成员总数
	 * @param organId 
	 * @return
	 */
	public int getMemberCount(int organId);
	
	/**
	 * 根据账号获取成员id
	 * @param targetNames
	 * @param organId 
	 * @return
	 */
	public List getMemberIdsByAccount(String[] targetNames, int organId);
	
	/**
	 * 获取成员指定参数
	 * @param ids
	 * @param pss
	 * @return
	 */
	public List getMemberParam(String ids, String[] pss);
	
	/**
	 * 获取超级管理员
	 * @param account
	 * @param account2
	 * @param organId 
	 * @return
	 */
	public TMember getSuperAdmin(String account, String account2, int organId);
	
	public TMember getMemberByMobile(String mobile);
	
	public TMember getMemberByEmail(String email);
	public TMember searchSigleUserByOrgan(String name, String password,
			int organId);
	public TMember getSuperMember(int organId);
	public int logicDelMemberByUserIds(String userids, String isLogic);
	public List<String> getNotDelIds(String userids, String isLogic);
	public TMember getMemberByWorkNo(String memberWorkNo, int organId);
	public List<TMember> getExportsMember(int organId);
} 

