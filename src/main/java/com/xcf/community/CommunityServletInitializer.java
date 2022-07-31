package com.xcf.community;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @author Joe
 * @ClassName CommunityServletInitializer.java
 * @Description Tomcat 通过该类作为入口启动项目
 * @createTime 2022年06月07日 17:19:00
 */
public class CommunityServletInitializer extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(CommunityApplication.class);
    }
}
