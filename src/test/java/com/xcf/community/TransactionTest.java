package com.xcf.community;

import com.xcf.community.service.impl.DemoServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Joe
 * @ClassName TransactionTest.java
 * @Description
 * @createTime 2022年05月17日 14:48:00
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
@SpringBootTest
public class TransactionTest {
    @Autowired
    private DemoServiceImpl demoServiceImpl;

    @Test
    public void test1(){
        Object save1 = demoServiceImpl.save1();
        System.out.println(save1);
    }

    @Test
    public void test2(){
        Object save2 = demoServiceImpl.save2();
        System.out.println(save2);
    }
}
