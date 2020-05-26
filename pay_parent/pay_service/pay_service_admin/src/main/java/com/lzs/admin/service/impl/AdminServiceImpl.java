package com.lzs.admin.service.impl;

import com.lzs.admin.dao.AdminMapper;
import com.lzs.admin.pojo.Record;
import com.lzs.admin.service.AdminService;
import com.lzs.admin.pojo.Admin;
import com.lzs.utils.IDUtil;
import com.lzs.utils.MD5Util;
import com.lzs.utils.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminMapper adminMapper;

    /**
     * 管理员登录
     * @param admin
     * @return
     */
    @Override
    public Admin login(Admin admin) {
        //获取用户名
        String username = admin.getUsername();
        //获取用户密码
        String password = admin.getPassword();
        String newPassword = MD5Util.encodeWithSalt(password,username);
        return adminMapper.findUserByNameAndPwd(username,newPassword);
    }

    /**
     * 用户扣钱商家加钱并将付款记录存入数据库
     * @param map
     */
    @Override
    @Transactional
    public void changeMoney(Map<String, Object> map) {
        Map<String,Object> payInfoMap = (Map<String, Object>) map.get("payInfo");
        //获取商品总金额
        BigDecimal sumAmount = null;
        String sumAmountStr = payInfoMap.get("sumAmount").toString();
        //判断总金额是整数还是小数
        boolean flag = StrUtil.isInteger(sumAmountStr);
        //总金额是整数
        if (flag) {
            Integer sumAmountInt = Integer.valueOf(sumAmountStr);
            sumAmount = new BigDecimal(sumAmountInt);
        } else { //总金额是小数
            double sumAmountDouble = Double.parseDouble(sumAmountStr);
            //保留两位小数
            DecimalFormat df1 = new DecimalFormat("0.00");
            String sumAmountStrs = df1.format(sumAmountDouble);
            sumAmount = new BigDecimal(sumAmountStrs);
        }
        //获取商家ID
        String shopID = (String) payInfoMap.get("sid");
        //商家加钱
        adminMapper.addMoney(shopID,sumAmount);
        /*//假设出现异常
        System.out.println(1/0);*/
        //获取用户ID
        String userID = (String) payInfoMap.get("userID");
        //用户扣钱
        adminMapper.reduceMoney(userID,sumAmount);
        //生成随机的付款ID
        String payID = IDUtil.randomId();
        //将付款记录存入数据库
        adminMapper.savePayDB(payID,shopID,userID,sumAmount);
    }

    /**
     * 查询所有付款记录
     * @return
     */
    @Override
    public List<Record> findAllRecord() {
        List<Record> records = adminMapper.findAllRecord();
        return records;
    }
}
