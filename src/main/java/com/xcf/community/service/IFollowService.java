package com.xcf.community.service;

import java.util.List;
import java.util.Map;

/**
 * @author Joe
 * @ClassName IFollowService.java
 * @Description
 * @createTime 2022年05月20日 16:11:00
 */
public interface IFollowService {
    void follow(int userId, int entityType, int entityId);

    void unfollow(int userId, int entityType, int entityId);

    long findFolloweeCount(int userId, int entityType);

    long findFollowerCount(int entityType, int entityId);

    boolean hasFollowed(int userId, int entityType, int entityId);

    List<Map<String, Object>> listFollowee(int userId, int offset, int limit);

    List<Map<String, Object>> listFollower(int userId, int offset, int limit);
}
