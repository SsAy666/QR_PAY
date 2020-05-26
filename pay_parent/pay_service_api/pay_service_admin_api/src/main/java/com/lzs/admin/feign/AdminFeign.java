package com.lzs.admin.feign;

import com.lzs.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "admin") //指明微服务名称
@Component
public interface AdminFeign {

    /**
     * 为用户分配密钥对
     * @param map 用户的id和支付密码的集合
     */
    @PostMapping("/admin/KeyPair4User")
    Result KeyPair4User(@RequestBody Map<String,String> map);

    /**
     * 为商家分配密钥对
     * @param shopId 商家ID
     * @return
     */
    @GetMapping("/admin/KeyPair4Shop/{shopId}")
    Result KeyPair4Shop(@PathVariable String shopId);

    /**
     * 完成对商家身份的认证并注册支付信息
     * @param map 支付信息
     * @return
     */
    @PostMapping("/admin/registerPayInfo")
    Result registerPayInfo(@RequestBody Map<String,Object> map);

    /**
     * 根据商家ID查询商家的公钥
     * @param sid 商家ID
     */
    @GetMapping("/admin/findKeyById/{sid}")
    Result findKeyById(@PathVariable String sid);
}
