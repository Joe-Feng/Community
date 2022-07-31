package com.xcf.community;

import com.xcf.community.utils.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Joe
 * @ClassName SensitiveFilterTest.java
 * @Description
 * @createTime 2022年05月16日 15:01:00
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
@SpringBootTest
public class SensitiveFilterTest {
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void test(){
        String s = "ajxf%%abcd8k&a*b#c";
        s = sensitiveFilter.filter(s);
        System.out.println(s);
    }
}
