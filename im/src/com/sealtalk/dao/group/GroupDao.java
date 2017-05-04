package com.sealtalk.dao.group;

import java.util.ArrayList;
import java.util.List;

import com.sealtalk.model.TGroup;
import com.sealtalk.model.TGroupMember;

/**
 * 群组dao
 * @author hao_dy
 * @since jdk1.7
 * @date 2017/01/12
 */
public interface GroupDao {

	/**
	 * 创建群组
	 * @param userid
	 * @param tempIds
	 * @param groupname
	 * @param i 
	 */
	public int createGroup(int userid, String code, String groupname, int i);
	
	/**
	 * 获取群组数量
	 * @return
	 */
	public int countGroup();

	/**
	 * 查询群组
	 * @param userid
	 * @param code
	 * @return
	 */
	public TGroup getGroupForIdAndCode(int userid, String code);

	/**
	 * 删除群组
	 * @param groupId
	 */
	public void removeGroup(TGroup tg);

	/**
	 * 按id查找群组
	 * @param groupId
	 * @return
	 */
	public TGroup getGroupForId(int groupId);
	
	/**
	 * 获取群列表成员返回id
	 * @param groups
	 */
	public List<TGroup> getGroupList(Integer[] groups);
	

	/**
	 * 按id删除群组
	 * @param groupId
	 */
	public int removeGroupForGroupId(String groupId);

	/**
	 * 转移群组
	 * @param userIdInt
	 * @param groupIdInt
	 * @return
	 */
	public int transferGroup(int userIdInt, int groupIdInt);

	/**
	 * 修改群名称
	 * @param groupIdInt
	 * @param groupName
	 * @return
	 */
	public int changeGroupName(int groupIdInt, String groupName);

	/**
	 * 更新群人数
	 * @param groupId
	 * @param memberVolume 
	 * @return
	 */
	public int updateGroupMemberNum(int groupId, int memberVolume);

}
