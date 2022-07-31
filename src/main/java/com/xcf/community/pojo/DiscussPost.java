package com.xcf.community.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;


import java.util.Date;

/**
 * @author Joe
 * @version 1.0.0
 * @ClassName DiscussPost.java
 * @Description
 * @createTime 2022年05月10日 11:41:00
 */
//lombok 相关注解
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//ES 相关注解
@Document(indexName = "discusspost") //discusspost 必须全小写
public class DiscussPost {
    /**
     * 主键 id
     * elasticsearch的id与discussPost的id一样
     */
    @Id
    private int id;

    /**
     * 用户主键id
     */
    @Field(type = FieldType.Integer)
    private int userId;

    /**
     * 帖子标题
     * analyzer = "ik_max_word" 存储使用的分词器，尽可能最大分词
     * searchAnalyzer = "ik_smart" 搜索使用的分词器，粗粒度分词
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;

    /**
     * 帖子内容
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String content;

    /**
     * 帖子类型
     * 0-普通；1-置顶
     */
    @Field(type = FieldType.Integer)
    private int type;

    /**
     * 帖子状态
     * 0-正常；1-精华；2-拉黑；
     */
    @Field(type = FieldType.Integer)
    private int status;

    /**
     * 帖子创建日期
     * TODO 无法将 HH:mm:ss 插入
     */
    @Field(type = FieldType.Date, format = DateFormat.custom,pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern ="yyyy-MM-dd",timezone="GMT+8")
    private Date createTime;

    /**
     * 帖子评论数量
     */
    @Field(type = FieldType.Integer)
    private int commentCount;

    /**
     * 帖子得分
     */
    @Field(type = FieldType.Double)
    private double score;
}
