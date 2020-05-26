package com.lzs.shop.service;

import com.lzs.shop.pojo.Product;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface ProductService {
    /**
     * 添加商品
     * @param product
     */
    void add(Product product);

    /**
     * 查询所有商品
     * @return
     */
    List<Product> findAll();

    /**
     * 根据商品ID查询该商品详情
     * @param pid 商品ID
     * @return
     */
    Product findById(String pid);
}
