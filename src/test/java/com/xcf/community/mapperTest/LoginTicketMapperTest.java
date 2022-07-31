package com.xcf.community.mapperTest;

import com.xcf.community.CommunityApplication;
import com.xcf.community.dao.LoginTicketMapper;
import com.xcf.community.pojo.LoginTicket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * @author Joe
 * @version 1.0.0
 * @ClassName LoginTicketMapperTest.java
 * @Description
 * @createTime 2022年05月13日 20:57:00
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
@SpringBootTest
public class LoginTicketMapperTest {
    @Autowired
    private LoginTicketMapper ticketMapper;

    @Test
    public void insertTest(){
        LoginTicket ticket = LoginTicket.builder()
                .userId(101)
                .ticket("abc")
                .status(0)
                .expired(new Date(System.currentTimeMillis() + 1000 * 60))
                .build();

        ticketMapper.insertLoginTicket(ticket);
    }

    @Test
    public void selectTest(){
        LoginTicket ticket = ticketMapper.selectByTicket("abc");
        System.out.println(ticket);
    }

    @Test
    public void updateTest(){
        int i = ticketMapper.updateStatus("abc", 1);
        System.out.println(i);
    }
}
