package com.xcf.community.dao;

import com.xcf.community.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Joe
 * @ClassName CommentMapper.java
 * @Description
 * @createTime 2022年05月17日 16:16:00
 */
@Mapper
@Repository
public interface CommentMapper {
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    int selectCountByEntity(int entityType, int entityId);

    int insertComment(Comment comment);

    Comment selectCommentById(int id);
}
