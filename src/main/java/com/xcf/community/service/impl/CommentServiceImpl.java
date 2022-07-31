package com.xcf.community.service.impl;

import com.xcf.community.dao.CommentMapper;
import com.xcf.community.dao.DiscussPostMapper;
import com.xcf.community.pojo.Comment;
import com.xcf.community.service.ICommentService;
import com.xcf.community.utils.CommunityConstant;
import com.xcf.community.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author Joe
 * @ClassName CommentService.java
 * @Description
 * @createTime 2022年05月17日 16:31:00
 */
@Service
public class CommentServiceImpl implements ICommentService, CommunityConstant {
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostMapper discussPostMapper;


    @Override
    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
    }

    @Override
    public int findCommentCount(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    @Override
    public Comment findCommentById(int id) {
        return commentMapper.selectCommentById(id);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        if(comment == null){
            throw new IllegalArgumentException("参数不能为空！");
        }

        //添加评论
        comment.setContent(HtmlUtils.htmlEscapeDecimal(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int rows = commentMapper.insertComment(comment);

        //更新数量:先增加评论，后更新
        if(comment.getEntityType() == ENTITY_TYPE_POST){
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostMapper.updateCommentCount(comment.getEntityId(), count);
        }

        return 0;
    }
}
