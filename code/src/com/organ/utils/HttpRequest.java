package com.organ.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import net.sf.json.JSONObject;

public class HttpRequest {

	private HttpRequest() {
	};

	private static class Inner {
		private static final HttpRequest HR = new HttpRequest();
	}

	public static HttpRequest getInstance() {
		return Inner.HR;
	}

	public String sendPost(String fun, JSONObject params, String url, String host) {
		String result = "";

		try {
			String protocol = PropertiesUtils.getStringByKey("im.protocol");

			if (protocol.equalsIgnoreCase("http")) {
				result = httpRequestHttp(fun, params, url, host);
			} else if (protocol.equalsIgnoreCase("https")) {
				result = httpRequestHttps(fun, params, url, host);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	private String httpRequestHttp(String fun, JSONObject params, String urlStr, String host) {
		String result = "";

		try {
			long timeStamp = TimeGenerator.getInstance().getUnixTime();
			String key = PropertiesUtils.getStringByKey("param.key");
			String sign = PasswordGenerator.getInstance().makeSign(params, key,
					timeStamp);
			params.put("timestamp", timeStamp);
			params.put("sign", sign);

			String info = params.toString();
			params = null;

			urlStr += fun;
			
			URL url = new URL(urlStr);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			// con.setRequestProperty("HTTP-Version:", "HTTP/1.1");
			con.setRequestProperty("Content-Type", "text/xml");
			con.setRequestProperty("Host", host);
			con.setConnectTimeout(5000);

			con.setDoOutput(true);
			con.setDoInput(true);
			con.setUseCaches(false);
			// con.setRequestMethod();
			//con.connect();

			con.setRequestProperty("Content-Length", info.length() + "");
			OutputStreamWriter out = new OutputStreamWriter(con
					.getOutputStream());
			out.write(new String(info.getBytes("utf-8")));
			out.flush();
			out.close();

			StringBuilder sb = new StringBuilder();
			InputStream inputStream = con.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream, "utf-8");
			BufferedReader br = new BufferedReader(inputStreamReader);
			String line = "";
			for (line = br.readLine(); line != null; line = br.readLine()) {
				sb.append(line);
			}
			br.close();
			inputStreamReader.close();
			inputStream.close();
			inputStream = null;
			//con.disconnect();

			result = sb.toString();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/*
	 * private String httpRequestHttps(String fun, JSONObject params){ String
	 * result = null; StringBuffer buffer = new StringBuffer(); try { String
	 * protocol = PropertiesUtils.getStringByKey("auth.protocol"); String host =
	 * PropertiesUtils.getStringByKey("auth.host"); String sys =
	 * PropertiesUtils.getStringByKey("auth.sys"); String urlStr = protocol +
	 * "://" + host + "/" + sys + "/" + fun; String keyStorePath =
	 * PropertiesUtils.getStringByKey("ssh.keystore");
	 * 
	 * long timeStamp = TimeGenerator.getInstance().getUnixTime(); String key =
	 * PropertiesUtils.getStringByKey("param.key"); String sign =
	 * PasswordGenerator.getInstance().makeSign(params, key, timeStamp);
	 * params.put("timestamp", timeStamp); params.put("sign", sign);
	 * 
	 * String info = params.toString(); params = null;
	 * 
	 * URL url = new URL(urlStr); HttpsURLConnection httpUrlConn =
	 * (HttpsURLConnection) url.openConnection(); KeyStore trustStore =
	 * KeyStore.getInstance(KeyStore.getDefaultType()); FileInputStream instream
	 * = new FileInputStream(new File(keyStorePath));
	 * 
	 * try { // 加载keyStore trustStore.load(instream, "D#s@a1".toCharArray()); }
	 * catch (NoSuchAlgorithmException e) { e.printStackTrace(); } finally { try
	 * { instream.close(); } catch (Exception ignore) { } } TrustManagerFactory
	 * tmf =
	 * TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm
	 * ()); tmf.init(trustStore);
	 * 
	 * X509TrustManager defaultTrustManager =
	 * (X509TrustManager)tmf.getTrustManagers()[0];
	 * 
	 * SSLContext ctx = SSLContext.getInstance("TLS"); ctx.init(null, new
	 * TrustManager[] {defaultTrustManager}, null);
	 * 
	 * SSLSocketFactory sslFactory = ctx.getSocketFactory();
	 * 
	 * httpUrlConn.setSSLSocketFactory(sslFactory);
	 * 
	 * httpUrlConn.setDoOutput(true); httpUrlConn.setDoInput(true);
	 * httpUrlConn.setUseCaches(false);
	 * httpUrlConn.setInstanceFollowRedirects(true);
	 * //httpUrlConn.setRequestMethod(requestMethod);
	 * httpUrlConn.setRequestProperty("Content-Type", "application/json");
	 * httpUrlConn.setRequestProperty("Accept", "application/json");
	 * 
	 * httpUrlConn.connect();
	 * 
	 * // 当有数据需要提交时 if (null != info) { OutputStream outputStream =
	 * httpUrlConn.getOutputStream(); // 注意编码格式，防止中文乱码
	 * outputStream.write(info.getBytes("UTF-8")); outputStream.close(); }
	 * 
	 * // 将返回的输入流转换成字符串 BufferedReader bufferedReader = new BufferedReader(new
	 * InputStreamReader(httpUrlConn.getInputStream(), "utf-8"));
	 * 
	 * String str = null; while ((str = bufferedReader.readLine()) != null) {
	 * buffer.append(str); } bufferedReader.close(); httpUrlConn.disconnect();
	 * result = buffer.toString(); } catch (ConnectException ce) {
	 * ce.printStackTrace(); } catch (Exception e) { e.printStackTrace(); }
	 * return result; }
	 */

	/**
	 * 获得KeyStore.
	 * 
	 * @param keyStorePath
	 *            密钥库路径
	 * @param password
	 *            密码
	 * @return 密钥库
	 * @throws Exception
	 */
	private KeyStore getKeyStore(String password, String keyStorePath)
			throws Exception {
		// 实例化密钥库
		KeyStore ks = KeyStore.getInstance("JKS");
		// 获得密钥库文件流
		FileInputStream is = new FileInputStream(keyStorePath);
		// 加载密钥库
		ks.load(is, password.toCharArray());
		// 关闭密钥库文件流
		is.close();
		return ks;
	}

	/**
	 * 获得SSLSocketFactory.
	 * 
	 * @param password
	 *            密码
	 * @param keyStorePath
	 *            密钥库路径
	 * @param trustStorePath
	 *            信任库路径
	 * @return SSLSocketFactory
	 * @throws Exception
	 */
	private SSLContext getSSLContext(String password, String keyStorePath,
			String trustStorePath) throws Exception {
		// 实例化密钥库
		KeyManagerFactory keyManagerFactory = KeyManagerFactory
				.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		// 获得密钥库
		KeyStore keyStore = getKeyStore(password, keyStorePath);
		// 初始化密钥工厂
		keyManagerFactory.init(keyStore, password.toCharArray());

		// 实例化信任库
		TrustManagerFactory trustManagerFactory = TrustManagerFactory
				.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		// 获得信任库
		KeyStore trustStore = getKeyStore(password, trustStorePath);
		// 初始化信任库
		trustManagerFactory.init(trustStore);
		// 实例化SSL上下文
		SSLContext ctx = SSLContext.getInstance("TLS");
		// 初始化SSL上下文
		ctx.init(keyManagerFactory.getKeyManagers(), trustManagerFactory
				.getTrustManagers(), null);
		// 获得SSLSocketFactory
		return ctx;
	}

	/**
	 * 初始化HttpsURLConnection.
	 * 
	 * @param password
	 *            密码
	 * @param keyStorePath
	 *            密钥库路径
	 * @param trustStorePath
	 *            信任库路径
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private void initHttpsURLConnection(String password, String keyStorePath,
			String trustStorePath) throws Exception {
		// 声明SSL上下文
		SSLContext sslContext = null;
		// 实例化主机名验证接口
		HostnameVerifier hnv = new MyHostnameVerifier();
		try {
			sslContext = getSSLContext(password, keyStorePath, trustStorePath);
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		if (sslContext != null) {
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext
					.getSocketFactory());
		}
		HttpsURLConnection.setDefaultHostnameVerifier(hnv);
	}

	private String httpRequestHttps(String fun, JSONObject params, String urlStr, String host) {
		HttpsURLConnection urlCon = null;
		StringBuilder sb = new StringBuilder();
        String password = PropertiesUtils.getStringByKey("ssh.password");  
        // 密钥库  
        String keyStorePath = PropertiesUtils.getStringByKey("ssh.keystore");  
        // 信任库  
        String trustStorePath = PropertiesUtils.getStringByKey("ssh.trustStore");  
	    
		try {
			initHttpsURLConnection(password, keyStorePath, trustStorePath);  

			long timeStamp = TimeGenerator.getInstance().getUnixTime();
			String key = PropertiesUtils.getStringByKey("param.key");
			String sign = PasswordGenerator.getInstance().makeSign(params, key,
					timeStamp);
			params.put("timestamp", timeStamp);
			params.put("sign", sign);

			String info = params.toString();
			params = null;
			
			urlCon = (HttpsURLConnection) (new URL(urlStr)).openConnection();
			urlCon.setRequestProperty("Content-Type", "text/xml");
			urlCon.setRequestProperty("Host", host);
			urlCon.setRequestProperty("Content-Length", String.valueOf(info.getBytes().length));
			urlCon.setConnectTimeout(5000);
			urlCon.setDoInput(true);
			urlCon.setDoOutput(true);
			urlCon.setUseCaches(false);
			//urlCon.setRequestMethod("POST");
			//urlCon.connect();
			OutputStreamWriter out = new OutputStreamWriter(urlCon
					.getOutputStream());
			out.write(new String(info.getBytes("utf-8")));
			out.flush();
			out.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(urlCon.getInputStream(), "utf-8"));
			String line;
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			in.close();
			in = null;
			//urlCon.disconnect();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

}

class MyHostnameVerifier implements HostnameVerifier {

	@Override
	public boolean verify(String hostname, SSLSession sslsession) {
		if ("127.0.0.1".equals(hostname)) {
			return true;
		} else {
			return false;
		}
	}
}