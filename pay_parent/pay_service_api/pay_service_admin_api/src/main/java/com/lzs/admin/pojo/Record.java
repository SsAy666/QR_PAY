package com.lzs.admin.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 付款记录实体类
 */
@Data
@Table(name="t_record")
public class Record {
    @Id
    private String id;                  //付款ID

    private String shopId;             //商家ID
    private String userId;             //用户ID
    private BigDecimal amount;         //交易金额
    private Date createTime;           //交易时间
}
