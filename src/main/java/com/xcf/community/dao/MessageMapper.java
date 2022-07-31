package com.xcf.community.dao;

import com.xcf.community.pojo.Message;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Joe
 * @ClassName MessageMapper.java
 * @Description
 * @createTime 2022年05月18日 13:23:00
 */
@Mapper
@Repository
public interface MessageMapper {
    //查询当前用户会话列表，针对每个会话只返回一条最新私信
    List<Message> selectConversion(int userId, int offset, int limit);

    //查询当前用户的会话数量
    int selectConversationCount(int userId);

    //查询某个会话所包含的私信列表
    List<Message> selectLetters(String conversationId, int offset, int limit);

    //查询某个会话包含的私信数量
    int selectLetterCount(String conversationId);

    //查询未读私信的数量:用户所有未读数量，某个会话未读数量
    int selectLetterUnReadCount(int userId, String conversationId);

    //新增消息
    int insertMessage(Message message);

    //修改消息状态 或 删除消息
    int updateStatus(List<Integer> ids, int status);

    //查询某个主题最新消息
    Message selectLatestNotice(int userId, String topic);

    //查询某个主题下最新的通知
    int selectNoticeCount(int userId, String topic);

    //查询未读的通知数量
    int selectNoticeUnreadCount(int userId, String topic);

    //查询消息列表
    List<Message> selectNotices(int userId, String topic, int offset, int limit);
}
