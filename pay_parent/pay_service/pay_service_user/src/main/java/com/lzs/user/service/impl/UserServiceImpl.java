package com.lzs.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.lzs.admin.pojo.Record;
import com.lzs.entity.Result;
import com.lzs.admin.feign.AdminFeign;
import com.lzs.entity.StatusCode;
import com.lzs.user.pojo.User;
import com.lzs.user.dao.UserMapper;
import com.lzs.user.service.UserService;
import com.lzs.utils.*;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.PrivateKey;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AdminFeign adminFeign;

    /**
     * 根据用户名查找该用户
     * @param username 用户名
     * @return
     */
    @Override
    public User findUserByName(String username) {
        return userMapper.findUserByName(username);
    }

    /**
     * 添加该用户到数据库
     * @param user
     */
    @Override
    public void addUser(User user) {
        userMapper.insertSelective(user);
    }

    /**
     * 用户登录
     * @param user
     * @return
     */
    @Override
    public User login(User user) {
        //获取用户名
        String username = user.getUsername();
        //根据用户名查询用户ID
        String id = userMapper.findIdByName(username);
        //获取用户密码
        String password = user.getPassword();
        String newPassword = MD5Util.encodeWithSalt(password,id);
        return userMapper.findUserByNameAndPwd(username,newPassword);
    }

    /**
     * 修改登录密码
     * @param id        用户ID
     * @param oldPwd    旧密码
     * @param newPwd    新密码
     */
    @Override
    public void updatePwd(String id, String oldPwd, String newPwd) {
        oldPwd = MD5Util.encodeWithSalt(oldPwd,id);
        User user = userMapper.findUserByIdAndPwd(id, oldPwd);
        if (user != null) {
            newPwd = MD5Util.encodeWithSalt(newPwd,id);
            userMapper.updatePwd(id,newPwd);
        } else {
            throw new RuntimeException("原密码错误");
        }
    }

    /**
     * 修改支付密码
     * @param id        用户ID
     * @param oldPwd    旧密码
     * @param newPwd    新密码
     */
    @Override
    public void updatePayPwd(String id, String oldPwd, String newPwd) {
        oldPwd = MD5Util.encodeWithSalt(oldPwd,id);
        User user = userMapper.findUserByIdAndPayPwd(id, oldPwd);
        if (user != null) {
            newPwd = MD5Util.encodeWithSalt(newPwd,id);
            userMapper.updatePayPwd(id,newPwd);
        } else {
            throw new RuntimeException("原密码错误");
        }
    }

    /**
     * 用户向移动支付服务器申请私钥以便进行数字签名
     * @param map 用户的id和密码的集合
     */
    @Override
    public void getPrivateKey(Map<String, String> map) {
        Result result = adminFeign.KeyPair4User(map);
        //获取加密的私钥
        String privateKey = JSON.toJSONString(result.getData());
        privateKey = privateKey.substring(1,privateKey.length()-1);
        //将该加密的私钥存入文件中
        String id = map.get("id");
        String path = "/usr/local/privateKey/user/" + id + ".key";
        //String path = FileUtil.getResourceBasePath() + "/src/main/resources/" + id + ".key";
        FileUtil.writeFile(path,privateKey);
    }

    /**
     * 获取商家公钥
     * @param sid 商家ID
     * @return
     */
    @Override
    public String getShopKey(String sid) {
        Result result = adminFeign.findKeyById(sid);
        //获取商家公钥
        String shopKey = (String) result.getData();
        return shopKey;
    }

    /**
     * 读取二维码中的内容
     * @param qrPath QR码的存储路径
     * @return
     */
    @Override
    public String readQR(String qrPath) {
        com.google.zxing.Result result = ReadQRCodeUtil.readQRCode(qrPath);
        String content = result.getText();
        return content;
    }

    /**
     * 对商家进行验证
     * @param shopKey       商家公钥
     * @param signPayInfo   已签名的支付信息
     * @param payInfo       支付信息
     * @return
     */
    @Override
    public boolean verifyShop(String shopKey, String signPayInfo, String payInfo) {
        byte[] sign = Base64.getDecoder().decode(signPayInfo);
        //验证签名
        try {
            java.security.PublicKey shopPublicKey = ECDSAUtil.string2PublicKey(shopKey);
            boolean verify = ECDSAUtil.verify(payInfo, shopPublicKey, sign);
            return verify;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 用户确认支付并进行签名
     * @param map 支付信息
     * @return
     */
    @Override
    public String payInfo2Sign(Map<String, Object> map) {
        //获取用户支付密码
        String payPwd = (String) map.get("payPwd");
        //获取用户ID
        Map<String,Object> payInfoMap = (Map<String, Object>) map.get("payInfo");
        String userID = (String) payInfoMap.get("userID");
        //将支付信息转成字符串
        JSONObject jsonObject = JSONObject.fromObject(payInfoMap);
        String payInfo = jsonObject.toString();
        //读取加密的私钥(linux)
        String path = "/usr/local/privateKey/user/" + userID + ".key";
        String enPrivateKey = FileUtil.readFile(path).replaceAll("\\\\n","");
        //解密获得私钥
        String privateKey = AESUtil.AESDncode(payPwd, enPrivateKey);
        try {
            //将base64编码的私钥转为ECPrivateKey对象
            PrivateKey ecPrivateKey = ECDSAUtil.string2PrivateKey(privateKey);
            //对支付信息进行签名
            byte[] signData = ECDSAUtil.sign(payInfo, ecPrivateKey);
            String sign = Base64.getEncoder().encodeToString(signData);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 查询用户当前余额
     * @param id 用户ID
     * @return
     */
    @Override
    public BigDecimal getAmount(String id) {
        User user = userMapper.selectByPrimaryKey(id);
        BigDecimal balance = user.getBalance();
        return balance;
    }

    /**
     * 用户充值
     * @param amount 充值金额
     * @param userId 用户ID
     */
    @Override
    public void addMoney(BigDecimal amount, String userId) {
        userMapper.addMoney(amount,userId);
    }

    /**
     * 查询用户消费明细
     * @param id 用户ID
     * @return
     */
    @Override
    public List<Record> payDetails(String id) {
        List<Record> records = userMapper.payDetails(id);
        return records;
    }
}
