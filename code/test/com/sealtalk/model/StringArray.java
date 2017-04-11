package com.sealtalk.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringArray {
	public static void main(String args[]) {
		test3();
		//arrayToList();
	}
	
	public static void test3() {
		List<String> l = new ArrayList<String>();
		l.add("1");
		l.add("2");
		l.add("3");
		System.out.println(l.toString());
	}
	
	public static void test1() {
		List<String> l = new ArrayList<String>();
		l.add("1");
		l.add("2");
		l.add("3");
		System.out.println(l.toString());
	}
	
	public static void test2(){
		List<Test> test = new ArrayList<Test>();
		
		for(int i = 0; i < 4; i++) {
			Test t =  new Test(i, i + "name");
			test.add(t);
		}
		
		System.out.println(test.toString());
	}
	
	public static void arrayToList() {
		String[] s = {"2", "1", "3"};
		ArrayList<String> a = new ArrayList<String>(Arrays.asList(s));
		
		for(int i = 0; i < a.size(); i++)
		{
			System.out.println(a.get(i));
		}
		
	}
	public static void test() {
		/*String str = "admin";
		String[] array = {str};
		for(int i = 0;i < array.length; i++) {
			System.out.println(i + ": " + array[i]);
		}*/
	
		
		ArrayList<String> a = new ArrayList<String>();
		
		System.out.println(a.size());
		a.add("1");
		a.add("2");
		a.add("3");
		
		String[] b = new String[3];
		a.toArray(b);
		for(int i = 0; i < b.length; i++) {
			System.out.println(b[i]);
		}
		
		System.out.println(Integer.MAX_VALUE);
		
		String test = "abcefwdfe.sfsf.png";
		
		int pos = test.lastIndexOf(".");
		
		System.out.println(pos);
		
		System.out.println(test.substring(pos, test.length()));
		
		
		String[] test1 = new String[2];
		test1[0] = "a";
		test1[1] = "b";
		
		System.out.println(test1.toString());
	}
}

class Test{
	private int id;
	private String name;
	
	public Test(){};
	public Test(int id, String name) {
		this.id = id; 
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
