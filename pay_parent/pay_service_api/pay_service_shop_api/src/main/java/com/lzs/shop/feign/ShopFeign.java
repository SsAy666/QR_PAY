package com.lzs.shop.feign;

import com.lzs.entity.Result;
import com.lzs.shop.pojo.Shop;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "shop") //指明微服务名称
@Component
public interface ShopFeign {

    /**
     * 生成支付信息并进行签名
     * @param map
     * @return
     */
    @PostMapping("/shop/payInfo2Sign")
    Result payInfo2Sign(@RequestBody Map<String,Object> map);

}
