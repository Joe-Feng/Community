package com.xcf.community;

import com.xcf.community.service.IDiscussPostService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Joe
 * @ClassName CaffeineTest.java
 * @Description
 * @createTime 2022年06月06日 16:52:00
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
@SpringBootTest
public class CaffeineTest {

    @Autowired
    private IDiscussPostService discussPostService;

    @Test
    public void testCaffeine(){
        discussPostService.findDiscussPosts(0, 0, 10, 1);
        discussPostService.findDiscussPosts(0, 0, 10, 1);
        discussPostService.findDiscussPosts(0, 0, 10, 1);
        discussPostService.findDiscussPosts(0, 0, 10, 0);
    }
}
