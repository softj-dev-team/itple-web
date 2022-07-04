package com.softj.itple.util;

import java.util.Objects;

public class LongUtils{
    public static boolean noneEmpty(Long no){
        return !isEmpty(no);
    }
    public static boolean isEmpty(Long no){
        return Objects.isNull(no) || no == 0;
    }
}
