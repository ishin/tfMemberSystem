package com.organ.service.member;

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
	 * @return
	 */
	public TMember searchSigleUser(String name, String password);

	/**
	 * 更新密码按账号
	 * @param userName
	 * @param newPwd
	 * @return
	 */
	public boolean updateUserPwdForAccount(String account, String newPwd);
	
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
	 * @return
	 */
	public String searchUser(String account);

	/**
	 * 验证旧密码
	 * @param account
	 * @param newPwd
	 * @return
	 */
	public boolean valideOldPwd(String account, String newPwd);

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
	 * @return
	 */
	public String getAllMemberInfo();

	/**
	 * 获取成员在线状态(1：在线，0：离线，3：手机在线，4繁忙)
	 * @param userids 
	 * @return
	 */
	public String getAllMemberOnLineStatus(String userids);


}
