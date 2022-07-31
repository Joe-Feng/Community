package com.xcf.community.service.impl;

import com.xcf.community.service.IDataService;
import com.xcf.community.utils.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Joe
 * @ClassName DataServiceImpl.java
 * @Description
 * @createTime 2022年06月01日 20:28:00
 */
@Service
@Slf4j
public class DataServiceImpl implements IDataService {

    @Autowired
    private RedisTemplate redisTemplate;

    private SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

    /**
     * 将指定ip计入UA
     * @param ip
     */
    @Override
    public void recordUV(String ip) {
        // 获取单日UV集合(HyperLogLog)的key
        String redisKey = RedisKeyUtil.getUVKey(df.format(new Date()));
        // 将数据记录到指定redisKey的HyperLogLog中
        log.info("ip:" +ip );
        redisTemplate.opsForHyperLogLog().add(redisKey, ip);
    }

    /**
     * 统计指定日期范围内的UA
     * @param start
     * @param end
     * @return
     */
    @Override
    public long calculateUV(Date start, Date end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }

        // keyList 用于整理该日期范围内的key
        List<String> keyList = new ArrayList<>();
        // Calendar 用于对日期进行运算
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        // !calendar.getTime().after(end) 当前时间的不晚于 end的时间时,进行while循环
        while (!calendar.getTime().after(end)) {
            // 获取单日UV集合(HyperLogLog)的key
            String key = RedisKeyUtil.getUVKey(df.format(calendar.getTime()));
            // 将key 存入集合
            keyList.add(key);
            // 日期时间向后推一天
            calendar.add(Calendar.DATE, 1);
        }

        // 获取区间UV(两个日期之间统计的UV)集合(HyperLogLog)的key
        String redisKey = RedisKeyUtil.getUVKey(df.format(start), df.format(end));
        // 合并redisKey对应的HyperLogLog集合和keyList集合
        redisTemplate.opsForHyperLogLog().union(redisKey, keyList.toArray());

        // 返回HyperLogLog中统计的数量
        return redisTemplate.opsForHyperLogLog().size(redisKey);
    }

    /**
     * 将指定用户计入DAU
     * @param userId
     */
    @Override
    public void recordDAU(int userId) {
        // 获取单日活跃用户集合(Bitmap)的key
        String redisKey = RedisKeyUtil.getDAUKey(df.format(new Date()));
        log.info("userId；" + userId);
        // 将数据记录到指定redisKey的Bitmap中,第三个参数表示是否活跃,true表示活跃
        redisTemplate.opsForValue().setBit(redisKey, userId, true);
    }

    /**
     * 统计指定日期方位内的DAU
     * @param start
     * @param end
     * @return
     */
    @Override
    public long calculateDAU(Date start, Date end) {
        if(start == null || end == null){
            throw new IllegalArgumentException("参数不能为空！");
        }

        //整理该日期范围内的key
        List<byte[]> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)){
            String key = RedisKeyUtil.getDAUKey(df.format(calendar.getTime()));
            // 将key 存入集合(参数为key的byte数组)
            keyList.add(key.getBytes());
            calendar.add(Calendar.DATE, 1);
        }


        //进行OR运算
        return (long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String redisKey = RedisKeyUtil.getDAUKey(df.format(start), df.format(end));
                connection.bitOp(RedisStringCommands.BitOperation.OR,
                        redisKey.getBytes(), keyList.toArray(new byte[0][0]));
                return connection.bitCount(redisKey.getBytes());
            }
        });
    }
}
