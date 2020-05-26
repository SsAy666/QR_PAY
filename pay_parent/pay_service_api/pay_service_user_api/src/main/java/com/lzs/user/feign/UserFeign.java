package com.lzs.user.feign;

import com.lzs.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "user") //指明微服务名称
@Component
public interface UserFeign {
    /**
     * 用户确认支付并进行签名
     * @param map 支付信息
     * @return
     */
    @PostMapping("/user/payInfo2Sign")
    Result payInfo2Sign(@RequestBody Map<String,Object> map);
}
