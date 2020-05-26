package com.lzs.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;

/**
 *  Base64编码与字节数组的相互转化工具类
 */
public class Base64AndByteUtil {

    //  字节组转Base64编码
    public static String byte2Base64(byte[] bytes){
        return new BASE64Encoder().encode(bytes);
    }

    //  base64编码转字节组
    public static byte[] base64Byte(String base64Code) throws IOException {
        return new BASE64Decoder().decodeBuffer(base64Code);
    }
}
