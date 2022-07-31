package com.xcf.community.controller.interceptor;


import com.xcf.community.pojo.LoginTicket;
import com.xcf.community.pojo.User;
import com.xcf.community.service.IUserService;
import com.xcf.community.utils.CookieUtil;
import com.xcf.community.utils.HostHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @author Joe
 * @version 1.0.0
 * @ClassName LoginTicketInterceptor.java
 * @Description 用于登录认证
 * @createTime 2022年05月14日 10:52:00
 */
@Component
@Slf4j
public class LoginTicketInterceptor implements HandlerInterceptor {
    @Autowired
    private IUserService userService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 在controller之前通过cookie中的 ticket 查找登录用户凭证
     * 利用凭证找到登录用户信息，让本地线程持有用户信息
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = CookieUtil.getValue(request, "ticket");


        if(ticket != null){
            //查询凭证
            LoginTicket loginTicket = userService.findLoginStickByStick(ticket);
            //检查凭证是否有效
            if(loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())){
                //根据凭证查找用户
                User user = userService.findUserById(loginTicket.getUserId());
                //在本次请求中持有用户：请求是多线程处理，需要每个线程保存自己处理的用户
                //需要使用 ThreadLocal
                hostHolder.setUsers(user);
                //HostHolder 保存的是用户信息， SecurityContext 保存的是认证结果
                //构建用户认证结果，并存入 SecurityContext， 以便 Security进行授权
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        user, user.getPassword(), userService.getAuthorities(user.getId()));
                SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
            }
        }

        return true;
    }

    /**
     * 在controller之后，获取用户信息，并将其信息填入modelAndView
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user != null && modelAndView != null){
            modelAndView.addObject("loginUser", user);
        }
    }

    /**
     * 在TemplateEngine后，将加入到本地线程中的信息清除
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();


        //不能清除security的认证结果
        //清除存入的认证结果
        //SecurityContextHolder.clearContext();
    }
}
