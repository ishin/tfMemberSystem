package com.organ.dao.adm.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

import com.organ.common.BaseDao;
import com.organ.common.Constants;
import com.organ.dao.adm.BranchDao;
import com.organ.model.ImpUser;
import com.organ.model.TBranch;
import com.organ.model.TBranchMember;
import com.organ.model.TMember;
import com.organ.service.adm.ImpService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class BranchDaoImpl extends BaseDao<TBranch, Integer> implements
		BranchDao {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sealtalk.dao.adm.BranchDao#getOrgan(java.lang.Integer) by alopex
	 */
	@Override
	public List getOrgan(Integer organId) {

		String sql = "select id, name from t_organ" + " where id = " + organId;
		return runSql(sql);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sealtalk.dao.adm.BranchDao#getBranch(java.lang.Integer) by
	 * alopex
	 */
	@Override
	public List getBranch(Integer organId) {

		String sql = "select id, parent_id, name from t_branch"
				+ " where organ_id = " + organId;
		return runSql(sql);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sealtalk.dao.adm.BranchDao#getMember(java.lang.Integer) by
	 * alopex
	 */
	@Override
	public List getMember(Integer organId) {

		String sql = "select m.id, bm.branch_id as parent_id, m.fullname as name"
				+ " from t_branch_member bm left join t_member m on bm.member_id = m.id"
				+ " where m.organ_id = " + organId;
		return runSql(sql);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sealtalk.dao.adm.BranchDao#getMemberById(java.lang.Integer) by
	 * alopex
	 */
	@Override
	public List getRole() {

		String sql = "select id, name from t_role order by listorder desc";
		return runSql(sql);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sealtalk.dao.adm.BranchDao#getMemberById(java.lang.Integer) by
	 * alopex
	 */
	@Override
	public List getSex() {

		String sql = "select id, name from t_sex order by listorder desc";
		return runSql(sql);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sealtalk.dao.adm.BranchDao#getMemberById(java.lang.Integer) by
	 * alopex
	 */
	@Override
	public List getPosition() {

		String sql = "select id, name from t_position order by listorder desc";
		return runSql(sql);
	}

	@Override
	public List getBranchMember(Integer memberId) {

		String sql = "select bm.id,b.name bn,p.name pn,bm.is_master"
				+ " from t_branch_member bm"
				+ " left join t_branch b on bm.branch_id = b.id"
				+ " left join t_position p on bm.position_id = p.id"
				+ " where bm.member_id = " + memberId + ""
				+ " order by bm.is_master asc, bm.listorder desc";
		return runSql(sql);
	}

	@SuppressWarnings("unchecked")
	@Override
	public TBranch getOneOfBranch(String name) {
		try {

			Criteria ctr = getCriteria();
			ctr.add(Restrictions.eq("name", name));

			List<TBranch> list = ctr.list();

			if (list.size() > 0) {
				return (TBranch) list.get(0);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public List getChildren(Integer branchId) {

		Criteria ctr = getCriteria();
		ctr.add(Restrictions.eq("parentId", branchId));

		return ctr.list();
	}

	@Override
	public JSONObject testUsers(JSONArray ja) {

		ImpService impService = new ImpService(this.getSessionFactory());

		JSONObject js = new JSONObject();
		js.put(Constants.GOOD, new JSONArray());
		js.put(Constants.WELL, new JSONArray());
		js.put(Constants.BAD, new JSONArray());

		int i = ja.size();
		while (i-- > 0) {
			JSONObject j = (JSONObject) ja.get(i);
			ImpUser user = jsonToUser(j);
			if (impService.handleUser(user)) {
				JSONArray jn = (JSONArray) js.get(impService.getStatus());
				jn.add(impService.getJson());
			} else {
				js.put("status", 3);// 数据库错
				break;
			}
		}
		if (js.get("status") == null) {
			js.put("status", 0);// 解析正常
		}

		return impService.clean(js);
	}

	private ImpUser jsonToUser(JSONObject j) {

		ImpUser user = new ImpUser();

		user.setMobile((String) j.get("mobile"));
		user.setName((String) j.get("name"));
		user.setWorkno((String) j.get("workno"));
		user.setSex((String) j.get("sex"));
		user.setBranch((String) j.get("branch"));
		user.setManager((String) j.get("manager"));
		user.setPosition((String) j.get("position"));
		user.setTelephone((String) j.get("telephone"));
		user.setEmail((String) j.get("email"));

		return user;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List getBranchTree() {

		String sql = "select id,parent_id,name from t_branch";
		SQLQuery query = this.getSession().createSQLQuery(sql);

		List list = query.list();

		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List getBrancTreeAndMember() {
		String sql = "select " + "BM.branch_id," + "BM.member_id,"
				+ "BM.position_id," + "BM.is_master," + "BC.id BCID,"
				+ "BC.parent_id," + "BC.name BCNAME," + "M.id MID,"
				+ "M.account," + "M.fullname," + "M.logo," + "M.telephone,"
				+ "M.email," + "M.address," + "M.token," + "M.birthday,"
				+ "M.workno," + "M.mobile," + "M.intro," + "P.id PID,"
				+ "P.name PNAME," + "S.id SID," + "S.name SNAME,"
				+ "TOR.id ORID," + "TOR.name TORNAME "
				+ "from t_branch_member BM "
				+ "right join t_branch BC on BM.branch_id=BC.id "
				+ "right join t_organ TOR on TOR.id=BC.organ_id "
				+ "left join t_member M on M.id=BM.member_id "
				+ "left join t_position P on BM.position_id=P.id "
				+ "left join t_sex S on M.sex=S.id";

		System.out.println(sql);
		try {
			SQLQuery query = this.getSession().createSQLQuery(sql);

			List list = query.list();

			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List getBranchMember(String branchId) {
		String sql = "select " + "M.id," + "M.account," + "M.fullname,"
				+ "M.logo," + "M.telephone," + "M.email," + "M.address,"
				+ "M.token," + "M.sex," + "M.birthday," + "M.workno,"
				+ "M.mobile," + "M.groupmax," + "M.groupuse," + "M.intro,"
				+ "P.id PID," + "P.name," + "S.id," + "S.name "
				+ "from t_branch_member BM "
				+ "left join t_position P on BM.position_id=P.id "
				+ "inner join t_member M on BM.member_id=M.id "
				+ "left join t_sex S on S.id=M.sex " + "where BM.branch_id="
				+ branchId;

		try {
			SQLQuery query = this.getSession().createSQLQuery(sql);

			List list = query.list();

			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
