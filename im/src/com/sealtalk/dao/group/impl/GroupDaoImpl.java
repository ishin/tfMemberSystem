package com.sealtalk.dao.group.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

import com.sealtalk.common.BaseDao;
import com.sealtalk.dao.group.GroupDao;
import com.sealtalk.model.TGroup;
import com.sealtalk.utils.LogUtils;
import com.sealtalk.utils.PropertiesUtils;
import com.sealtalk.utils.StringUtils;
import com.sealtalk.utils.TimeGenerator;

/**
 * @功能  群组数据管理层
 * @author hao_dy
 * @date 2017/01/04
 * @since jdk1.7
 */
public class GroupDaoImpl extends BaseDao<TGroup, Long> implements GroupDao {
	private static final Logger logger = LogManager.getLogger(GroupDaoImpl.class);

	@Override
	public int createGroup(int userId, String code, String groupname, int memberNum) {
		int id = -1;
		
		try {
			TGroup tg = new TGroup();
			
			int volume = StringUtils.getInstance().strToInt(PropertiesUtils.getStringByKey("group.volume"));
			int space = StringUtils.getInstance().strToInt(PropertiesUtils.getStringByKey("group.space"));
			int spaceUse = StringUtils.getInstance().strToInt(PropertiesUtils.getStringByKey("group.spaceuse"));
			int annexLong = StringUtils.getInstance().strToInt(PropertiesUtils.getStringByKey("group.annexlong"));
			
			volume = volume == -1 ? 0 : volume;
			space = space == -1 ? 0 : space;
			spaceUse = spaceUse == -1 ? 0 : spaceUse;
			annexLong = annexLong == -1 ? 0 : annexLong;
			
			tg.setCreatorId(userId);
			tg.setCode(code);
			tg.setName(groupname);
			tg.setCreatedate(TimeGenerator.getInstance().formatNow("yyyyMMdd"));
			tg.setVolume(volume);
			tg.setVolumeuse(memberNum);
			tg.setSpace(space);
			tg.setSpaceuse(spaceUse);
			tg.setAnnexlong(annexLong);
			tg.setNotice("");
			tg.setListorder(0);
			save(tg);
			
			id = tg.getId();
			
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		} 
		
		return id;
		
	}

	@Override
	public int countGroup() {
		int count = 0;
		
		try {
			count = count(" from TGroup");
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		
		return count;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TGroup getGroupForIdAndCode(int userid, String code) {
		try {
			
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.eq("id", userid));
			ctr.add(Restrictions.eq("code", code));
			
			List<TGroup> list = ctr.list();
			
			if (list.size() > 0) {
				return (TGroup) list.get(0);
			}
			
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public void removeGroup(TGroup tg) {
		try {
			delete(tg);
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public TGroup getGroupForId(int groupId) {
		try {
			
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.eq("id", groupId));
			
			List<TGroup> list = ctr.list();
			
			if (list.size() > 0) {
				return (TGroup) list.get(0);
			}
			
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TGroup> getGroupList(Integer[] groupIds) {
		try {
			
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.in("id", groupIds));
			
			List<TGroup> list = ctr.list();
			
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
	public int removeGroupForGroupId(String groupId) {
		try {
			String hql = "delete TGroup where id=" + groupId;

			logger.info("removeGroupForGroupId sql: " + hql);
			int result = delete(hql);
			
			return result;
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int transferGroup(int userIdInt, int groupIdInt) {
		try {
			String hql = "update TGroup t set t.creatorId=" + userIdInt + " where t.id=" + groupIdInt;
			logger.info("transferGroup sql: " + hql);
			int result = update(hql);
			return result;
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int changeGroupName(int groupIdInt, String groupName) {
		try {
			String hql = "update TGroup t set t.name='" + groupName + "' where t.id=" + groupIdInt;
			logger.info("changeGroupName sql: " + hql);
			int result = update(hql);
			return result;
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int updateGroupMemberNum(int groupId, int i) {
		try {
			String hql = "update TGroup t set t.volumeuse=volumeuse+" + i + " where t.id=" + groupId;
			logger.info("updateGroupMemberNum sql: " + hql);
			int result = update(hql);
			return result;
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int updateCreateIdAndVolume(int groupId, Integer memberId) {
		try {
			String hql = "update TGroup t set t.creatorId=" + memberId + ",t.volumeuse=t.volumeuse-1 where t.id=" + groupId;
			int result = update(hql);
			return result;
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		return 0;
	}

}
