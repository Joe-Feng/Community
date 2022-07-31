package com.xcf.community.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author Joe
 * @ClassName QuartzDemo.java
 * @Description
 * @createTime 2022年06月02日 11:12:00
 */
public class QuartzDemo implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println(Thread.currentThread().getName() + ": execute a quartz job!");
    }
}
