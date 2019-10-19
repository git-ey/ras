package com.ey.test;
public class StringArray {
	
	public static void main(String[] args) {
		// 测试
		int[] arr1 = { 1, 1, 0, 0, 1 };
		int[] arr2 = { 1, 1, 1, 1, 1 };
		String matchFlag = "Y";
		for (int i = 0; i < arr1.length; i++) {
			if (arr1[i] == 1) {
				if (arr2[i] == 0) {
					matchFlag = "N";
					break;
				}
			}
		}
		System.out.println("存在匹配标识：" + matchFlag);
	}
	
}