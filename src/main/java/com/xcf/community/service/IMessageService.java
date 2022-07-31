package com.xcf.community.service;

import com.xcf.community.pojo.Message;

import java.util.List;

/**
 * @author Joe
 * @ClassName IMessageService.java
 * @Description
 * @createTime 2022年05月18日 15:20:00
 */
public interface IMessageService {
    List<Message> findConversations(int userId, int offset, int limit);

    int findConversationCount(int userId);

    List<Message> findLetters(String conversationId, int offset, int limit);

    int findLetterCount(String conversationId);

    int findLetterUnReadCount(int userId, String conversationId);

    int addMessage(Message message);

    int readMessage(List<Integer> ids);

    Message findLatestNotice(int userId, String topic);

    int findNoticeCount(int userId, String topic);

    int findNoticeUnreadCount(int userId, String topic);

    List<Message> findNotices(int userId, String topic, int offset, int limit);
}
