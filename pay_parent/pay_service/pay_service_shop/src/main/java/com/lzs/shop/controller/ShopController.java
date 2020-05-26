package com.lzs.shop.controller;

import com.lzs.admin.pojo.Record;
import com.lzs.entity.Result;
import com.lzs.entity.StatusCode;
import com.lzs.shop.pojo.Shop;
import com.lzs.shop.service.ShopService;
import com.lzs.utils.*;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(allowCredentials="true",allowedHeaders="*")
@RequestMapping("/shop")
public class ShopController {
    @Autowired
    private ShopService shopService;

    /**
     * 注册商家
     * @param shop
     * @return Result
     */
    @PostMapping("/regist")
    public Result regist(@RequestBody Shop shop){
        Map<String, String> map = new HashMap<>();
        Shop existShop = shopService.findShopByName(shop.getShopName());
        if (existShop != null) {
            //如果商家名已存在
            return new Result(false, StatusCode.ERROR,"商家名已存在");
        } else {
            //设置商家ID
            shop.setId(IDUtil.randomId());
            //对输入的密码MD5加密(并用商家ID进行撒盐)存储到数据库中
            String oldPassword = shop.getPassword();
            String newPassword = MD5Util.encodeWithSalt(oldPassword,shop.getId());
            shop.setPassword(newPassword);
            //设置刚注册的商家的余额为0
            shop.setBalance(new BigDecimal(0));
            try {
                //商家向移动支付服务器申请私钥以便进行数字签名
                String shopId = shop.getId();
                shopService.getPrivateKey(shopId);
                //申请私钥成功后添加该商家到数据库
                shopService.addShop(shop);
                return new Result(true, StatusCode.OK,"注册成功");
            } catch (Exception e) {
                e.printStackTrace();
                return new Result(false, StatusCode.ERROR,"注册失败");
            }
        }
    }

    /**
     * 商家登录
     * @param shop
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody Shop shop, HttpServletRequest request) {
        Shop loginShop = shopService.login(shop);
        if (loginShop != null){
            request.getSession().setAttribute("shop",loginShop);
            return new Result(true, StatusCode.OK,"登录成功");
        } else {
            return new Result(false, StatusCode.ERROR,"账号或密码错误，请重新输入！");
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
        Shop shop = (Shop) request.getSession().getAttribute("shop");
        if (shop == null) {
            //没找到，返回错误
            return new Result(false, StatusCode.ERROR,"当前用户暂未登录");
        }else {
            //找到，返回成功信息
            return new Result(true, StatusCode.OK,"登录成功",shop);
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
     * 修改密码
     * @param map
     */
    @PostMapping("/updatePwd")
    public Result updatePwd(@RequestBody Map<String,String> map){
        //获取商家ID
        String id = map.get("shopId");
        //获取旧密码
        String oldPwd = map.get("oldPwd");
        //获取新密码
        String newPwd = map.get("newPwd");
        try {
            //修改密码
            shopService.updatePwd(id, oldPwd,newPwd);
        } catch (RuntimeException e) {
            return new Result(false,StatusCode.ERROR,e.getMessage());
        }
        return new Result(true, StatusCode.OK,"修改密码成功");
    }

    /**
     * 生成支付信息并进行签名
     * @param map 支付信息
     * @return
     */
    @PostMapping("/payInfo2Sign")
    public Result payInfo2Sign(@RequestBody Map<String,Object> map) {
        try {
            String sign = shopService.payInfo2Sign(map);
            return new Result(true, StatusCode.OK,"商家签名成功",sign);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,StatusCode.ERROR,"商家签名失败");
        }
    }

    /**
     * 商家通过二维码向用户展示已签名的支付信息（含时间戳）
     * @param map 支付信息
     * @return
     */
    @PostMapping("/PayInfo2QR")
    public Result PayInfo2QR(@RequestBody Map<String,Object> map,HttpServletRequest request) {

        /*真实的支付信息*/
        //获取商家ID
        String sid = (String) map.get("sid");
       /* //修改时间戳
        String fakeTimeStamp = "2020-05-18 13:36:1";
        //将假时间戳加入假支付信息中
        map.put("timeStamp",fakeTimeStamp);*/
        //获取商品个数
        String pnumStr = (String) map.get("pnum");
        Integer pnum = new Integer(pnumStr);
        //获取商品单价
        String shopPriceStr = map.get("shopPrice").toString();
        //判断单价是整数还是小数
        boolean flag = StrUtil.isInteger(shopPriceStr);
        BigDecimal sumAmount = null;
        //单价是整数
        if (flag) {
            Integer shopPriceInt = Integer.valueOf(shopPriceStr);
            BigDecimal shopPrice = new BigDecimal(shopPriceInt);
            //获取商品总金额
            sumAmount = shopPrice.multiply(new BigDecimal(pnum));
            //将总金额加入支付信息中
            map.put("sumAmount",sumAmount);
        } else { //单价是小数
            double shopPriceDouble = Double.parseDouble(shopPriceStr);
            //保留两位小数
            DecimalFormat df1 = new DecimalFormat("0.00");
            String shopPriceStrs = df1.format(shopPriceDouble);
            BigDecimal shopPrice = new BigDecimal(shopPriceStrs);
            //获取商品总金额
            sumAmount = shopPrice.multiply(new BigDecimal(pnum));
            //将总金额加入支付信息中
            map.put("sumAmount",sumAmount);
        }

        /*伪造支付信息*/
       /* //创建假支付信息
        Map<String,Object> mapFake = new LinkedHashMap<>();
        //获取商品ID
        String pid = (String) map.get("pid");
        //获取商品名
        String pname = (String) map.get("pname");
        //修改商品总金额
        //BigDecimal fakeSumAmount = new BigDecimal(1000);
        //修改商家ID
        String fakeShopID = "F119B0E5032F48EC8CD47D5AF0182E6D";
        //将假商家加入假支付信息中
        mapFake.put("sid",fakeShopID);
        //将商品ID加入假支付信息中
        mapFake.put("pid",pid);
        //将商品名加入假支付信息中
        mapFake.put("pname",pname);
        //将商品个数加入假支付信息中
        mapFake.put("pnum",pnumStr);
        //将商品单价加入假支付信息中
        mapFake.put("shopPrice",shopPriceStr);
        //将假时间戳加入假支付信息中
        mapFake.put("timeStamp",fakeTimeStamp);
        //将假总金额加入假支付信息中
        mapFake.put("sumAmount",sumAmount);*/

        //将支付信息转成字符串
        JSONObject jsonObject = JSONObject.fromObject(map);
        String payInfo = jsonObject.toString();

        try {
            //移动支付服务器对商家身份进行认证并注册该支付信息
            boolean isRegister = shopService.register2Admin(map);
            if (isRegister) {
                try {
                    //将支付信息（含时间戳）进行签名
                    String signPayInfo = shopService.payInfo2Sign(map);
                    //将商家ID + 已签名的支付信息（含时间戳）+ 未签名的支付信息（含时间戳） 生成二维码
                    String QRInfo = sid + signPayInfo + payInfo;
                    String picName = shopService.PayInfo2QR(QRInfo,request);
                    return new Result(true, StatusCode.OK,"二维码生成成功",picName);
                } catch (Exception e) {
                    e.printStackTrace();
                    return new Result(false, StatusCode.ERROR,"二维码生成失败");
                }
            } else {
                return new Result(false, StatusCode.ERROR,"商家身份认证失败，无法进行交易");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, StatusCode.ERROR,"商家身份认证失败，无法进行交易");
        }
    }

    /**
     * 商家收入明细
     * @param id 商家ID
     * @return
     */
    @GetMapping("/payDetails")
    public Result payDetails(String id) {
        try {
            List<Record> records = shopService.payDetails(id);
            return new Result(true, StatusCode.OK,"查询收入明细成功",records);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,StatusCode.ERROR,"查询收入明细失败");
        }
    }
}
