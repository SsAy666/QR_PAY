package com.lzs.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableEurekaClient
@EnableTransactionManagement
@MapperScan(basePackages = {"com.lzs.shop.dao"})
@EnableFeignClients(basePackages = {"com.lzs.admin.feign"})
public class ShopApplication {
    public static void main(String[] args) {
        SpringApplication.run( ShopApplication.class);
    }
}
