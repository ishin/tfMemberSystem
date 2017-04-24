package com.sealtalk.dao.group;

import java.util.ArrayList;
import java.util.List;

import com.sealtalk.model.TGroup;
import com.sealtalk.model.TGroupMember;
import com.sealtalk.model.TMember;

/**
 * 群组成员关系
 * @author hao_dy
 * @since jdk1.7
 * @date 2017/01/12
 */
public interface GroupMemberDao {

	/**
	 * 保存群组成员关系
	 * @param idsArr
	 */
	public void saveGroupMemeber(ArrayList<TGroupMember> idsArr);

	/**
	 * 获取群组成员
	 * @param groupId
	 * @return
	 */
	public List<TGroupMember> getTGroupMemberList(int groupId);

	/**
	 * 退出群组
	 * @param userIdsInt
	 * @param groupIdInt
	 */
	public void removeGroupMemeber(String userIdsInt, int groupIdInt);

	/**
	 * 删除群成员
	 * @param groupId
	 */
	public int removeGroupMember(int groupId);

	/**
	 * 获取特定群的群人数
	 * @param groupId
	 * @return
	 */
	public int getGroupMemberCountForGoupId(String groupId);

	/**
	 * 转移群主
	 * @param userIdInt		要转移的id
	 * @param groupIdInt	群id
	 * @param integer 		原群主关系记录id
	 * @return
	 */
	public int transferGroup(int userIdInt, int groupIdInt, Integer integer);

	/**
	 * 获取群主记录
	 * @param groupId
	 * @return
	 */
	public TGroupMember getGroupMemberCreator(int groupId);

	/**
	 * 获取群成员
	 * @param groupId
	 * @return
	 */
	public List<TGroupMember> listGroupMembers(int groupId);

	/**
	 * 依据用户id获取群成员关系记录
	 * @param userId
	 * @return
	 */
	public List<TGroupMember> getGroupMemberForUserId(int userId);

	/**
	 * 依据组id，及成员id删除成员关系
	 * @param groupIdInt
	 * @param needDelStr
	 */
	public void delGroupMemberForMemberIdsAndGroupId(int groupIdInt,
			String needDelStr);

	/**
	 * 根据组id获取群成员
	 * @param groupIds
	 * @return
	 */
	public List<TGroupMember> getGroupMemberByGroupIds(Integer[] groupIds);

}
