package com.lzs.admin.service;

import java.util.Map;

public interface PublicKeyService {
    /**
     * 为用户/商家分配密钥对
     * @param id    用户/商家的id
     */
    void savePublicKey(String id, String publicKey);

    /**
     * 根据商家ID查询商家的公钥
     * @param sid 商家ID
     */
    String findKeyById(String sid);

    /**
     * 对商家进行数字签名验证
     * @param map 支付信息
     * @return
     */
    boolean verifyShop(Map<String, Object> map);

    /**
     * 对用户进行数字签名验证
     * @param map 支付信息和用户的支付密码
     * @return
     */
    boolean verifyUser(Map<String, Object> map);
}
