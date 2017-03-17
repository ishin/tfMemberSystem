package com.sealtalk.model;

import org.json.JSONException;
import org.json.JSONStringer;

public class JsonTest {

	public static void main(String[] args) {
		/*JSONStringer json = new JSONStringer();
		try {
			json.object();
			json.key("id").value("0");
			json.key("item");
			json.array();
			for(int i = 1;i < 3;i++) {
				json.object();
				json.key("id").value(i);
				json.key("text").value("json" + i);
				json.endObject();
			}
			json.endArray();
			json.endObject();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		System.out.println(json.toString());*/
		String a = "abcdefefeggefdfasgegew";
		System.out.println(a.substring(0, 10));
		System.out.println(a.substring(10, a.length()));
	}

}
