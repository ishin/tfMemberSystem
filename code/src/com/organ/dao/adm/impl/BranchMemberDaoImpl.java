package com.organ.dao.adm.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.organ.common.BaseDao;
import com.organ.dao.adm.BranchMemberDao;
import com.organ.model.TBranchMember;

public class BranchMemberDaoImpl extends BaseDao<TBranchMember, Integer>
		implements BranchMemberDao {

	@SuppressWarnings("unchecked")
	@Override
	public TBranchMember getBranchMemberByBranchPosition(Integer branchId,
			Integer positionId) {

		Criteria c = getCriteria();
		c.add(Restrictions.eq("branchId", branchId));
		c.add(Restrictions.eq("positionId", positionId));

		List list = c.list();
		if (list.isEmpty()) {
			return null;
		}
		return (TBranchMember) list.get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TBranchMember> getBranchMemberByMember(Integer memberId) {

		Criteria c = getCriteria();
		c.add(Restrictions.eq("memberId", memberId));

		List list = c.list();
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TBranchMember> getBranchMemberByBranch(Integer branchId) {

		Criteria c = getCriteria();
		c.add(Restrictions.eq("branchId", branchId));

		List list = c.list();
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void selectMaster(Integer memberId) {

		Criteria c = getCriteria();
		c.add(Restrictions.eq("memberId", memberId));
		c.add(Restrictions.eq("isMaster", "0"));
		c.addOrder(Order.asc("listorder"));

		List list = c.list();
		if (list.isEmpty())
			return;

		TBranchMember bm = (TBranchMember) list.get(0);
		bm.setIsMaster("1");
		update(bm);
	}

	@SuppressWarnings("unchecked")
	@Override
	public TBranchMember getBranchMemberByBranchMember(Integer branchId,
			Integer memberId) {

		Criteria c = getCriteria();
		c.add(Restrictions.eq("branchId", branchId));
		c.add(Restrictions.eq("memberId", memberId));

		List list = c.list();
		if (list.isEmpty()) {
			return null;
		}
		return (TBranchMember) list.get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List getBranchMemberByMemberIds(String memberIds) {

		String hql = (new StringBuilder(
				"select BM.member_id mid, P.name, B.name bname from t_branch_member BM left join t_branch B on B.id=BM.branch_id left join t_position P on BM.position_id=P.id where BM.member_id in (")
				.append(memberIds).append(")")).toString();
		return getSession().createSQLQuery(hql).list();
	}

	@Override
	public int updatePositionByUseId(int userIdInt, int positionId) {
		String hql = (new StringBuilder(
				"update TBranchMember t set t.positionId=").append(positionId)
				.append("where t.memberId=").append(userIdInt)).toString();
		return update(hql);
	}

	@Override
	public int delRelationByIds(String userids) {
		try {
			String hql = (new StringBuilder("delete from TBranchMember where memberId in (").append(userids).append(")")).toString();
			return delete(hql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int getBranchMemberCountByPositionId(Integer id) {
		return count("from TBranchMember t where positionId=" + id);
	}

}
