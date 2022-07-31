package com.xcf.community.controller;

import com.xcf.community.event.EventProducer;
import com.xcf.community.pojo.Event;
import com.xcf.community.pojo.Page;
import com.xcf.community.pojo.User;
import com.xcf.community.service.IFollowService;
import com.xcf.community.service.IUserService;
import com.xcf.community.utils.CommunityConstant;
import com.xcf.community.utils.CommunityUtil;
import com.xcf.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @author Joe
 * @ClassName FollowController.java
 * @Description
 * @createTime 2022年05月20日 16:57:00
 */
@Controller
public class FollowController implements CommunityConstant {
    @Autowired
    private IFollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private IUserService userService;

    @Autowired
    private EventProducer eventProducer;

    @PostMapping(path = "/follow")
    @ResponseBody
    public String follow(int entityType, int entityId){
        User user = hostHolder.getUser();

        followService.follow(user.getId(), entityType, entityId);

        //触发关注事件
        Event event = new Event()
                .setTopic(TOPIC_FOLLOW)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);
        eventProducer.fireMessage(event);

        return CommunityUtil.getJsonString(0, "已关注");
    }

    @PostMapping(path = "/unfollow")
    @ResponseBody
    public String unfollow(int entityType, int entityId){
        User user = hostHolder.getUser();

        followService.unfollow(user.getId(), entityType, entityId);

        return CommunityUtil.getJsonString(0, "已取消关注");
    }

    /**
     * 获取关注列表
     * @param userId
     * @param model
     * @param page
     * @return
     */
    @GetMapping(path = "/followee/{userId}")
    public String listFollowee(@PathVariable("userId") int userId, Model model, Page page){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user", user);

        //设置分页
        page.setLimit(5);
        page.setPath("/followee/" + userId);
        page.setRows((int)followService.findFolloweeCount(userId, ENTITY_TYPE_USER));

        List<Map<String, Object>> userList = followService.listFollowee(userId, page.getOffset(), page.getLimit());
        if(userList != null){
            for(Map<String, Object> map : userList){
                User u = (User) map.get("user");
                //本用户是否关注了 u.getId() 用户
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);

        return "/site/followee";
    }

    /**
     * 获取粉丝列表
     * @param userId
     * @param model
     * @param page
     * @return
     */
    @GetMapping(path = "/follower/{userId}")
    public String listFollower(@PathVariable("userId") int userId, Model model, Page page){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user", user);

        //设置分页
        page.setLimit(5);
        page.setPath("/follower/" + userId);
        page.setRows((int)followService.findFollowerCount(ENTITY_TYPE_USER, userId));

        List<Map<String, Object>> userList = followService.listFollower(userId, page.getOffset(), page.getLimit());
        if(userList != null){
            for(Map<String, Object> map : userList){
                User u = (User) map.get("user");
                //本用户是否关注了 u.getId() 用户
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);


        return "/site/follower";
    }

    //判断该用户是否关注了userId用户
    private boolean hasFollowed(int userId){
        //用户未登录
        if(hostHolder.getUser() == null){
            return false;
        }

        return followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
    }
}
