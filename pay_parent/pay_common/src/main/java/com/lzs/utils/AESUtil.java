package com.lzs.utils;

import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/*
 * AES对称加密和解密工具类
 */
public class AESUtil {
    /**
     * 加密
     * @param encodeRules 加密的规则
     * @param content     加密的内容
     * @return
     */
    public static String AESEncode(String encodeRules,String content){
        try {
            //1.构造密钥生成器，指定为AES算法,不区分大小写
            KeyGenerator keygen=KeyGenerator.getInstance("AES");
            //2.根据ecnodeRules规则初始化密钥生成器，生成一个128位的随机源,根据传入的字节数组
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(encodeRules.getBytes());
            keygen.init(128, random);
            //3.产生原始对称密钥
            SecretKey original_key=keygen.generateKey();
            //4.获得原始对称密钥的字节数组
            byte [] raw=original_key.getEncoded();
            //5.根据字节数组生成AES密钥
            SecretKey key=new SecretKeySpec(raw, "AES");
            //6.根据指定算法AES自成密码器
            Cipher cipher=Cipher.getInstance("AES");
            //7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密解密(Decrypt_mode)操作，第二个参数为使用的KEY
            cipher.init(Cipher.ENCRYPT_MODE, key);
            //8.获取加密内容的字节数组(这里要设置为utf-8)不然内容中如果有中文和英文混合中文就会解密为乱码
            byte [] byte_encode=content.getBytes("utf-8");
            //9.根据密码器的初始化方式--加密：将数据加密
            byte [] byte_AES=cipher.doFinal(byte_encode);
            //10.将加密后的数据转换为字符串
            //这里用Base64Encoder中会找不到包
            //解决办法：
            //在项目的Build path中先移除JRE System Library，再添加库JRE System Library，重新编译后就一切正常了。
            String AES_encode=new String(new BASE64Encoder().encode(byte_AES));
            //11.将字符串返回
            return AES_encode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //如果有错就返加nulll
        return null;
    }

    /**
     * 解密
     * @param encodeRules 解密的规则
     * @param content     解密的内容
     * @return
     */
    public static String AESDncode(String encodeRules,String content){
        try {
            //1.构造密钥生成器，指定为AES算法,不区分大小写
            KeyGenerator keygen=KeyGenerator.getInstance("AES");
            //2.根据ecnodeRules规则初始化密钥生成器，生成一个128位的随机源,根据传入的字节数组
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(encodeRules.getBytes());
            keygen.init(128, random);
            //3.产生原始对称密钥
            SecretKey original_key=keygen.generateKey();
            //4.获得原始对称密钥的字节数组
            byte [] raw=original_key.getEncoded();
            //5.根据字节数组生成AES密钥
            SecretKey key=new SecretKeySpec(raw, "AES");
            //6.根据指定算法AES自成密码器
            Cipher cipher=Cipher.getInstance("AES");
            //7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密(Decrypt_mode)操作，第二个参数为使用的KEY
            cipher.init(Cipher.DECRYPT_MODE, key);
            //8.将加密并编码后的内容解码成字节数组
            byte [] byte_content= new BASE64Decoder().decodeBuffer(content);
            //解密
            byte [] byte_decode=cipher.doFinal(byte_content);
            String AES_decode=new String(byte_decode,"utf-8");
            return AES_decode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //如果有错就返加nulll
        return null;
    }

    public static void main(String[] args) {
        //加密
        String encodeRules="123456";
        String content = "CPu1RjGq4jEaU/yLqpwyt7u/iaf89NAAWgBydypMcQ8hMCRqaqSIf5oM9cNc90FFWDBfhpJSZ/oX\\ne+BsdBjUMCQeeSnUa58kS/6J/it2HpJ8+4uourVHFKTrsO67s7u018PEsrN9fNVblm1VgfeLJltB\\nx/RUGltNS/87b/vjodT7Rhr1DmCBseR0I0igCLqOf4R8a4LCNE4axQ51LCBPDOZu2aXQ6YZ/7R9k\\nXjYc6i1xX8IGKg63P+JHirshsQxmv4D75xIcXY9sEH1Su5Txfg==";
        String s = content.replaceAll("\\\\n", "");
        System.out.println(s.length());
        //String s = content.replaceAll("\r|\n", "");
        //System.out.println("根据输入的规则"+encodeRules+"加密后的密文是:"+AESUtil.AESEncode(encodeRules, content));

        //解密
       /* String encodeRules="123456";
        String str = "P43Sdy6vM/TOxTSI4aS4gtlfCVLJz1U8F/xrGlD4WcqMAZMl5i8/38QTR4Iv3dMrjFw2YZHYZ0T9\n" +
                "rYh8kU715zn0tV6aqNvpmWJ5z9w1lFClEniWjgny6+ijW+lcE46QKOrRAT4KZ7KlIYKOZErhIgzU\n" +
                "ywIGhLOUk0/ebiam4REqvWkUTMFxgmQX3YPlg6W58YbyVHqXbNyf4ql7il3hMPXuH4DSyPh+Rn/u\n" +
                "kXred/OE3ZGSDHIvFv3br/DlezvEpEmJU0L7QE4nAzI85nwYBA==";

        System.out.println("根据输入的规则"+encodeRules+"解密后的明文是:"+AESUtil.AESDncode(encodeRules, str));*/

    }

}