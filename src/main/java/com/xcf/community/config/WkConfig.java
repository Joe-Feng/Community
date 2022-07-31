package com.xcf.community.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * @author Joe
 * @ClassName WkConfig.java
 * @Description
 * @createTime 2022年06月03日 10:36:00
 */
@Configuration
@Slf4j
public class WkConfig {

    @Value("${wk.image.storage}")
    public String wkImageStorage;

    @PostConstruct
    public void init(){
        //创建wk图片目录
        File file = new File(wkImageStorage);
        if(!file.exists()){
            file.mkdir();
            log.info("创建wk图片目录: " + wkImageStorage);
        }
    }
}
