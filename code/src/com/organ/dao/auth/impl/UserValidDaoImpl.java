package com.organ.dao.auth.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.organ.dao.auth.UserValidDao;
import com.organ.model.UserValid;
import com.organ.common.BaseDao;
import com.organ.utils.LogUtils;

/**
 * 验证管理
 * 
 * @author hao_dy
 * @date 2017/03/08
 * @since jdk1.7
 */
public class UserValidDaoImpl extends BaseDao<UserValid, Integer> implements UserValidDao {

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

			List<UserValid> list = ctr.list();

			if (list.size() > 0) {
				return (UserValid) list.get(0);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
