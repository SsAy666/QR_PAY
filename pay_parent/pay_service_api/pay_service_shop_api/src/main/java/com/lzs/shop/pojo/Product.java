package com.lzs.shop.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品实体类
 */
@Data
@Table(name="t_product")
public class Product implements Serializable {
    @Id
    private String pid;                 //商品id

    private String pname;                //商品名
    private String pdesc;               //商品描述
    private BigDecimal marketPrice;     //商品原价
    private BigDecimal shopPrice;       //商品现价
    private Integer pnum;               //商品数量
    private String pimage;              //商品数量
    private String sid;                 //商家ID
}
