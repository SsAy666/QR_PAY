package com.lzs.utils;

import java.util.UUID;

/**
 *  生成随机ID工具类
 */
public class IDUtil {
    public static String randomId(){
        String s = UUID.randomUUID().toString();
        s = s.replace("-", "").toUpperCase();
        return s;
    }
}
