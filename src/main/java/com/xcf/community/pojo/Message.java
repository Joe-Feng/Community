package com.xcf.community.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author Joe
 * @ClassName Message.java
 * @Description
 * @createTime 2022年05月18日 13:19:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    private int id;
    private int fromId;
    private int toId;
    private String conversationId;
    private String content;
    private int status;
    private Date createTime;
}
