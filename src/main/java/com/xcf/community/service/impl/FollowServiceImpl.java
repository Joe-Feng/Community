package com.xcf.community.service.impl;

import com.xcf.community.pojo.User;
import com.xcf.community.service.IFollowService;
import com.xcf.community.service.IUserService;
import com.xcf.community.utils.CommunityConstant;
import com.xcf.community.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Joe
 * @ClassName FollowServiceImpl.java
 * @Description
 * @createTime 2022年05月20日 16:12:00
 */
@Service
public class FollowServiceImpl implements IFollowService, CommunityConstant {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IUserService userService;

    /**
     * 关注
     * @param userId
     * @param entityType
     * @param entityId
     */
    @Override
    public void follow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getPrefixFollowee(userId, entityType);
                String followerKey = RedisKeyUtil.getPrefixFollower(entityType, entityId);

                operations.multi();

                operations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                operations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());

                return operations.exec();
            }
        });
    }

    /**
     * 取消关注
     * @param userId
     * @param entityType
     * @param entityId
     */
    @Override
    public void unfollow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getPrefixFollowee(userId, entityType);
                String followerKey = RedisKeyUtil.getPrefixFollower(entityType, entityId);

                operations.multi();

                operations.opsForZSet().remove(followeeKey, entityId);
                operations.opsForZSet().remove(followerKey, userId);

                return operations.exec();
            }
        });
    }

    /**
     * 查询 userID 用户关注的数量
     * @param userId
     * @param entityType
     * @return
     */
    @Override
    public long findFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getPrefixFollowee(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    /**
     * 查询实体 entityId 的粉丝数
     * @param entityType
     * @param entityId
     * @return
     */
    @Override
    public long findFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getPrefixFollower(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    /**
     * 查询当前用户 userId 是否已经关注实体 entityId
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    @Override
    public boolean hasFollowed(int userId, int entityType, int entityId) {
        String followeeKey = RedisKeyUtil.getPrefixFollowee(userId, entityType);
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
    }

    /**
     * 查询用户 userId 的关注列表
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    @Override
    public List<Map<String, Object>> listFollowee(int userId, int offset, int limit) {
        String followeeKey = RedisKeyUtil.getPrefixFollowee(userId, ENTITY_TYPE_USER);
        //按照时间从最新开始往后排
        //zset 的 range() reverseRange()方法返回的有序集合，有序是由redis内部进行实现的
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);

        if(targetIds == null){
            return null;
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for(Integer targetId: targetIds){
            Map<String, Object> map = new HashMap<>();
            User user = userService.findUserById(targetId);
            map.put("user", user);
            Double score = redisTemplate.opsForZSet().score(followeeKey, targetId);
            map.put("followTime", new Date(score.longValue()));

            list.add(map);
        }

        return list;
    }

    /**
     * 查询用户 userId 的粉丝列表
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    @Override
    public List<Map<String, Object>> listFollower(int userId, int offset, int limit) {
        String followerKey = RedisKeyUtil.getPrefixFollower(ENTITY_TYPE_USER, userId);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);

        if(targetIds == null){
            return null;
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for(Integer targetId: targetIds){
            Map<String, Object> map = new HashMap<>();
            //返回用户
            User user = userService.findUserById(targetId);
            map.put("user", user);
            Double score = redisTemplate.opsForZSet().score(followerKey, targetId);
            //返回时间
            map.put("followTime", new Date(score.longValue()));

            list.add(map);
        }

        return list;
    }
}
