package com.organ.service.member;

import javax.servlet.ServletException;

import com.organ.model.TMember;

/**
 * 用户逻辑处理接口
 * @author hao_dy
 * @since jdk1.7
 *
 */
public interface MemberService {

	/**
	 * 登陆验证
	 * @param name
	 * @param password
	 * @param organId 
	 * @param organ 
	 * @return
	 */
	public TMember searchSigleUser(String name, String password, int organId);

	/**
	 * 更新密码按账号
	 * @param userName
	 * @param newPwd
	 * @param organId 
	 * @return
	 */
	public boolean updateUserPwdForAccount(String account, String newPwd, int organId);
	
	/**
	 * 更新密码按手机号
	 * @param userName
	 * @param newPwd
	 * @return
	 */
	public boolean updateUserPwdForPhone(String phone, String newPwd);

	/**
	 * 获取单个成员
	 * @param account
	 * @return
	 */
	public String getOneOfMember(String account);

	/**
	 * 更新token
	 * @param userId
	 * @param token
	 * @return
	 */
	public int updateUserTokenForId(String userId, String token);

	/**
	 * 搜索用户按账号或拼音
	 * @param account
	 * @param organId 
	 * @return
	 */
	public String searchUser(String account, int organId);

	/**
	 * 验证旧密码
	 * @param account
	 * @param newPwd
	 * @param organId 
	 * @return
	 */
	public boolean valideOldPwd(String account, String newPwd, int organId);

	/**
	 * 保存短信验证码
	 * @param code
	 * @param code2 
	 */
	public void saveTextCode(String phone, String code);

	/**
	 * 获取短信验证码
	 * @param phone
	 * @return
	 */
	public String getTextCode(String phone);

	/**
	 * 更新个人设置web端
	 * @param account
	 * @param sex
	 * @param email
	 * @param phone
	 * @param sign
	 * @param sign2 
	 * @param phone2 
	 * @return
	 */
	public String updateMemberInfoForWeb(String userId, String position, String fullName, String sign);
	
	/**
	 * 更新个人设置app端
	 * @param userid
	 * @param email
	 * @param mobile
	 * @param phone
	 * @param address
	 * @return
	 */
	public String updateMemberForApp(String userid, String email, String mobile, String phone, String address);
	
	/**
	 * 更新密码
	 * @param userName
	 * @param newPwd
	 * @return
	 */
	public boolean updateUserPwd(String account, String newPwd);

	/**
	 * 根据token取成员
	 * @param token
	 * @return
	 */
	public TMember getMemberByToken(String token);

	/**
	 * 获取所有成员
	 * @param organId 
	 * @return
	 */
	public String getAllMemberInfo(int organId);

	/**
	 * 获取成员在线状态(1：在线，0：离线，3：手机在线，4繁忙)
	 * @param organId 
	 * @param userids 
	 * @return
	 */
	public String getAllMemberOnLineStatus(int organId, String userids);


	/**
	 * 统计成员账号个数
	 * @param organId 
	 * @param account
	 * @param userpwd
	 * @return
	 */
	public int countMember(int organId);

	/**
	 * 多账号查询成员
	 * @param mulMemberStr
	 * @param organId 
	 * @return
	 */
	public String getMultipleMemberForAccounts(String mulMemberStr, int organId);

	/**
	 * 根据成员账号获取成员id
	 * @param names
	 * @return
	 */
	public String getMemberIdsByAccount(String names);

	/**
	 * 检测 是否正在使用头像
	 * @return
	 * @throws ServletException
	 */
	public String isUsedPic(String userId, String picName);

	/**
	 * 根据单账号获取id
	 * @param account
	 * @param organId 
	 * @return
	 */
	public String getMemberIdForAccount(String account, int organId);

	/**
	 * 根据id获取多成员
	 * @param ids
	 * @return
	 */
	public String getMultipleMemberForIds(String ids);

	/**
	 * 根据id获取单个成员
	 * @param userId
	 * @return
	 */
	public String getMemberForId(String userId);

	/**
	 * 获取指定数量的用户id
	 * @param mapMax
	 * @param organId 
	 * @return
	 */
	public String getLimitMemberIds(String mapMax, int organId);

	/**
	 * 获取成员指定参数
	 * @param id
	 * @param ps
	 * @return
	 */
	public String getMemberParam(String id, String ps);

	/**
	 * 获取超级管理员账号
	 * @param account
	 * @param userpwd
	 * @param organId 
	 * @return
	 */
	public TMember getSuperAdmin(String account, String userpwd, int organId);
}
