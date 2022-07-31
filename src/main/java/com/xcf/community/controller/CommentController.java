package com.xcf.community.controller;

import com.xcf.community.event.EventProducer;
import com.xcf.community.pojo.Comment;
import com.xcf.community.pojo.DiscussPost;
import com.xcf.community.pojo.Event;
import com.xcf.community.service.ICommentService;
import com.xcf.community.service.IDiscussPostService;
import com.xcf.community.utils.CommunityConstant;
import com.xcf.community.utils.HostHolder;
import com.xcf.community.utils.RedisKeyUtil;
import io.netty.channel.EventLoopGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

/**
 * @author Joe
 * @ClassName CommentController.java
 * @Description
 * @createTime 2022年05月18日 09:28:00
 */
@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {
    @Autowired
    private ICommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private IDiscussPostService discussPostService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping(path = "/add/{discussPostId}")
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment){
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        //触发评论事件
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId", discussPostId);
        if(comment.getEntityType() == ENTITY_TYPE_POST){
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }else if (comment.getEntityType() == ENTITY_TYPE_COMMENT){
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setUserId(target.getUserId());
        }
        eventProducer.fireMessage(event);

        //评论帖子之后触发事件
        //为什么在评论帖子之后还要触发发帖事件？
        //因为对帖子发表评论会导致discussPost中的commentCount变化，所以需要改变elasticsearch中的数据
        //该条数据和elasticsearch中的数据id一样，会覆盖里边的那条数据
        if(comment.getEntityType() == ENTITY_TYPE_POST){
            //触发发帖事件
            event = new Event()
                    .setTopic(TOPIC_PUBLIC)
                    .setUserId(comment.getUserId())
                    .setEntityType(ENTITY_TYPE_POST)
                    .setEntityId(discussPostId);
            eventProducer.fireMessage(event);

            // 计算帖子分数
            String redisKey = RedisKeyUtil.getPostScoreKey();
            //选用set来存分数变化的帖子id
            redisTemplate.opsForSet().add(redisKey, discussPostId);
        }

        return "redirect:/discuss/detail/" + discussPostId;
    }
}
