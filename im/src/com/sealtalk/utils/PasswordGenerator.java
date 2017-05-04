package com.sealtalk.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.RandomStringUtils;


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
				'z', '*', '/', '$', '#', ';', '&' };
		int len = c.length;
		StringBuilder sb = new StringBuilder();
		Random r = new Random();

		for (int i = 0; i < 8; i++) {
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

	public String createId(int bit) {
		String id = UUID.randomUUID().toString();
		id = DEKHash(id) + "";
		int diff = bit - id.length();
		
		String randStr = RandomStringUtils.randomAlphabetic(bit);
		
		for (int i = 0; i < diff; i++) {
			int randIndex = (int) (Math.random() * randStr.length());
			int index = (int) (Math.random() * id.length());
			id = id.substring(0, index) + randStr.charAt(randIndex)
					+ id.substring(index, id.length());
		}
		
		return id;
	}

	private int DEKHash(String str) {
		int hash = str.length();

		for (int i = 0; i < str.length(); i++) {
			hash = ((hash << 5) ^ (hash >> 27)) ^ str.charAt(i);
		}

		return (hash & 0x7FFFFFFF);
	}
	
	/**
	 * 生成参数sign
	 * @param paramMap
	 * @param timeStamp
	 * @param validTime
	 * @param key
	 * @return
	 */
	public String makeSign(JSONObject param, String key, long timeStamp) {
		StringBuilder sbp = new StringBuilder();
		Iterator<String> it = param.keys();
		
		while(it.hasNext()) {
			String jsonKey = it.next();
			String jsonValue = param.getString(jsonKey);
			sbp.append(jsonKey).append("=").append(jsonValue);
		}
		
		String pStr = sbp.toString();
		
		pStr = StringUtils.getInstance().sortByChars(pStr);
		System.out.println("sort1: " + pStr);
		pStr = key + pStr + timeStamp;
		System.out.println("sort2: " + pStr);
		String caclSign = PasswordGenerator.getInstance().getMD5Str(sbp.toString());
		
		System.out.println("im sign: " + caclSign);
		
		return caclSign;
	}
	
	/**
	 * 验证参数有效性
	 * @param paramMap
	 * @param timeStamp
	 * @return
	 */
	public boolean valideMd5(JSONObject params, String timeStamp, long validTime, String key) {
		long now = TimeGenerator.getInstance().getUnixTime();
		long maxTime = now + validTime;
		long minTime = now - validTime;
		
		long timeStampLong = timeStamp != null ? Long.parseLong(timeStamp) : 0;
		
		if (timeStampLong < minTime || timeStampLong > maxTime) {
			return false;
		}
		
		String sign = null;
		StringBuilder sbp = new StringBuilder();
		Iterator<String> it = params.keys();
		
		while(it.hasNext()) {
			String t = it.next();
			String v = params.getString(t);
			if (t.equals("sign")) {
				sign = v;
				continue;
			}
			if (t.equals("timestamp")) continue;
			sbp.append(t).append("=").append(v);
		}
		String pStr = sbp.toString();
		pStr = StringUtils.getInstance().sortByChars(pStr);
		System.out.println("sort1: " + pStr);
		pStr = key + pStr + timeStamp;
		System.out.println("sort2: " + pStr);
		
		String caclSign = PasswordGenerator.getInstance().getMD5Str(sbp.toString());
		
		System.out.println("organ sign: " + caclSign);
		
		if (!caclSign.equals(sign)) {
			return false;
		}
		
		return true;
	}
	
}
