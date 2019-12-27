package com.i4uworks.weys.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {

	protected static Logger logger = LoggerFactory.getLogger(Utils.class);
	
	/* 숫자 포맷 1000 자리 넘는거 변경 */
	public static String setStringFormatInteger(String number) {

		String underNum = "";
		if(number.contains(".")){
			underNum = number.substring(number.indexOf("."));
			number = number.substring(0, number.indexOf("."));
		}
		int length = number.length();
		if(length < 4)
			return number + underNum;
		
		String prefix = number.substring(0, length - 3);
		String suffix = number.substring(length - 3);
		
		return setStringFormatInteger(prefix) + "," + suffix + underNum;
	}

	/* 오늘 날짜 format 에 맞춰 가져오기 */
	public static String getTodayDate(String format) {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.KOREA);
		return sdf.format(date);
	}


	/**
	 * Comment : 특정 날짜로 부터 날짜 계산해서 구하기
	 * 
	 * @param when
	 *            날짜 차이 ( 2일전, 4일전 )
	 * @param year
	 *            해당 년도
	 * @param month
	 *            해당 월
	 * @param day
	 *            해당 일
	 */
	public static String getDateFormat(int when, int year, int month, int day) {

		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day);
		cal.add(Calendar.DATE, when);
		Date d = cal.getTime();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA);
		String date = sdf.format(d);

		return date;
	}

	public static String getDateFormat(int when, String dt) {

		String [] dtArray = dt.split("\\.");
		
		Calendar cal = Calendar.getInstance();
		cal.set(Integer.parseInt(dtArray[0]), Integer.parseInt(dtArray[1]) - 1, Integer.parseInt(dtArray[2]));
		cal.add(Calendar.DATE, when);
		Date d = cal.getTime();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA);
		String date = sdf.format(d);

		return date;
	}

	/**
	 * Comment : 오늘부터 몇일 날짜 차이 구하기
	 * 
	 * @param when
	 *            날짜 차이 ( 2일전, 4일전 )
	 * @param year
	 *            해당 년도
	 * @param month
	 *            해당 월
	 * @param day
	 *            해당 일
	 */
	public static String getDiffDate(int when) {

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, when);
		Date d = cal.getTime();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA);
		String date = sdf.format(d);

		return date;
	}

	public static int getDay(String date) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
		Date d = sdf.parse(date);
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		return cal.get(Calendar.DAY_OF_WEEK) - 1;
	}

	public static long diffTwoDate(String date1, String date2){

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
		
		long days = 0;
		try{
			Date d1 = sdf.parse(date1);
			Date d2 = sdf.parse(date2);
			
			long diff = d1.getTime() - d2.getTime();
			days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		return days;
	}

	/**
	 * min분 이후 구하기
	 * @return
	 */
	public static String getAfter30Min(int min){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, min);
		Date d = cal.getTime();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREA);
		String date = sdf.format(d);
		return date;
	}
	
	public static String getDiffMinHM(int min){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 120);
		Date d = cal.getTime();

		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.KOREA);
		String date = sdf.format(d);
		return date;
	}
}
