package com.organ.service.upload.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import com.organ.common.Tips;
import com.organ.dao.member.MemberDao;
import com.organ.dao.upload.CutLogoTempDao;
import com.organ.model.TCutLogoTemp;
import com.organ.service.upload.UploadService;
import com.organ.utils.FileUtil;
import com.organ.utils.PropertiesUtils;
import com.organ.utils.StringUtils;
import com.organ.utils.TimeGenerator;

public class UploadServiceImpl implements UploadService {

	@Override
	public String saveSelectedPic(String userId, String picName) {
		JSONObject jo = new JSONObject();
		
		try {
			if (StringUtils.getInstance().isBlank(userId) || StringUtils.getInstance().isBlank(picName)) {
				jo.put("code", -1);
	 			jo.put("text", Tips.WRONGPARAMS.getText());
			} else {
				int userIdInt = Integer.parseInt(userId);
				TCutLogoTemp cltList = cutLogoTempDao.getTempLogoForIdAndPicName(userIdInt, picName);
				
				if (cltList != null && cltList.getLogoName().equals(picName)) {
					int ret = memberDao.updateUserLogo(userIdInt, picName);
					
					if (ret > 0) {
						jo.put("code", 1);
						jo.put("text", Tips.OK.getText());
					} else {
						jo.put("code", 0);
						jo.put("text", Tips.FAIL.getText());
					}
				} else {
					jo.put("code", 0);
					jo.put("text", Tips.NOLOGOERR.getText());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jo.toString();
	}
	

	@Override
	public String delUserLogos(String userId, String picName) {
		JSONObject jo = new JSONObject();
		if (StringUtils.getInstance().isBlank(userId) || StringUtils.getInstance().isBlank(picName)) {
			jo.put("code", -1);
 			jo.put("text", Tips.WRONGPARAMS.getText());
		} else {
			try {
				int userIdInt = StringUtils.getInstance().strToInt(userId);
				boolean used = memberDao.isUsedPic(userIdInt, picName);
				
				if (used) {
					jo.put("code", -1);
		 			jo.put("text", Tips.USEDLOGO.getText());
				} else {
					int ret = cutLogoTempDao.delUserLogos(userIdInt, picName);
					
					if (ret > 0) {
						jo.put("code", 1);
						jo.put("text", Tips.OK.getText());
					} else {
						jo.put("code", 0);
						jo.put("text", Tips.FAIL.getText());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return jo.toString();
	}
	
	
	@Override
	public String getUserLogos(String userId) {
		JSONObject jo = new JSONObject();
		
		if (StringUtils.getInstance().isBlank(userId)) {
			jo.put("code", -1);
 			jo.put("text", Tips.WRONGPARAMS.getText());
		} else {
			try {
				int userIdInt = StringUtils.getInstance().strToInt(userId);

				List<TCutLogoTemp> clTList = cutLogoTempDao.getUserLogos(userIdInt);
				
				if (clTList != null) {
					ArrayList<String> picArr = new ArrayList<String>();
					
					for(int i = 0; i < clTList.size(); i++) {
						picArr.add(clTList.get(i).getLogoName());
					} 
					
					jo.put("code", 1);
					jo.put("text", picArr);
				} else {
					jo.put("code", 0);
					jo.put("text", Tips.NOLOGOERR.getText());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return jo.toString();
	}

	@Override
	public String uploadUserLogNotCut(String userId, File imageFile, String realPath) {
		JSONObject jo = new JSONObject();
		
		if (StringUtils.getInstance().isBlank(userId) || 
				imageFile == null) {
			jo.put("code", -1);
			jo.put("text", Tips.WRONGPARAMS.getText());
		} else {
			String seperate = PropertiesUtils.getStringByKey("dir.seperate");
	        String resourcePath="upload" + seperate +  "images" + seperate;  

			int userIdInt = StringUtils.getInstance().strToInt(userId);
	        
	        if(imageFile != null){  
	             try{  
		            //文件名  
		             //String name= imageFile.getName();  
		             
		             File dir = new File(realPath + resourcePath);  
		             
		             if (!dir.exists()){  
		                 dir.mkdirs();  
		             }  
		             
		             String suffix = PropertiesUtils.getStringByKey("upload.suffix");
	            	 String newName = userId + "-" + TimeGenerator.getInstance().getUnixTime() + "." + suffix;
	            	 
		             File file = new File(dir, newName);  
		             
		             FileUtil.copyFile(imageFile, file);
		            	
                	 TCutLogoTemp clte = new TCutLogoTemp();
                	 
                	 clte.setLogoName(newName);
                	 clte.setUserId(userIdInt);
                	 
                	 cutLogoTempDao.saveTempPic(clte);
                	 
                	 this.saveSelectedPic(userId, newName);
                	 
                	jo.put("code", 1);
         			jo.put("text", clte.getLogoName());
	             } catch (Exception e) {  
	            	jo.put("code", 0);
         			jo.put("text", Tips.FAIL.getText());
	                e.printStackTrace();  
	            }  
	        }
         }  
		
		return jo.toString();
	}

	@Override
	public String saveTempPic(String userId, String logName) {
		try {
			 TCutLogoTemp clte = new TCutLogoTemp();
        	 
        	 clte.setLogoName(logName);
        	 clte.setUserId(Integer.parseInt(userId));
        	 
        	 cutLogoTempDao.saveTempPic(clte);
        	 
        	 this.saveSelectedPic(userId, logName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public String httpUpload(String fileName, InputStream input, String realPath) {
		FileOutputStream fos = null;
		JSONObject response = new JSONObject();
		
		try {
			if (!StringUtils.getInstance().isBlank(fileName)) {
				String imgPath = PropertiesUtils.getStringByKey("cfg.uploaddir");
				String sep = PropertiesUtils.getStringByKey("dir.seperate");
				
				if (imgPath.endsWith(sep)) {
					imgPath += sep;
				}
				
				String dirPath = realPath + imgPath;
				String fileFullPath = dirPath + fileName;
				File file = new File(dirPath);
				
				if (!file.exists()) {
					file.mkdirs();
				}
				
				fos = new FileOutputStream(fileFullPath);
				int size = 0;
				byte[] buffer = new byte[1024];
				
				while ((size = input.read(buffer, 0, 1024)) != -1) {
					fos.write(buffer, 0, size);
				}
				// 生成响应的json字符串
				response.put("code", 1);
				response.put("text", Tips.OK.getText());
			} else {
				response.put("code", 0);
				response.put("text", Tips.FAIL.getText());
			}
		} catch (IOException e) {
			response.put("code", 0);
			response.put("text", e.getMessage());
		} finally {
			try {
				if (input != null) {
					input.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return response.toString();
	}
	
	private MemberDao memberDao;
	private CutLogoTempDao cutLogoTempDao;

	public MemberDao getMemberDao() {
		return memberDao;
	}

	public void setMemberDao(MemberDao memberDao) {
		this.memberDao = memberDao;
	}

	public CutLogoTempDao getCutLogoTempDao() {
		return cutLogoTempDao;
	}

	public void setCutLogoTempDao(CutLogoTempDao cutLogoTempDao) {
		this.cutLogoTempDao = cutLogoTempDao;
	}

}
