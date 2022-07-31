package com.xcf.community.mapperTest;

import com.xcf.community.CommunityApplication;
import com.xcf.community.utils.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @author Joe
 * @version 1.0.0
 * @ClassName MailTests.java
 * @Description
 * @createTime 2022年05月12日 15:09:00
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
@SpringBootTest
public class MailTests {
    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void sendTest(){
        mailClient.sendMail("1322593809@qq.com", "Test", "Welcome.");
    }

    @Test
    public void htmlTest(){
        Context context = new Context();
        context.setVariable("username", "sunday");

        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);

        mailClient.sendMail("1322593809@qq.com", "HTML", content);
    }
}
