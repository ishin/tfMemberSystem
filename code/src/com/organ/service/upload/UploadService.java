package com.organ.service.upload;

import java.io.File;

public interface UploadService {

	/**
	 * 确定最终选择的头像
	 * @param userid
	 * @param picname
	 */
	public String saveSelectedPic(String userid, String picname);

	/**
	 * 删除某一张头像从头像库
	 * @param userid
	 * @param picname
	 * @return
	 */
	public String delUserLogos(String userid, String picname);

	/**
	 * 拉取头像库
	 * @param userid
	 * @return
	 */
	public String getUserLogos(String userid);

	/**
	 * 上传头像(非裁剪)
	 * @param userid
	 * @param file
	 * @param realPath
	 * @return
	 */
	public String uploadUserLogNotCut(String userid, File file, String realPath);


	/**
	 * 保存成员头像
	 * @param userId
	 * @param logName
	 * @return
	 */
	public String saveTempPic(String userId, String logName);

}
