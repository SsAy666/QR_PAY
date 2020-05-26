package com.lzs.admin.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 公钥实体类
 */
@Data
@Table(name="t_public_key")
public class PublicKey {
    @Id
    private String id;            //用户id

    private String publicKey;     //公钥
}
