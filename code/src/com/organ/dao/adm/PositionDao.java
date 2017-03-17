package com.organ.dao.adm;

import com.organ.common.IBaseDao;
import com.organ.model.TPosition;

public interface PositionDao extends IBaseDao<TPosition, Integer> {

	public TPosition getPositionByName(Integer organId, String name);
}
