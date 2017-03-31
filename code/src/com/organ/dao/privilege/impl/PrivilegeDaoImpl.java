package com.organ.dao.privilege.impl;

import java.util.List;

import org.apache.log4j.Logger;

import com.organ.common.BaseDao;
import com.organ.dao.privilege.PrivilegeDao;
import com.organ.model.TPriv;
import com.organ.utils.LogUtils;

public class PrivilegeDaoImpl extends BaseDao<TPriv, Long> implements PrivilegeDao {
	private static final Logger logger = Logger.getLogger(PrivilegeDaoImpl.class);
	
	@Override
	public List<TPriv> getAllPrivilege() {
		try {
			return findAll();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
		}
		return null;
	}
	
}
