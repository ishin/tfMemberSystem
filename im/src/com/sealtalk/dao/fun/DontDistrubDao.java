package com.sealtalk.dao.fun;

import java.util.List;

import com.sealtalk.common.IBaseDao;
import com.sealtalk.model.TDontDistrub;
import com.sealtalk.model.TFunction;

/**
 * 辅助功能  
 * @author hao_dy
 * @since jdk1.7
 * @date 2017/01/09
 */
public interface DontDistrubDao extends IBaseDao<TDontDistrub, Long> {
	
	/**
	 * 设置消息免打扰
	 * @param tf
	 */
	public void setDontDistrub(TDontDistrub tf);

	/**
	 * 获取免打扰信息
	 * @param userIdInt
	 * @return
	 */
	public List<TDontDistrub> getDistrubListForUserId(int userIdInt);

	/**
	 * 获取单个免打扰信息
	 * @param userIdInt
	 * @param groupIdInt 
	 * @return
	 */
	public TDontDistrub getSingleDistrubListForUserId(int userIdInt, int groupIdInt);

} 
