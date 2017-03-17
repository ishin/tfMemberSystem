package com.organ.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class HttpRequest {

	private HttpRequest(){};
	
	private static class Inner{
		private static final HttpRequest HR = new HttpRequest();
	}
	
	public static HttpRequest getInstance() {
		return Inner.HR;
	}
	
    public String sendPost(String urlStr, String info) {
    	String result = "";
    	
        try {  
        	String host = "120.27.141.25";
        	
        	URL url = new URL(urlStr);   
            URLConnection con = url.openConnection();  
         
            //con.setRequestProperty("HTTP-Version:", "HTTP/1.1");  
            con.setRequestProperty("Content-Type", "text/xml");  
            con.setRequestProperty("Host", host);
         
            con.setDoOutput(true);
            con.setDoInput(true);
           
            con.setRequestProperty("Content-Length", info.length() + "");
            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());      
            
            System.out.println("urlStr=" + url);  
            System.out.println("info=" + info);  
            
            out.write(new String(info.getBytes("utf-8")));  
            out.flush();  
            out.close();  
            
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));  
            String line = "";  
            
            for (line = br.readLine(); line != null; line = br.readLine()) {  
            	sb.append(line);
            }  
            
            result = sb.toString();
            
        } catch (MalformedURLException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }
        
        return result;
    }     
   
}