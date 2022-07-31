package com.xcf.community.service;

import com.xcf.community.pojo.DiscussPost;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Joe
 * @version 1.0.0
 * @ClassName IDiscussPostService.java
 * @Description
 * @createTime 2022年05月10日 16:43:00
 */
public interface IDiscussPostService {
    /**
     * 分页查找用户帖子
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<DiscussPost> findDiscussPosts(int userId, int offset, int limit, int orderMode);

    /**
     * 查看用户发帖总数
     * @param userId
     * @return
     */
    int findDiscussPostRows(int userId);

    int addDiscussPost(DiscussPost discussPost);

    DiscussPost findDiscussPostById(int id);

    int updateCommentCount(int id, int commentCount);

    int updateType(int id, int type);

    int updateStatus(int id, int status);

    int updateScore(int id, double score);
}
