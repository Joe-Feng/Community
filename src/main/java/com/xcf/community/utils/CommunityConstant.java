package com.xcf.community.utils;

/**
 * @author Joe
 * @version 1.0.0
 * @ClassName CommunityConstant.java
 * @Description
 * @createTime 2022年05月12日 21:48:00
 */
public interface CommunityConstant {
    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     */
    int ACTIVATION_FAILURE = 2;

    /**
     * 默认状态的登录凭证的超时时间：1天
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;


    /**
     * 默认状态的登录凭证的超时时间:14天
     */
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 14;

    /**
     * 实体类型：帖子
     */
    int ENTITY_TYPE_POST = 1;

    /**
     * 实体类型：评论
     */
    int ENTITY_TYPE_COMMENT = 2;

    /**
     * 实体类型：评论
     */
    int ENTITY_TYPE_USER = 3;

    /**
     * 主体：评论
     */
    String TOPIC_COMMENT = "comment";

    /**
     * 主体：点赞
     */
    String TOPIC_LIKE = "like";

    /**
     * 主体：关注
     */
    String TOPIC_FOLLOW = "follow";


    /**
     * 主体：发帖
     */
    String TOPIC_PUBLIC = "public";

    /**
     * 主体：删帖
     */
    String TOPIC_DELETE = "delete";

    /**
     * 主体：分享
     */
    String TOPIC_SHARE = "share";


    /**
     * 系统用户id
     */
    int SYSTEM_USER_ID = 1;

    /**
     * 权限：普通用户
     */
    String AUTHORITY_USER = "user";


    /**
     * 权限：管理员
     */
    String AUTHORITY_ADMIN = "admin";

    /**
     * 权限：版主
     */
    String AUTHORITY_MODERATOR = "moderator";
}
