package com.ey.util;

import java.util.UUID;

/**
 * UUID处理工具
 *
 */
public class UuidUtil {

	public static String get32UUID() {
		String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
		return uuid;
	}
	public static void main(String[] args) {
		System.out.println(get32UUID());
	}
}

