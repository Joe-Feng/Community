package com.xcf.community.controller;

import com.xcf.community.service.IDiscussPostService;
import com.xcf.community.service.IUserService;
import com.xcf.community.utils.CommunityConstant;
import com.xcf.community.utils.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Joe
 * @version 1.0.0
 * @ClassName DemoController.java
 * @Description
 * @createTime 2022年05月13日 15:47:00
 */
@Controller
@RequestMapping("/demo")
public class DemoController {

    @GetMapping(path = "/cookie/set")
    @ResponseBody
    public  String setCookie(HttpServletResponse response){
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        cookie.setPath("/community/alpha");
        cookie.setMaxAge(60 * 10); //单位：秒 60s * 10
        response.addCookie(cookie);

        return "set cookie";
    }

    @GetMapping(path = "/cookie/get")
    @ResponseBody
    public  String setCookie(@CookieValue("code") String code){
        System.out.println(code);
        return "get cookie";
    }

    @GetMapping(path = "/session/set")
    @ResponseBody
    public String setSession(HttpSession session){
        session.setAttribute("id", 1);
        session.setAttribute("name", "xcf");
        return "set Session";

    }

    @GetMapping(path = "/session/get")
    @ResponseBody
    public String getSession(HttpSession session){
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get Session";

    }

    @PostMapping("/ajax")
    @ResponseBody
    public String testAjax(String name, int age){
        System.out.println(name);
        System.out.println(age);
        return CommunityUtil.getJsonString(0, "操作成功");
    }
}
