package com.organ.dao.auth.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import com.organ.common.BaseDao;
import com.organ.dao.auth.UserValidDao;
import com.organ.model.UserValid;
import com.organ.utils.LogUtils;

/**
 * 验证管理
 * 
 * @author hao_dy
 * @date 2017/03/08
 * @since jdk1.7
 */
public class UserValidDaoImpl extends BaseDao<UserValid, Integer> implements
		UserValidDao {

	@Override
	public void setUnAuthToken(UserValid uv) {
		try {
			saveOrUpdate(uv);
			logger.info("UnAuthToken is save!");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
	}

	@Override
	public UserValid getUserValidByUnAuthToken(String unAuthToken) {
		try {
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.eq("unAuthToken", unAuthToken));
			ctr.add(Restrictions.eq("isDel", "1"));

			List<UserValid> list = ctr.list();

			if (list.size() > 0) {
				return (UserValid) list.get(0);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public UserValid getUserValidByAuthToken(String authToken) {
		try {
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.eq("authToken", authToken));
			ctr.add(Restrictions.eq("isDel", 1));

			List<UserValid> list = ctr.list();

			if (list.size() > 0) {
				return (UserValid) list.get(0);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public UserValid getUserValidByRealToken(String visitToken) {
		try {
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.eq("visitToken", visitToken));
			ctr.add(Restrictions.eq("isDel", "1"));

			List<UserValid> list = ctr.list();

			if (list.size() > 0) {
				return (UserValid) list.get(0);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public UserValid getUserValidByAsId(int asId) {
		try {
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.eq("asid", asId));
			ctr.add(Restrictions.eq("isDel", "1"));

			List<UserValid> list = ctr.list();

			if (list.size() > 0) {
				return list.get(0);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void delUserValid(int id) {
		try {
			String sql = (new StringBuilder("delete from UserValid where asid=")
					.append(id)).toString();
			System.out.println(sql);
			delete(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public int deleteRelationByIds(String userids, String isLogic) {
		try {
			if (isLogic.equals("1")) {
				String hql = (new StringBuilder("update UserValid set isDel=0 where userId in (").append(userids).append(")")).toString();
				return update(hql);
			} else {
				String hql = (new StringBuilder("delete from UserValid where userId in (").append(userids).append(")")).toString();
				return delete(hql);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	@Override
	public void updateUserValid(UserValid uv) {
		try {
			String hql = (new StringBuilder(
					"update UserValid u set u.unAuthToken='").append(
					uv.getUnAuthToken()).append("',unAuthTokenTime=").append(uv
					.getUnAuthTokenTime()).append(" where u.asid=").append(uv.getAsid())).toString();
			update(hql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
