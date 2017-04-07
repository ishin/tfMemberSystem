package com.organ.dao.upload.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.organ.common.BaseDao;
import com.organ.dao.upload.CutLogoTempDao;
import com.organ.model.TCutLogoTemp;


/**
 * 裁剪头像模型
 * @author hao_dy
 * @date 2017/01/04
 * @since jdk1.7
 */
public class CutLogoTempDaoImpl extends BaseDao<TCutLogoTemp, Long> implements CutLogoTempDao {

	@Override
	public void saveTempPic(TCutLogoTemp clt) {
		try {
			save(clt);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TCutLogoTemp> getTempLogoForId(int userid) {
		try {
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.eq("userId", userid));
			
			List<TCutLogoTemp> list = ctr.list();
			
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
	public TCutLogoTemp getTempLogoForIdAndPicName(int userIdInt, String picName) {
		try {
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.and(Restrictions.eq("userId", userIdInt), Restrictions.eq("logoName", picName)));
			
			List<TCutLogoTemp> list = ctr.list();
			
			if (list.size() > 0) {
				return list.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public int delUserLogos(int userIdInt, String picName) {
		try {
			String hql = "delete TCutLogoTemp CT where CT.userId=" + userIdInt + " and CT.logoName='" + picName + "'";
			int ret = delete(hql);
			
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TCutLogoTemp> getUserLogos(int userIdInt) {
		try {
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.eq("userId", userIdInt));
			
			List<TCutLogoTemp> list = ctr.list();
			
			if (list.size() > 0) {
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	

}
