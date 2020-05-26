package com.lzs.admin.service.impl;

import com.lzs.admin.dao.PublicKeyMapper;
import com.lzs.admin.service.PublicKeyService;
import com.lzs.entity.Result;
import com.lzs.admin.pojo.PublicKey;
import com.lzs.shop.feign.ShopFeign;
import com.lzs.user.feign.UserFeign;
import com.lzs.utils.ECDSAUtil;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Base64;
import java.util.Map;

@Service
public class PublicKeyServiceImpl implements PublicKeyService {
    @Autowired
    private PublicKeyMapper publicKeyMapper;

    @Autowired
    private ShopFeign shopFeign;

    @Autowired
    private UserFeign userFeign;

    /**
     * 存储公钥到数据库
     * @param id    用户/商家的id
     */
    @Override
    public void savePublicKey(String id, String publicKey) {
        PublicKey pKey = new PublicKey();
        pKey.setId(id);
        pKey.setPublicKey(publicKey);
        publicKeyMapper.insertSelective(pKey);
    }

    /**
     * 根据商家ID查询商家的公钥
     * @param sid 商家ID
     */
    @Override
    public String findKeyById(String sid) {
        PublicKey publicKey = publicKeyMapper.selectByPrimaryKey(sid);
        return publicKey.getPublicKey();
    }

    /**
     * 验证商家的签名
     * @param map 支付信息
     * @return
     */
    @Override
    public boolean verifyShop(Map<String, Object> map) {
        //获取商家的签名
        Result result = shopFeign.payInfo2Sign(map);
        String data = (String) result.getData();
        byte[] sign = Base64.getDecoder().decode(data);
        //获取商家ID
        String id = (String) map.get("sid");
        //根据商家ID获取商家公钥
        PublicKey pKey = publicKeyMapper.selectByPrimaryKey(id);
        String publicKey = pKey.getPublicKey();
        //生成支付信息
        JSONObject jsonObject = JSONObject.fromObject(map);
        String payInfo = jsonObject.toString();
        //验证签名
        try {
            java.security.PublicKey ecPublicKey = ECDSAUtil.string2PublicKey(publicKey);
            boolean verify = ECDSAUtil.verify(payInfo, ecPublicKey, sign);
            return verify;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 验证用户的签名
     * @param map 支付信息和用户的支付密码
     * @return
     */
    @Override
    public boolean verifyUser(Map<String, Object> map) {
        //获取用户的签名
        Result result = userFeign.payInfo2Sign(map);
        String data = (String) result.getData();
        byte[] sign = Base64.getDecoder().decode(data);
        //获取用户ID
        Map<String,Object> payInfoMap = (Map<String, Object>) map.get("payInfo");
        String userID = (String) payInfoMap.get("userID");
        //根据用户ID获取用户公钥
        PublicKey pKey = publicKeyMapper.selectByPrimaryKey(userID);
        String publicKey = pKey.getPublicKey();
        //生成字符串的支付信息
        JSONObject jsonObject = JSONObject.fromObject(payInfoMap);
        String payInfo = jsonObject.toString();
        //验证签名
        try {
            java.security.PublicKey ecPublicKey = ECDSAUtil.string2PublicKey(publicKey);
            boolean verify = ECDSAUtil.verify(payInfo, ecPublicKey, sign);
            return verify;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
