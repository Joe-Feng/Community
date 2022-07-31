package com.xcf.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.xcf.community.pojo.Message;
import com.xcf.community.pojo.Page;
import com.xcf.community.pojo.User;
import com.xcf.community.service.IMessageService;
import com.xcf.community.service.IUserService;
import com.xcf.community.utils.CommunityConstant;
import com.xcf.community.utils.CommunityUtil;
import com.xcf.community.utils.HostHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

/**
 * @author Joe
 * @ClassName MessageController.java
 * @Description
 * @createTime 2022年05月18日 15:35:00
 */
@Slf4j
@Controller
public class MessageController implements CommunityConstant {
    @Autowired
    private IMessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private IUserService userService;

    @GetMapping(path = "/letter/list")
    public String getLetterList(Model model, Page page){
        User user = hostHolder.getUser();
        //设置分页
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));

        //会话列表
        List<Message> conversationList = messageService.findConversations(
                user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<>();
        if(conversationList != null){
            for (Message message: conversationList) {
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", message);
//                log.debug("-----letterCount: " + messageService.findLetterCount(message.getConversionId()));
                map.put("letterCount", messageService.findLetterCount(message.getConversationId()));
//                log.debug("****unreadCount: " + messageService.findLetterUnReadCount(user.getId(), message.getConversionId()));
                map.put("unreadCount", messageService.findLetterUnReadCount(user.getId(), message.getConversationId()));
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target", userService.findUserById(targetId));

                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);

        //查询未读消息总数
        int letterUnreadCount = messageService.findLetterUnReadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        return "/site/letter";
    }

    @GetMapping(path = "/letter/detail/{conversationId}")
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model){
        //分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        //私信列表
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if(letterList != null){
            for(Message letter : letterList){
                Map<String, Object> map = new HashMap<>();
                map.put("letter", letter);
                map.put("fromUser", userService.findUserById(letter.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);

        //私信目标
        User letterTarget = getLetterTarget(conversationId);
        model.addAttribute("letterTarget", letterTarget);

        //设置已读
        List<Integer> ids = getLetterIds(letterList);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";
    }

    private List<Integer> getLetterIds(List<Message> letterList){
        List<Integer> ids = new ArrayList<>();

        if(letterList != null){
            for(Message message : letterList){
                if(hostHolder.getUser().getId() == message.getToId() && message.getStatus() == 0){
                    ids.add(message.getId());
                }
            }
        }

        return ids;
    }

    private User getLetterTarget(String conversationId){
        String[] id = conversationId.split("_");
        int id0 = Integer.parseInt(id[0]);
        int id1 = Integer.parseInt(id[1]);

        if(hostHolder.getUser().getId() == id0){
            return userService.findUserById(id1);
        }else{
            return userService.findUserById(id0);
        }
    }

    @PostMapping(path = "/letter/send")
    @ResponseBody
    public String senLetter(String toName, String content){
//        Integer.parseInt("abc");

        User target = userService.findUserByName(toName);
        if(target == null){
            return CommunityUtil.getJsonString(1, "目标用户不存在！");
        }
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        if(message.getFromId() < message.getToId()){
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        }else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());

        messageService.addMessage(message);

        return CommunityUtil.getJsonString(0);
    }

    @GetMapping("/notice/list")
    public String getNotices(Model model){
        User user = hostHolder.getUser();


        //查询评论
        Message message = messageService.findLatestNotice(user.getId(), TOPIC_COMMENT);
        Map<String, Object> messageVo = new HashMap<>();
        if(message != null){
            messageVo.put("message", message);

            //去除转移字符，将content转换为对象
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVo.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVo.put("entityType", data.get("entityType"));
            messageVo.put("entityId", data.get("entityId"));
            messageVo.put("postId", data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_COMMENT);
            messageVo.put("count", count);

            int unread = messageService.findNoticeUnreadCount(user.getId(), TOPIC_COMMENT);
            messageVo.put("unread", unread);

            //fix:当没有该类通知时不进行处理
            model.addAttribute("commentNotice", messageVo);
        }


        //查询点赞
        message = messageService.findLatestNotice(user.getId(), TOPIC_LIKE);
        messageVo = new HashMap<>();
        if(message != null){
            messageVo.put("message", message);

            //去除转移字符，将content转换为对象
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVo.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVo.put("entityType", data.get("entityType"));
            messageVo.put("entityId", data.get("entityId"));
            messageVo.put("postId", data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_LIKE);
            messageVo.put("count", count);

            int unread = messageService.findNoticeUnreadCount(user.getId(), TOPIC_LIKE);
            messageVo.put("unread", unread);

            //fix:当没有该类通知时不进行处理
            model.addAttribute("likeNotice", messageVo);
        }



        //查询关注
        message = messageService.findLatestNotice(user.getId(), TOPIC_FOLLOW);
        messageVo = new HashMap<>();
        if(message != null){
            messageVo.put("message", message);

            //去除转移字符，将content转换为对象
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVo.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVo.put("entityType", data.get("entityType"));
            messageVo.put("entityId", data.get("entityId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_FOLLOW);
            messageVo.put("count", count);

            int unread = messageService.findNoticeUnreadCount(user.getId(), TOPIC_FOLLOW);
            messageVo.put("unread", unread);

            //fix:当没有该类通知时不进行处理
            model.addAttribute("followNotice", messageVo);
        }



        //查询未读消息
        int letterUnreadCount = messageService.findLetterUnReadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        return "/site/notice";
    }

    @GetMapping(path = "/notice/detail/{topic}")
    public String getNoticeDetail(@PathVariable("topic") String topic, Model model, Page page){
        User user = hostHolder.getUser();

        //System.out.println("----------" + topic);

        //设置分页
        page.setLimit(5);
        page.setPath("/notice/detail/" + topic);
        page.setRows(messageService.findNoticeCount(user.getId(), topic));


        List<Message> notices = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String, Object>> noticeList = new ArrayList<>();
        if(notices != null){
            for (Message notice: notices) {
                Map<String, Object> map = new HashMap<>();
                //通知
                map.put("notice", notice);

                //内容
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String, Object> data = JSONObject.parseObject(content, Map.class);
                map.put("user", userService.findUserById((Integer) data.get("userId")));
                map.put("entityType", data.get("entityType"));
                map.put("entityId", data.get("entityId"));
                map.put("postId", data.get("postId"));

                //通知作者
                map.put("fromUser", userService.findUserById(notice.getFromId()));

                noticeList.add(map);
            }
            model.addAttribute("notices", noticeList);

            //设置已读
            List<Integer> ids = getLetterIds(notices);
            if(!ids.isEmpty()){
                messageService.readMessage(ids);
            }
        }
        return "/site/notice-detail";
    }
}
