package com.xcf.community;

import com.xcf.community.pojo.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
@SpringBootTest
public class CommunityApplicationTests {
    @Autowired
    private User user;

    @Test
    public void contextLoads() {
        user.setUsername("test");
        System.out.println(user);
    }

}
