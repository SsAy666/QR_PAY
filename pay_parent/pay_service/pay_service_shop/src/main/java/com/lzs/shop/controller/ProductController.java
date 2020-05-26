package com.lzs.shop.controller;

import com.lzs.entity.Result;
import com.lzs.entity.StatusCode;
import com.lzs.shop.pojo.Product;
import com.lzs.shop.service.ProductService;
import com.lzs.utils.IDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;

@RestController
@CrossOrigin(allowCredentials="true",allowedHeaders="*")
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Resource
    private ResourceLoader resourceLoader;

    //配置文件中文件上传路径
    @Value("${prop.upload-folder}")
    private String UPLOAD_FOLDER;

    /**
     * 上传图片
     * @param file
     * @return
     */
    @PostMapping("/addImage")
    public Result addImage(@RequestParam(name = "image_data", required = false) MultipartFile file) {
        //文件为空不能上传
        if (file == null) {
            return new Result(false, StatusCode.ERROR,"请选择要上传的图片");
        }
        //文件过大不能上传
        if (file.getSize() > 1024 * 1024 * 10) {
            return new Result(false, StatusCode.ERROR,"文件大小不能大于10M");
        }
        //获取文件后缀
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1, file.getOriginalFilename().length());
        if (!"jpg,jpeg,gif,png".toUpperCase().contains(suffix.toUpperCase())) {
            return new Result(false, StatusCode.ERROR,"请选择jpg,jpeg,gif,png格式的图片");
        }
        //设置文件存储路径
        String savePath = UPLOAD_FOLDER;
        File savePathFile = new File(savePath);
        if (!savePathFile.exists()) {
            //若不存在该目录，则创建目录
            savePathFile.mkdirs();
        }
        //通过UUID生成唯一文件名
        String filename = IDUtil.randomId() + "." + suffix;
        //数据库保存位置
        String saveDB = "resources/product_img/" + filename;
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
     * 添加商品
     * @param product
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody Product product) {
        //设置商品ID
        product.setPid(IDUtil.randomId());
        //添加商品到数据库
        try {
            productService.add(product);
            return new Result(true, StatusCode.OK,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, StatusCode.ERROR,"添加失败");
        }
    }

    /**
     * 查询所有商品
     * @return
     */
    @GetMapping("/showAllProduct")
    public Result showAllProduct() {
        try {
            List<Product> products = productService.findAll();
            return new Result(true, StatusCode.OK,"查询所有商品成功",products);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, StatusCode.ERROR,"查询所有商品失败");
        }
    }

    /**
     * 根据商品ID查询该商品详情
     * @param pid 商品ID
     * @return
     */
    @GetMapping("/findProductById")
    public Result findProductById(String pid) {
        System.out.println(pid);
        try {
            Product product = productService.findById(pid);
            return new Result(true, StatusCode.OK,"查询商品成功",product);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, StatusCode.ERROR,"查询商品失败");
        }
    }

    /**
     * 生产环境下保存支付二维码
     * @param qrPic
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("/saveQR/{qrPic}")
    public void saveQR(@PathVariable("qrPic") String qrPic, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //获取二维码图片的绝对路径
        String filePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/qrCode_img/";
        String fileName = qrPic;
        //获取输入流对象（用于读文件）
        FileInputStream fis = new FileInputStream(new File(filePath, fileName));
        //获取文件后缀（.jpg）
        String extendFileName = fileName.substring(fileName.lastIndexOf('.'));
        //动态设置响应类型，根据前台传递文件类型设置响应类型
        response.setContentType(request.getSession().getServletContext().getMimeType(extendFileName));
        //设置响应头,attachment表示以附件的形式下载，inline表示在线打开
        response.setHeader("content-disposition", "attachment;fileName=" + URLEncoder.encode(fileName, "UTF-8"));
        //获取输出流对象（用于写文件）
        ServletOutputStream os = response.getOutputStream();
        //下载文件,使用spring框架中的FileCopyUtils工具
        FileCopyUtils.copy(fis, os);
    }
}
