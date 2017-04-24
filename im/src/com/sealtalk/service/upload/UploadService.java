package com.sealtalk.service.upload;

import java.io.File;

public interface UploadService {

	/**
	 * 处理图片
	 * @param userId
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param angle
	 * @param file
	 * @return
	 */
	public String cutImage(String userId, String x, String y, String width,
			String height, String angle, File file, String realPath);

	
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

}
