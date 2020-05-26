package com.lzs.shop.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商家实体类
 */
@Data
@Table(name="t_shop")
public class Shop implements Serializable {
    @Id
    private String id;                  //商家id

    private String shopName;            //商家名
    private String phone;               //手机号
    private String password;            //密码
    private BigDecimal balance;         //余额
}
