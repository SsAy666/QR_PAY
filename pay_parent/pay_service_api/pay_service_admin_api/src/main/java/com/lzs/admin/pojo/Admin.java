package com.lzs.admin.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 管理员实体类
 */
@Data
@Table(name="t_admin")
public class Admin implements Serializable {
    @Id
    private String username;            //用户名
    private String password;            //密码
}
