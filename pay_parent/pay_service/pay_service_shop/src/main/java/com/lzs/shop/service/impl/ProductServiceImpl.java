package com.lzs.shop.service.impl;

import com.lzs.shop.dao.ProductMapper;
import com.lzs.shop.pojo.Product;
import com.lzs.shop.service.ProductService;
import com.lzs.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductMapper productMapper;

    //配置文件中文件上传路径
    @Value("${prop.upload-folder}")
    private String UPLOAD_FOLDER;

    /**
     * 添加商品到数据库
     * @param product
     */
    @Override
    public void add(Product product) {
        productMapper.insertSelective(product);
    }

    /**
     * 查询所有商品
     * @return
     */
    @Override
    public List<Product> findAll() {
        List<Product> products = productMapper.selectAll();
        return products;
    }

    /**
     * 根据商品ID查询该商品详情
     * @param pid 商品ID
     * @return
     */
    @Override
    public Product findById(String pid) {
        Product product = productMapper.selectByPrimaryKey(pid);
        return product;
    }
}
