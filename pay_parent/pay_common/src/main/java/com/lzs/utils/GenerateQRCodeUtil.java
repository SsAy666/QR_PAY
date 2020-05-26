package com.lzs.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;

/**
 *  生成二维码的工具类
 */
public class GenerateQRCodeUtil {
    public static void generate(String content,String pathname) {
        int width = 300;    //二维码宽度
        int height = 300;    //二维码高度
        String format = "png";         //二维码图片格式
        //定义二维码参数
        HashMap hints = new HashMap();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");    //定义内容字符集的编码
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);        //定义纠错等级
        hints.put(EncodeHintType.MARGIN, 2);    //边框空白

        //生成二维码
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height,hints);
            Path file = new File(pathname).toPath();
            MatrixToImageWriter.writeToPath(bitMatrix, format, file);
            //MatrixToImageWriter.writeToStream(bitMatrix, format, stream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
