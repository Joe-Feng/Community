package com.xcf.community.quartz;

import com.xcf.community.pojo.DiscussPost;
import com.xcf.community.service.IDiscussPostService;
import com.xcf.community.service.IElasticSearchService;
import com.xcf.community.service.ILikeService;
import com.xcf.community.utils.CommunityConstant;
import com.xcf.community.utils.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Joe
 * @ClassName PostScoreRfreshJob.java
 * @Description
 * @createTime 2022年06月02日 16:32:00
 */
@Slf4j
public class PostScoreRefreshJob implements Job, CommunityConstant {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IDiscussPostService discussPostService;

    @Autowired
    private ILikeService likeService;

    @Autowired
    private IElasticSearchService elasticSearchService;

    //设置建站元年时间常量
    private static final Date epoch ;

    //初始化常量:因为epoch之初始化一次，所以使用静态代码块
    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-11-11 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化建站纪元失败", e);
        }
    }

    /**
     * 定时任务
     * @param context
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String redisKey = RedisKeyUtil.getPostScoreKey();
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);

        //判断是否有需要刷新的帖子
        if(operations.size() == 0){
            log.info("[任务取消] 没有需要刷新的帖子！");
            return;
        }

        log.info("[任务开始] 正在刷新的帖子分数：" + operations.size());
        while (operations.size() > 0){
            //从 operations 弹出帖子id，并刷新帖子的分数，直到所有帖子都被刷新
            this.refresh((Integer) operations.pop());
        }
        log.info("[任务结束] 帖子分数刷新完毕！");
    }

    /**
     * 刷新成绩
     * @param postId
     */
    private void refresh(int postId){
        DiscussPost post = discussPostService.findDiscussPostById(postId);

        if (post == null){
            log.error("该帖子不存在：id = " + postId);
            return;
        }

        //是否加精
        boolean isWonderful = post.getStatus() == 1;
        //评论数
        int commentCount = post.getCommentCount();
        //点赞数
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, postId);

        //计算权重
        double weight = (isWonderful ? 75 : 0) + commentCount * 10 + likeCount * 2;
        //计算分数：分数 = 权重 + 距离建站纪元的天数
        double score = Math.log10(Math.max(weight, 1)) +
                (post.getCreateTime().getTime() - epoch.getTime()) / (1000 * 3600 * 24);


        //更新帖子分数
        discussPostService.updateScore(postId, score);
        //同步到ES中的搜索结果
        post.setScore(score);
        elasticSearchService.saveDiscussPost(post);
    }
}
