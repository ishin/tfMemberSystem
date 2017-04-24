package com.organ.dao.limit;

import java.util.List;

import com.organ.common.IBaseDao;
import com.organ.model.TPriv;


public interface LimitDao extends IBaseDao<TPriv, Long>{

	public int updatePriv(int parentId,String name,String app, int organId);//添加权限接口
	
	public int DeletePriv(int priv_id);//删除权限

	public int editPriv(int priv_id,String pid,String name,String app);//编辑权限
	
	public List searchPriv(int organId, String Name,int pagesize,int pageindex);
	
	public int getCount(int organId);
	
	public int getSearchCount(int organId, String name);//获取查询结构的个数
	
	public List getLimitbyRole(Integer roleId2, String appName);//根据姓名获取接口
	
	public List getRoleList(Integer appId, int organId);
	
	public List getPrivNamebytwo(int organId, String appName);//获取父id为2的所有权限名称
	
}

