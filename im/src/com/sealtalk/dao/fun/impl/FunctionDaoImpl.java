package com.sealtalk.dao.fun.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.sealtalk.common.BaseDao;
import com.sealtalk.dao.fun.FunctionDao;
import com.sealtalk.model.TFunction;
import com.sealtalk.model.TMsgtop;

/**
 * 其它功能管理层
 * @author hao_dy
 * @date 2017/01/04
 * @since jdk1.7
 */
public class FunctionDaoImpl extends BaseDao<TFunction, Long> implements FunctionDao {

	@SuppressWarnings("unchecked")
	@Override
	public TFunction getFunctionStatus(String string) {
		try {
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.eq("name", string));
			
			List<TFunction> list = ctr.list();
			
			if (list.size() > 0) {
				return list.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void delFunctionStatus(TFunction tf) {
		try{
			delete(tf);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void setFunctionStatus(TFunction tf) {
		try {
			save(tf);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateFunctionStatus(String name, String status) {
		try {
			
			String hql = "update TFunction t set t.isOpen='" + status + "' where name='" + name + "'";
			update(hql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
