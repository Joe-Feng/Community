package com.xcf.community.config;

import com.xcf.community.controller.interceptor.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Joe
 * @version 1.0.0
 * @ClassName WebMvcConfig.java
 * @Description
 * @createTime 2022年05月14日 10:15:00
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private InterceptorDemo interceptorDemo;

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    private DataInterceptor dataInterceptor;

    //@Autowired
    //private LoginRequiredInterceptor loginRequiredInterceptor;

    @Autowired
    private MessageInterceptor messageInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptorDemo)
        .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg") //不拦截  /**表示 /static目录下的所有文件
        .addPathPatterns("/register", "/login"); //拦截的路径

        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg"); //不拦截  /**表示 /static目录下的所有文件

        //registry.addInterceptor(loginRequiredInterceptor)
        //        .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

        registry.addInterceptor(messageInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

        registry.addInterceptor(dataInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");
    }
}
