package com.organ.dao.adm;

import java.util.ArrayList;
import java.util.List;

import com.organ.common.IBaseDao;
import com.organ.model.TPosition;

public interface PositionDao extends IBaseDao<TPosition, Integer> {

	public TPosition getPositionByName(Integer organId, String name);

	public List<TPosition> getMultiplePositionByIds(Integer[] posIds);
}
