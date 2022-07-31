package com.xcf.community.service;

import com.xcf.community.pojo.DiscussPost;

import java.util.List;

/**
 * @author Joe
 * @ClassName IElasticSearchService.java
 * @Description
 * @createTime 2022年05月28日 20:46:00
 */
public interface IElasticSearchService {
    void saveDiscussPost(DiscussPost post);

    void deleteDiscussPost(int id);


    List<Object> searchDiscussPost(String keyword, int current, int limit);
}
