package com.xcf.community.controller.interceptor;

import com.xcf.community.pojo.User;
import com.xcf.community.service.ICommentService;
import com.xcf.community.service.IMessageService;
import com.xcf.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Joe
 * @ClassName MessageInterceptor.java
 * @Description
 * @createTime 2022年05月26日 21:13:00
 */
@Component
public class MessageInterceptor implements HandlerInterceptor {

    @Autowired
    private IMessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user != null && modelAndView != null){
            int letterUnReadCount = messageService.findLetterUnReadCount(user.getId(), null);
            int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
            modelAndView.addObject("allUnreadCount", letterUnReadCount + noticeUnreadCount);
        }
    }
}
