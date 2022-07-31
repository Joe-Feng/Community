package com.xcf.community.service;

/**
 * @author Joe
 * @ClassName ILikeService.java
 * @Description
 * @createTime 2022年05月19日 21:24:00
 */
public interface ILikeService {

    void like(int userId, int entityType, int entityId, int entityUserId);

    long findEntityLikeCount(int entityType, int entityId);

    int findEntityLikeStatus(int userId, int entityType, int entityId);

    int findUserLikeCount(int userId);
}
