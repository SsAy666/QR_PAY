package com.lzs.utils;

/**
 * 判断该字符串是否为正数或小数工具类
 */
public class StrUtil {
    public static boolean isInteger(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        boolean numeric = isInteger("33.3");
        System.out.println(numeric);
    }
}
