package com.xcf.community.controller;

import com.google.code.kaptcha.Producer;
import com.xcf.community.pojo.User;
import com.xcf.community.service.IUserService;
import com.xcf.community.utils.CommunityConstant;
import com.xcf.community.utils.CommunityUtil;
import com.xcf.community.utils.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Joe
 * @version 1.0.0
 * @ClassName LoginController.java
 * @Description
 * @createTime 2022年05月12日 16:30:00
 */
@Controller
@Slf4j
public class LoginController implements CommunityConstant {
    @Autowired
    private IUserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegister(){
        return "/site/register";
    }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLogin(){
        return "/site/login";
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user){
        Map<String, Object> map = userService.register(user);
        if(map == null || map.isEmpty()){
            model.addAttribute("msg", "注册成功，已经向您的邮箱发送了一份激活邮件，请尽快激活!");
            model.addAttribute("target", "/community/index");
            return "/site/operate-result";
        }else{
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }
    }

    @RequestMapping(path = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code){
        int result = userService.activation(userId, code);
        if(result == ACTIVATION_SUCCESS){
            model.addAttribute("msg", "激活成功，您的账号可以正常使用!");
            model.addAttribute("target", "/community/login");
        }else if(result == ACTIVATION_REPEAT){
            model.addAttribute("msg", "无效操作，您的账号已经激活过了!");
            model.addAttribute("target", "/community/index");
        }else{
            model.addAttribute("msg", "激活失败，您提供的激活码不正确!");
            model.addAttribute("target", "/community/index");
        }
        return "/site/operate-result";
    }

    @GetMapping(path = "/kaptcha")
    public void getKaptcha(HttpServletResponse response/*, HttpSession session*/){
        //生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        //将验证码存入Session
//      session.setAttribute("kaptcha", text);

        //验证码的归属
        String kaptchaOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);
        //将验证码存入Redis
        String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey, text, 60, TimeUnit.SECONDS);


        //将图片传输给浏览器
        response.setContentType("image/png");
        try {
            OutputStream outputStream = response.getOutputStream();
            ImageIO.write(image, "png", outputStream);
        } catch (IOException e) {
            log.error("响应验证码失败：" + e.getMessage());
        }
    }

    @PostMapping("/login")
    public String login(String username, String password, String code, boolean rememberme,
                        Model model, /*HttpSession session,*/ HttpServletResponse response,
                        @CookieValue("kaptchaOwner") String kaptchaOwner){
        //检查验证码
//        String kaptcha = (String) session.getAttribute("kaptcha");
        String kaptcha = null;
        if(StringUtils.isNotBlank(kaptchaOwner)){
            String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        }

        if(StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg", "验证码不正确!");
            return "/site/login";
        }
        //检查账号、密码
        int expiredSeconds = rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if(map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        }else{
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }

    @GetMapping(path = "/logout")
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        //退出时清除 security 认证结果
        SecurityContextHolder.clearContext();
        return "redirect:/login"; //默认Get请求
    }
}
