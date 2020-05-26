package com.lzs.user.controller;

import com.alibaba.fastjson.JSON;
import com.lzs.admin.pojo.Record;
import com.lzs.entity.Result;
import com.lzs.entity.StatusCode;
import com.lzs.user.pojo.User;
import com.lzs.user.service.UserService;
import com.lzs.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(allowCredentials="true",allowedHeaders="*")
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    //配置文件中文件上传路径
    @Value("${prop.upload-folder}")
    private String UPLOAD_FOLDER;

    /**
     * 注册用户
     * @param user
     * @return Result
     */
    @PostMapping("/regist")
    public Result regist(@RequestBody User user){
        Map<String, String> map = new HashMap<>();
        User existUser = userService.findUserByName(user.getUsername());
        if (existUser != null) {
            //如果用户名已存在
            return new Result(false, StatusCode.ERROR,"用户名已存在");
        } else {
            //设置用户ID
            user.setId(IDUtil.randomId());
            //对输入的密码MD5加密(并用用户ID进行撒盐)存储到数据库中
            String oldPassword = user.getPassword();
            String newPassword = MD5Util.encodeWithSalt(oldPassword,user.getId());
            user.setPassword(newPassword);
            //对输入的支付密码MD5加密(并用用户ID进行撒盐)存储到数据库中
            String oldPayPwd = user.getPayPwd();
            String newPayPwd = MD5Util.encodeWithSalt(oldPayPwd,user.getId());
            user.setPayPwd(newPayPwd);
            //设置刚注册的用户的余额为0
            user.setBalance(new BigDecimal(0));
            try {
                //用户向移动支付服务器申请私钥以便进行数字签名
                map.put("id",user.getId());
                map.put("payPwd",oldPayPwd);
                userService.getPrivateKey(map);
                //申请私钥成功后添加该用户到数据库
                userService.addUser(user);
                return new Result(true, StatusCode.OK,"注册成功");
            } catch (Exception e) {
                e.printStackTrace();
                return new Result(false, StatusCode.ERROR,"注册失败");
            }
        }
    }

    /**
     * 用户登录
     * @param user
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody User user, HttpServletRequest request) {
        User loginUser = userService.login(user);
        if (loginUser != null){
            request.getSession().setAttribute("user",loginUser);
            return new Result(true, StatusCode.OK,"登录成功");
        } else {
            return new Result(false, StatusCode.ERROR,"登录失败");
        }
    }

    /**
     * 用户名回显
     * @param request
     * @return
     */
    @GetMapping("/getName")
    public Result getName(HttpServletRequest request) {
        //获取自己的session对象
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            //没找到，返回错误
            return new Result(false, StatusCode.ERROR,"当前用户暂未登录");
        }else {
            //找到，返回成功信息
            return new Result(true, StatusCode.OK,"登录成功",user);
        }
    }

    /**
     * 退出登录
     * @param request
     * @return
     */
    @GetMapping("/logout")
    public Result logout (HttpServletRequest request) {
        request.getSession().invalidate();
        return new Result(true, StatusCode.OK,"退出登录成功");
    }

    /**
     * 修改登录密码
     * @param map
     */
    @PostMapping("/updatePwd")
    public Result updatePwd(@RequestBody Map<String,String> map){
        //获取用户ID
        String id = map.get("userId");
        //获取旧密码
        String oldPwd = map.get("oldPwd");
        //获取新密码
        String newPwd = map.get("newPwd");
        try {
            //修改密码
            userService.updatePwd(id, oldPwd,newPwd);
            return new Result(true, StatusCode.OK,"修改密码成功");
        } catch (Exception e) {
            return new Result(false,StatusCode.ERROR,e.getMessage());
        }
    }

    /**
     * 修改支付密码
     * @param map
     */
    @PostMapping("/updatePayPwd")
    public Result updatePayPwd(@RequestBody Map<String,String> map){
        //获取用户ID
        String id = map.get("userId");
        //获取旧密码
        String oldPwd = map.get("oldPwd");
        //获取新密码
        String newPwd = map.get("newPwd");
        try {
            //修改密码
            userService.updatePayPwd(id, oldPwd,newPwd);
            return new Result(true, StatusCode.OK,"修改密码成功");
        } catch (Exception e) {
            return new Result(false,StatusCode.ERROR,e.getMessage());
        }
    }

    /**
     * 上传QR码
     * @param file
     * @return
     */
    @PostMapping("/addQR")
    public Result addQR(@RequestParam(name = "image_data", required = false) MultipartFile file) {
        //文件为空不能上传
        if (file == null) {
            return new Result(false, StatusCode.ERROR,"请选择要上传的图片");
        }
        //文件过大不能上传
        if (file.getSize() > 1024 * 1024 * 10) {
            return new Result(false, StatusCode.ERROR,"文件大小不能大于10M");
        }
        //设置文件存储路径
        String savePath = UPLOAD_FOLDER;
        File savePathFile = new File(savePath);
        if (!savePathFile.exists()) {
            //若不存在该目录，则创建目录
            savePathFile.mkdirs();
        }
        //获取文件名
        String filename = file.getOriginalFilename();
        //前端保存位置
        String saveDB = "resources/qrInfo_img/" + filename;
        //将图片保存指定目录
        String savePic = savePath + saveDB;
        try {
            file.transferTo(new File(savePic));
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, StatusCode.ERROR,"图片上传失败");
        }
        //返回文件名称
        return new Result(true, StatusCode.OK,"图片上传成功",saveDB);
    }

    /**
     * 用户验证商家身份并展示支付信息
     * @param map
     * @return
     */
    @PostMapping("/showInfo")
    public Result showInfo(@RequestBody Map<String,String> map) {
        //获取二维码路径
        String qrPic = map.get("qrPic");
        String qrPath = UPLOAD_FOLDER + qrPic;
        //读取二维码中的内容
        String content = userService.readQR(qrPath);
        //商家ID
        String sid = content.substring(0,32);
        //根据商家ID获取商家的公钥
        String shopKey = userService.getShopKey(sid);
        //已签名的支付信息（含时间戳）
        String signPayInfo = content.substring(32, 128);
        //未签名的支付信息（含时间戳）
        String payInfo = content.substring(128, content.length());
        //对商家进行验证
        try {
            boolean isShop = userService.verifyShop(shopKey,signPayInfo,payInfo);
            if (isShop) {
                //将支付信息（含时间戳）从字符串转成Map
                Map mapPayInfo = JSON.parseObject(payInfo);
                return new Result(true, StatusCode.OK,"对商家身份认证成功，继续支付！",mapPayInfo);
            } else {
                return new Result(false, StatusCode.ERROR,"对商家身份认证失败，无法继续支付！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, StatusCode.ERROR,"对商家身份认证失败，无法继续支付！");
        }
    }

    /**
     * 用户确认支付并进行签名
     * @param map 支付信息
     * @return
     */
    @PostMapping("/payInfo2Sign")
    public Result payInfo2Sign(@RequestBody Map<String,Object> map) {
        try {
            String sign = userService.payInfo2Sign(map);
            return new Result(true, StatusCode.OK,"用户签名成功",sign);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,StatusCode.ERROR,"用户签名失败");
        }
    }

    /**
     * 查询用户当前余额
     * @param id 用户ID
     * @return
     */
    @GetMapping("/getAmount")
    public Result getAmount(String id) {
        try {
            BigDecimal balance = userService.getAmount(id);
            return new Result(true, StatusCode.OK,"查询用户余额成功",balance);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,StatusCode.ERROR,"查询用户余额失败");
        }
    }

    /**
     * 用户充值
     * @param map 充值金额和用户ID
     * @return
     */
    @PostMapping("/recharge")
    public Result recharge(@RequestBody Map<String,Object> map) {
        //获取充值的金额
        String amountStr = (String) map.get("amount");
        BigDecimal amount = new BigDecimal(amountStr);
        //获取用户ID
        String userId = (String) map.get("userId");
        try {
            userService.addMoney(amount,userId);
            return new Result(true, StatusCode.OK,"充值成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,StatusCode.ERROR,"充值失败");
        }
    }

    /**
     * 查询用户消费明细
     * @param id 用户ID
     * @return
     */
    @GetMapping("/payDetails")
    public Result payDetails(String id) {
        try {
            List<Record> records = userService.payDetails(id);
            return new Result(true, StatusCode.OK,"查询消费明细成功",records);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,StatusCode.ERROR,"查询消费明细失败");
        }
    }
}
