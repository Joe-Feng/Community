package com.xcf.community.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Joe
 * @version 1.0.0
 * @ClassName CookieUtil.java
 * @Description 从Cookie中获取值
 * @createTime 2022年05月14日 10:53:00
 */
public class CookieUtil {
    /**
     * 从Cookie中获取值
     * @param request 请求
     * @param name key
     * @return  查到的cookie值
     */
    public static String getValue(HttpServletRequest request, String name){
        if(request == null || name == null){
            throw new IllegalArgumentException("参数为空!");
        }
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for (Cookie cookie: cookies) {
                if(cookie.getName().equals(name)){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
