package com.xcf.community.config;

import com.xcf.community.quartz.PostScoreRefreshJob;
import com.xcf.community.quartz.QuartzDemo;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

/**
 * @author Joe
 * @ClassName QuartzConfig.java
 * @Description 此配置类并不是每次都起作用，只有第一次会被读取并存入数据库中
 * 配置 -> 数据库 -> 调用
 * @createTime 2022年06月02日 11:14:00
 */
@Configuration
public class QuartzConfig {

    /**
     * BeanFactory：容器的顶层接口
     * FactoryBean：简化 Bean 的实例化过程
     * 1. 通过FactoryBean封装Bean的实例化过程
     * 2. 将 FactoryBean 装配到 Spring 容器中
     * 3. 将 FactoryBean 注入给其他 Bean
     * 4. 该 Bean 得到的是 FactoryBean 所管理的实例对象
     */

    /**
     * 配置 JobDetail
     * @return
     */
    //@Bean
    public JobDetailFactoryBean demoJobDetail(){
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(QuartzDemo.class);
        factoryBean.setName("quartzDemo");
        factoryBean.setGroup("quartzDemoGroup");
        factoryBean.setDurability(true); //是否持久化
        factoryBean.setRequestsRecovery(true); //是否可以恢复
        return factoryBean;
    }

    /**
     * 配置 Trigger (SimpleTriggerFactoryBean, CronTriggerFactoryBean)
     * @return
     */
    //@Bean
    public SimpleTriggerFactoryBean demoTrigger(JobDetail jobDetail){
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setName("demoTrigger");
        factoryBean.setGroup("demoTriggerGroup");
        factoryBean.setRepeatInterval(1000); //间隔多长时间执行一次任务
        factoryBean.setJobDataMap(new JobDataMap());  //Trigger 底层要存 Job 的状态，
        return factoryBean;
    }



    /**
     * 配置 postScoreRefreshJob 的 JobDetail
     * @return
     */
    @Bean
    public JobDetailFactoryBean postScoreRefreshJobDetail(){
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(PostScoreRefreshJob.class);
        factoryBean.setName("postScoreRefreshJob");
        factoryBean.setGroup("communityJobGroup");
        factoryBean.setDurability(true); //是否持久化
        factoryBean.setRequestsRecovery(true); //是否可以恢复
        return factoryBean;
    }

    /**
     * 配置 postScoreRefreshJob 的 Trigger (SimpleTriggerFactoryBean, CronTriggerFactoryBean)
     * @return
     */
    @Bean
    public SimpleTriggerFactoryBean postScoreRefreshTrigger(JobDetail postScoreRefreshJobDetail){
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(postScoreRefreshJobDetail);
        factoryBean.setName("postScoreRefreshTrigger");
        factoryBean.setGroup("communityTriggerGroup");
        factoryBean.setRepeatInterval(1000 * 60 * 5); //5分钟刷新一次
        factoryBean.setJobDataMap(new JobDataMap());  //Trigger 底层要存 Job 的状态，
        return factoryBean;
    }
}
