package com.lzs.user.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;;

/**
 * user实体类
 */
@Data
@Table(name="t_user")
public class User implements Serializable {

    @Id
    private String id;                  //用户id

    private String username;            //用户名
    private String phone;               //手机号
    private String email;               //邮箱
    private String password;            //登录密码
    private String payPwd;              //支付密码
    private BigDecimal balance;         //余额
    private String avatar;              //头像
    private String sex;                 //性别
    private String sign;                //个性签名

}
