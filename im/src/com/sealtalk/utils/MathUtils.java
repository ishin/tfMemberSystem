package com.sealtalk.utils;

public class MathUtils {
	private MathUtils() {}
	
	private static class Inner {
		private static final MathUtils MU = new MathUtils();
	}
	
	public static MathUtils getInstance() {
		return Inner.MU;
	}
	
	public long getRandomSpecBit(double bit) {
		
		return Math.round((Math.random() * 9 + 1) * getPowValue(10, bit - 1));
	}
	
	public double getPowValue(double i, double b) {
		return Math.pow(i, b);
	}
}
