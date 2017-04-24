package com.sealtalk.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;

/**
 * 上传图片
 * 
 * @author hao_dy
 * 
 */
public class HTTPPostUploadUtil {

	private HTTPPostUploadUtil() {
	};

	private static class Inner {
		private static final HTTPPostUploadUtil HPUU = new HTTPPostUploadUtil();
	}

	public static HTTPPostUploadUtil getInstance() {
		return Inner.HPUU;
	}

	public String httpUpload(String urlPs, String fileName) {
		DataInputStream in = null;
		OutputStream out = null;
		HttpURLConnection conn = null;
		String responseTxt = null;
		InputStream ins = null;
		ByteArrayOutputStream outStream = null;
		
		try {
			URL url = new URL(urlPs);
			conn = (HttpURLConnection) url.openConnection();
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "text/html");
			conn.setRequestProperty("Cache-Control", "no-cache");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.connect();
			conn.setConnectTimeout(10000);
			out = conn.getOutputStream();

			File file = new File(fileName);
			in = new DataInputStream(new FileInputStream(file));

			int bytes = 0;
			byte[] buffer = new byte[1024];
			
			while ((bytes = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytes);
			}
			
			out.flush();

			// 返回流
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				ins = conn.getInputStream();
				
				outStream = new ByteArrayOutputStream();
				
				byte[] data = new byte[1024];
				int count = -1;
				
				while ((count = ins.read(data, 0, 1024)) != -1) {
					outStream.write(data, 0, count);
				}
				
				data = null;
				responseTxt = new String(outStream.toByteArray(), "UTF-8");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
				if (ins != null) {
					ins.close();
				}
				if (outStream != null) {
					outStream.close();
				}
				if (conn != null) {
					conn.disconnect();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return responseTxt;
	}

	/**
	 * 上传图片
	 * 
	 * @param urlStr
	 * @param textMap
	 * @param fileMap
	 * @return
	 */
	@Deprecated
	public String formUpload(String urlStr, Map<String, String> textMap,
			Map<String, String> fileMap) {
		String res = "";
		HttpURLConnection conn = null;
		String BOUNDARY = "123821742118716";
		try {
			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(30000);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn
					.setRequestProperty("User-Agent",
							"Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + BOUNDARY);

			OutputStream out = new DataOutputStream(conn.getOutputStream());

			// text
			if (textMap != null) {
				StringBuffer strBuf = new StringBuffer();
				Iterator<Map.Entry<String, String>> iter = textMap.entrySet()
						.iterator();
				while (iter.hasNext()) {
					Map.Entry<String, String> entry = iter.next();
					String inputName = (String) entry.getKey();
					String inputValue = (String) entry.getValue();
					if (inputValue == null) {
						continue;
					}
					strBuf.append("\r\n").append("--").append(BOUNDARY).append(
							"\r\n");
					strBuf.append("Content-Disposition: form-data; name=\""
							+ inputName + "\"\r\n\r\n");
					strBuf.append(inputValue);
				}
				out.write(strBuf.toString().getBytes());
			}

			// file
			if (fileMap != null) {
				Iterator<Map.Entry<String, String>> iter = fileMap.entrySet()
						.iterator();
				while (iter.hasNext()) {
					Map.Entry<String, String> entry = iter.next();
					String inputName = (String) entry.getKey();
					String inputValue = (String) entry.getValue();
					if (inputValue == null) {
						continue;
					}
					File file = new File(inputValue);
					String filename = file.getName();
					MagicMatch match = Magic.getMagicMatch(file, false, true);
					String contentType = match.getMimeType();

					StringBuffer strBuf = new StringBuffer();
					strBuf.append("\r\n").append("--").append(BOUNDARY).append(
							"\r\n");
					strBuf.append("Content-Disposition: form-data; name=\""
							+ inputName + "\"; filename=\"" + filename
							+ "\"\r\n");
					strBuf.append("Content-Type:" + contentType + "\r\n\r\n");

					out.write(strBuf.toString().getBytes());

					DataInputStream in = new DataInputStream(
							new FileInputStream(file));
					int bytes = 0;
					byte[] bufferOut = new byte[1024];
					while ((bytes = in.read(bufferOut)) != -1) {
						out.write(bufferOut, 0, bytes);
					}
					in.close();
				}
			}

			byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
			out.write(endData);
			out.flush();
			out.close();

			// 读取返回数据
			StringBuffer strBuf = new StringBuffer();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				strBuf.append(line).append("\n");
			}
			res = strBuf.toString();
			reader.close();
			reader = null;
		} catch (Exception e) {
			System.out.println("发送POST请求出错。" + urlStr);
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		return res;
	}

	/**
	 * @param args
	 */
	/*
	 * public static void main(String[] args) { String filepath =
	 * "E:\\ziliao\\0.jpg"; String urlStr =
	 * "http://127.0.0.1:8080/minicms/up/up_result.jsp"; Map<String, String>
	 * textMap = new HashMap<String, String>(); textMap.put("name", "testname");
	 * Map<String, String> fileMap = new HashMap<String, String>();
	 * fileMap.put("userfile", filepath); String ret = formUpload(urlStr,
	 * textMap, fileMap); System.out.println(ret); }
	 */

}