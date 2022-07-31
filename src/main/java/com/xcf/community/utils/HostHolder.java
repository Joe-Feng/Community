package com.xcf.community.utils;

import com.xcf.community.pojo.User;
import org.springframework.stereotype.Component;

/**
 * @author Joe
 * @version 1.0.0
 * @ClassName HostHolder.java
 * @Description 持有用户信息，用于代替session对象
 * @createTime 2022年05月14日 11:07:00
 */
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<User>();

    public void setUsers(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }
}
