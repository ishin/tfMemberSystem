package com.sealtalk.common;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;


/**
 * @since jdk1.7
 * @author hao_dy
 */
@SuppressWarnings("unchecked")
public class BaseDao<T, PK extends Serializable> extends HibernateDaoSupport implements IBaseDao<T, PK>{
    
    private final Class<T> aclass;
    
    
    public BaseDao(){
        this.aclass = (Class<T>)((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public int count(final String hql,final Map mparams) {
        
        List tempList = this.getHibernateTemplate().executeFind(new HibernateCallback(){
            public List doInHibernate(Session session) throws HibernateException, SQLException {
                
                StringBuffer countQuery = new StringBuffer("select count(*) ");
                countQuery.append(hql);
                Query query = session.createQuery(countQuery.toString());
                String params[] = query.getNamedParameters();
                for(int i=0;i<params.length;i++){
                    query.setParameter(params[i], mparams.get(params[i]));
                }
                List result = query.list();
                return result;
            }
        });
        
        Object obj = tempList!=null&&tempList.size()>0?tempList.get(0):0;
        if (obj instanceof Long) {
            Integer count =    Integer.valueOf(String.valueOf(obj));
            return count;
        }else{
            Integer count = Integer.valueOf(String.valueOf(obj));
            return count.intValue();
        }
    }

    public Integer count(String hql) {
        List<Object> result = getHibernateTemplate().find("select count(*) "+hql);
        Object obj = result.get(0);
        if(obj instanceof Integer){
            Integer count =    (Integer)obj;
            return count;
        }else{
            Long count = (Long)obj;
            return count.intValue();
        }
    }

    public int countByCriteria(DetachedCriteria dc) {
        
        return 0;
    }

    public void delete(final T obj) {
        getHibernateTemplate().execute(new HibernateCallback(){
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                session.clear();
                session.delete(obj);
                return null;
            }

        });
    }
    
    public void deleteByList(final List<T> obj) {
        getHibernateTemplate().execute(new HibernateCallback(){
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                session.clear();
                for(T t:obj){
                    session.delete(t);
                }
                return null;
            }
            
        });
    }

    public int deleteAll(final String tableName) {
        
        StringBuffer hql = new StringBuffer("delete from ");
        hql.append(tableName);
        return executeUpdate(hql.toString());
    }

    public int executeUpdate(final String hql) {
        
        Object obj = getHibernateTemplate().execute(new HibernateCallback(){
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                
                Query query = session.createQuery(hql);
                int result = 0 ; 
                result = query.executeUpdate();
                getHibernateTemplate().flush();
                return result;
            }
        });
        if(obj!=null)
            return ((Integer)obj).intValue();
        else
            return 0;
    }

    public int executeUpdate(final String hql,final Map pMap) {
        
        Object obj = getHibernateTemplate().execute(new HibernateCallback(){
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                
                Query query = session.createQuery(hql);
                String params[] = query.getNamedParameters();
                for(int i=0;i<params.length;i++){
                    query.setParameter(params[i], pMap.get(params[i]));
                }
                int result = 0;
                result = query.executeUpdate();
                getHibernateTemplate().flush();
                return result;
            }
        });
        if(obj!=null)
            return ((Integer)obj).intValue();
        else
            return 0;
    }

    public List<T> find(final String hql) {
        return getHibernateTemplate().find(hql);
    }
    
    public List<T> findObj(final String hql) {
        return getHibernateTemplate().find(hql);
    }

    public List<T> find(final String hql, final Map map) {
        return getHibernateTemplate().executeFind(new HibernateCallback(){
            public List<T> doInHibernate(Session session) throws HibernateException, SQLException {
                
                Query query = session.createQuery(hql);
                String[] pamars = query.getNamedParameters();  
                for(int i=0;i<pamars.length;i++){
                    query.setParameter(pamars[i], map.get(pamars[i]));
                }
                List<T> results = new ArrayList();
                results = query.list();
                return results;
            }
        });
    }
    
    public List findObj(final String hql, final Map map) {
        return getHibernateTemplate().executeFind(new HibernateCallback(){
            public List<T> doInHibernate(Session session) throws HibernateException, SQLException {
                
                Query query = session.createQuery(hql);
                String[] pamars = query.getNamedParameters();  
                for(int i=0;i<pamars.length;i++){
                    query.setParameter(pamars[i], map.get(pamars[i]));
                }
                List<T> results = new ArrayList();
                results = query.list();
                return results;
            }
        });
    }

    public List<T> findAll() throws DataAccessException {
        return findByCriteria();
    }
    
    public List<T> findByCriteria(DetachedCriteria dc, int firstResult, int maxResults) {
        return null;
    }

    public T get(PK id) {
        return (T)getHibernateTemplate().get(aclass, id);
    }
    
    public Object getObj(Class cls,PK id) {
        return (T)getHibernateTemplate().get(cls, id);
    }

    public T load(PK id) throws DataAccessException {
        
        return (T)getHibernateTemplate().load(aclass, id);
    }

    public void save(T obj) {//1
        
        this.getHibernateTemplate().save(obj);
    }
    
    public void saveByObj(T obj) {
        
        this.getHibernateTemplate().save(obj);
    }

    public void save(List<T> ls) {
        
        this.getHibernateTemplate().saveOrUpdateAll(ls);
        getHibernateTemplate().flush();
    }

    public void saveOrUpdateMerge(T obj)
    {
        obj=(T)this.getHibernateTemplate().merge(obj);
        this.getHibernateTemplate().saveOrUpdate(obj);
        getHibernateTemplate().flush();
        getHibernateTemplate().clear();
    }
    
    public void saveOrUpdate(T obj) {
        this.getHibernateTemplate().saveOrUpdate(obj);
        getHibernateTemplate().flush();
    }
    
    public void saveOrUpdateClear(T obj) {
        this.getHibernateTemplate().saveOrUpdate(obj);
        getHibernateTemplate().flush();
        getHibernateTemplate().clear();
    }
    public void saveOrUpdate(List<T> lsj) {
        this.getHibernateTemplate().saveOrUpdateAll(lsj);
        getHibernateTemplate().flush();
    }

    public void update(T obj) {
        this.getHibernateTemplate().update(obj);
        getHibernateTemplate().flush();
    }
    
    public HibernateTemplate getHibTemplate(){
        return this.getHibernateTemplate();
    }

    public int delete(String hql, Map map) {
        return executeUpdate(hql, map);
    }

    public int update(String hql, Map map) {
        return executeUpdate(hql, map);
    }

    public int update(String hql) {
        return executeUpdate(hql);
    }

    public int delete(String hql) {
        return executeUpdate(hql);
    }
    
    public List<T> find(final String hql,final int pageNo,final int maxResults) {
        return getHibernateTemplate().executeFind(new HibernateCallback(){
            public List<T> doInHibernate(Session session) throws HibernateException, SQLException {
                
                Query query = session.createQuery(hql);
                if(maxResults>0)
                    query.setFirstResult((pageNo-1)*maxResults).setMaxResults(maxResults);
                List<T> results = query.list();
                return results;
            }
        });
    }
    
    public List<T> find(final String hql,final Map map,final int pageNo,final int maxResults) {
        return getHibernateTemplate().executeFind(new HibernateCallback(){
            public List<T> doInHibernate(Session session) throws HibernateException, SQLException {
                
                Query query = session.createQuery(hql);
                String[] pamars = query.getNamedParameters();  
                for(int i=0;i<pamars.length;i++){
                    query.setParameter(pamars[i], map.get(pamars[i]));
                }
                if(maxResults>0)
                    query.setFirstResult((pageNo-1)*maxResults).setMaxResults(maxResults);
                List<T> results = new ArrayList();
                results = query.list();
                return results;
            }
        });
    }
    
    public List findObj(final String hql,final Map map,final int pageNo,final int maxResults) {
        
        return getHibernateTemplate().executeFind(new HibernateCallback(){
            public List<T> doInHibernate(Session session) throws HibernateException, SQLException {
                
                Query query = session.createQuery(hql);
                String[] pamars = query.getNamedParameters();  
                for(int i=0;i<pamars.length;i++){
                    query.setParameter(pamars[i], map.get(pamars[i]));
                }
                if(maxResults>0)
                    query.setFirstResult((pageNo-1)*maxResults).setMaxResults(maxResults);
                List<T> results = new ArrayList();
                results = query.list();
                return results;
            }
        });
    }

    public void deleteById(final PK id) {
        getHibernateTemplate().execute(new HibernateCallback(){
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                session.delete(session.get(aclass, id));
                return null;
            }

        });
    }
    
    public List<T> findByCriteria(final int firstResult, final int maxResults, final Criterion... criterion) throws DataAccessException{
        return findByCriteria(firstResult, maxResults, null, false, criterion);
    }
    
    public List<T> findByCriteria(final int firstResult, final int maxResults, final String sortProperty, final boolean ascend, final Criterion... criterion){
        return getHibernateTemplate().executeFind(new HibernateCallback(){
            public List<T> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria crit = session.createCriteria(aclass);
                if(criterion != null){
                    for(Criterion c : criterion){
                        crit.add(c);
                    }
                }
                if(firstResult > 0)
                    crit.setFirstResult(firstResult);
                if(maxResults > 0)
                    crit.setMaxResults(maxResults);
                if(sortProperty != null && sortProperty.trim().length() > 0){
                    crit.addOrder(ascend ? Order.asc(sortProperty) : Order.desc(sortProperty));
                }
                return crit.list();
            }

        });
    }
    
    public List<T> findByCriteria(final Criterion...criterion) throws DataAccessException {
        return findByCriteria(-1, -1, criterion);
    }
    
    public Criteria getCriteria(){
        return this.getSession().createCriteria(aclass);
    }
    
    public int countByCriteria(final Criterion... criterion) throws DataAccessException{
        Object obj = getHibernateTemplate().execute(new HibernateCallback(){
            public Integer doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria crit = session.createCriteria(aclass);
                for(Criterion c : criterion){
                    crit.add(c);
                }
                crit.setProjection(Projections.rowCount());
                Integer total = (Integer)crit.uniqueResult();
                return total;
            }

        });
        if(obj!=null)
            return (Integer)obj;
        return 0;
    }
    
     public T mergeObj(T obj){
         return (T)this.getSession().merge(obj);
     }



 	/*
 	 * shorcut util
 	 * by alopex 2017.1.13
 	 */
 	protected List runSql(String sql) {
 		
 		return this.getSession().createSQLQuery(sql).list();
 		
 	}

	@Override
	public Integer getMax(final String field, final String hql) {
        
        List tempList = this.getHibernateTemplate().executeFind(new HibernateCallback(){
            public List doInHibernate(Session session) throws HibernateException, SQLException {
                
                StringBuffer countQuery = new StringBuffer("select max(" + field + ") ");
                countQuery.append(hql);
               Query query = session.createQuery(countQuery.toString());
                List result = query.list();
                return result;
            }
        });
        
        Object obj = tempList!=null&&tempList.size()>0?tempList.get(0):0;
        if (obj instanceof Long) {
            Integer count =    Integer.valueOf(String.valueOf(obj));
            return count;
        }else{
            Integer count = Integer.valueOf(String.valueOf(obj));
            return count.intValue();
        }
	}
}