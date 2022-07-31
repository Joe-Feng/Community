package com.xcf.community.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * @author Joe
 * @ClassName AspectDemo.java
 * @Description
 * @createTime 2022年05月19日 10:42:00
 */
//@Component
//@Aspect
@Slf4j
public class AspectDemo {
    /**
     * 切点
     */
    @Pointcut("execution(* com.xcf.community.service.*.*(..))")
    public void pointcut(){

    }

    @Before("pointcut()")
    public void before(){
        log.info("before");
    }

    @After("pointcut()")
    public void after(){
        log.info("after");
    }

    @AfterReturning("pointcut()")
    public void afterReturning(){
        log.info("afterReturning");
    }

    @AfterThrowing("pointcut()")
    public void afterThrowing(){
        log.info("afterThrowing");
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("around before");
        Object obj = joinPoint.proceed();
        log.info("around after");
        return obj;
    }
}
