package com.xcf.community;

import java.io.IOException;

/**
 * @author Joe
 * @ClassName WkTest.java
 * @Description
 * @createTime 2022年06月03日 10:14:00
 */
public class WkTest {
    public static void main(String[] args) {
        String cmd = "D:/wkhtmltopdf/bin/wkhtmltoimage --quality 75 www.baidu.com D:/code/niuke/wk-img/1.png";
        try {
            // Runtime.getRuntime().exec(cmd);将命令交给操作系统去执行
            //main程序 与 执行生成图片的操作是并发的
            Runtime.getRuntime().exec(cmd);
            System.out.println("ok");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
