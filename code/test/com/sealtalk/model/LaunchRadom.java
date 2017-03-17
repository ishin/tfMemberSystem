package com.sealtalk.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class LaunchRadom {

	public static void main(String[] args) {
		Map<String ,String> map = new HashMap<String, String>();
		map.put("1", "超市买饼");
		map.put("2", "酸菜牛肉面");
		map.put("3", "茄丁拌饭");
		map.put("4", "豆豉");
		map.put("5", "茄丁拌面");
		map.put("6", "红烧牛肉面");
		map.put("7", "骨汤面");
		map.put("8", "1");
		map.put("9", "2");
		map.put("10", "3");
		map.put("11", "4");
		
		int number = new Random().nextInt(11) + 1;
		Iterator<String> it = map.keySet().iterator();  
        while(it.hasNext()){  
        	try {
				Thread.sleep(1000);
				String key = it.next();    
	            if(key.equals(String.valueOf(number))) {
	            	System.out.println("今天吃: " + map.get(key));
	            	break;
	            } else {
	            	System.out.println("正在选择：" + map.get(key));
	            }
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }  
	}

}
