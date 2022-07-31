package com.xcf.community.controller.interceptor;

import com.xcf.community.pojo.User;
import com.xcf.community.service.IDataService;
import com.xcf.community.utils.HostHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Joe
 * @ClassName DataInterceptor.java
 * @Description
 * @createTime 2022年06月01日 21:12:00
 */
@Component
@Slf4j
public class DataInterceptor implements HandlerInterceptor {

    @Autowired
    private IDataService dataService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //统计UA
        // 获得访客的IP
        String ip = request.getRemoteHost();
        dataService.recordUV(ip);


        //统计DAU
        User user = hostHolder.getUser();
        if(user != null){
            dataService.recordDAU(user.getId());
        }

        return true;
    }
}
