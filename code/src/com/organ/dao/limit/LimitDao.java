package com.organ.dao.limit;

import java.util.List;

import com.organ.common.IBaseDao;
import com.organ.model.TPriv;


public interface LimitDao extends IBaseDao<TPriv, Long>{

	public int updatePriv(int parentId,String name,String app);//添加权限接口
	
	public int DeletePriv(int priv_id);//删除权限

	public int editPriv(int priv_id,String pid,String name,String app);//编辑权限
	
	public List searchPriv(String Name,int pagesize,int pageindex);
	
	public int getCount();
	
	public int getSearchCount(String name);//获取查询结构的个数
	
	public List getLimitbyRole(Integer roleId,String appName);//根据姓名获取接口
	
	public List getRoleList(String appname);
	
	public List getPrivNamebytwo(String appName);//获取父id为2的所有权限名称
	
}

