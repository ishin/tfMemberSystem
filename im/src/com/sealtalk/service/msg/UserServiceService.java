package com.sealtalk.service.msg;

/**
 * 融云接口用户服务
 * @author hao_dy
 * @since jdk1.7
 * @date 2017/01/11
 *
 */
public interface UserServiceService {

	/**
	 * 获取用户token
	 * @param id
	 * @return
	 */
	public String getToken(String id);

	/**
	 * 刷新用户
	 * @param userid
	 * @return
	 */
	public String refreshUser(String userid);

	/**
	 * 检测用户在线
	 * @param userid
	 * @return
	 */
	public String checkOnline(String userid);
}
