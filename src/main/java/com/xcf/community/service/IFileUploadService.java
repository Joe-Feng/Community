package com.xcf.community.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

/**
 * @author Joe
 * @ClassName IFileUploadService.java
 * @Description
 * @createTime 2022年06月04日 11:44:00
 */
public interface IFileUploadService {
    //文件上传到 aliyun-oss
    String upload(MultipartFile uploadFile);

    String download(String fileName, HttpServletResponse response) throws UnsupportedEncodingException;

    String getImage(String fileName, HttpServletResponse response);

    String delete(String fileName);
}
