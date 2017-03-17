package com.organ.service.adm.impl;

import java.util.List;

import com.organ.dao.adm.PositionDao;
import com.organ.model.TPosition;
import com.organ.service.adm.PositionService;

public class PositionServiceImpl implements PositionService {

	private PositionDao positionDao;
	
	public PositionDao getPositionDao() {
		return positionDao;
	}
	public void setPositionDao(PositionDao positionDao) {
		this.positionDao = positionDao;
	}

	@Override
	public List getByOrgan(Integer organId) {
		
		return positionDao.find("from TPosition where organId = " + organId + " order by listorder desc");
	}
	@Override
	public void del(Integer id) {
		
		positionDao.deleteById(id);
	}
	@Override
	public TPosition save(String name, Integer organId) {
		
		List list = positionDao.find("from TPosition where name = '" + name + "'");
		if (list.size() > 0) return null;
		
		TPosition p = new TPosition();
		p.setName(name);
		p.setOrganId(organId);
		p.setListorder(positionDao.getMax("listorder", "from TPosition") + 1);
		positionDao.save(p);
		
		return p;
	}

}
