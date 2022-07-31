package com.xcf.community.controller;

import com.xcf.community.event.EventProducer;
import com.xcf.community.pojo.Event;
import com.xcf.community.service.IElasticSearchService;
import com.xcf.community.utils.CommunityConstant;
import com.xcf.community.utils.CommunityUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Joe
 * @ClassName ShareController.java
 * @Description
 * @createTime 2022年06月03日 11:56:00
 */
@Controller
@Slf4j
public class ShareController implements CommunityConstant {

    @Autowired
    private EventProducer eventProducer;
    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${wk.image.storage}")
    public String wkImageStorage;

    /**
     * 异步生成长图
     * @param htmlUrl
     * @return
     */
    @GetMapping("/share")
    @ResponseBody
    public String share(String htmlUrl){
        //文件名
        String filename = CommunityUtil.generateUUID();

        //异步生成长图
        Event event = new Event()
                .setTopic(TOPIC_SHARE)
                .setData("htmlUrl", htmlUrl)
                .setData("filename", filename)
                .setData("suffix", ".png");
        eventProducer.fireMessage(event);

        //返回访问路径
        Map<String, Object> map = new HashMap<>();
        map.put("shareUrl", domain + contextPath + "/share/image/" + filename);

        return CommunityUtil.getJsonString(0, null, map);
    }

    /**
     * 获取长图到页面
     * @param filename
     * @param response
     */
    @GetMapping("/share/image/{filename}")
    public void getShareImage(@PathVariable("filename") String filename, HttpServletResponse response){
        if(StringUtils.isBlank(filename)){
            throw new IllegalArgumentException("文件名不能为空！");
        }

        response.setContentType("image/png");
        File file = new File(wkImageStorage + "/" + filename + ".png");
        try {
            OutputStream os = response.getOutputStream();
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1){
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            log.error("获取长图失败：" + e.getMessage());
        }
    }
}
