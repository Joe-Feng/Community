package com.xcf.community.service;

import com.xcf.community.pojo.Comment;

import java.util.List;

/**
 * @author Joe
 * @ClassName ICommentService.java
 * @Description
 * @createTime 2022年05月17日 16:30:00
 */
public interface ICommentService {
    List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit);

    int findCommentCount(int entityType, int entityId);

    int addComment(Comment comment);

    Comment findCommentById(int id);
}
