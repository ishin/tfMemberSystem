package com.sealtalk.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.organ.utils.StringUtils;

public class StringTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//test1();
		
		replace();
	}

	public static void test1() {
		String test = "2,3,10,11,20,13,12";
		
	//	System.out.println(Integer.parseInt(test.replace("\"", "")));
		System.out.println(test.contains("1"));
		
		System.out.println(Integer.MAX_VALUE);
		System.out.println(Integer.parseInt("2147483647"));
		System.out.println(isNumeric("2147483648"));
		
	}
	
	public static void replace() {
		String b = "s+gdse+d";
		
		System.out.println(b);
		
		String a = "dfd";
		
		System.out.println(StringUtils.getInstance().replaceChar(a, "d", ""));
	}
	
	
	public static boolean isNumeric(String str){ 
		   Pattern pattern = Pattern.compile("[0-9]*"); 
		   Matcher isNum = pattern.matcher(str);
		   if( !isNum.matches() ){
		       return false; 
		   } 
		   return true; 
		}
}

