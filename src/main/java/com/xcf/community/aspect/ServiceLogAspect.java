package com.xcf.community.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Joe
 * @ClassName ServiceLogAspect.java
 * @Description
 * @createTime 2022年05月19日 09:59:00
 */
@Component
@Aspect
@Slf4j
public class ServiceLogAspect {
    /**
     * 切点
     */
    @Pointcut("execution(* com.xcf.community.service.*.*(..))")
    public void pointcut(){

    }

    @Before("pointcut()")
    public void before(JoinPoint joinPoint){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes != null){
            HttpServletRequest request = attributes.getRequest();
            String ip = request.getRemoteHost();
            String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String target = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
            //用户[1.2.2.2],在[xxx],访问了[com.xcf.community.service.xxx()]
            log.info(String.format("用户[%s],在[%s],访问了[%s].", ip, now, target));
        }
    }
}
