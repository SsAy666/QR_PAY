package com.lzs.utils;

import java.security.MessageDigest;

/**
 *  MD5加密工具类
 */
public class MD5Util {
    private static final char[] HEXDIGESTS=new char[]{'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    public static String encode(String source){
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");

            md5.update(source.getBytes("utf-8"));

            byte[] digests = md5.digest();
            char[] result=new char[digests.length*2];
            int resultIndex=0;
            for (int i=0;i<digests.length;i++){
                byte bytex=digests[i];


                /***
                 * 25=0 0 0 1 1 0 0 1>>>4=======0 0 0 0 0 0 0 1& 0 0 0 0 1 1 1 1----0 0 0 0  0 0 0 1
                 * 25=0 0 0 1 1 0 0 1&0 0 0 0 1 1 1 1=------0 0 0 0 1 0 0 1
                 *
                 */
                result[resultIndex++]=HEXDIGESTS[bytex>>>4&0xf];
                result[resultIndex++]=HEXDIGESTS[bytex&0xf];
            }
            return new String(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean compare(String source,String encoded){
        return encode(source).equals(encoded);
    }
    public static String encodeWithSalt(String source,String salt){
        return encode(source+salt);
    }

    public static boolean compareWithSalt(String source,String salt,String encoded){
        return compare(encodeWithSalt(source,salt),encoded);
    }

    public static void main(String[] args) {
        String encode = encodeWithSalt("123456","50AD74EF7DD7494CA43F2490AD1C75C8");
        System.out.println(encode);
    }
}
