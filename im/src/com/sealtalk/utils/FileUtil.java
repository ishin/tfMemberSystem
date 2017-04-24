package com.sealtalk.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

public class FileUtil {
	private static final int BUFFER_SIZE = 16 * 1024; 
	public static String readFile(String path) {
       try{
	        File f = new File(path);
	        if(!f.exists()){
	        	return null;
	        }else{
		        BufferedReader br = new BufferedReader(new FileReader(f));
		        StringBuffer txt = new StringBuffer();
	    		String temp=null;
	    		while( (temp=br.readLine())!=null){
	    			txt.append(temp + "\n");
		    		temp = null;
				}
		        br.close();
		        return txt.toString();
	        }
	   } catch (IOException e){
		   e.printStackTrace();
		   return null;
       }
	}
	
	public static void writeFile(File src, File dst) {  
        try {  
            InputStream in = null;  
            OutputStream out = null;  
            try {  
                in = new BufferedInputStream(new FileInputStream(src),  
                        BUFFER_SIZE);  
                out = new BufferedOutputStream(new FileOutputStream(dst),  
                        BUFFER_SIZE);  
                byte[] buffer = new byte[BUFFER_SIZE];  
                while (in.read(buffer) > 0) {  
                    out.write(buffer);  
                }  
            } finally {  
                if (null != in) {  
                    in.close();  
                }  
                if (null != out) {  
                    out.close();  
                }  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
          
        System.out.println("写入成功！");  
	}  
	public static void writeFile(String path, String txt) {
       try{
	        File f = new File(path);
			FileWriter fw = new FileWriter(f,false);
			fw.write(txt);
			fw.flush();
			fw.close();
	   } catch (IOException e) {
		   e.printStackTrace();
       }
	}
	/**
	 * 复制单个文件
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf.txt
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf.txt
	 * @param overwrite
	 *   boolean 是否重写现有文件
	 *            
	 * @return boolean
	 */
	public static void copyFile(String oldPath, String newPath,boolean overwrite) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			File newFile = new File(newPath);
			if(newFile.exists()){ //目标文件已存在
				if(overwrite){ 
					newFile.renameTo(
							new File(newFile.getParentFile().getAbsoluteFile()+File.separator+newFile.getName()
							+"."+UimpUtil.DateToString(new Date(), "yyyyMMddHHmmss"))
					);
				}
				else{
					return ;
				}
			}
			
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldfile); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newFile);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();
		}

	}
	
	/**
	 * 复制单个文件
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf.txt
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf.txt
	 * @return boolean
	 */
	public static void copyFile(String oldPath, String newPath) {
		copyFile(oldPath, newPath, false);
	}
	
	/**
	 * 复制整个文件夹内容
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf/ff
	 * @return boolean
	 */
	public static void copyFolder(String oldPath, String newPath,boolean overwrite) {

		try {
			(new File(newPath)).mkdirs(); // 如果目标文件夹不存在 则建立新文件夹
			
			File oldfile = new File(oldPath);
			String[] file = oldfile.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}

				if (temp.isFile()) {// 如果是文件
					copyFile(temp.getAbsolutePath(),newPath+ File.separator + (temp.getName()).toString(),overwrite);
				}
				if (temp.isDirectory()) {// 如果是子文件夹
					copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i],overwrite);
				}
			}
		} catch (Exception e) {
			System.out.println("复制整个文件夹内容操作出错");
			e.printStackTrace();

		}

	}
	/**
	 * 复制整个文件夹内容
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf/ff
	 * @return boolean
	 */
	public static void copyFolder(String oldPath, String newPath) {
		copyFolder(oldPath, newPath, false);
	}
	
	public static boolean createZip(File compressFile, List<File> files) {
		boolean flag = false;
		try {
			ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(
					compressFile));
			zout.setEncoding("GBK");
			for (File file : files) {
				zip(zout, file, file.getName());
			}
			zout.close();
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	private static boolean zip(ZipOutputStream out, File file, String base) {
		boolean result = true;
		try {
			if (file.isDirectory()) {
				File[] subfiles = file.listFiles();
				if (base.length() != 0) {
					 //创建一个ZipEntry，并设置Name和其它的一些属性
					ZipEntry ze = new ZipEntry(base +"/");
                    ze.setSize(file.length());
                    ze.setTime(file.lastModified());
					out.putNextEntry(ze);
					base += "/";
				}

				for (int i = 0; i < subfiles.length; i++) {
					zip(out, subfiles[i], base + subfiles[i].getName());
				}
			} else {
				String filename = base;// java.net.URLEncoder.encode(base,"UTF-8");
				ZipEntry ze = new ZipEntry(filename);
                ze.setSize(file.length());
                ze.setTime(file.lastModified());
				out.putNextEntry(new ZipEntry(filename));
				FileInputStream in = new FileInputStream(file);
				int b;
				try {
					while ((b = in.read()) != -1) {
						out.write(b);
					}
				} catch (Exception e) {
					result = false;
				} finally {
					in.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}
	
	public static void copyFile(File src, File dst) {  
		  
        try {  
  
            InputStream in = null;  
  
            OutputStream out = null;  
  
            try {  
  
                in = new BufferedInputStream(new FileInputStream(src),  
                        BUFFER_SIZE);  
  
                out = new BufferedOutputStream(new FileOutputStream(dst),  
                        BUFFER_SIZE);  
  
                byte[] buffer = new byte[BUFFER_SIZE];  
  
                while (in.read(buffer) > 0) {  
  
                    out.write(buffer);  
  
                }  
  
            } finally {  
  
                if (null != in) {  
  
                    in.close();  
  
                }  
  
                if (null != out) {  
  
                    out.close();  
  
                }  
  
            }  
  
        } catch (Exception e) {  
  
            e.printStackTrace();  
  
        }  
  
    }  
	
	
	public static boolean deleteFile(String filePath) {// 删除单个文件
		boolean flag = false;
		File file = new File(filePath);
		if (file.isFile() && file.exists()) {// 路径为文件且不为空则进行删除
			file.delete();// 文件删除
			flag = true;
		}
		return flag;
	}

	
}