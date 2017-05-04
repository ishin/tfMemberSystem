package com.organ.service.adm.impl;

import java.util.List;

import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.organ.common.Tips;
import com.organ.dao.adm.BranchMemberDao;
import com.organ.dao.adm.PositionDao;
import com.organ.model.TBranchMember;
import com.organ.model.TPosition;
import com.organ.service.adm.PositionService;
import com.organ.utils.LogUtils;

public class PositionServiceImpl implements PositionService {
	private static final Logger logger = LogManager.getLogger(PositionServiceImpl.class);
	private PositionDao positionDao;
	private BranchMemberDao branchMemberDao; 
	
	public void setPositionDao(PositionDao positionDao) {
		this.positionDao = positionDao;
	}
	

	public void setBranchMemberDao(BranchMemberDao branchMemberDao) {
		this.branchMemberDao = branchMemberDao;
	}


	@Override
	public List getByOrgan(Integer organId) {
		
		return positionDao.find("from TPosition where organId = " + organId + " order by listorder desc");
	}
	@Override
	public int del(Integer id) {
		int ret = 0;
		try {
			/*int count = branchMemberDao.getBranchMemberCountByPositionId(id);
			
			if (count == 0) {
				positionDao.deleteById(id);
				ret = id;
			} else {
				ret = 0;
			}*/
			positionDao.deleteById(id);
			ret = id;
		} catch(Exception e) {	
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		return ret;
	}
	@Override
	public TPosition save(String name, Integer organId) {
		try{
			List list = positionDao.find("from TPosition where name = '" + name + "' and organId="+organId);
			if (list.size() > 0) return null;
			//Integer max = positionDao.getMax("listorder", "from TPosition where organId="+organId);
			//max = max == null ? 0 : max;
			TPosition p = new TPosition();
			p.setName(name);
			p.setOrganId(organId);
			p.setListorder(0);
			positionDao.save(p);
			return p;
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		return null;
	}

}
