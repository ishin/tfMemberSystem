package com.sealtalk.dao.fun;

import java.util.List;

import com.sealtalk.common.IBaseDao;
import com.sealtalk.model.TFunction;
import com.sealtalk.model.TMsgtop;

/**
 * 置顶功能  
 * @author hao_dy
 * @since jdk1.7
 * @date 2017/01/09
 */
public interface MsgTopDao extends IBaseDao<TMsgtop, Integer> {

	/**
	 * 获取置顶状态
	 * @param userId
	 * @return
	 */
	public List<TMsgtop> getMsgTop(Integer userId);

	/**
	 * 设置置顶
	 * @param tm
	 */
	public void setMsgTop(TMsgtop tm);

	/**
	 * 获取置顶项数量
	 * @param userIdInt
	 * @return
	 */
	public int getCountMsgTopForUserId(int userIdInt);

	/**
	 * 取消置顶
	 * @param userIdInt
	 * @param topIdInt
	 * @param topType
	 */
	public void cancelMsgTop(int userIdInt, int topIdInt, String topType);
	
} 
