package com.sealtalk.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import net.sf.json.JSONObject;

public class HttpRequest {

	private HttpRequest(){};
	
	private static class Inner{
		private static final HttpRequest HR = new HttpRequest();
	}
	
	public static HttpRequest getInstance() {
		return Inner.HR;
	}
	
    public String sendPost(String fun, JSONObject params) {
    	String result = "";
    	
        try {  
        	String protocol = PropertiesUtils.getStringByKey("auth.protocol");
        	String host = PropertiesUtils.getStringByKey("auth.host");
        	String sys = PropertiesUtils.getStringByKey("auth.sys");
        	String urlStr = protocol + "://" + host + "/" + sys + "/" + fun;
        	
        	long timeStamp = TimeGenerator.getInstance().getUnixTime();
        	String key = PropertiesUtils.getStringByKey("param.key");
        	String sign = PasswordGenerator.getInstance().makeSign(params, key, timeStamp);
        	params.put("timestamp", timeStamp);
        	params.put("sign", sign);
        	
        	String info = params.toString();
        	params = null;
        	
        	URL url = new URL(urlStr);   
            URLConnection con = url.openConnection();  
            
            //con.setRequestProperty("HTTP-Version:", "HTTP/1.1");  
            con.setRequestProperty("Content-Type", "text/xml");
            con.setRequestProperty("Host", host);
            con.setConnectTimeout(5000);
            
            con.setDoOutput(true);
            con.setDoInput(true);
           
            con.setRequestProperty("Content-Length", info.length() + "");
            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());      
            
            out.write(new String(info.getBytes("utf-8")));  
            out.flush();  
            out.close();  
            
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));  
            String line = "";   
            for (line = br.readLine(); line != null; line = br.readLine()) {  
            	sb.append(line);
            }  
            
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
    
}