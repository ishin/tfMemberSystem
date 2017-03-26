package com.organ.dao.limit;

import java.util.List;


public interface LimitDao {

	public int updatePriv(int parentId,String name,String category,String app);//添加权限接口
	
	public int DeletePriv(int priv_id);//编辑权限

	public int editPriv(int priv_id,String pid,String name,String category,String app);//编辑权限
	
	public List searchPriv(String Name);
}
