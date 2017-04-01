package com.organ.dao.limit.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;

import com.organ.common.BaseDao;
import com.organ.dao.limit.LimitDao;
import com.organ.model.TPriv;
import com.organ.utils.PrivUrlNameUtil;

public class LimitDaoImpl extends BaseDao<TPriv, Long> implements LimitDao {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(LimitDaoImpl.class);

	/**
	 * 实现添的接口
	 */
	@Override
	public int updatePriv(int parentId, String name, String app) {
		// TODO Auto-generated method stub
		TPriv tPriv = new TPriv();
		tPriv.setParentId(parentId);
		tPriv.setName(name);
		tPriv.setApp(app);
		tPriv.setUrl(PrivUrlNameUtil.initUrlName(name));
		tPriv.setListorder(0); // 这个不能为空
		//tPriv.setCategory("0");
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
	public int editPriv(int priv_id, String pid, String name, String app) {
		// TODO Auto-generated method stub
		try {
			String hql = "update TPriv tp set tp.name= '" + name
					+ "',tp.parentId='" + pid + "',tp.category='" + pid
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
	public List searchPriv(String Name, int pagesize, int pageindex) {
		try {
			int start = pageindex * pagesize;
			String hql;
			if (!StringUtils.isBlank(Name)) {
				hql = "select " + "tp.id," + "tp.parent_id," + "tp.NAME,"
						+ "tp.category," + "tp.url," + "tp.app "
						+ "from t_priv tp where tp.name like '%" + Name + "%'"
						+ "or tp.url like '%" + Name + "%' limit " + start
						+ "," + pagesize;
			} else {
				hql = "select " + "tp.id," + "tp.parent_id," + "tp.NAME,"
						+ "tp.category," + "tp.url," + "tp.app "
						+ "from t_priv tp limit " + start + "," + pagesize;
			}
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

	@Override
	public int getCount() {
		try {
			int result = count("from TPriv");
			return result;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int getSearchCount(String name) {
		// TODO Auto-generated method stub
		String sql;
		try {
			if (!StringUtils.isBlank(name)) {
				sql = "select count(*) from (" + "select " + "tp.id," + "tp.parent_id,"
						+ "tp.NAME," + "tp.category," + "tp.url," + "tp.app "
						+ "from t_priv tp where tp.name like '%" + name + "%'"
						+ "or tp.url like '%" + name + "%') as tt";
			} else {
				sql = "select count(*) from (" + "select " + "tp.id," + "tp.parent_id,"
						+ "tp.NAME," + "tp.category," + "tp.url," + "tp.app "
						+ "from t_priv tp) as tt";
			}
			int SearchCount =Integer.parseInt(this.getSession().createSQLQuery(sql).list().get(0).toString());
			return SearchCount;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return 0;
	}

}
