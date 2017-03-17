package com.sealtalk.model;

import java.util.HashMap;
import java.util.Map;

public class MapTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Map<String, Integer> map = new HashMap<String, Integer>();
		
		if (map.get("1") == null) {
			System.out.println(2);
		} else {
			System.out.print(1);
		}
	}
		
}
