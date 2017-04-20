package com.organ.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class PasswordGenerator {

	private PasswordGenerator() {
	};

	private static class Inner {
		private static final PasswordGenerator PG = new PasswordGenerator();
	}

	public static PasswordGenerator getInstance() {
		return Inner.PG;
	}

	public String makePwd() {
		char c[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
				'b', 'c', 'd', 'e', 'f', 'j', 'h', 'k', 'g', 'i', 'l', 'm',
				'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
				'z', '*', '/', '$', '#' , ';', '&'};
		int len = c.length;
		StringBuilder sb = new StringBuilder();
		Random r = new Random();
		
		for(int i = 0; i < 8; i++) {
			int p = r.nextInt(len);
			sb.append(c[p]);
		}
		
		return sb.toString();
	}
	
	public String getMD5Str(String pwdContext) {
		String result = null;

		char hexs[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
				'b', 'c', 'd', 'e', 'f' };

		byte[] source = pwdContext.getBytes();

		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(source);

			byte md5Digest[] = md5.digest();

			char str[] = new char[16 * 2];

			int c = 0;

			for (int i = 0; i < 16; i++) {
				byte byte0 = md5Digest[i];

				str[c++] = hexs[byte0 >>> 4 & 0xf];
				str[c++] = hexs[byte0 & 0xf];

			}

			result = new String(str);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return result;
	}
	

	/*public static void main(String[] arg) {
		System.out.println(makePwd());
	}*/
}
