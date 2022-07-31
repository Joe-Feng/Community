package com.xcf.community.config;

import com.xcf.community.utils.CommunityConstant;
import com.xcf.community.utils.CommunityUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Joe
 * @ClassName SecurityConfig.java
 * @Description
 * @createTime 2022年05月30日 21:30:00
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {

    /**
     * 排除对静态资源的过滤
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
       web.ignoring().antMatchers("/resources/**");
    }

    /**
     * 授权
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //授权
        http.authorizeRequests()
                .antMatchers(
                        "/user/setting",     //进入设置
                        "/user/upload",                 //上传头像
                        "/user/changePassword",         //修改密码
                        "/discuss/add",                 //添加帖子
                        "/comment/add/**",              //添加评论
                        "/letter/**",                    //私信
                        "/notice/**",                   //系统通知
                        "/like",                         //点赞
                        "/follow",                      //关注
                        "/unfollow"                     //取消关注
                )
                .hasAnyAuthority(
                        //哪些用户可以访问上面的路径
                        AUTHORITY_USER,
                        AUTHORITY_ADMIN,
                        AUTHORITY_MODERATOR
                )
                .antMatchers(
                        "/discuss/top", //帖子置顶
                        "/discuss/wonderful" //帖子加精
                ).hasAnyAuthority(
                        AUTHORITY_MODERATOR
                )
                .antMatchers(
                        "/discuss/delete", //删帖
                        "/data/**",  //网站数据
                        "/actuator/**"  //网站监控所有路径
                )
                .hasAnyAuthority(
                        AUTHORITY_ADMIN
                )
                //其他路径允许所用用户访问
                .anyRequest().permitAll()
                //禁用csrf检查
                .and().csrf().disable();


        //权限不够时的处理:有多种请求，普通请求转到页面，异步请求返回JSON
        http.exceptionHandling()
                //未登录时的处理
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                        //判断是普通请求还是异步请求
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if("XMLHttpRequest".equals(xRequestedWith)){
                            //异步请求：发送JSON提示
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJsonString(403, "你还未登录！"));
                        }else{
                            //普通请求:重定向到登录页面
                            response.sendRedirect(request.getContextPath() + "/login");
                        }
                    }
                })
                //权限不足时的处理
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                        //判断是普通请求还是异步请求
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if("XMLHttpRequest".equals(xRequestedWith)){
                            //异步请求：发送JSON提示
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJsonString(403, "你没有访问此功能的权限！"));
                        }else{
                            //普通请求:重定向到登录页面
                            response.sendRedirect(request.getContextPath() + "/denied");
                        }
                    }
                });

        //Security 底层默认会拦截/logout请求，进行退出处理
        //覆盖它默认的逻辑才会执行我们自己的退出代码
        http.logout().logoutUrl("/securitylogout");
    }
}
