package com.lzs.user.dao;

import com.lzs.admin.pojo.Record;
import com.lzs.user.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.List;

public interface UserMapper extends Mapper<User> {

    //根据用户名查询用户
    @Select("SELECT * FROM t_user WHERE username = #{username}")
    User findUserByName(@Param("username") String username);

    //根据用户名查询用户ID
    @Select("SELECT id FROM t_user WHERE username = #{username}")
    String findIdByName(String username);

    //根据用户名和密码查询用户
    @Select("SELECT * FROM t_user WHERE username = #{username} and password = #{password}")
    User findUserByNameAndPwd(String username, String password);

    //根据用户ID和密码查询用户
    @Select("SELECT * FROM t_user WHERE id = #{id} and password = #{oldPwd}")
    User findUserByIdAndPwd(String id, String oldPwd);

    //根据用户ID和支付密码查询用户
    @Select("SELECT * FROM t_user WHERE id = #{id} and pay_pwd = #{oldPwd}")
    User findUserByIdAndPayPwd(String id, String oldPwd);

    //根据用户ID修改用户密码
    @Update("UPDATE t_user SET `password` = #{newPwd} WHERE id = #{id} ")
    void updatePwd(String id, String newPwd);

    //根据用户ID修改用户支付密码
    @Update("UPDATE t_user SET `pay_pwd` = #{newPwd} WHERE id = #{id} ")
    void updatePayPwd(String id, String newPwd);

    //用户充值
    @Update("UPDATE t_user SET balance = balance + #{amount} WHERE id = #{userId}")
    void addMoney(BigDecimal amount, String userId);

    //查询用户所有交易记录
    @Select("SELECT * FROM t_record WHERE user_id = #{id}")
    List<Record> payDetails(String id);
}
