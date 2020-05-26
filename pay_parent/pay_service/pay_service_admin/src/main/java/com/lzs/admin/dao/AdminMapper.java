package com.lzs.admin.dao;

import com.lzs.admin.pojo.Admin;
import com.lzs.admin.pojo.Record;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.List;

public interface AdminMapper extends Mapper<Admin> {

    //根据用户名和密码查询该用户是否存在
    @Select("SELECT * FROM t_admin WHERE username = #{username} and password = #{password}")
    Admin findUserByNameAndPwd(String username, String password);

    //商家加钱
    @Update("UPDATE t_shop SET balance = balance + #{sumAmount} WHERE id=#{shopID}")
    void addMoney(String shopID, BigDecimal sumAmount);

    //用户减钱
    @Update("UPDATE t_user SET balance = balance - #{sumAmount} WHERE id=#{userID}")
    void reduceMoney(String userID, BigDecimal sumAmount);

    //将付款记录存入数据库
    @Insert("INSERT INTO t_record(id,shop_id,user_id,amount) VALUES(#{payID},#{shopID},#{userID},#{sumAmount})")
    void savePayDB(String payID, String shopID, String userID, BigDecimal sumAmount);

    //查询所有付款记录
    @Select("SELECT * FROM t_record")
    List<Record> findAllRecord();
}
