package com.sealtalk.model;

public class ThreeAndSeven {
	public static void main(String[] args) {
		int arr[] = new int[1000];
		int j = 0;
		for(int i = 1;i < 1000;i++) {
			if(i < 10) {
				if(i == 3 || i == 7 || i % 3 == 0 || i % 7 == 0) {
					arr[j] = i;
					j++;
					continue ;
				}
			}
			if(i >= 10 && i < 100) {
				if((i % 10) == 3 || (i % 10 == 7)) {
					arr[j] = i;
					j++;
					continue ;
				}
			}
			if(i >= 100 && i < 1000) {
				if((i % 100) == 3 || (i % 100) == 7) {
					arr[j] = i;
					j++;
					continue ;
				}
			}
			if(i % 3 == 0 || i % 7 == 0){
				arr[j] = i;
				j++;
				continue ;
			}
		}
		for(int i = 0;i < arr.length;i++) {
			if(arr[i] == 0) { 
				continue ;
			}
			System.out.print(arr[i]+" ");
			if(i % 15 == 0 && i != 0) {
				System.out.println();
			}
		}
	}
}
