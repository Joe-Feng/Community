package com.xcf.community.service.impl;

import com.xcf.community.service.ILikeService;
import com.xcf.community.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

/**
 * @author Joe
 * @ClassName LikeServiceImpl.java
 * @Description
 * @createTime 2022年05月19日 21:24:00
 */
@Service
public class LikeServiceImpl implements ILikeService {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 点赞
     * @param userId 用户id
     * @param entityType 实体类型： 1 帖子 2 评论
     * @param entityId 评论的实体id：entityType = 1 ：entityId = discussId entityType = 2 ：entityId = entityId
     */
    @Override
    public void like(int userId, int entityType, int entityId, int entityUserId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                //该条判断要在事务之外，在事务内得不到结果的
                Boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);

                operations.multi();
                if(isMember){
                    //已经点过赞，再点就是取消
                    redisTemplate.opsForSet().remove(entityLikeKey, userId);
                    redisTemplate.opsForValue().decrement(userLikeKey);
                }else {
                    //为点过赞，则是点赞
                    redisTemplate.opsForSet().add(entityLikeKey, userId);
                    redisTemplate.opsForValue().increment(userLikeKey);
                }
                return operations.exec();
            }
        });
    }

    /**
     * 查询某实体点赞的数量
     * @param entityType
     * @param entityId
     * @return
     */
    @Override
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    /**
     * 查询某人对某实体的点赞状态
     * @param userId
     * @param entityType
     * @param entityId
     * @return 为了业务扩张性，返回结果类型设置为int  0：未点赞  1：点赞  2：点踩
     */
    @Override
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }

    /**
     * 查询用户获得的赞数
     * @param userId 用户id
     * @return
     */
    @Override
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count.intValue();
    }
}
