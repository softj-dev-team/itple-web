package com.softj.itple.util;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;

public class CustomUtil {
	//날짜 더하기
	public static Date dateAdd(Date curDate, int day) {
		Calendar cal = Calendar.getInstance();
        cal.setTime(curDate);
        cal.add(Calendar.DATE, day);
		return cal.getTime();
	}

	//바이트 구하기
	public static int getByte(String str){
		String charset = "euc-kr";
		try {
			return str.getBytes(charset).length;
		}catch (UnsupportedEncodingException e){
			e.printStackTrace();
			return 0;
		}
	}

	//핸드폰번호 하이픈표시
	public static String phoneToHyphen(String phone){
		String phone1 = phone.replace("-","");
		if(phone1.startsWith("02")){
			if(phone1.length() == 10){
				phone1 = phone1.substring(0,2)+"-"+phone1.substring(2,6)+"-"+phone1.substring(6,10);
			}else if(phone1.length() == 9){
				phone1 = phone1.substring(0,2)+"-"+phone1.substring(2,5)+"-"+phone1.substring(5,9);
			}else{
				phone1 = phone;
			}
		}else if(phone1.startsWith("10")){
			if(phone1.length() == 10){
				phone1 = "0"+phone1.substring(0,2)+"-"+phone1.substring(2,6)+"-"+phone1.substring(6,10);
			}else if(phone1.length() == 9){
				phone1 = "0"+phone1.substring(0,2)+"-"+phone1.substring(2,5)+"-"+phone1.substring(5,9);
			}else{
				phone1 = phone;
			}
		}else if(phone1.length() == 11){
			phone1 = phone1.substring(0,3)+"-"+phone1.substring(3,7)+"-"+phone1.substring(7,11);
		}else if(phone1.length() == 10){
			phone1 = phone1.substring(0,3)+"-"+phone1.substring(3,6)+"-"+phone1.substring(6,10);
		}else{
			phone1 = phone;
		}
		return phone1;
	}
}
