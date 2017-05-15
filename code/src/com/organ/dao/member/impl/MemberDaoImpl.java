package com.organ.dao.member.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

import com.organ.common.BaseDao;
import com.organ.dao.member.MemberDao;
import com.organ.model.TMember;
import com.organ.utils.StringUtils;
import com.organ.utils.TimeGenerator;

/**
 * @功能 成员数据管理层
 * @author hao_dy
 * @date 2017/01/04
 * @since jdk1.7
 */
public class MemberDaoImpl extends BaseDao<TMember, Integer> implements MemberDao {

	@Override
	public TMember getMemberByName(String name, Integer organId) {

		Criteria ctr = getCriteria();
		ctr.add(Restrictions.eq("fullname", name));
		ctr.add(Restrictions.eq("organId", organId));
		ctr.add(Restrictions.eq("isDel", 1));

		List list = ctr.list();

		if (list.size() > 0) {
			return (TMember) list.get(0);
		}

		return null;
	}

	@Override
	public List getMemberPosition(Integer memberId) {

		String sql = "select position_id, branch_id, id from t_branch_member"
				+ " where member_id = " + memberId + " and isdel='1' order by is_master desc";
		SQLQuery query = this.getSession().createSQLQuery(sql);

		List list = query.list();

		return list;
	}

	@Override
	public List getMemberRole(Integer memberId) {

		String sql = "select role_id from t_member_role"
				+ " where member_id = " + memberId + " and isdel='1'";
		SQLQuery query = this.getSession().createSQLQuery(sql);

		List list = query.list();

		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TMember searchSigleUser(String name, String password, int organId) {

		try {
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.eq("account", name));
			ctr.add(Restrictions.eq("password", password));
			ctr.add(Restrictions.eq("organId", organId));
			ctr.add(Restrictions.eq("isDel", 1));

			List list = ctr.list();

			if (list.size() > 0) {
				return (TMember) list.get(0);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public boolean updateUserPwdForAccount(String account, String md5Pwd,
			int organId) {

		String hql = (new StringBuilder("update TMember set password='")
				.append(md5Pwd).append("' where account='")
				.append(account)
				.append("' and organId=")
				.append(organId)).toString();

		boolean status = true;

		try {
			executeUpdate(hql);
		} catch (Exception e) {
			status = false;
			e.printStackTrace();
		}

		return status;
	}

	@Override
	public boolean updateUserPwdForPhone(String phone, String md5Pwd) {

		String hql = "update TMember set password='" + md5Pwd
				+ "' where mobile='" + phone + "'";

		boolean status = true;

		try {
			executeUpdate(hql);
		} catch (Exception e) {
			status = false;
			e.printStackTrace();
		}

		return status;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getOneOfMember(int id) {
		try {
			String hql = "select "
					+ "M.id MID,"
					+ "M.account,"
					+ "M.fullname,"
					+ "M.logo,"
					+ "M.telephone,"
					+ "M.email,"
					+ "M.address,"
					+ "M.token,"
					+ "M.sex,"
					+ "M.birthday,"
					+ "M.workno,"
					+ "M.mobile,"
					+ "M.intro,"
					+ "B.id BID,"
					+ "B.name BNAME,"
					+ "P.id PID,"
					+ "P.name PNAME,"
					+ "O.id OID,"
					+ "O.name ONAME,"
					+ "BM.is_master "
					+ "from t_member M left join t_branch_member BM on M.id=BM.member_id "
					+ "left join t_branch B on BM.branch_id=B.id "
					+ "left join t_position P on BM.position_id=P.id "
					+ "inner join t_organ O on M.organ_id=O.id "
					+ "where M.id=" + id + " and M.isdel=1";
			
			SQLQuery query = this.getSession().createSQLQuery(hql);

			System.out.println("getOneOfMember->hql :" + hql);

			List list = query.list();

			if (list != null) {
				int len = list.size();
				if(len == 1) {
					return (Object[])list.get(0);
				} else if (len > 1) {
					Object[] ret = null;
					for(int i = 0; i < len; i++) {
						Object[] t = (Object[]) list.get(0);
						if (String.valueOf(t[19]).equals("1")) {
							ret = t;
							break;
						}
					}
					return ret;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TMember> getMultipleMemberForAccounts(String[] mulMemberStr, int organId) {
		try {

			Criteria ctr = getCriteria();
			ctr.add(Restrictions.in("account", mulMemberStr));
			ctr.add(Restrictions.eq("organId", organId));
			ctr.add(Restrictions.eq("isDel", 1));

			List<TMember> list = ctr.list();

			if (list.size() > 0) {
				return list;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TMember> getMultipleMemberForIds(Integer[] ids) {
		try {

			Criteria ctr = getCriteria();
			ctr.add(Restrictions.in("id", ids));

			List<TMember> list = ctr.list();

			if (list.size() > 0) {
				return list;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public int updateUserTokenForId(String userId, String token) {

		try {
			long now = TimeGenerator.getInstance().getUnixTime();
			String hql = "update TMember mem set mem.token='" + token
					+ "',createtokendate=" + now + " where id=" + userId;

			int row = update(hql);

			return row;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TMember getMemberForId(int id) {
		try {

			Criteria ctr = getCriteria();
			ctr.add(Restrictions.eq("id", id));

			List<TMember> list = ctr.list();

			if (list.size() > 0) {
				return (TMember) list.get(0);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int getMemberIdForAccount(String account, int organId) {
		try {
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.eq("account", account));
			ctr.add(Restrictions.eq("organId", organId));
			ctr.add(Restrictions.eq("isDel", 1));

			List<TMember> list = ctr.list();

			if (list.size() > 0) {
				return list.get(0).getId();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List searchUser(String account, int organId) {
		try {
			String hql = "select "
					+ "M.id MID,"
					+ "M.account,"
					+ "M.fullname,"
					+ "M.logo,"
					+ "M.telephone,"
					+ "M.email,"
					+ "M.address,"
					+ "M.token,"
					+ "M.sex,"
					+ "M.birthday,"
					+ "M.workno,"
					+ "M.mobile,"
					+ "M.groupmax,"
					+ "M.groupuse,"
					+ "M.intro,"
					+ "B.name BNAME,"
					+ "P.name PNAME,"
					+ "O.name ONAME,"
					+ "M.isdel "
					+ "from t_member M left join t_branch_member BM on M.id=BM.member_id "
					+ "left join t_branch B on BM.branch_id=B.id "
					+ "left join t_position P on BM.position_id=P.id "
					+ "inner join t_organ O on M.organ_id=O.id "
					+ "where M.organ_id=" + organId
					+ " and M.account like '%" + account
					+ "%' or M.fullname like '%" + account
					+ "%' or M.pinyin like '%" + account
					+ "%' or M.allpinyin like '%" + account
					+ "%' or M.mobile='" + account + "'";

			SQLQuery query = this.getSession().createSQLQuery(hql);

			List list = query.list();

			if (list.size() > 0) {
				return list;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean valideOldPwd(String account, String oldPwd, int organId) {
		try {
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.eq("account", account));
			ctr.add(Restrictions.eq("password", oldPwd));
			ctr.add(Restrictions.eq("organId", organId));

			List<TMember> list = ctr.list();

			if (list.size() > 0) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public int updateUserLogo(int userId, String picName) {

		try {
			String hql = "update TMember mem set mem.logo='" + picName
					+ "' where id=" + userId;
			int ret = update(hql);
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isUsedPic(int userId, String picName) {
		try {
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.and(Restrictions.eq("id", userId),
					Restrictions.eq("logo", picName)));

			List<TMember> list = ctr.list();

			if (list.size() > 0) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public int updateMemeberInfoForWeb(int userId, String fullName, String sign) {
		StringBuilder sbSql = new StringBuilder();

		sbSql.append("update TMember T set ");

		boolean bl = false;

		if (!StringUtils.getInstance().isBlank(fullName)) {
			bl = true;
			sbSql.append("T.fullname='").append(fullName).append("'");
		}
		if (!StringUtils.getInstance().isBlank(sign)) {
			bl = true;
			sbSql.append(",T.intro='").append(sign).append("'");
		}

		sbSql.append(" where id=").append(userId);

		if (bl) {
			String hql = sbSql.toString();
			System.out.println(hql);
			try {
				return update(hql);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return 0;
	}

	@Override
	public int updateMemeberInfoForApp(int userId, String email, String mobile,
			String phone, String address) {
		StringBuilder sbSql = new StringBuilder();

		sbSql.append("update TMember T set ");

		boolean bl = false;

		if (!StringUtils.getInstance().isBlank(email)) {
			bl = true;
			sbSql.append("T.email='").append(email).append("'");
		}
		if (!StringUtils.getInstance().isBlank(mobile)) {
			bl = true;
			sbSql.append(",T.mobile='").append(mobile).append("'");
		}
		if (!StringUtils.getInstance().isBlank(phone)) {
			bl = true;
			sbSql.append(",T.telephone='").append(phone).append("'");
		}
		if (!StringUtils.getInstance().isBlank(address)) {
			bl = true;
			sbSql.append(",T.address='").append(address).append("'");
		}

		sbSql.append(" where id=").append(userId);

		if (bl) {
			String hql = sbSql.toString();
			try {
				return update(hql);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return 0;
	}

	@Override
	@Deprecated
	public boolean updateUserPwd(String account, String md5Pwd) {

		String hql = "update TMember set password='" + md5Pwd
				+ "' where account='" + account + "'";

		boolean status = true;

		try {
			executeUpdate(hql);
		} catch (Exception e) {
			status = false;
			e.printStackTrace();
		}

		return status;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TMember getOneMember(String account, int organId) {
		try {

			Criteria ctr = getCriteria();
			ctr.add(Restrictions.eq("account", account));
			ctr.add(Restrictions.eq("organId", organId));
			ctr.add(Restrictions.eq("isDel", 1));

			List<TMember> list = ctr.list();

			if (list.size() > 0) {
				return (TMember) list.get(0);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TMember> getLimitMemberIds(int limit, int organId) {
		String sql = (new StringBuilder(
				"select new TMember(t.id) from TMember t where t.organId=").append(organId).append(" and t.idDel=1")).toString();

		try {
			Query query = getSession().createQuery(sql);
			query.setFirstResult(0);
			query.setMaxResults(limit);

			List<TMember> list = query.list();

			if (list.size() > 0) {
				return list;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TMember getMemberByToken(String token) {
		try {
			String hql = (new StringBuilder("from TMember t where t.token='").append(token).append("' and t.isDel=1")).toString();

			List<TMember> list = getSession().createQuery(hql).list();

			if (list.size() > 0) {
				return (TMember) list.get(0);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TMember> getAllMemberInfo(int organId) {
		String sql = (new StringBuilder("from TMember t where t.organId=").append(organId).append(" and t.isDel=1")).toString();

		try {
			Query query = getSession().createQuery(sql);
			List<TMember> list = query.list();

			if (list.size() > 0) {
				return list;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public Object[] getAuthResouce(int id) {
		try {
			String hql = "select "
					+ "M.fullname,"
					+ "M.logo,"
					+ "M.telephone,"
					+ "M.email,"
					+ "M.mobile,"
					+ "M.id,"
					+ "M.account,"
					+ "S.name SNAME,"
					+ "P.name PNAME,"
					+ "O.name ONAME "
					+ "from t_member M left join t_branch_member BM on M.id=BM.member_id "
					+ "left join t_branch B on BM.branch_id=B.id "
					+ "left join t_position P on BM.position_id=P.id "
					+ "left join t_sex S on M.sex=S.id "
					+ "inner join t_organ O on M.organ_id=O.id "
					+ "where M.id=" + id + " and M.isdel=1";

			SQLQuery query = this.getSession().createSQLQuery(hql);

			System.out.println("getAuthResouce->hql :" + hql);

			List list = query.list();

			if (list.size() > 0) {
				int len = list.size();
				if(len == 1) {
					return (Object[])list.get(0);
				} else if (len > 1) {
					Object[] ret = null;
					for(int i = 0; i < len; i++) {
						Object[] t = (Object[]) list.get(0);
						if (String.valueOf(t[8]).equals("1")) {
							ret = t;
							break;
						}
					}
					return ret;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public int getMemberCount(int organId) {
		try {
			return count("from TMember where organId=" + organId + " and isDel=1");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public List getMemberIdsByAccount(String[] targetNames, int organId) {
		try {
			StringBuilder sql = new StringBuilder(
					"select id from t_member where organ_id=").append(organId).append(" and isdel=1").append(" and account in(");
			int len = targetNames.length;

			for (int i = 0; i < len; i++) {
				sql.append("\"").append(targetNames[i]).append("\"");
				if (i < len - 1) {
					sql.append(",");
				}
			}
			sql.append(")");
			SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			List list = query.list();

			if (list != null && list.size() > 0) {
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List getMemberParam(String ids, String[] pss) {
		try {
			StringBuilder sql = new StringBuilder("select ");
			int len = pss.length;

			for (int i = 0; i < len; i++) {
				sql.append(pss[i]);
				if (i < len - 1) {
					sql.append(",");
				}
			}

			sql.append(" from t_member where isDel=1 and id in(");
			sql.append(ids);
			sql.append(")");

			SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			List list = query.list();

			if (list != null && list.size() > 0) {
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TMember getSuperAdmin(String account, String password, int organId) {

		try {
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.eq("account", account));
			ctr.add(Restrictions.eq("password", password));
			ctr.add(Restrictions.eq("organId", organId));
			ctr.add(Restrictions.eq("superAdmin", 1));
			ctr.add(Restrictions.eq("isDel", 1));

			List<TMember> list = ctr.list();

			if (list.size() > 0) {
				return (TMember) list.get(0);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TMember getMemberByMobile(String mobile) {
		try {
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.eq("mobile", mobile));
			ctr.add(Restrictions.eq("isDel", 1));

			List<TMember> list = ctr.list();

			if (list.size() > 0) {
				return (TMember) list.get(0);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TMember getMemberByEmail(String email) {
		try {

			Criteria ctr = getCriteria();
			ctr.add(Restrictions.eq("email", email));
			ctr.add(Restrictions.eq("isDel", 1));

			List<TMember> list = ctr.list();

			if (list.size() > 0) {
				return (TMember) list.get(0);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TMember searchSigleUserByOrgan(String name, String password,
			int organId) {
		try {
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.eq("account", name));
			ctr.add(Restrictions.eq("password", password));
			ctr.add(Restrictions.eq("organId", organId));
			ctr.add(Restrictions.eq("isDel", 1));

			List<TMember> list = ctr.list();

			if (list.size() > 0) {
				return (TMember) list.get(0);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public TMember getSuperMember(int organId) {
		try {
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.eq("superAdmin", 1));
			ctr.add(Restrictions.eq("organId", organId));
			ctr.add(Restrictions.eq("isDel", 1));

			List<TMember> list = ctr.list();

			if (list.size() > 0) {
				return (TMember) list.get(0);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public int logicDelMemberByUserIds(String userids, String isLogic) {
		try {
			if (isLogic.equals("1")) {
				String hql = (new StringBuilder("update TMember set isDel=0 where id in(").append(userids).append(")")).toString();
				return update(hql);
			} else {
				String hql = (new StringBuilder("delete from TMember where id in(").append(userids).append(")")).toString();
				return delete(hql);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public List<String> getNotDelIds(String userids, String isLogic) {
		try {
			String hql = null;
			if (isLogic.equals("1")) {
				hql = (new StringBuilder("select id from t_member t where t.isdel!=0 and t.id in(").append(userids).append(")")).toString();
			} else {
				hql = (new StringBuilder("select id from t_member t where t.id in(").append(userids).append(")")).toString();
			}
			List list = runSql(hql);
			List<String> ids = new ArrayList<String>();
			
			if (list != null && list.size() > 0) {
				for(int i = 0; i < list.size(); i++) {
					ids.add(String.valueOf(list.get(i)));
				}
			}
			
			return ids;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
