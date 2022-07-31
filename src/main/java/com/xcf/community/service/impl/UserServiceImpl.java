package com.xcf.community.service.impl;

import com.xcf.community.dao.LoginTicketMapper;
import com.xcf.community.dao.UserMapper;
import com.xcf.community.pojo.LoginTicket;
import com.xcf.community.pojo.User;
import com.xcf.community.service.IUserService;
import com.xcf.community.utils.CommunityConstant;
import com.xcf.community.utils.CommunityUtil;
import com.xcf.community.utils.MailClient;
import com.xcf.community.utils.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Joe
 * @version 1.0.0
 * @ClassName UserServiceImpl.java
 * @Description
 * @createTime 2022年05月10日 17:21:00
 */
@Service
@Slf4j
public class UserServiceImpl implements IUserService, CommunityConstant{
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

//    @Autowired
//    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;


    /**
     * 通过id获取用户
     * 重构：使用redis缓存用户数据
     * @param id
     * @return
     */
    @Override
    public User findUserById(int id) {
        // return userMapper.selectById(id);
        User user = getCache(id);
        if(user == null){
            user = initCache(id);
        }
        return user;
    }

    /**
     * 注册
     * @param user
     * @return
     */
    @Override
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();

        //空值处理
        if(user == null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg", "邮箱不能为空!");
            return map;
        }

        //验证账号
        User u = userMapper.selectByName(user.getUsername());
        if(u != null){
            map.put("usernameMsg", "该用户名已存在!");
            return map;
        }

        //验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if(u != null){
            map.put("emailMsg", "该邮箱已经被注册!");
            return map;
        }

        //注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //发送激活邮件
       Context context = new Context();
       context.setVariable("email", user.getEmail());
       //http://localhost:8088/community/activation/101/code
       String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
       context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);

        return map;
    }

    /**
     * 激活：三种状态：重复激活、激活、激活失败
     * @param userId 用户id
     * @param code 激活码
     * @return
     */
    @Override
    public int activation(int userId, String code){
        User user = userMapper.selectById(userId);
        if(user.getStatus() == 1){
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId, 1);
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }
    }

    /**
     * 登录
     * @param username 用户名
     * @param password 密码
     * @param expiredSeconds 登录状态保持时间
     * @return
     */
    @Override
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

        //空值判断
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }

        //验证账号
        User user = userMapper.selectByName(username);
        if(user == null){
            map.put("usernameMsg", "该账号不存在!");
            return map;
        }
        if(user.getStatus() == 0){
            map.put("usernameMsg", "该账号未激活!");
            return map;
        }
        if(!CommunityUtil.md5(password + user.getSalt()).equals(user.getPassword())){
            map.put("passwordMsg", "密码不正确!");
            return map;
        }

        //生成登录凭证
        LoginTicket loginTicket = LoginTicket.builder()
                .userId(user.getId())
                .ticket(CommunityUtil.generateUUID())
                .status(0)
                .expired(new Date(System.currentTimeMillis() + expiredSeconds * 1000))
                .build();
       // loginTicketMapper.insertLoginTicket(loginTicket);

        //登录凭证存入redis
        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey, loginTicket); //将对象存为string类型，redis会将对象转换为JSON格式

        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    /**
     * 退出登录：设置登录凭证状态为2（失效）
     * @param ticket
     */
    @Override
    public void logout(String ticket){
       // loginTicketMapper.updateStatus(ticket, 1);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey, loginTicket);
    }

    /**
     * 通过ticket查找LoginStick
     * @param ticket
     * @return
     */
    @Override
    public LoginTicket findLoginStickByStick(String ticket) {
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    /**
     * 更换头像
     * @param userId
     * @param headerUrl
     * @return
     */
    @Override
    public int updateHeader(int userId, String headerUrl) {
        int rows = userMapper.updateHeader(userId, headerUrl);
        clearCache(userId);
        return rows;
        // return userMapper.updateHeader(userId, headerUrl);
    }

    /**
     * 通过用户名查找用户
     * @param name
     * @return
     */
    @Override
    public User findUserByName(String name) {
        return userMapper.selectByName(name);
    }

    /**
     * 更换密码
     * @param user
     * @param newPassword
     * @return
     */
    @Override
    public int updatePassword(User user, String newPassword) {
        int rows = userMapper.updatePassword(user.getId(), CommunityUtil.md5(newPassword + user.getSalt()));
        clearCache(user.getId());
        return rows;
        // return userMapper.updatePassword(user.getId(), CommunityUtil.md5(newPassword + user.getSalt()));
    }

    /**
     * 从Redis中获取用户信息
     * @param userId
     * @return
     */
    @Override
    public User getCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }

    /**
     * 将用户信息存入Redis
     * @param userId
     * @return
     */
    @Override
    public User initCache(int userId) {
        User user  = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }

    /**
     * 清除Redis中的用户信息
     * @param userId
     */
    @Override
    public void clearCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(int userId) {
        User user = userMapper.selectById(userId);

        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
               switch (user.getType()){
                   case 1:
                       return AUTHORITY_ADMIN;
                   case 2:
                       return AUTHORITY_MODERATOR;
                   default:
                       return AUTHORITY_USER;
               }
            }
        });
        return list;
    }
}
