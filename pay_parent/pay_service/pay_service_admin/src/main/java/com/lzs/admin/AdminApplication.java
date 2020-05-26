package com.lzs.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableEurekaClient
@EnableTransactionManagement
@MapperScan(basePackages = {"com.lzs.admin.dao"})
@EnableFeignClients(basePackages = {"com.lzs.shop.feign","com.lzs.user.feign"})
public class AdminApplication {
    public static void main(String[] args) {
        SpringApplication.run( AdminApplication.class);
    }
}
