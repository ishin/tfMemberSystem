package com.sealtalk.service.friend;

/**
 * 好友关系管理 
 * @since jdk1.7
 * @author hao_dy
 *
 */
public interface FriendService {
	/**
	 * 添加好友关系 
	 * @param account
	 * @param friend
	 * @param organId 
	 * @return
	 */
	public String addFriend(String account, String friend, int organId);

	/**
	 * 删除好友关系
	 * @param account
	 * @param friend
	 * @param organId 
	 * @return
	 */
	public String delFriend(String account, String friend, int organId);

	/**
	 * 获取联系人列表
	 * @param account
	 * @param organId 
	 * @return
	 */
	public String getMemberFriends(String account, int organId);

	/**
	 * 获取好友关系
	 * @param account
	 * @param friend
	 * @return
	 */
	public String getFriendsRelation(String userId, String friendId);

}
