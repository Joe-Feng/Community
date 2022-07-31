package com.xcf.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * @author Joe
 * @ClassName RedisTest.java
 * @Description
 * @createTime 2022年05月19日 15:52:00
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
@SpringBootTest
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testString(){
        String redisKey = "test:count";

        redisTemplate.opsForValue().set(redisKey, 1);

        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
    }

    @Test
    public void testHash(){
        String redisKey = "test:user";

        redisTemplate.opsForHash().put(redisKey, "id", 1);
        redisTemplate.opsForHash().put(redisKey, "name", "xx");

        System.out.println(redisTemplate.opsForHash().get(redisKey, "id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey, "name"));
    }

    @Test
    public void testLists(){
        String redisKey = "test:list";

        redisTemplate.opsForList().rightPush(redisKey, 1);
        redisTemplate.opsForList().rightPush(redisKey, 2);
        redisTemplate.opsForList().rightPush(redisKey, 3);

        System.out.println(redisTemplate.opsForList().size(redisKey));
        System.out.println(redisTemplate.opsForList().index(redisKey, 0));
        System.out.println(redisTemplate.opsForList().range(redisKey, 0, 2));

        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));

    }

    @Test
    public void testSets(){
        String redisKey = "test:key";

        redisTemplate.opsForSet().add(redisKey, "1", "2", "3", 4, 5);

        System.out.println(redisTemplate.opsForSet().size(redisKey));
        System.out.println(redisTemplate.opsForSet().members(redisKey));
        System.out.println(redisTemplate.opsForSet().pop(redisKey));
        System.out.println(redisTemplate.opsForSet().members(redisKey));
    }

    @Test
    public void testSortSets(){
        String redisKey = "test:zkey";

        redisTemplate.opsForZSet().add(redisKey, "唐僧", 90);
        redisTemplate.opsForZSet().add(redisKey, "悟空", 18);
        redisTemplate.opsForZSet().add(redisKey, "沙僧", 10);
        redisTemplate.opsForZSet().add(redisKey, "八戒", 11);

        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
        System.out.println(redisTemplate.opsForZSet().score(redisKey, "悟空"));
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey, "八戒"));
        System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey, 0, 2));
    }

    @Test
    public void testKeys(){
        redisTemplate.delete("test:list");

        System.out.println(redisTemplate.opsForList().size("test:list"));

        redisTemplate.expire("test:user", 10, TimeUnit.SECONDS);
    }

    @Test
    public void testBoundOperations(){
        String redisKey = "test:count";
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
    System.out.println(operations.get());
    }

    //编程式事务
    @Test
    public void testTx(){
        Object obj =
            redisTemplate.execute(
                new SessionCallback() {
                  @Override
                  public Object execute(RedisOperations operations) throws DataAccessException {
                    String redisKey = "test:tx";

                    // 开始事务
                    operations.multi();
                    // 执行的操作会放在对队列里，提交事务的时候一起执行操作
                    operations.opsForSet().add(redisKey, "zh");
                    operations.opsForSet().add(redisKey, "zw");
                    operations.opsForSet().add(redisKey, "zx");
                    //在事务期间的查询，查的还是此刻的结果，但是不会立即返回查询值，在事务结束后才会返回
                    System.out.println(operations.opsForSet().members(redisKey));

                    // 结束事务
                    return operations.exec();
                  }
                });
        System.out.println(obj);
    }

    //统计20万个重复数据的独立总数
    @Test
    public void testHll(){
        String redisKey = "test:hll:01";

        for (int i = 0; i < 100; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey, i);
        }

        for (int i = 0; i <= 100; i++) {
            int r = (int) (Math.random() * 100 + 1);
            redisTemplate.opsForHyperLogLog().add(redisKey, r);
        }

        Long size = redisTemplate.opsForHyperLogLog().size(redisKey);
        System.out.println(size);
    }

    //三种数据合并，再统计合并后的重复数据的独立总数
    @Test
    public void testUnion(){
        String redisKey2 = "test:hll:02";
        for (int i = 0; i <= 10000 ; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey2, i);
        }
        String redisKey3 = "test:hll:03";
        for (int i = 10001; i <= 15000 ; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey3, i);
        }
        String redisKey4 = "test:hll:04";
        for (int i = 15001; i <= 20000 ; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey4, i);
        }

        String redisKey = "test:hll:union";
        redisTemplate.opsForHyperLogLog().union(redisKey, redisKey2, redisKey3, redisKey4);

        Long size = redisTemplate.opsForHyperLogLog().size(redisKey);
        System.out.println(size);
    }

    //统计一组数据的布尔值
    @Test
    public void testBitmap(){
        String redisKey = "test:bm:01";

        redisTemplate.opsForValue().setBit(redisKey, 1, true); //true:1 false:0
        redisTemplate.opsForValue().setBit(redisKey, 4, true);
        redisTemplate.opsForValue().setBit(redisKey, 7, true);


        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 0));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 1));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 2));

        Object o = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.bitCount(redisKey.getBytes());
            }
        });
        System.out.println(o);
    }

    // 统计三组数据的布尔值，并对数据做OR运算
    @Test
    public void testBitMapOperation(){
        String redisKey2 = "test:bm:02";
        redisTemplate.opsForValue().setBit(redisKey2, 0, true); //true:1 false:0
        redisTemplate.opsForValue().setBit(redisKey2, 1, true);
        redisTemplate.opsForValue().setBit(redisKey2, 2, true);

        String redisKey3 = "test:bm:03";
        redisTemplate.opsForValue().setBit(redisKey3, 2, true); //true:1 false:0
        redisTemplate.opsForValue().setBit(redisKey3, 3, true);
        redisTemplate.opsForValue().setBit(redisKey3, 4, true);

        String redisKey4 = "test:bm:04";
        redisTemplate.opsForValue().setBit(redisKey4, 4, true); //true:1 false:0
        redisTemplate.opsForValue().setBit(redisKey4, 5, true);
        redisTemplate.opsForValue().setBit(redisKey4, 6, true);

        String redisKey = "test:bm:or";
        Object execute = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.bitOp(RedisStringCommands.BitOperation.OR,
                        redisKey.getBytes(), redisKey2.getBytes(), redisKey3.getBytes(), redisKey4.getBytes());
                return connection.bitCount(redisKey.getBytes());
            }
        });

        System.out.println(execute);

        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 0));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 1));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 2));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 3));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 4));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 5));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 6));
    }
}
