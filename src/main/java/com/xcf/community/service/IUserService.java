package com.xcf.community.service;

import com.xcf.community.pojo.LoginTicket;
import com.xcf.community.pojo.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

/**
 * @author Joe
 * @version 1.0.0
 * @ClassName IUserService.java
 * @Description
 * @createTime 2022年05月10日 17:21:00
 */
public interface IUserService {
    User findUserById(int id);

    Map<String, Object> register(User user);

    int activation(int userId, String code);

    Map<String, Object> login(String username, String password, int expiredSeconds);

    void logout(String ticket);

    LoginTicket findLoginStickByStick(String stick);

    int updateHeader(int userId, String headerUrl);

    int updatePassword(User user, String password);

    User findUserByName(String name);

    User getCache(int userId);

    User initCache(int userId);

    void clearCache(int userId);

    Collection<? extends GrantedAuthority> getAuthorities(int userId);
}
