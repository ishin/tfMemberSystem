package com.organ.dao.upload;

import java.util.List;

import com.organ.common.IBaseDao;
import com.organ.model.TCutLogoTemp;


public interface CutLogoTempDao extends IBaseDao<TCutLogoTemp, Long> {

	/**
	 * 保存临时裁剪图 片
	 * @param clte
	 */
	public void saveTempPic(TCutLogoTemp clte);

	/**
	 * 根据id获取临时头像
	 * @param userid
	 * @return
	 */
	public List<TCutLogoTemp> getTempLogoForId(int userid);

	/**
	 * 根据用户id和图片名称查找图片
	 * @param userIdInt
	 * @param picName
	 * @return
	 */
	public TCutLogoTemp getTempLogoForIdAndPicName(int userIdInt, String picName);

	/**
	 * 删除指定头像从头像库
	 * @param userIdInt
	 * @param picName
	 * @return
	 */
	public int delUserLogos(int userIdInt, String picName);

	/**
	 * 拉取头像库
	 * @param userIdInt
	 * @return
	 */
	public List<TCutLogoTemp> getUserLogos(int userIdInt);

	public int deleteRelationByIds(String userids, String isLogic);
	
	
} 
