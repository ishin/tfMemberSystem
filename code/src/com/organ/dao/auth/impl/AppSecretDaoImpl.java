package com.organ.dao.auth.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.organ.common.BaseDao;
import com.organ.dao.auth.AppSecretDao;
import com.organ.model.AppSecret;
import com.organ.utils.LogUtils;
import com.organ.dao.auth.impl.AppSecretDaoImpl;

/**
 * 验证管理
 * 
 * @author hao_dy
 * @date 2017/03/08
 * @since jdk1.7
 */
public class AppSecretDaoImpl extends BaseDao<AppSecret, Integer> implements
		AppSecretDao {
	private static final Logger logger = Logger
			.getLogger(AppSecretDaoImpl.class);

	@Override
	public void setAppIDAndSecretAndUrl(AppSecret as) {
		try {
			save(as);
			logger.info("AppId and Secret and callbackUrl were saved!");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public AppSecret getAppSecretByAppId(String appId) {
		try {
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.eq("appId", appId));

			List<AppSecret> list = ctr.list();

			if (list.size() > 0) {
				return (AppSecret) list.get(0);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void updateAppSecret(AppSecret as) {
		try {
			update(as);
			logger.info("AppIdSecret was updated, !");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
	}

	@Override
	public AppSecret getAppSecretBySecret(String secret, Integer organId) {
		try {
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.eq("secert", secret));
			ctr.add(Restrictions.eq("organId", organId));

			List<AppSecret> list = ctr.list();

			if (list.size() > 0) {
				return (AppSecret) list.get(0);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public AppSecret getAppSecretByAppIdAndSecret(String appId, String secret) {
		try {
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.and(Restrictions.eq("secert", secret),
					Restrictions.eq("appId", appId)));

			List<AppSecret> list = ctr.list();

			if (list.size() > 0) {
				return (AppSecret) list.get(0);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
