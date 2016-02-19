package com.letv.framework.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 日期相关工具类
 */
public class DateUtil {

	public static final String DATE_FORMAT_TYPE_1 = "yyyy.MM.dd HH:mm:ss.S";
	public static final String DATE_FORMAT_TYPE_2 = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_FORMAT_TYPE_3 = "yyyy-MM-dd";
	public static final String DATE_FORMAT_TYPE_4 = "yyyy";
	public static final String DATE_FORMAT_TYPE_5 = "MM-dd";
	public static final String DATE_FORMAT_TYPE_6 = "HH:mm:ss";
	public static final String DATE_FORMAT_TYPE_7 = "HH:mm";
	public static final String DATE_FORMAT_TYPE_8 = "yyyy年MM月dd日";
	public static final String DATE_FORMAT_TYPE_9 = "MM月dd日";

	/**
	 * 将时间毫秒值格式化为指定格式的日期
	 * @param timeMillis	时间毫秒值
	 * @param format		格式化日期格式
	 * @return				指定格式的日期，如果格式有误则返回""
	 */
	public static String dateFormat(long timeMillis, String format){
		String dateFormat = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			dateFormat = sdf.format(new Date(timeMillis));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateFormat;
	}
	
	/**
	 * 将时间毫秒值格式化为小时
	 * @param timeMillis
	 * @param format
	 * @param timezone	指定时区
	 * @return
	 */
	public static String dateFormat(long timeMillis, String format, String timezone){
		String dateFormat = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
			sdf.setTimeZone(TimeZone.getTimeZone(timezone));
			dateFormat = sdf.format(new Date(timeMillis));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateFormat;
	}
	
	/**
	 * 将指定格式的日期转为时间毫秒值
	 * @param dateStr			日期
	 * @param format			日期格式
	 * @return					时间毫秒值，如果格式有误则返回0
	 */
	public static long getTimeMillis(String dateStr, String format){
		long time = 0;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			Date date = sdf.parse(dateStr);
			time = date.getTime();
		} catch (Exception e) {
		}
		return time;
	}
	
}
