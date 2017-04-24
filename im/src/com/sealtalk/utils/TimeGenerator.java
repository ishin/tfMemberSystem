package com.sealtalk.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeGenerator {

	private TimeGenerator () {}
	
	private static class Inner {
		private static final TimeGenerator TIME = new TimeGenerator();
	}
	
	public static final TimeGenerator getInstance() {
		return Inner.TIME;
	}
	
	public long getUnixTime() {
		long seconds = Calendar.getInstance().getTimeInMillis() / 1000;
		return (long)Math.floor((double)seconds);
	}
	
	public long getUnixTimeMills() {
		long seconds = Calendar.getInstance().getTimeInMillis();
		return (long)Math.floor((double)seconds);
	}
	
	public int getMinutes() {
		return Calendar.getInstance().get(Calendar.MINUTE);
	}
	
	public int getSeconds() {
		return Calendar.getInstance().get(Calendar.SECOND);
	}
	
	public int getHour() {
		return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
	}
	
	public int getDay() {
		return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
	}
	
	public int getYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}
	
	public int getMonth() {
		return Calendar.getInstance().get(Calendar.MONTH) + 1;
	}
	
	/**
	 * @Description 格式化时间为Unix时间
	 * */
	public long formatDateToUnixTime(int year, int month, int day, int hour, int minute, int second) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d H:m:s");  
			Date date = dateFormat.parse(new StringBuffer().append(year)
					.append("-").append(month).append("-").append(day)
					.append(" ").append(hour).append(":").append(minute)
					.append(":").append(second).toString());  
			
			Calendar c = Calendar.getInstance();
			
			c.setTime(date);
			
			return (long)Math.floor(c.getTimeInMillis() / 1000);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;  
	  
	}
	
	/**
	 * @Description 判断是否是同一天
	 * @param time1
	 * @param time2
	 * @return
	 */
	public boolean isDiffDay(long time1, long time2) {
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		
		time1 = time1 * 1000;
		time2 = time2 * 1000;
		
		c1.setTimeInMillis(time1);
		c2.setTimeInMillis(time2);
	
		if (c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR)) {
			return true;
		}
		
		if (c1.get(Calendar.MONTH) + 1 != c2.get(Calendar.MONTH) + 1) {
			return true;
		}
		
		if (c1.get(Calendar.DAY_OF_MONTH) != c2.get(Calendar.DAY_OF_MONTH)) {
			return true;
		}
		
		return false;
	}
	
	
	/**
	 * @Description 获取一天中距离某时间相差的秒数
	 * @param hour
	 * @param minute
	 * @param second
	 * @return delaySeconds ;
	 * */
	public long getDelay(int hour, int minute, int second) {
		int year = this.getYear();
		int month = this.getMonth();
		int day = this.getDay();
		
		//定时时间
		long delayTime = this.formatDateToUnixTime(year, month, day, hour, minute, second);
		//当前unix时间
		long curTime = this.getUnixTime();
		
		//已过当天的定时时间,延迟到下一天的定时时间
		if ( curTime > delayTime ) {
			return 86400 - (curTime - delayTime);
		}
		
		return delayTime - curTime;
	}
	
	/**
	 * @Description 返回一天的秒数
	 * */
	public long getAllDaySeconds() {
		return 24 * 60 * 60;
	}

	/**
	 * 格式化当前时间 
	 * @param format
	 * @return
	 */
	public String formatNow(String format) {
		try {
			Date date = new Date();
			
			/*Calendar c = Calendar.getInstance();
			
			c.setTime(date);*/
			
			if (format == null || "".equals(format)) {
				format = "dd/MM/yyyy:HH:mm:ss";
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			
			return sdf.format(date).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
