package com.xcf.community.controller;

import com.xcf.community.event.EventProducer;
import com.xcf.community.pojo.Event;
import com.xcf.community.pojo.User;
import com.xcf.community.service.ILikeService;
import com.xcf.community.utils.CommunityConstant;
import com.xcf.community.utils.CommunityUtil;
import com.xcf.community.utils.HostHolder;
import com.xcf.community.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Joe
 * @ClassName LikeController.java
 * @Description
 * @createTime 2022年05月19日 21:50:00
 */
@Controller
public class LikeController implements CommunityConstant {
    @Autowired
    private ILikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int postId) {
        User user = hostHolder.getUser();

        //TODO:需要跳转到登录页面
        if(user == null){
            return CommunityUtil.getJsonString(403, "请你先登录!");
        }

        //点赞
        likeService.like(user.getId(), entityType, entityId, entityUserId);
        //数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        //状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);

        //返回的结果
        Map<String ,Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        //触发点赞事件
        if(likeStatus == 1){
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId", postId);
            eventProducer.fireMessage(event);
        }

        //触发修改帖子分数
        if(entityType == ENTITY_TYPE_POST){
            // 计算帖子分数
            String redisKey = RedisKeyUtil.getPostScoreKey();
            //选用set来存分数变化的帖子id
            redisTemplate.opsForSet().add(redisKey, postId);
        }

        return CommunityUtil.getJsonString(0, null, map);
    }
}
