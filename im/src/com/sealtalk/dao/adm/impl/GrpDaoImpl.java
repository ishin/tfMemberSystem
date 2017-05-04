package com.sealtalk.dao.adm.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sealtalk.common.BaseDao;
import com.sealtalk.dao.adm.GrpDao;
import com.sealtalk.model.TGroup;

public class GrpDaoImpl extends BaseDao<TGroup, Integer> implements GrpDao {

	private static final Logger logger = LogManager.getLogger(GrpDaoImpl.class);
	
	@Override
	public void delGroupMemberByGroup(Integer id) {
		String sql = "delete from TGroupMember where groupId = " + id;
		logger.info("delGroupMemberByGroup sql: " + sql);
		delete(sql);
	}

	@Override
	public Integer getMemberCountByGrp(Integer id) {

		String sql = "select count(id) from t_group_member where group_id = " + id;
		logger.info("getMemberCountByGrp sql: " + sql);
		List list = runSql(sql);

		String c = String.valueOf(list.get(0));

		return Integer.parseInt(c);
	}

	@Override
	public void changeCreator(Integer groupId, Integer groupMemberId, Integer cid) {
		String sql1 = "update TGroupMember set isCreator = '0' where groupId = " + groupId;
		String sql2 = "update TGroupMember set isCreator = '1' where id = " + groupMemberId;
		String sql3 = "update TGroup set creatorId=" + cid + " where id=" + groupId;
		
		logger.info("changeCreator sql1: " + sql1);
		logger.info("changeCreator sql2: " + sql2);
		logger.info("changeCreator sql3: " + sql3);
		executeUpdate(sql1);
		executeUpdate(sql2);
		executeUpdate(sql3);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List getLimitList(Integer page, Integer itemsperpage) {
		String sql = "select g.id,g.code,g.name,g.createdate date,g.creator_id cid from t_group g";
		if (page != null)
			sql += " limit " + page * itemsperpage + ", " + itemsperpage;
		logger.info("getLimitList sql: " + sql);
		return runSql(sql);
	}

	@Override
	public List getLimitListById(Integer id, Integer page, Integer itemsperpage) {
		String sql = "select gm.is_creator iscreator,gm.id gmid,gm.member_id mid from t_group_member gm where gm.group_id = " + id + " order by gm.is_creator desc";
		
		if (page != null && itemsperpage != null)
			sql += " limit " + page * itemsperpage + ", " + itemsperpage;

		logger.info("getLimitListById sql: " + sql);
		
		return runSql(sql);
	}

}
