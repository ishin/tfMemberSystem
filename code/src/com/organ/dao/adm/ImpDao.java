package com.organ.dao.adm;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.organ.model.ImpUser;
import com.organ.utils.StringUtils;

public class ImpDao {

	private SessionFactory factory = null;
	private ImpUser user = null;

	public ImpDao(SessionFactory factory) {
		this.factory = factory; 
	}
	
	public void setUser(ImpUser user) {
		this.user = user;
	}
	
	public boolean testManagerCollision() throws HibernateException{
		
		String sql = "select * from t_branch b, t_member m"
				+ " where b.manager_id = m.id"
				+ " and b.name = '" + user.getBranch() + "'"
				+ " and m.fullname = '" + user.getManager() + "'";
		
		List list = runSql(sql);
		
		return list.isEmpty();
	}
	
	public boolean testManager() throws HibernateException {
	
		String sql = "select * from t_member"
				+ " where fullname = '" + user.getManager() + "'";
		
		List list = runSql(sql);
		
		return !list.isEmpty();
	}
	
	public boolean testBranch() throws HibernateException {
		
		String sql = "select * from t_branch"
				+ " where name = '" + user.getBranch() + "'";
		
		List list = runSql(sql);
		
		return !list.isEmpty();
	}
	
	public boolean testMobileExist() throws HibernateException{
		if (StringUtils.getInstance().isBlank(user.getMobile())) {
			return false;
		}
		String sql = "select * from t_member"
				+ " where mobile ='" + user.getMobile() + "' and isdel=1";
		
		List list = runSql(sql);
		
		return !list.isEmpty();
	}
	
	public boolean testWorkNoExist(int organId) throws HibernateException{
		
		if (StringUtils.getInstance().isBlank(user.getWorkno())) {
			return false;
		}
		String sql = "select * from t_member"
			+ " where workno ='" + user.getWorkno() + "' and organ_id=" + organId + " and isdel=1";
		
		List list = runSql(sql);
		
		return !list.isEmpty();
	}

	public boolean testEmailNoExist() throws HibernateException{
		
		if (StringUtils.getInstance().isBlank(user.getEmail())) {
			return false;
		}
 		String sql = "select * from t_member"
			+ " where email ='" + user.getEmail() + "' and isdel=1";
		
		List list = runSql(sql);
		
		return !list.isEmpty();
	}
	
	private List runSql(String sql) {
		
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		List list = session.createSQLQuery(sql).list();
		t.commit();
		session.close();

		return list;
	}
}
