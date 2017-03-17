package com.sealtalk.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTest {
	public static void main(String args[]) {
		Date date = new Date();
		//System.out.println(date.getTime());
		formatDemo();
	}
	
	public static void formatDemo()
    {
        Date d = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date = formatter.format(d);
        System.out.println(date);
    }
}
