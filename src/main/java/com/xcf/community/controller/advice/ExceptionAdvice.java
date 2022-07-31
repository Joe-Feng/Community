package com.xcf.community.controller.advice;


import com.xcf.community.utils.CommunityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Joe
 * @ClassName ExceptionAdvice.java
 * @Description
 * @createTime 2022年05月18日 22:50:00
 */
@ControllerAdvice(annotations = Controller.class)
@Slf4j
public class ExceptionAdvice {
    @ExceptionHandler(Exception.class)
    public void handlerException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.error("服务器发生异常：" + e.getMessage());
        for(StackTraceElement element : e.getStackTrace()){
            log.error(element.toString());
        }

        //判断是普通请求还是异步请求
        String xRequestedWith = request.getHeader("x-requested-with");
        if("XMLHttpRequest".equals(xRequestedWith)){
            //异步请求
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJsonString(1, "服务器异常！"));
        }else {
            //跳转页面请求
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}
