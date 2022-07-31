package com.xcf.community.dao;

import com.xcf.community.pojo.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Joe
 * @version 1.0.0
 * @ClassName DiscussPostMapper.java
 * @Description
 * @createTime 2022年05月10日 13:05:00
 */
@Mapper
@Repository
public interface DiscussPostMapper {
    //用于个人用户主页,主页不需要，所以使用动态sql
    //支持分页
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit, int orderMode);

    //@Param 注解用于给参数取别名
    //如果只有一个参数，并且在<if>里使用，则必须加别名
    int selectDiscussPostRows(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(int id);

    int updateCommentCount(int id, int commentCount);

    int updateType(int id, int type);

    int updateStatus(int id, int status);

    int updateScore(int id, double score);
}
