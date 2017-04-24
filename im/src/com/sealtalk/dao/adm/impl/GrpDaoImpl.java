package com.sealtalk.dao.adm.impl;

import java.util.List;

import com.sealtalk.common.BaseDao;
import com.sealtalk.dao.adm.GrpDao;
import com.sealtalk.model.TGroup;

public class GrpDaoImpl extends BaseDao<TGroup, Integer> implements GrpDao {

	@Override
	@Deprecated
	public List getList(Integer page, Integer itemsperpage) {

		String sql = "select g.id, g.code,g.name, g.createdate date, m.fullname member"
				+ " from t_group g"
				+ " left join t_member m on m.id=g.creator_id";
		if (page != null)
			sql += " limit " + page * itemsperpage + ", " + itemsperpage;

		return runSql(sql);
	}

	@Override
	public void delGroupMemberByGroup(Integer id) {

		delete("delete from TGroupMember where groupId = " + id);
	}

	@Override
	public Integer getMemberCountByGrp(Integer id) {

		String sql = "select count(id)"
				+ " from t_group_member where group_id = " + id;
		List list = runSql(sql);

		String c = String.valueOf(list.get(0));

		return Integer.parseInt(c);
	}

	@Override
	@Deprecated
	public List getMemberByGrp(Integer id, Integer page, Integer itemsperpage) {

		String sql = "select gm.is_creator iscreator, m.fullname name, m.account, gm.id gmid"
				+ " from t_group_member gm"
				+ " left join t_member m on m.id=gm.member_id"
				+ " where gm.group_id = " + id + " order by gm.is_creator desc";
		if (page != null)
			sql += " limit " + page * itemsperpage + ", " + itemsperpage;

		return runSql(sql);
	}

	@Override
	public void changeCreator(Integer groupId, Integer groupMemberId) {
		executeUpdate("update TGroupMember set isCreator = '0' where groupId = "
				+ groupId);
		executeUpdate("update TGroupMember set isCreator = '1' where id = "
				+ groupMemberId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List getLimitList(Integer page, Integer itemsperpage) {
		String sql = "select g.id,g.code,g.name,g.createdate date,g.creator_id cid from t_group g";
		if (page != null)
			sql += " limit " + page * itemsperpage + ", " + itemsperpage;

		return runSql(sql);
	}

	@Override
	public List getLimitListById(Integer id, Integer page, Integer itemsperpage) {
		String sql = "select gm.is_creator iscreator,gm.id gmid,gm.member_id mid from t_group_member gm where gm.group_id = " + id + " order by gm.is_creator desc";
		
		if (page != null && itemsperpage != null)
			sql += " limit " + page * itemsperpage + ", " + itemsperpage;

		System.out.println("getLimitListById sql: " + sql);
		
		return runSql(sql);
	}

}
