package com.xcf.community.pojo;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Joe
 * @ClassName Event.java
 * @Description 事件触发时的信息
 * @createTime 2022年05月24日 15:30:00
 */

@NoArgsConstructor
@AllArgsConstructor
public class Event {
    //事件类型：Producer发送的主题
    private String topic;
    //触发事件的用户id
    private int userId;
    //实体类型
    private int entityType;
    //实体id
    private int entityId;
    //实体拥有者id
    private int entityUserId;
    //其他信息
    private Map<String, Object> data = new HashMap<>();

    public String getTopic() {
        return topic;
    }

    public int getUserId() {
        return userId;
    }

    public int getEntityType() {
        return entityType;
    }

    public int getEntityId() {
        return entityId;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Event setData(String key, Object value) {
        data.put(key, value);
        return this;
    }

    @Override
    public String toString() {
        return "Event{" +
                "topic='" + topic + '\'' +
                ", userId=" + userId +
                ", entityType=" + entityType +
                ", entityId=" + entityId +
                ", entityUserId=" + entityUserId +
                ", data=" + data +
                '}';
    }
}
