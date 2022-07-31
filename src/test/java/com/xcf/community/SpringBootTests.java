package com.xcf.community;

import com.xcf.community.pojo.DiscussPost;
import com.xcf.community.service.IDiscussPostService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * @author Joe
 * @ClassName SpringBootTests.java
 * @Description
 * @createTime 2022年06月06日 19:27:00
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
@SpringBootTest
public class SpringBootTests {

    @Autowired
    private IDiscussPostService discussPostService;

    private DiscussPost data;

    /**
     * 在类加载之前加载一次
     */
    @BeforeClass
    public static void beforeClass(){
        System.out.println("beforeClass");
    }

    /**
     * 在类结束之后加载一次
     */
    @AfterClass
    public static void afterClass(){
        System.out.println("afterClass");
    }

    /**
     * 在每个方法之前加载
     */
    @Before
    public void before(){
        //初始化测试数据
        data = DiscussPost.builder()
                .userId(111)
                .title("test title")
                .content("test content")
                .createTime(new Date())
                .build();
        discussPostService.addDiscussPost(data);
        System.out.println("before");
    }

    /**
     * 在每个方法执行完之后加载
     */
    @After
    public void after(){
        System.out.println("after");

        //删除测试数据
        discussPostService.updateStatus(data.getId(), 2);
    }

    @Test
    public void test1(){
        System.out.println("test1");
    }

    @Test
    public void test2() {
        System.out.println("test2");
    }

    /**
     * 测试 findDiscussPostById 方法
     */
    @Test
    public void testFindById(){
        DiscussPost post = discussPostService.findDiscussPostById(data.getId());

        //使用断言来判断
        Assert.assertNotNull(post);
        Assert.assertEquals(post.getUserId(), data.getUserId());
        Assert.assertEquals(post.getTitle(), data.getTitle());
        Assert.assertEquals(post.getContent(), data.getContent());
        //Assert.assertEquals(post.getCreateTime(), data.getCreateTime());
    }

    /**
     * 测试 updateScore 方法
     */
    @Test
    public void testUpdateScore(){
        int rows = discussPostService.updateScore(data.getId(), 2000.00);
        Assert.assertEquals(rows, 1);

        DiscussPost post = discussPostService.findDiscussPostById(data.getId());
        Assert.assertEquals(2000.00, post.getScore(), 2);
    }
}
