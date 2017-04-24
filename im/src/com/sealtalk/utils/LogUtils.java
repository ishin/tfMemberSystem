package com.sealtalk.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class LogUtils {
	private LogUtils(){};
	
	private static class Inner {
		private static final LogUtils LU = new LogUtils();
	}
	
	public static LogUtils getInstance() {
		return Inner.LU;
	}
	
	/**
	 * 输出Exception stacktrace 的toString
	 * @param e
	 * @return
	 */
	@SuppressWarnings("unused")
	public String getErrorInfoFromException(Exception e) {
		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			return "\r\n" + sw.toString() + "\r\n";
		} catch (Exception e2) {
			return "bad getErrorInfoFromException";
		}
	}
}
