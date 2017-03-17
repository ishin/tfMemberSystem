package com.organ.dao.group.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

import com.organ.common.BaseDao;
import com.organ.dao.group.GroupDao;
import com.organ.model.TGroup;
import com.organ.utils.PropertiesUtils;
import com.organ.utils.StringUtils;
import com.organ.utils.TimeGenerator;

/**
 * @功能  群组数据管理层
 * @author hao_dy
 * @date 2017/01/04
 * @since jdk1.7
 */
public class GroupDaoImpl extends BaseDao<TGroup, Long> implements GroupDao {

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
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public void removeGroup(TGroup tg) {
		try {
			delete(tg);
		} catch (Exception e) {
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
			e.printStackTrace();
		}
		
		return null;
	}
	
	public List<Object[]> getGroupListWithCreaterInfo(String groupIds) {
        try
        {
            String hql = (new StringBuilder("select M.id MID,M.account,M.fullname,M.logo,M.telephone,M.email,M.address,M.token,M.sex,M.birthday,M.workno,M.mobile,M.groupmax,M.groupuse,M.intro,G.id GID,G.code,G.name,G.createdate,G.volume,G.volumeuse,G.space,G.spaceuse,G.annexlong,G.notice from t_member M right join t_group G on G.creator_id=M.id where G.id in (")).append(groupIds).append(")").toString();
       
            System.out.println("getGroupListWithCreaterInfo() sql: " + hql);
            SQLQuery query = getSession().createSQLQuery(hql);
            List list = query.list();
            
            return list;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

	@Override
	public int removeGroupForGroupId(String groupId) {
		try {
			String hql = "delete TGroup where id=" + groupId;
			
			int result = delete(hql);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int transferGroup(int userIdInt, int groupIdInt) {
		try {
			String hql = "update TGroup t set t.creatorId=" + userIdInt + " where t.id=" + groupIdInt;
			
			int result = update(hql);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int changeGroupName(int groupIdInt, String groupName) {
		try {
			String hql = "update TGroup t set t.name='" + groupName + "' where t.id=" + groupIdInt;
			
			int result = update(hql);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int updateGroupMemberNum(int groupId, int i) {
		try {
			String hql = "update TGroup t set t.volumeuse=volumeuse+" + i + " where t.id=" + groupId;
			
			int result = update(hql);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] groupInfo(int id) {
		/*try {
			
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.eq("id", id));
			
			List<TGroup> list = ctr.list();
			
			if (list.size() > 0) {
				return list.get(0);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
		try
        {
            String hql = (new StringBuilder("select M.id MID,M.account,M.fullname,M.logo,M.telephone,M.email,M.address,M.token,M.sex,M.birthday,M.workno,M.mobile,M.groupmax,M.groupuse,M.intro,G.id GID,G.code,G.name,G.createdate,G.volume,G.volumeuse,G.space,G.spaceuse,G.annexlong,G.notice from t_member M right join t_group G on G.creator_id=M.id where G.id=")).append(id).toString();
            SQLQuery query = getSession().createSQLQuery(hql);
            List list = query.list();
            if(list.size() > 0)
                return (Object[])list.get(0);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
		
	}

}
