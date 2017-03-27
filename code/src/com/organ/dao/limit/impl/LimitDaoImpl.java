package com.organ.dao.limit.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;

import com.organ.common.BaseDao;
import com.organ.dao.limit.LimitDao;
import com.organ.dao.privilege.impl.PrivilegeDaoImpl;
import com.organ.model.TPriv;
import com.organ.utils.PrivUrlNameUtil;

public class LimitDaoImpl extends BaseDao<TPriv, Long> implements LimitDao {

	private static final Logger logger = Logger
			.getLogger(PrivilegeDaoImpl.class);

	/**
	 * 这个实现添加全选的接口，这儿的逻辑
	 */
	@Override
	public int updatePriv(int parentId, String name, String category, String app) {
		// TODO Auto-generated method stub
		TPriv tPriv = new TPriv();
		tPriv.setParentId(parentId);
		tPriv.setName(name);
		tPriv.setApp(app);
		tPriv.setUrl(PrivUrlNameUtil.initUrlName(name));
		tPriv.setListorder(0); // 这个不能为空
		tPriv.setCategory(category);
		tPriv.setGrouping("0");

		try {
			save(tPriv);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tPriv.getId();

	}

	@Override
	public int DeletePriv(int privId) {
		// TODO Auto-generated method stub
		try {
			String hql = "delete TPriv where id=" + privId;

			int result = delete(hql);

			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int editPriv(int priv_id, String pid, String name, String category,
			String app) {
		// TODO Auto-generated method stub
		try {
			String hql = "update TPriv tp set tp.name= '" + name
					+ "',tp.parentId='" + pid + "',tp.category='" + category
					+ "',tp.app='" + app + "',tp.url='"
					+ PrivUrlNameUtil.initUrlName(name) + "' where tp.id="
					+ priv_id;
			int result = update(hql);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 根据权限名称搜索
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List searchPriv(String Name) {
		try {
			String hql = "select " + "tp.id," + "tp.parent_id," + "tp.NAME,"
					+ "tp.category," + "tp.url," + "tp.app "
					+ "from t_priv tp where tp.name like '%" + Name + "%'"
					+ "or tp.url like '%" + Name + "%'";
			SQLQuery query = this.getSession().createSQLQuery(hql);

			List list = query.list();

			if (list.size() > 0) {
				return list;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return null;
	}

}
