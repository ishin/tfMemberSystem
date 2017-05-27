package com.sealtalk.dao.group.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

import com.sealtalk.common.BaseDao;
import com.sealtalk.dao.group.GroupMemberDao;
import com.sealtalk.model.TGroup;
import com.sealtalk.model.TGroupMember;
import com.sealtalk.utils.LogUtils;

/**
 * @功能  群、成员关第管理层
 * @author hao_dy
 * @date 2017/01/04
 * @since jdk1.7
 */
public class GroupMemberDaoImpl extends BaseDao<TGroupMember, Long> implements GroupMemberDao {

	private static final Logger logger = LogManager.getLogger(GroupMemberDaoImpl.class);
	
	@Override
	public void saveGroupMemeber(ArrayList<TGroupMember> gmList) {
		try {
			save(gmList);
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TGroupMember> getTGroupMemberList(int groupId) {
		try {
			
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.eq("groupId", groupId));
			
			List list = ctr.list();
			
			if (list.size() > 0) {
				return list;
			}
			
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}	
		
		return null;
	}

	@Override
	public void removeGroupMemeber(String userIds, int groupId) {
		try {
			String sql = (new StringBuilder("delete TGroupMember where groupId=").append(groupId).append(" and memberId in(").append(userIds).append(")")).toString();
			logger.info("removeGroupMember sql: " + sql);
			delete(sql);
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
	}

	@Override
	public int removeGroupMember(int groupId) {
		try {
			String sql = "delete TGroupMember t where t.groupId=" + groupId;
			int result = delete(sql);
			return result;
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			throw new HibernateException(e);
		}
	}

	@Override
	public int getGroupMemberCountForGoupId(String groupId) {
		try {
			int count = count("from TGroupMember where groupId=" + groupId);
			return count;
		} catch(Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int transferGroup(int userIdInt, int groupIdInt, Integer id) {
		try {
			String hql = "update TGroupMember t set t.isCreator=0 where t.id=" + id;
			update(hql);
			
			hql = "update TGroupMember t set t.isCreator=1 where t.groupId=" + groupIdInt + " and t.memberId=" + userIdInt;
			
			int result = update(hql);
			
			return result;
		} catch(Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TGroupMember getGroupMemberCreator(int groupId) {
		String hql = (new StringBuilder("from TGroupMember tm where tm.groupId=").append(groupId).append(" and tm.isCreator=1")).toString();
		
		try {
			List<TGroupMember> list = getSession().createQuery(hql).list();
			
			if (list.size() > 0) {
				return list.get(0);
			}
			
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}	
		
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TGroupMember> listGroupMembers(int groupId) {
		try {
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.eq("groupId", groupId));
			
			List<TGroupMember> list = ctr.list();
			
			if (list.size() > 0) {
				return list;
			}
			
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TGroupMember> getGroupMemberForUserId(int userId) {
		try {
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.eq("memberId", userId));
			
			List<TGroupMember> list = ctr.list();
			
			if (list.size() > 0) {
				return list;
			}
			
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public void delGroupMemberForMemberIdsAndGroupId(int groupIdInt, String needDelIdsStr) {
		try {
			String hql = "delete TGroupMember where groupId=" + groupIdInt + " and memberId in (" + needDelIdsStr + ")";
			delete(hql);
		} catch(Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TGroupMember> getGroupMemberByGroupIds(Integer[] groupIds) {
		try {
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.in("groupId", groupIds));
			ctr.add(Restrictions.eq("isDel", "1"));
			
			List<TGroupMember> list = ctr.list();
			
			if (list.size() > 0) {
				return list;
			}
			
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public int deleteRelationByIds(String ids, String isLogic) {
		try {
			if (isLogic.equals("1")) {
				String hql = (new StringBuilder("update TGroupMember set isDel='0',isCreator='0' where memberId in (").append(ids).append(")")).toString();
				return update(hql);
			} else {
				String hql = (new StringBuilder("delete from TGroupMember where memberId in (").append(ids).append(")")).toString();
				return delete(hql);
			}
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TGroupMember getGroupMemberById(Integer groupMemberId) {
		try {
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.eq("id", groupMemberId));
			
			List<TGroupMember> list = ctr.list();
			
			if (list.size() > 0) {
				return list.get(0);
			}
			
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}	
		
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TGroupMember> getGroupMemberByMembIds(Integer[] ids) {
		try {
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.in("memberId", ids));
			ctr.add(Restrictions.eq("isDel", "1"));
			
			List<TGroupMember> list = ctr.list();
			
			if (list.size() > 0) {
				return list;
			}
			
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}	
		
		return null;
	}

	@Override
	public void updateGroupMember(TGroupMember tgm) {
		try {
			update(tgm);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public TGroupMember judgeGroupCreate(Integer userIdInt, Integer groupIdInt) {
		try {
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.eq("memberId", userIdInt));
			ctr.add(Restrictions.eq("groupId", groupIdInt));
			ctr.add(Restrictions.eq("isCreator", "1"));
			ctr.add(Restrictions.eq("isDel", "1"));
			
			List<TGroupMember> list = ctr.list();
			
			if (list.size() > 0) {
				return list.get(0);
			}
			
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}	
		
		return null;
	}

}
