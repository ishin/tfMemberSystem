package com.sealtalk.dao.fun;

import com.sealtalk.common.IBaseDao;
import com.sealtalk.model.TFunction;
import com.sealtalk.model.TMsgtop;

/**
 * 辅助功能  
 * @author hao_dy
 * @since jdk1.7
 * @date 2017/01/09
 */
public interface FunctionDao extends IBaseDao<TFunction, Long> {

	/**
	 * 获取系统状态
	 * @param string
	 * @return
	 */
	public TFunction getFunctionStatus(String string);

	/**
	 * 设置系统提示音状态
	 * @param tf
	 */
	public void setFunctionStatus(TFunction tf);

	public void updateFunctionStatus(String name, String status);

	public void delFunctionStatus(TFunction tf);
	
} 
