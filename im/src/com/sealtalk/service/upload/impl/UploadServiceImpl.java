package com.sealtalk.service.upload.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sealtalk.common.SysInterface;
import com.sealtalk.common.Tips;
import com.sealtalk.model.TCutLogoTemp;
import com.sealtalk.service.upload.UploadService;
import com.sealtalk.utils.FileUtil;
import com.sealtalk.utils.HTTPPostUploadUtil;
import com.sealtalk.utils.HttpRequest;
import com.sealtalk.utils.ImageUtils;
import com.sealtalk.utils.JSONUtils;
import com.sealtalk.utils.LogUtils;
import com.sealtalk.utils.PropertiesUtils;
import com.sealtalk.utils.StringUtils;
import com.sealtalk.utils.TimeGenerator;

public class UploadServiceImpl implements UploadService {

	private static final Logger logger = LogManager.getLogger(UploadServiceImpl.class);
	
	@Override
	public String cutImage(String userId, String x, String y, String width,
			String height, String angle, File imageFile, String realPath) {

		JSONObject jo = new JSONObject();

		if (StringUtils.getInstance().isBlank(userId)
				|| StringUtils.getInstance().isBlank(x)
				|| StringUtils.getInstance().isBlank(y)
				|| StringUtils.getInstance().isBlank(width)
				|| StringUtils.getInstance().isBlank(height)
				|| StringUtils.getInstance().isBlank(angle)
				|| imageFile == null) {
			jo.put("code", -1);
			jo.put("text", Tips.WRONGPARAMS.getText());
		} else {
			String seperate = PropertiesUtils.getStringByKey("dir.seperate");
			String resourcePath = "upload" + seperate + "images" + seperate;
			boolean status = true;

			if (imageFile != null) {
				ArrayList<String> names = new ArrayList<String>();

				try {
					// 文件名
					String name = imageFile.getName();
					File dir = new File(realPath + resourcePath);

					if (!dir.exists()) {
						dir.mkdirs();
					}

					// 先把用户上传到原图保存到服务器上
					File file = new File(dir, name);

					FileUtil.copyFile(imageFile, file);

					if (file.exists()) {
						String suffix = PropertiesUtils
								.getStringByKey("upload.suffix");
						String size = PropertiesUtils
								.getStringByKey("upload.size");
						String newName = userId + "-"
								+ TimeGenerator.getInstance().getUnixTime();
						String srcImg = realPath + resourcePath + name;
						String newFileName = realPath + resourcePath + newName;

						String destImg = newFileName + "." + suffix;
						String[] sizes = StringUtils.getInstance().stringSplit(
								size, ",");

						int scaleWidth = StringUtils.getInstance().strToInt(
								sizes[0]);
						int scaleHeight = StringUtils.getInstance().strToInt(
								sizes[1]);

						String scaleFilename = newFileName + "_" + scaleWidth
								+ "_" + scaleHeight + "." + suffix;

						System.out.println("newFileName: " + newFileName);

						names.add(srcImg);
						names.add(destImg);

						int xInt = StringUtils.getInstance().strToInt(
								StringUtils.getInstance().clearNumPoint(x));
						int yInt = StringUtils.getInstance().strToInt(
								StringUtils.getInstance().clearNumPoint(y));
						int widthInt = StringUtils.getInstance().strToInt(
								StringUtils.getInstance().clearNumPoint(width));
						int heightInt = StringUtils.getInstance()
								.strToInt(
										StringUtils.getInstance()
												.clearNumPoint(height));
						int angleInt = StringUtils.getInstance().strToInt(
								StringUtils.getInstance().clearNumPoint(angle));

						boolean[] flag = new boolean[2];

						// 旋转后剪裁图片
						flag[0] = ImageUtils.cutAndRotateImage(srcImg, destImg,
								xInt, yInt, widthInt, heightInt, angleInt,
								suffix);

						// //缩放图片,生成不同大小的图片，应用于不同的大小的头像显示
						flag[1] = ImageUtils.scale2(destImg, scaleFilename,
								scaleWidth, scaleHeight, true, suffix);

						if (flag[0] && flag[1]) {
							List<TCutLogoTemp> cltList = new ArrayList<TCutLogoTemp>();

							for (int i = 0; i < names.size(); i++) {
								System.out.println(names.get(i));
								FileUtil.deleteFile(names.get(i));
							}

							String picName = newName + "_" + scaleWidth + "_" + scaleHeight + "." + suffix;
							JSONObject p = new JSONObject();
							p.put("logName", picName);
							p.put("userId", userId);

							HttpRequest.getInstance().sendPost(SysInterface.SAVETEMPPIC.getName(), p);

							//服务器对传
							String protocol = PropertiesUtils.getStringByKey("auth.protocol");
				        	String host = PropertiesUtils.getStringByKey("auth.host");
				        	String sys = PropertiesUtils.getStringByKey("auth.sys");
				        	
				        	String imgPath = protocol + "://" + host + "/" + sys + "/abmember!httpUploadAb?fileName="+picName;
				        	String picUploadName = realPath + resourcePath + picName;
							String ret = HTTPPostUploadUtil.getInstance().httpUpload(imgPath, picUploadName);
				        	
							JSONObject json = JSONUtils.getInstance().stringToObj(ret);
							
							if (json.getInt("code") == 1) {
								//FileUtil.deleteFile(picUploadName);
								jo.put("code", 1);
								jo.put("text", picName);
							} else {
								this.delUserLogos(userId, picName);
								status = false;
							}
						} else {
							status = false;
						}
					} else {
						status = false;
					}

					if (!status) {
						jo.put("code", 0);
						jo.put("text", Tips.FAIL.getText());
					}
				} catch (Exception e) {
					logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
					e.printStackTrace();
				}
			}
		}

		logger.info(jo.toString());
		return jo.toString();
	}

	@Override
	public String saveSelectedPic(String userId, String picName) {
		String ret = null;

		try {
			if (StringUtils.getInstance().isBlank(userId)
					|| StringUtils.getInstance().isBlank(picName)) {
				JSONObject jo = new JSONObject();
				jo.put("code", -1);
				jo.put("text", Tips.WRONGPARAMS.getText());
				ret = jo.toString();
			} else {
				JSONObject p = new JSONObject();
				p.put("userId", userId);
				p.put("picName", picName);

				ret = HttpRequest.getInstance().sendPost(
						SysInterface.SAVEPIC.getName(), p);

			}
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		logger.info(ret);
		return ret;
	}

	@Override
	public String delUserLogos(String userId, String picName) {
		JSONObject jo = new JSONObject();
		String result = null;
		
		try {
			if (StringUtils.getInstance().isBlank(userId)
					|| StringUtils.getInstance().isBlank(picName)) {
				jo.put("code", -1);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			} else {
				JSONObject p = new JSONObject();
				p.put("userId", userId);
				p.put("picName", picName);
				String ret = HttpRequest.getInstance().sendPost(SysInterface.ISUSEDPIC.getName(), p);
				JSONObject jr = JSONUtils.getInstance().stringToObj(ret);
				
				boolean used = jr.getBoolean("text");
				
				if (used) {
					jo.put("code", -1);
					jo.put("text", Tips.USEDLOGO.getText());
					result = jo.toString();
				} else {
					p.remove("sign");
					p.remove("timestamp");
					result = HttpRequest.getInstance().sendPost(SysInterface.DELUSERLOGS.getName(), p);
				}
			}
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		logger.info(result);
		return result;
	}

	@Override
	public String getUserLogos(String userId) {
		JSONObject jo = new JSONObject();
		String result = null;
		
		try {
			if (StringUtils.getInstance().isBlank(userId)) {
				jo.put("code", -1);
				jo.put("text", Tips.WRONGPARAMS.getText());
				result = jo.toString();
			} else {
				JSONObject p = new JSONObject();
				p.put("userId", userId);
				result = HttpRequest.getInstance().sendPost(SysInterface.GETUSERLOGOS.getName(), p);
			}
		} catch (Exception e) {
			logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
			e.printStackTrace();
		}
		logger.info(result);
		return result;
	}

	@Override
	public String uploadUserLogNotCut(String userId, File imageFile, String realPath) {
		JSONObject jo = new JSONObject();

		if (StringUtils.getInstance().isBlank(userId) || imageFile == null) {
			jo.put("code", -1);
			jo.put("text", Tips.WRONGPARAMS.getText());
		} else {
			if (imageFile != null) {
				try {
					// 文件名
					// String name= imageFile.getName();
					/*String protocol = PropertiesUtils.getStringByKey("auth.protocol");
		        	String host = PropertiesUtils.getStringByKey("auth.host");
		        	String sys = PropertiesUtils.getStringByKey("auth.sys");
		        	String realPath = protocol + "://" + host + "/" + sys + "/";*/
		        	String seperate = PropertiesUtils.getStringByKey("dir.seperate");
					String resourcePath = "upload" + seperate + "images" + seperate;
		        	
					File dir = new File(realPath + resourcePath);

					if (!dir.exists()) {
						dir.mkdirs();
					}

					String suffix = PropertiesUtils
							.getStringByKey("upload.suffix");
					String newName = userId + "-"
							+ TimeGenerator.getInstance().getUnixTime() + "."
							+ suffix;

					File file = new File(dir, newName);

					FileUtil.copyFile(imageFile, file);
					JSONObject p = new JSONObject();
					p.put("logName", newName);
					p.put("userId", userId);

					HttpRequest.getInstance().sendPost(SysInterface.SAVETEMPPIC.getName(), p);
					
					String protocol = PropertiesUtils.getStringByKey("auth.protocol");
		        	String host = PropertiesUtils.getStringByKey("auth.host");
		        	String sys = PropertiesUtils.getStringByKey("auth.sys");
		        	
		        	String imgPath = protocol + "://" + host + "/" + sys + "/abmember!httpUploadAb?fileName="+newName;
		        	String picUploadName = realPath + resourcePath + newName;
					String ret = HTTPPostUploadUtil.getInstance().httpUpload(imgPath, picUploadName);
		        	
					JSONObject json = JSONUtils.getInstance().stringToObj(ret);
					
					if (json.getInt("code") == 1) {
						//FileUtil.deleteFile(picUploadName);
						jo.put("code", 1);
						jo.put("text", newName); 
					} else {
						this.delUserLogos(userId, newName);
						jo.put("code", 0);
						jo.put("text", Tips.FAIL.getText());
					}
					//this.saveSelectedPic(userId, newName);
				} catch (Exception e) {
					logger.error(LogUtils.getInstance().getErrorInfoFromException(e));
					e.printStackTrace();
				}
			}
		}

		logger.info(jo.toString());
		return jo.toString();
	}

}
