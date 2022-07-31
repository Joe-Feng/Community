package com.xcf.community.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Joe
 * @ClassName Comment.java
 * @Description
 * @createTime 2022年05月17日 16:13:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    private int id;
    private int userId;
    private int entityType;
    private int entityId;
    private int targetId;
    private String content;
    private int status;
    private Date createTime;
}
