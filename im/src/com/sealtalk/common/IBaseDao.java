package com.sealtalk.common;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;

@SuppressWarnings("unchecked")
public interface IBaseDao<T, PK extends Serializable> {
    /**
     * 保存PO对象
     * @param obj PO所要保存值对象
     * @return 保存记录数 0：为保存失败
     * */
    public void save(T obj);
    /**
     * 保存PO对象
     * @param obj PO所要保存值对象
     * @return 保存记录数 0：为保存失败
     * */
    public void saveByObj(T obj);
    
    /**
     * 保存多个PO对象，将PO封装到List中
     * @param obj PO所要保存值对象
     * @return 保存记录数 0：为保存失败
     * */
    public void save(List<T> ls);
    
    /**
     * 删除表中所有数据,通过Object.ClassName得到表名称
     * @param tableName删除表名称
     * @return 删除记录数据
     * */
    public int deleteAll(String tableName);
    
    public void deleteById(PK id);
    
    /**  
     * 根据查询语句，返回对象列表  
     *   
     * @param hql  
     *            查询语句  
     * @return 符合查询语句的对象列表 
     */  
    public List find(String hql);
    
    /**  
     * 返回指定起始位置，指定条数的对象  
     *   
     * @param hql  
     *            查询语句  
     * @param firstResult  
     *            起始位置  
     * @param maxResults  
     *            最多纪录数  
     * @return list 结果列表  
     */  
    //public List<T> find(String hql,int firstResult,int maxResults);
    
    /**  
     * 返回指定起始位置，指定条数的对象  
     *   
     * @param hql  
     *            查询语句  
     * @param pageNo  
     *            页数  
     * @return list 结果列表  
     */  
    public List<T> find(String hql,int pageNo,int maxResults);
    
    /**  
     * 查询语句需要的条件参数。通过map传递  
     *   
     * @param hql  
     *            查询语句  
     * @param map  
     *            参数  
     * @param firstResult  
     *            起始位置  
     * @param maxResults  
     *            最多纪录数  
     * @return list 结果列表  
     */  
    //public List<T> find(String hql, Map map, int firstResult,int maxResults);
    
    /**  
     * 查询语句需要的条件参数。通过map传递  
     *   
     * @param hql  
     *            查询语句  
     * @param map  
     *            参数  
     * @param pageNo  
     *            页数 
     */  
    public List<T> find(String hql, Map map, int pageNo,int maxResults);
    
    /**  
     * 根据查询语句，返回对象列表  
     *   
     * @param hql  
     *            查询语句  
     * @return 符合查询语句的对象列表 
     */  
    public List<T> find(String hql, Map map);
    
    
    /**  
     * 通过Hql 执行update/delete操作  
     *   
     * @param hql  
     * @return  
     */  
    public int executeUpdate(String hql);
    
    /**  
     * 查询表中所有记录  
     */ 
    public List<T> findAll() ;
    
    /**  
     * 通过 DetachedCriteria 进行查询指定查询条数
     * @param firstResult  
     *            起始位置  
     * @param maxResults  
     *            最多纪录数 
     * @param dc 
     * 
     * @return 符合查询语句的对象列表
     */  
    public List<T> findByCriteria(DetachedCriteria dc,int firstResult, int maxResults);
    
    /**
     * 统计符合查询语句的条数
     * @param dc 
     * */
    public int countByCriteria(DetachedCriteria dc);
    
    /**  
     * 通过Hql 执行update/delete操作 带条件查询
     *   
     * @param hql  
     * @return  
     */ 
    public int executeUpdate(String hql, Map pMap);
    
    /**
     * 删除obj指定ID记录,通过捕捉异常来判断是否删除成功
     * @param obj 指定ID记录数
     * */
    public void delete(T obj);
    
    /**
     * 根据ID从内存中加载记录
     * @param aclass 类
     * @param id 可以为任意类型
     * */
    public T load(PK id) ;
    
    /**
     * 加载数据
     * */
    public T get(PK id);
    
    public Object getObj(Class cls,PK id);
    
    /**
     * 保存或修改
     * @param obj 保存对象
     * */
    public void saveOrUpdate(T obj);
    
    public void saveOrUpdate(List<T> lsj);
    /**
     * 更新记录
     * @param obj 保存对象
     * */
    public void update(T obj);
    /**
     * 更新记录
     * @param obj 保存对象
     * */
    public int update(String hql);
    /**  
     * TODO count hql 方法 . 带条件 
     */  
    public int count(String hql, Map params);
    
    /**  
     * TODO count hql 方法
     */  
    public Integer count(String hql);
    
    /**
     * 根据hql删除记录
     * @param hql 删除语句
     * @param map 条件参数
     * @return 删除条数
     * */
    public int delete(String hql,Map map);
    
    /**
     * 根据hql删除记录
     * @param hql 删除语句
     * @param map 条件参数
     * @return 删除条数
     * */
    public int delete(String hql);
    
    /**
     * 根据hql语句，更新记录部分字段
     * @param hql 更新语句
     * @param map 更新参数及条件
     * @return 返回值大于0则更新成功，否则更新失败
     * */
    public int update(String hql,Map map);
    
    /**
     * 返回Object List数组
     * @param hql
     * @param map        hql中的参数和参数值
     * @param pageNo     页码
     * @param maxResults 每页显示最大条数
     * @return
     */
    public List findObj( String hql, Map map, int pageNo, int maxResults);
    
    /**
     * 返回Object List数组
     * @param hql
     * @return
     */
    public List findObj( String hql);
    
    /**
     * 返回Object List数组
     * @param hql
     * @param map        hql中的参数和参数值
     * @return
     */
    public List findObj( String hql, Map map);
    
    /**
     * 
     * 查询
     * @param firstResult首条数据位数
     * @param maxResult  每次取出最大条数
     * @param criterion
     * @return
     * @throws DataAccessException   
     * @return：List<T> 
     */
    public List<T> findByCriteria( int firstResult,  int maxResults,  Criterion... criterion) ;
    
    /**
     * 
     * @功能描述： 查义
     * @param firstResult   首条查询位置
     * @param maxResults    第次查询最大数  小于0查询所有
     * @param sortProperty  排序字段
     * @param ascend        排序方法  true: ASC fasle: DESC
     * @param criterion     
     * @return   
     * @return：List<T> 
     */
    public List<T> findByCriteria( int firstResult,  int maxResults,  String sortProperty,  boolean ascend,  Criterion... criterion);
    
    /**
     * 
     * @功能描述： 
     * @param criterion
     * @return
     * @throws DataAccessException   
     * @return：List<T> 
     */
    public List<T> findByCriteria( Criterion...criterion); 
    //throws DataAccessException;
    
    /**
     * 
     * @功能描述： 
     * @param criterion
     * @return
     * @throws DataAccessException   
     * @return：int 
     */
    public int countByCriteria( Criterion... criterion) ;
    
    /**
     * @功能描述： 根据List删除数据
     * @param obj List<T>
     */
    public void deleteByList(List<T> obj);
    
    /**
     * @功能描述： 合并重复obj后保存或添加
     * @param obj
     */
    public void saveOrUpdateMerge(T obj);
    
    /**
     * 
    * @Title: saveOrUpdateClear 
    * @Description: 多了一句话.clear()
    * @return： void
    * @param：
    * @throws
     */
    public void saveOrUpdateClear(T obj);
    
    public T mergeObj(T obj);
    
    public Integer getMax(String field, String hql);
}