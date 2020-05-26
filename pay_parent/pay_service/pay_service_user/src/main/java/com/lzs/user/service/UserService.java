package com.lzs.user.service;

import com.lzs.admin.pojo.Record;
import com.lzs.user.pojo.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface UserService {

    /**
     * 根据用户名查找该用户
     * @param username 用户名
     * @return
     */
    User findUserByName(String username);

    /**
     * 添加该用户到数据库
     * @param user
     */
    void addUser(User user);

    /**
     * 用户登录
     * @param user
     * @return
     */
    User login(User user);

    /**
     * 修改登录密码
     * @param id        用户ID
     * @param oldPwd    旧密码
     * @param newPwd    新密码
     */
    void updatePwd(String id, String oldPwd, String newPwd);

    /**
     * 修改支付密码
     * @param id        用户ID
     * @param oldPwd    旧密码
     * @param newPwd    新密码
     */
    void updatePayPwd(String id, String oldPwd, String newPwd);

    /**
     * 用户向移动支付服务器申请私钥以便进行数字签名
     * @param map 用户的id和密码的集合
     */
    void getPrivateKey(Map<String, String> map);

    /**
     * 用户向移动支付服务器请求商家的公钥
     * @param sid 商家ID
     */
    String getShopKey(String sid);

    /**
     * 读取二维码中的内容
     * @param qrPath QR码的存储路径
     * @return
     */
    String readQR(String qrPath);

    /**
     * 对商家进行验证
     * @param shopKey       商家公钥
     * @param signPayInfo   已签名的支付信息
     * @param payInfo       支付信息
     * @return
     */
    boolean verifyShop(String shopKey, String signPayInfo, String payInfo);

    /**
     * 用户确认支付并进行签名
     * @param map 支付信息
     * @return
     */
    String payInfo2Sign(Map<String, Object> map);

    /**
     * 查询用户当前余额
     * @param id 用户ID
     * @return
     */
    BigDecimal getAmount(String id);

    /**
     * 用户充值
     * @param amount 充值金额
     * @param userId 用户ID
     */
    void addMoney(BigDecimal amount, String userId);

    /**
     * 查询用户消费明细
     * @param id 用户ID
     * @return
     */
    List<Record> payDetails(String id);
}
