package com.softj.itple.util;

public class StringUtils extends org.springframework.util.StringUtils {
    public static boolean noneEmpty(Object str){
        return !isEmpty(str);
    }
}
