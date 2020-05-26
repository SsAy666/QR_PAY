package com.lzs.shop.service.impl;

import com.alibaba.fastjson.JSON;
import com.lzs.admin.pojo.Record;
import com.lzs.entity.Result;
import com.lzs.admin.feign.AdminFeign;
import com.lzs.shop.pojo.Shop;
import com.lzs.shop.dao.ShopMapper;
import com.lzs.shop.service.ShopService;
import com.lzs.utils.*;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.security.PrivateKey;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class ShopServiceImpl implements ShopService {
    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private AdminFeign adminFeign;

    //配置文件中文件上传路径
    @Value("${prop.upload-folder}")
    private String UPLOAD_FOLDER;

    /**
     * 根据商家名查找该商家
     * @param shopName 商家名
     * @return
     */
    @Override
    public Shop findShopByName(String shopName) {
        return shopMapper.findUserByName(shopName);
    }

    /**
     * 添加该商家到数据库
     * @param shop
     */
    @Override
    public void addShop(Shop shop) {
        shopMapper.insertSelective(shop);
    }

    /**
     * 商家登录
     * @param shop
     * @return
     */
    @Override
    public Shop login(Shop shop) {
        //获取用户名
        String shopName = shop.getShopName();
        //根据商家名查询商家ID
        String id = shopMapper.findIdByName(shopName);
        //获取用户密码
        String password = shop.getPassword();
        String newPassword = MD5Util.encodeWithSalt(password,id);
        return shopMapper.findUserByNameAndPwd(shopName,newPassword);
    }

    /**
     * 修改密码
     * @param id        商家ID
     * @param oldPwd    旧密码
     * @param newPwd    新密码
     */
    @Override
    public void updatePwd(String id, String oldPwd, String newPwd) {
        oldPwd = MD5Util.encodeWithSalt(oldPwd,id);
        Shop shop = shopMapper.findUserByIdAndPwd(id, oldPwd);
        if (shop != null) {
            newPwd = MD5Util.encodeWithSalt(newPwd,id);
            shopMapper.updatePwd(id,newPwd);
        } else {
            throw new RuntimeException("原密码错误");
        }
    }

    /**
     * 商家向移动支付服务器申请私钥以便进行数字签名
     * @param shopId 商家ID
     */
    @Override
    public void getPrivateKey(String shopId) {
        //获取私钥
        Result result = adminFeign.KeyPair4Shop(shopId);
        String privateKey = JSON.toJSONString(result.getData());
        privateKey = privateKey.substring(1,privateKey.length()-1);
        //将该私钥存入文件中
        String path = "/usr/local/privateKey/shop/" + shopId + ".key";
        //String path = FileUtil.getResourceBasePath() + "/src/main/resources/" + shopId + ".key";
        FileUtil.writeFile(path,privateKey);
    }

    /**
     * 生成支付信息并进行签名
     * @param map 支付信息
     * @return
     */
    @Override
    public String payInfo2Sign(Map<String, Object> map) {
        //生成支付信息
        JSONObject jsonObject = JSONObject.fromObject(map);
        String payInfo = jsonObject.toString();
        //获取商家ID
        String shopId = (String) map.get("sid");
        System.out.println("shopId" + shopId);
        //读取商家私钥（linux）
        String path = "/usr/local/privateKey/shop/" + shopId + ".key";
        String privateKey = FileUtil.readFile(path).replaceAll("\\\\n","");
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
     * 移动支付服务器对商家身份进行认证并注册该支付信息
     * @param map
     * @return
     */
    @Override
    public boolean register2Admin(Map<String, Object> map) {
        Result result = adminFeign.registerPayInfo(map);
        boolean flag = result.isFlag();
        return flag;
    }

    /**
     * 将已签名的支付信息（含时间戳）生成二维码
     * @param signPayInfo 已签名的支付信息（含时间戳）
     * @return
     */
    @Override
    public String PayInfo2QR(String signPayInfo, HttpServletRequest request) {
        //获取二维码图片的绝对路径
        String filePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/qrCode_img/";
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        //获取二维码的图片名
        String picName = IDUtil.randomId() + ".png";
        //生成二维码
        GenerateQRCodeUtil.generate(signPayInfo,filePath+picName);
        return picName;
    }

    /**
     * 商家收入明细
     * @param id 商家ID
     * @return
     */
    @Override
    public List<Record> payDetails(String id) {
        List<Record> records = shopMapper.payDetails(id);
        return records;
    }
}

