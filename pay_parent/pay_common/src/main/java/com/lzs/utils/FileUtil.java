package com.lzs.utils;

import org.springframework.util.ResourceUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 文件操作的工具类
 */
public class FileUtil {

    /**
     * 将字符串写入文件中
     * @param path 文件所在路径
     * @param s    要写入文件的字符串
     */
    public static void writeFile(String path,String s) {
        FileWriter writer;
        try {
            writer = new FileWriter(path);
            writer.write(s);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件中读取字符串
     * @param path 文件所在路径
     */
    public static String readFile(String path) {
        //定义一个file对象，用来初始化FileReader
        File file = new File(path);
        StringBuilder sb = null;
        try {
            //定义一个fileReader对象，用来初始化BufferedReader
            FileReader reader = new FileReader(file);
            //new一个BufferedReader对象，将文件内容读取到缓存
            BufferedReader bReader = new BufferedReader(reader);
            //定义一个字符串缓存，将字符串存放缓存中
            sb = new StringBuilder();
            String s = "";
            //逐行读取文件内容，不读取换行符和末尾的空格
            while ((s =bReader.readLine()) != null) {
                //将读取的字符串添加换行符后累加存放在缓存中
                sb.append(s);
            }
            bReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String str = sb.toString();
        return str;
    }

    /**
     * 获取项目根路径
     * @return
     */
    public static String getResourceBasePath() {
        // 获取根目录
        File path = null;
        try {
            path = new File(ResourceUtils.getURL("classpath:").getPath());
        } catch (FileNotFoundException e) {
            // nothing to do
        }
        if (path == null || !path.exists()) {
            path = new File("");
        }
        String pathStr = path.getAbsolutePath();
        // 如果是在IDEA中运行，则和target同级目录,如果是jar部署到服务器，则默认和jar包同级
        pathStr = pathStr.replace("\\target\\classes", "");
        return pathStr;
    }
}
