package com.xcf.community.controller;

import com.xcf.community.event.EventProducer;
import com.xcf.community.pojo.*;
import com.xcf.community.service.ICommentService;
import com.xcf.community.service.IDiscussPostService;
import com.xcf.community.service.ILikeService;
import com.xcf.community.service.IUserService;
import com.xcf.community.utils.CommunityConstant;
import com.xcf.community.utils.CommunityUtil;
import com.xcf.community.utils.HostHolder;
import com.xcf.community.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author Joe
 * @ClassName DiscussPostController.java
 * @Description
 * @createTime 2022年05月16日 20:05:00
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private IDiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private IUserService userService;

    @Autowired
    private ICommentService commentService;

    @Autowired
    private ILikeService likeService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/add")
    @ResponseBody
    public String addPost(String title, String content){
        User user = hostHolder.getUser();
        if(user == null){
            return CommunityUtil.getJsonString(403, "你还没登录");
        }

        DiscussPost post =DiscussPost.builder()
                .userId(user.getId())
                .title(title)
                .content(content)
                .createTime(new Date())
                .build();
        discussPostService.addDiscussPost(post);

        //触发发帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLIC)
                .setUserId(user.getId())
                .setEntityId(post.getId())
                .setEntityType(ENTITY_TYPE_POST);
        eventProducer.fireMessage(event);

        // 计算帖子分数
        String redisKey = RedisKeyUtil.getPostScoreKey();
        //选用set来存分数变化的帖子id
        redisTemplate.opsForSet().add(redisKey, post.getId());


        //TODO:报错的情况同一处理
        return CommunityUtil.getJsonString(0, "发布成功");
    }

    @GetMapping(path = "/detail/{discussPostId}")
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
        //帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post", post);
        //作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);
        //点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeCount", likeCount);
        //点赞状态
        User u = hostHolder.getUser();
        int likeStatus = (u == null) ? 0
                : likeService.findEntityLikeStatus(u.getId(), ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeStatus", likeStatus);


        //评论分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());

        //帖子回复列表
        List<Comment> commentList = commentService.findCommentsByEntity(
                ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if(commentList != null){
            for(Comment comment : commentList){
                Map<String, Object> commentVo = new HashMap<>();
                //评论
                commentVo.put("comment", comment);
                //评论的作者
                commentVo.put("user", userService.findUserById(comment.getUserId()));
                //点赞数量
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeCount", likeCount);
                //点赞状态
                likeStatus = (u == null) ? 0
                        : likeService.findEntityLikeStatus( u.getId(), ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeStatus", likeStatus);

                //评论回复列表
                List<Comment> replayList = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT,
                        comment.getId(), 0, Integer.MAX_VALUE);
                //评论回复VO列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if(replayList != null){
                    for(Comment replay : replayList){
                        Map<String, Object> replayVo = new HashMap<>();
                        //回复
                        replayVo.put("replay", replay);
                        //回复作者
                        replayVo.put("user", userService.findUserById(replay.getUserId()));
                        //回复目标
                        User target =  replay.getTargetId() == 0 ? null : userService.findUserById(replay.getTargetId());
                        replayVo.put("target", target);
                        //点赞数量
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, replay.getId());
                        replayVo.put("likeCount", likeCount);
                        //点赞状态
                        likeStatus = (u == null) ? 0
                                : likeService.findEntityLikeStatus( u.getId(), ENTITY_TYPE_COMMENT, replay.getId());
                        replayVo.put("likeStatus", likeStatus);

                        replyVoList.add(replayVo);
                    }
                }
                //评论回复
                commentVo.put("replays", replyVoList);

                //回复数量
                int replayCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replayCount", replayCount);

                commentVoList.add(commentVo);
            }
        }


        model.addAttribute("comments", commentVoList);
        return "/site/discuss-detail";
    }

    /**
     * 置顶
     * @return
     */
    @PostMapping("/top/{type}")
    @ResponseBody
    public String setTop(int id, @PathVariable("type") int type){
        if(type == 0){
            type = 1; //原来为0：普通，变为加精
        }else {
            type = 0; //原来为，变为0
        }
        discussPostService.updateType(id, type);

        //触发发帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLIC)
                .setUserId(hostHolder.getUser().getId()) //谁触发的事件
                .setEntityId(id)
                .setEntityType(ENTITY_TYPE_POST);
        eventProducer.fireMessage(event);

        return CommunityUtil.getJsonString(0);
    }

    /**
     * 加精
     * @return
     */
    @PostMapping("/wonderful/{status}")
    @ResponseBody
    public String setWonderful(int id, @PathVariable("status") int status){
        if(status == 0) {
            status = 1;
        }else {
            status = 0;
        }
        discussPostService.updateStatus(id, status);

        //触发发帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLIC)
                .setUserId(hostHolder.getUser().getId()) //谁触发的事件
                .setEntityId(id)
                .setEntityType(ENTITY_TYPE_POST);
        eventProducer.fireMessage(event);

        // 计算帖子分数
        String redisKey = RedisKeyUtil.getPostScoreKey();
        //选用set来存分数变化的帖子id
        redisTemplate.opsForSet().add(redisKey, id);

        return CommunityUtil.getJsonString(0);
    }

    /**
     * 删除
     * @return
     */
    @PostMapping("/delete")
    @ResponseBody
    public String setDelete(int id){
        discussPostService.updateStatus(id, 2);

        //触发删帖事件
        Event event = new Event()
                .setTopic(TOPIC_DELETE)
                .setUserId(hostHolder.getUser().getId()) //谁触发的事件
                .setEntityId(id)
                .setEntityType(ENTITY_TYPE_POST);
        eventProducer.fireMessage(event);

        return CommunityUtil.getJsonString(0);
    }
}
