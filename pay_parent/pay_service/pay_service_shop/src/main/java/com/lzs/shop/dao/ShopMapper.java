package com.lzs.shop.dao;

import com.lzs.admin.pojo.Record;
import com.lzs.shop.pojo.Shop;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ShopMapper extends Mapper<Shop> {
    //根据商家名查询商家
    @Select("SELECT * FROM t_shop WHERE shop_name = #{shopName}")
    Shop findUserByName(@Param("shopName") String shopName);

    //根据商家名查询商家ID
    @Select("SELECT id FROM t_shop WHERE shop_name = #{username}")
    String findIdByName(String shopName);

    //根据商家名和密码查询商家
    @Select("SELECT * FROM t_shop WHERE shop_name = #{shopName} and password = #{password}")
    Shop findUserByNameAndPwd(String shopName, String password);

    //根据商家ID和密码查询商家
    @Select("SELECT * FROM t_shop WHERE id = #{id} and password = #{oldPwd}")
    Shop findUserByIdAndPwd(String id, String oldPwd);

    //修改密码
    @Update("UPDATE t_shop SET `password` = #{newPwd} WHERE id = #{id}")
    void updatePwd(String id, String newPwd);

    //查询商家所有交易记录
    @Select("SELECT * FROM t_record WHERE shop_id = #{id}")
    List<Record> payDetails(String id);
}
