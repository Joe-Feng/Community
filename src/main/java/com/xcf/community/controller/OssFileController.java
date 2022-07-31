package com.xcf.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.xcf.community.service.impl.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Joe
 * @ClassName OssFileController.java
 * @Description
 * @createTime 2022年06月04日 14:15:00
 */
@RequestMapping("api/pri/file")
@RestController
public class OssFileController {

    @Autowired
    private FileUploadService fileUploadService;

    /*
     * 文件上传api
     * @param: file
     * @return: com.alibaba.fastjson.JSONObject
     */
    @PostMapping("upload")
    public JSONObject upload(@RequestParam("file") MultipartFile file) {
        JSONObject jsonObject = new JSONObject();
        if (file != null) {
            String returnFileUrl = fileUploadService.upload(file);
            if (returnFileUrl.equals("error")) {
                jsonObject.put("error", "文件上传失败！");
                return jsonObject;
            }
            jsonObject.put("success", "文件上传成功！");
            jsonObject.put("returnFileUrl", returnFileUrl);
            return jsonObject;
        } else {
            jsonObject.put("error", "文件上传失败！");
            return jsonObject;
        }
    }

    /*
     * 文件下载api
     * @param: fileName
     * @param: response
     * @return: com.alibaba.fastjson.JSONObject
     * @create: 2020/10/31 17:35
     * @author: csp1999
     */
    @GetMapping(value = "download/{fileName}")
    public JSONObject download(@PathVariable("fileName") String fileName, HttpServletResponse response) throws Exception {
        JSONObject jsonObject = new JSONObject();

        String status = fileUploadService.download(fileName, response);
        if (status.equals("error")) {
            jsonObject.put("error", "文件下载失败！");
            return jsonObject;
        } else {
            jsonObject.put("success", "文件下载成功！");
            return jsonObject;
        }
    }

    /*
     * 文件删除api
     * @param: fileName
     * @return: com.alibaba.fastjson.JSONObject
     * @create: 2020/10/31 17:35
     * @author: csp1999
     */
    @GetMapping("/delete/{fileName}")
    public JSONObject DeleteFile(@PathVariable("fileName") String fileName) {
        JSONObject jsonObject = new JSONObject();

        String status = fileUploadService.delete(fileName);
        if (status.equals("error")) {
            jsonObject.put("error", "文件删除失败！");
            return jsonObject;
        } else {
            jsonObject.put("success", "文件删除成功！");
            return jsonObject;
        }
    }
}
