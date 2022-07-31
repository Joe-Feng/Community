package com.xcf.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Joe
 * @ClassName QuartzTest.java
 * @Description
 * @createTime 2022年06月02日 14:39:00
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
@SpringBootTest
public class QuartzTest {

    @Autowired
    private Scheduler scheduler;

    /**
     * 删除 quartz 数据库中的配置信息
     */
    @Test
    public void testDelete() {
        try {
            //返回值：是否删除成功
            boolean res = scheduler.deleteJob(new JobKey("postScoreRefreshJob", "communityJobGroup"));
            System.out.println(res);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
