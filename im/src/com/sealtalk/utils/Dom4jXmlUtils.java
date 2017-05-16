package com.sealtalk.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class Dom4jXmlUtils {
	private Dom4jXmlUtils() {}
	
	private static class Inner {
		public static final Dom4jXmlUtils d = new Dom4jXmlUtils();
	}
	
	public static Dom4jXmlUtils getInstance() {
		return Inner.d;
	}
	
	public Document load(String filename) { 
       Document document = null; 
       
       try { 
           SAXReader saxReader = new SAXReader(); 
           document = saxReader.read(new File(filename)); 
       } catch (Exception ex){ 
           ex.printStackTrace(); 
       }   
       
       return document; 
    }
	
	public String doc2String(Document document) { 
      String s = ""; 
      
      try { 
       	//使用输出流来进行转化 
        ByteArrayOutputStream out = new ByteArrayOutputStream(); 
        //使用utf-8编码 
        OutputFormat format = new OutputFormat("   ", true, "utf-8"); 
        XMLWriter writer = new XMLWriter(out, format); 
        writer.write(document); 
        s = out.toString(); 
      } catch(Exception ex) {             
           ex.printStackTrace(); 
      }    
      
      return s; 
    }
}
