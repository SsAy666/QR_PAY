package com.lzs.admin.controller;

import com.lzs.admin.pojo.Record;
import com.lzs.admin.service.AdminService;
import com.lzs.admin.service.PublicKeyService;
import com.lzs.entity.Result;
import com.lzs.entity.StatusCode;
import com.lzs.admin.pojo.Admin;
import com.lzs.utils.AESUtil;
import com.lzs.utils.ECDSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.KeyPair;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(allowCredentials="true",allowedHeaders="*")
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @Autowired
    private PublicKeyService publicKeyService;

    /**
     * 管理员登录
     * @param admin
     * @param request
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody Admin admin, HttpServletRequest request) {
        Admin loginAdmin = adminService.login(admin);
        if (loginAdmin != null){
            request.getSession().setAttribute("admin",loginAdmin);
            return new Result(true, StatusCode.OK,"登录成功");
        } else {
            return new Result(false, StatusCode.ERROR,"用户名或者密码错误，请重新输入！");
        }
    }

    /**
     * 用户名回显
     * @param request
     * @return
     */
    @GetMapping("/getName")
    public Result getName(HttpServletRequest request) {
        //获取自己的session对象
        Admin admin = (Admin) request.getSession().getAttribute("admin");
        if (admin == null) {
            //没找到，返回错误
            return new Result(false, StatusCode.ERROR,"系统管理员暂未登录");
        }else {
            //找到，返回成功信息
            return new Result(true, StatusCode.OK,"登录成功",admin);
        }
    }

    /**
     * 退出登录
     * @param request
     * @return
     */
    @GetMapping("/logout")
    public Result logout (HttpServletRequest request) {
        request.getSession().invalidate();
        return new Result(true, StatusCode.OK,"退出登录成功");
    }

    /**
     * 为用户分配密钥对
     * @param map 用户的id和支付密码的集合
     */
    @PostMapping("/KeyPair4User")
    public Result KeyPair4User(@RequestBody Map<String,String> map) {
        //获取用户的id
        String id = map.get("id");
        //获取用户的支付密码
        String payPWD = map.get("payPwd");
        try {
            //生成密钥对
            KeyPair keyPair = ECDSAUtil.getKeyPair();
            //获取公钥
            String publicKey = ECDSAUtil.getPublicKey(keyPair);
            //获取私钥
            String privateKey = ECDSAUtil.getPrivateKey(keyPair);
            //存储公钥到数据库
            publicKeyService.savePublicKey(id,publicKey);
            //将私钥进行对称加密
            String enPrivateKey = AESUtil.AESEncode(payPWD, privateKey);
            return new Result(true, StatusCode.OK,"密钥分配成功",enPrivateKey);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, StatusCode.ERROR,"密钥分配失败");
        }
    }

    /**
     * 为商家分配密钥对
     * @param shopId 商家ID
     * @return
     */
    @GetMapping("/KeyPair4Shop/{shopId}")
    public Result KeyPair4Shop(@PathVariable String shopId) {
        try {
            //生成密钥对
            KeyPair keyPair = ECDSAUtil.getKeyPair();
            //获取公钥
            String publicKey = ECDSAUtil.getPublicKey(keyPair);
            //获取私钥
            String privateKey = ECDSAUtil.getPrivateKey(keyPair);
            //存储公钥到数据库
            publicKeyService.savePublicKey(shopId,publicKey);
            return new Result(true, StatusCode.OK,"密钥分配成功",privateKey);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, StatusCode.ERROR,"密钥分配失败");
        }
    }

    /**
     * 完成对商家身份的认证并注册支付信息
     * @param map 支付信息
     * @return
     */
    @PostMapping("/registerPayInfo")
    public Result registerPayInfo(@RequestBody Map<String,Object> map) {
        //对商家进行数字签名验证
        try {
            boolean isShop = publicKeyService.verifyShop(map);
            if (isShop) {
                return new Result(true, StatusCode.OK,"认证成功，已注册该支付信息",map);
            }else {
                return new Result(false, StatusCode.ERROR,"认证失败，无法注册该支付信息");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, StatusCode.ERROR,"认证失败，无法注册该支付信息");
        }
    }

    /**
     * 根据商家ID查询商家的公钥
     * @param sid 商家ID
     */
    @GetMapping("/findKeyById/{sid}")
    public Result findKeyById(@PathVariable String sid) {
        try {
            String shopKey = publicKeyService.findKeyById(sid);
            return new Result(true, StatusCode.OK,"查询商家公钥成功",shopKey);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, StatusCode.ERROR,"查询商家公钥失败！");
        }
    }

    /**
     * 验证用户的签名并将付款记录注册
     * @param map 支付信息和用户的支付密码
     * @return
     */
    @PostMapping("/verifyPayInfo")
    public Result verifyPayInfo(@RequestBody Map<String,Object> map) {
        try {
            //对用户进行数字签名验证
            boolean isUser = publicKeyService.verifyUser(map);
            if (isUser) {
                try {
                    //用户扣钱商家加钱并将付款记录存入数据库
                    adminService.changeMoney(map);
                    return new Result(true, StatusCode.OK,"支付成功");
                } catch (Exception e) {
                    e.printStackTrace();
                    return new Result(false, StatusCode.ERROR,"余额不足，该交易失败！");
                }
            } else {
                return new Result(false, StatusCode.ERROR,"支付密码错误，该交易失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, StatusCode.ERROR,"对用户身份认证失败，无法进行交易！");
        }
    }

    /**
     * 查询所有付款记录
     * @return
     */
    @GetMapping("/findAllRecord")
    public Result findAllRecord() {
        try {
            List<Record> records = adminService.findAllRecord();
            return new Result(true, StatusCode.OK,"查询所有付款记录成功",records);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, StatusCode.ERROR,"查询所有付款记录失败");
        }
    }
}
