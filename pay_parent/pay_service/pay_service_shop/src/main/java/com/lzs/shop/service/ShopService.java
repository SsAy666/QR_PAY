package com.lzs.shop.service;

import com.lzs.admin.pojo.Record;
import com.lzs.shop.pojo.Shop;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface ShopService {

    /**
     * 根据商家名查找该商家
     * @param shopName 商家名
     * @return
     */
    Shop findShopByName(String shopName);

    /**
     * 添加该商家到数据库
     * @param shop
     */
    void addShop(Shop shop);

    /**
     * 商家登录
     * @param shop
     * @return
     */
    Shop login(Shop shop);

    /**
     * 修改密码
     * @param id  商家ID
     * @param oldPwd    旧密码
     * @param newPwd    新密码
     */
    void updatePwd(String id, String oldPwd, String newPwd);

    /**
     * 商家向移动支付服务器申请私钥以便进行数字签名
     * @param shopId 商家ID
     */
    void getPrivateKey(String shopId);

    /**
     * 生成支付信息并进行签名
     * @param map 支付信息
     * @return
     */
    String payInfo2Sign(Map<String, Object> map);

    /**
     * 移动支付服务器对商家身份进行认证并注册该支付信息
     * @param map
     * @return
     */
    boolean register2Admin(Map<String, Object> map);

    /**
     * 将已签名的支付信息（含时间戳）生成二维码
     * @param signPayInfo 已签名的支付信息（含时间戳）
     * @return
     */
    String PayInfo2QR(String signPayInfo, HttpServletRequest request);

    /**
     * 商家收入明细
     * @param id 商家ID
     * @return
     */
    List<Record> payDetails(String id);
}
