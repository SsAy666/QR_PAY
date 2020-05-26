package com.lzs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableEurekaClient
@MapperScan(basePackages = {"com.lzs.user.dao"})
@EnableFeignClients(basePackages = {"com.lzs.admin.feign"})
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run( UserApplication.class);
    }
}
