package com.sealtalk.model;

public class LoaderTest {

	private String baseName = "base";
	
	public LoaderTest(){
		System.out.println("2");
		callName();
	}
	
	public void callName(){
		System.out.println(baseName);
	}
	
	static class Sub extends LoaderTest {

		private String baseName = "sub";
		
		public Sub() {
			System.out.println("1");
			callName();
		}
		
		public void callName(){
			System.out.println("3");
			System.out.println(baseName);
		}
	}
	
	public static void main(String[] args) {
		 new Sub();
	}

}
