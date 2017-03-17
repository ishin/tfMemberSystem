package com.sealtalk.model;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

public class JsonToList {

	public static void main(String[] args) {
		String json="[{'name':'huangbiao','age':15},{'name':'liumei','age':14}]";
		JSONArray jsonarray = JSONArray.fromObject(json);
		System.out.println(jsonarray);
		List list = (List)JSONArray.toCollection(jsonarray, Person.class);
		Iterator it = list.iterator();
		while(it.hasNext()){
			Person p = (Person)it.next();
			System.out.println(p.getAge());
		}
	}
	
	@Test
	public void jsonToList1(){
		String json="[{'name':'huangbiao','age':15},{'name':'liumei','age':14}]";
		JSONArray jsonarray = JSONArray.fromObject(json);
		System.out.println(jsonarray);
		List list = (List)JSONArray.toList(jsonarray, Person.class);
		Iterator it = list.iterator();
		while(it.hasNext()){
			Person p = (Person)it.next();
			System.out.println(p.getAge());
		}
		
	}
	
	@Test
	public void jsonToList2(){
		String json="[{'name':'huangbiao','age':15},{'name':'liumei','age':14}]";
		JSONArray jsonarray = JSONArray.fromObject(json);
		System.out.println(jsonarray);
		System.out.println("------------");
		List list = (List)JSONArray.toList(jsonarray, new Person(), new JsonConfig());
		Iterator it = list.iterator();
		while(it.hasNext()){
			Person p = (Person)it.next();
			System.out.println(p.getAge());
		}
		
	}

}
