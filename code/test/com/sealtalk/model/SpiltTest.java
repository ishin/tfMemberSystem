package com.sealtalk.model;

public class SpiltTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//pathSplit();
	}

	private static void pathSplit(){
		String path = "D:\\abc\\edu\\abefe\\123\\3.jpg";
		System.out.println(path);
		String pathParam[] = path.split("\\\\");
		String handlePath = pathParam[0];
		for(int i = 1;i < pathParam.length; i++) {
			handlePath += "\\\\" + pathParam[i]; 
		}
		//handlePath = handlePath.substring(0, handlePath.length() - 1);
		System.out.println(handlePath);
	}
	
	private void jsonSplit() {
		String jsonArray[];
		String json = "[{id:0,'children':[{text:'系统维护',leaf:false},{text:'学员管理系统',leaf:false}]}]";
		
		jsonArray = json.split("children");
		
		for(int i = 0;i < jsonArray.length;i++) {
			System.out.println(jsonArray[i]);
		}
		jsonArray[1] = jsonArray[1].substring(2);
		System.out.println(jsonArray[1]);
		
		String str = "";
		String arr[] = str.split(",");
		System.out.println(arr.length);
	}
}
