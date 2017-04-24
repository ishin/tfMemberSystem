package com.sealtalk.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.util.StringUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class UimpUtil
{
	
	public static Date StringToDate(String StingDate, String DateFormat)
	{
		DateFormat format = new SimpleDateFormat(DateFormat);
		try
		{
			Date date = format.parse(StingDate);
			return date;
		} catch (ParseException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	
	public static String DateToString(Date date, String DateFormat)
	{
		DateFormat format = new SimpleDateFormat(DateFormat);
		return format.format(date);
	}
	
	
	
	/*
	 * (1).HanyuPinyinOutputFormat,定义汉语拼音的输出形式.
	 * (2).HanyuPinyinCaseType,定义汉语拼音的大小写,如: LOWERCASE min2 UPPERCASE MIN2
	 * (3).HanyuPinyinToneType,定义音调的显示方式.如: WITH_TONE_MARK dǎ ,带音调
	 * WITH_TONE_NUMBER da3 ,带音调,用12345表示平上去入和轻声 WITHOUT_TONE da ,不带音调
	 * (4).HanyuPinyinVCharType,定义'ü' 的显示方式.如: WITH_U_AND_COLON u:
	 * ,u加两点表示,如律师表示为lu:shi WITH_V v ,用字母v表示,这个用搜狗输入法的人想必有些印象. WITH_U_UNICODE ü
	 * (5).input[i]).matches("[\\u4E00-\\u9FA5]+"),这个用来判断是否为中文的.
	 * (6).PinyinHelper.toHanyuPinyinStringArray(input[i],
	 * format),这个返回单字的拼音字符串数组.
	 * 如果音调类型为WITH_TONE_NUMBER的话,"张",将返回"zhang1","李",会返回"li4".
	 */
	/**
	 * 将汉字转换为全拼
	 * 
	 * @param src
	 * @return String
	 */
	public static String getPinYin(String src)
	{
		
		char[] t1 = null;
		t1 = src.toCharArray();
		// System.out.println(t1.length);
		String[] t2 = new String[t1.length];
		// System.out.println(t2.length);
		// 设置汉字拼音输出的格式
		HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
		t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		t3.setVCharType(HanyuPinyinVCharType.WITH_V);
		String t4 = "";
		int t0 = t1.length;
		try
		{
			for (int i = 0; i < t0; i++)
			{
				// 判断能否为汉字字符
				// System.out.println(t1[i]);
				if (Character.toString(t1[i]).matches("[\\u4E00-\\u9FA5]+"))
				{
					t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);// 将汉字的几种全拼都存到t2数组中
					t4 += t2[0];// 取出该汉字全拼的第一种读音并连接到字符串t4后
				} else
				{
					// 如果不是汉字字符，间接取出字符并连接到字符串t4后
					t4 += Character.toString(t1[i]);
				}
			}
		} catch (BadHanyuPinyinOutputFormatCombination e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return t4;
	}
	
	
	
	/**
	 * 提取每个汉字的首字母 TODO 对于多音字，取最常用音
	 * 
	 * @param str
	 * @return String
	 */
	public static String getPinYinHeadChar(String str)
	{
		String convert = "";
		for (int j = 0; j < str.length(); j++)
		{
			char word = str.charAt(j);
			// 提取汉字的首字母
			String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
			if (pinyinArray != null)
			{
				convert += pinyinArray[0].charAt(0);
			} else
			{
				convert += word;
			}
		}
		return convert.toUpperCase();
	}
	
	
	
	/**
	 * 提取首个汉字的首字母
	 * 
	 * @param str
	 * @return 返回首个汉字的首字母的大写字母字符串数组，对于多音字，返回多个首字母
	 */
	public static String[] getFirstHeadChar(String str)
	{
		if (StringUtils.hasText(str))
		{
			char word = str.trim().charAt(0);
			// 提取汉字的首字母
			String[] pin = PinyinHelper.toHanyuPinyinStringArray(word);
			if (pin != null)
			{
				String[] result = new String[pin.length];
				for (int i = 0; i < pin.length; i++)
				{
					result[i] = String.valueOf(pin[i].charAt(0)).toUpperCase();
				}
				return result;
			}
		}
		return null;
	}
	
	
	
	/**
	 * 将字符串转换成ASCII码
	 * 
	 * @param cnStr
	 * @return String
	 */
	public static String getCnASCII(String cnStr)
	{
		StringBuffer strBuf = new StringBuffer();
		// 将字符串转换成字节序列
		byte[] bGBK = cnStr.getBytes();
		for (int i = 0; i < bGBK.length; i++)
		{
			// System.out.println(Integer.toHexString(bGBK[i] & 0xff));
			// 将每个字符转换成ASCII码
			strBuf.append(Integer.toHexString(bGBK[i] & 0xff));
		}
		return strBuf.toString();
	}
	
	
	public static void main(String[] args)
	{
		// String cnStr = "单";
		// System.out.println(getPinYin(cnStr));
		// System.out.println(getPinYinHeadChar(cnStr));
		// System.out.println(getCnASCII(cnStr));
		// System.out.println(StringUtils.arrayToDelimitedString(
		// getFirstHeadChar(cnStr), ""));
		
		String path = "D:\\Program Files\\tomcat6.0\\webapps\\uimp";
		System.out.println(formatPath(path));
	}
	
	
	
	/**
	 * 格式化文件路径，将其中不规范的分隔转换为标准的分隔符,并且添加末尾的"/"符号。
	 * 
	 * @param path
	 *            文件路径
	 * @return 格式化后的文件路径
	 */
	public static String formatPath(String path)
	{
		String reg0 = "\\\\＋";
		String reg = "\\\\＋|/＋";
		String temp = path.trim().replaceAll(reg0, "/");
		temp = temp.replaceAll(reg, "/");
		if (!temp.endsWith("/")) {
			temp +="/";
		}
		if (System.getProperty("file.separator").equals("\\"))
		{
			temp = temp.replace('/', '\\');
		}
		return temp;
	}
}
