package com.lzs.admin.service;

import com.lzs.admin.pojo.Admin;
import com.lzs.admin.pojo.Record;

import java.util.List;
import java.util.Map;

public interface AdminService {
    /**
     * 管理员登录
     * @param admin
     * @return
     */
    Admin login(Admin admin);

    /**
     * 用户扣钱商家加钱并将付款记录存入数据库
     * @param map
     */
    void changeMoney(Map<String, Object> map);

    /**
     * 查询所有付款记录
     * @return
     */
    List<Record> findAllRecord();
}
