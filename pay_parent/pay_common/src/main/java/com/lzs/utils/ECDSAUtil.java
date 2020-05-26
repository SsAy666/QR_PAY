package com.lzs.utils;


import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * ECDSA数字签名算法工具类
 */
public class ECDSAUtil {
    static {
        try {
            Security.addProvider(new BouncyCastleProvider());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //  生成密钥对
    public static KeyPair getKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", "BC");
        keyPairGenerator.initialize(256, new SecureRandom());
        return keyPairGenerator.generateKeyPair();
    }

    //  获取公钥(Base64编码)
    public static String getPublicKey(KeyPair keyPair) {
        ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();
        return Base64AndByteUtil.byte2Base64(publicKey.getEncoded());
    }

    //  获取私钥(Base64编码)
    public static String getPrivateKey(KeyPair keyPair) {
        ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();
        return Base64AndByteUtil.byte2Base64(privateKey.getEncoded());
    }

    //  将Base64编码的公钥转为ECPublicKey对象
    public static PublicKey string2PublicKey(String pubStr) throws Exception {
        byte[] keyBytes = Base64AndByteUtil.base64Byte(pubStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");
        return keyFactory.generatePublic(keySpec);
    }

    //  将base64编码的私钥转为ECPrivateKey对象
    public static PrivateKey string2PrivateKey(String priStr) throws Exception{
        byte[] keyBytes = Base64AndByteUtil.base64Byte(priStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");
        return keyFactory.generatePrivate(keySpec);
    }

    //加签，needSignedData是需要签名的原始数据，privateKey是私钥
    public static byte[] sign(String needSignedData,PrivateKey privateKey) throws Exception {
        byte[] data = needSignedData.getBytes();
        Signature signature = Signature.getInstance("SHA512withECDSA");
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }

    //验签，needSignedData是需要签名的原始数据，publicKey是公钥，sign是已签名的数据
    public static boolean verify(String needSignedData,PublicKey publicKey, byte[] sign) throws Exception {
        byte[] data = needSignedData.getBytes();
        Signature signature = Signature.getInstance("SHA512withECDSA");
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(sign);
    }

    public static void main(String[] args) {
        try {
            //生成密钥对
            KeyPair keyPair = getKeyPair();
            /*//生成私钥
            PrivateKey privateKey = keyPair.getPrivate();
            //生成公钥
            PublicKey publicKey = keyPair.getPublic();*/

            String publicKey = getPublicKey(keyPair);
            String privateKey = getPrivateKey(keyPair);
            PublicKey ecPublicKey = string2PublicKey(publicKey);
            PrivateKey ecPrivateKey = string2PrivateKey(privateKey);

            //准备需要签名数据
            String needSignedData = "heo";            //假设需要签名的数据被篡改
            //String needSignedDataError = "hello";
            //签名
            byte[] sign = sign(needSignedData, ecPrivateKey);
            String signData = Base64.getEncoder().encodeToString(sign);
            System.out.println(signData);
            //验签
            boolean verify = verify(needSignedData, ecPublicKey, sign);
            System.out.println("签名成功？" + verify);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}