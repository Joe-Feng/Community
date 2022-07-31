package com.xcf.community.dao;

import com.xcf.community.pojo.Message;
import com.xcf.community.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author Joe
 * @version 1.0.0
 * @ClassName UserMapper.java
 * @Description
 * @createTime 2022年05月09日 20:40:00
 */
@Mapper
@Repository
public interface UserMapper {
    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateStatus(int id, int status);

    int updateHeader(int id, String headerUrl);

    int updatePassword(int id, String password);
}
