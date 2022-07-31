package com.xcf.community.controller.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Joe
 * @version 1.0.0
 * @ClassName interceptorDemo.java
 * @Description
 * @createTime 2022年05月14日 10:08:00
 */
@Component
@Slf4j
public class InterceptorDemo implements HandlerInterceptor {

    //run before controller
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.debug("preHandle: " + handler.toString());
        return true;
    }

    //run after controller
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.debug("postHandle: " + handler.toString());
    }

    //run after TemplateEngine
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.debug("afterCompletion:  " + handler.toString());
    }
}
