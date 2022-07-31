package com.xcf.community.event;

import com.alibaba.fastjson.JSONObject;
import com.xcf.community.pojo.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author Joe
 * @ClassName EventProducer.java
 * @Description 生产者
 * @createTime 2022年05月24日 15:44:00
 */
@Component
public class EventProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    /**
     * 处理事件
     * @param event 事件
     */
    public void fireMessage(Event event){
        //将事件发送到指定主体
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}
