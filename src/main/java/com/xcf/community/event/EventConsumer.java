package com.xcf.community.event;

import com.alibaba.fastjson.JSONObject;
import com.xcf.community.pojo.DiscussPost;
import com.xcf.community.pojo.Event;
import com.xcf.community.pojo.Message;
import com.xcf.community.service.IDiscussPostService;
import com.xcf.community.service.IElasticSearchService;
import com.xcf.community.service.IMessageService;
import com.xcf.community.utils.CommunityConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Joe
 * @ClassName EventConsumer.java
 * @Description 消费者
 * @createTime 2022年05月24日 15:45:00
 */
@Component
@Slf4j
public class EventConsumer implements CommunityConstant {

    @Autowired
    private IMessageService messageService;

    @Autowired
    private IDiscussPostService discussPostService;

    @Autowired
    private IElasticSearchService elasticSearchService;

    @Value("${wk.image.storage}")
    public String wkImageStorage;

    @Value("${wk.image.command}")
    public String wkImageCommand;

    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW})
    public void handleMessage(ConsumerRecord record){
        if(record == null || record.value() == null){
            log.error("消息的内容为空！");
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null){
            log.error("消息格式错误！");
            return;
        }

        //发送站内通知
        Message message = Message.builder()
                .fromId(SYSTEM_USER_ID)
                .toId(event.getEntityUserId())
                .conversationId(event.getTopic())
                .createTime(new Date())
                .build();

        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());

        if(!event.getData().isEmpty()){
            for(Map.Entry<String, Object> entry : event.getData().entrySet()){
                content.put(entry.getKey(), entry.getValue());
            }
        }

        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }

    //消费发帖事件
    @KafkaListener(topics = {TOPIC_PUBLIC})
    public void handlePublicMessage(ConsumerRecord record){
        if(record == null || record.value() == null){
            log.error("消息的内容为空！");
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null){
            log.error("消息格式错误！");
            return;
        }

        //查出
        DiscussPost post = discussPostService.findDiscussPostById(event.getEntityId());
        elasticSearchService.saveDiscussPost(post);
    }

    //消费删帖事件
    @KafkaListener(topics = {TOPIC_DELETE})
    public void handleDeleteMessage(ConsumerRecord record){
        if(record == null || record.value() == null){
            log.error("消息的内容为空！");
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null){
            log.error("消息格式错误！");
            return;
        }

        //删除 Elasticsearch 中的数据
        elasticSearchService.deleteDiscussPost(event.getEntityId());
    }

    //消费分享事件
    @KafkaListener(topics = {TOPIC_SHARE})
    public void handleShareMessage(ConsumerRecord record){
        if(record == null || record.value() == null){
            log.error("消息的内容为空！");
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null){
            log.error("消息格式错误！");
            return;
        }

        //获取数据
        String htmlUrl = (String) event.getData().get("htmlUrl");
        String filename = (String) event.getData().get("filename");
        String suffix = (String) event.getData().get("suffix");
        //拼接成 cmd 命令
        String cmd = wkImageCommand + " --quality 75 "
                + htmlUrl + " " + wkImageStorage + "/" + filename + suffix;
        //执行
        try {
            Runtime.getRuntime().exec(cmd);
            log.info("生成长图成功：" + cmd);
        } catch (IOException e) {
            e.printStackTrace();
            log.info("生成长图失败：" + e.getMessage());
        }
    }
}
