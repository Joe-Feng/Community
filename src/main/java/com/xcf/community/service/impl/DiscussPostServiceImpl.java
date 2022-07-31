package com.xcf.community.service.impl;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.xcf.community.dao.DiscussPostMapper;
import com.xcf.community.pojo.DiscussPost;
import com.xcf.community.service.IDiscussPostService;
import com.xcf.community.utils.SensitiveFilter;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Joe
 * @version 1.0.0
 * @ClassName DiscussPostServiceImpl.java
 * @Description
 * @createTime 2022年05月10日 16:47:00
 */
@Service
@Slf4j
public class DiscussPostServiceImpl implements IDiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    //本地缓存最大数据条数
    @Value("${caffeine.posts.max-size}")
    private int caffeineCacheMaxsize;

    //本地缓存数据过期时间
    @Value("${caffeine.posts.expire-second}")
    private int caffeineCacheExpireSeconds;

    /**
     * Caffeine 核心接口：Cache , LoadingCache , AsyncLoadingCache
     * LoadingCache: 单线程从缓存中取数据，会维护一个从缓存中取数据的队列
     * AsyncLoadingCache: 异步，采用多线程从缓存中查询数据
     */
    //帖子列表缓存
    //LoadingCache是按 key:value 来查询的
    private LoadingCache<String, List<DiscussPost>> discussPostListCache;

    //帖子总数缓存:
    private LoadingCache<Integer, Integer> discussPostRowsCache;

    /**
     *
     */
    @PostConstruct
    public void init(){
        //初始化帖子列表缓存
        discussPostListCache = Caffeine.newBuilder()
                .maximumSize(caffeineCacheMaxsize)
                .expireAfterWrite(caffeineCacheExpireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    //这个方法作用：当在本地缓存中无法找到内容，应该如何加载数据
                    @Nullable
                    @Override
                    public List<DiscussPost> load(String key) throws Exception {
                        //判断获取缓存的key是否正确
                        if (key == null || key.length() == 0){
                            throw new IllegalArgumentException("参数不能为空！");
                        }
                        //分割 key, 获取参数
                        String[] params = key.split(":");
                        if (params == null || params.length != 2){
                            throw new IllegalArgumentException("参数不正确！");
                        }

                        // 扩展：可以自己再加一个二级缓存 Redis -> Mysql


                        // 从数据库查数据,获取后将数据放入本地缓存
                        int offset = Integer.parseInt(params[0]);
                        int limit = Integer.parseInt(params[1]);
                        log.info("load data from DB");
                        return discussPostMapper.selectDiscussPosts(0, offset, limit, 1);
                    }
                });

        // 初始化帖子总数缓存
        discussPostRowsCache = Caffeine.newBuilder()
                .maximumSize(caffeineCacheMaxsize)
                .expireAfterWrite(caffeineCacheExpireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Nullable
                    @Override
                    public Integer load(Integer key) throws Exception {
                        log.info("load data from DB");
                        return discussPostMapper.selectDiscussPostRows(key);
                    }
                });
    }

    @Override
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit, int orderMode) {
        //只对热门帖子进行缓存，即userId  = 0, orderMode = 1
        if(userId == 0 && orderMode == 1){
            //以起始和每页数量组合作为key可以保证缓存数据key的唯一性
            String cacheKey = offset + ":" + limit;
            //返回缓存中的数据
            return discussPostListCache.get(cacheKey);
        }

        log.info("load data from DB");
        return discussPostMapper.selectDiscussPosts(userId, offset, limit, orderMode);
    }

    @Override
    public int findDiscussPostRows(int userId) {
        //只对首页的帖子总数进行缓存
        if(userId == 0) {
            //cacheKey 总是为0
            Integer cacheKey = userId;
            return discussPostRowsCache.get(cacheKey);
        }
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    @Override
    public int addDiscussPost(DiscussPost discussPost) {
        if(discussPost == null){
            throw new IllegalArgumentException("参数不能为空！");
        }

        //转义HTML标记
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));

        //敏感词过滤
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));


        return discussPostMapper.insertDiscussPost(discussPost);
    }

    @Override
    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    @Override
    public int updateCommentCount(int id, int commentCount) {
        return discussPostMapper.updateCommentCount(id, commentCount);
    }

    /**
     * 更新帖子类型
     * 0-普通；1-置顶；
     * @param id
     * @param type
     * @return
     */
    @Override
    public int updateType(int id, int type) {
        return discussPostMapper.updateType(id, type);
    }

    /**
     * 更新帖子状态
     * 0-普通；1-精华；2-拉黑；
     * @param id
     * @param status
     * @return
     */
    @Override
    public int updateStatus(int id, int status) {
        return discussPostMapper.updateStatus(id, status);
    }

    @Override
    public int updateScore(int id, double score) {
        return discussPostMapper.updateScore(id, score);
    }
}
