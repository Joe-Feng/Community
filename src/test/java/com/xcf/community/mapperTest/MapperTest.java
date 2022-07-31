package com.xcf.community.mapperTest;

import com.xcf.community.CommunityApplication;
import com.xcf.community.dao.DiscussPostMapper;
import com.xcf.community.dao.MessageMapper;
import com.xcf.community.dao.UserMapper;
import com.xcf.community.pojo.DiscussPost;
import com.xcf.community.pojo.Message;
import com.xcf.community.pojo.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author Joe
 * @version 1.0.0
 * @ClassName UserTest.java
 * @Description
 * @createTime 2022年05月09日 20:42:00
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
@SpringBootTest
public class MapperTest {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private MessageMapper messageMapper;


    @Test
    public void insertTest(){
        User user = new User();
        user.setUsername("test1");
        user.setPassword("123454");
        user.setSalt("1111");
        user.setEmail("123@qq.com");
        user.setHeaderUrl("http://www.mewCode.com/101.png");
        user.setActivationCode("8787");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void selectByIdTest(){
        User user = userMapper.selectById(101);
        System.out.println(user);
    }

    @Test
    public void selectByNameTest(){
        User user = userMapper.selectByName("test1");
        System.out.println(user);
    }

    @Test
    public void updatePasswordTest(){
        int  rows = userMapper.updatePassword(101, "34567");
        User user = userMapper.selectById(101);
        System.out.println(rows);
        System.out.println(user.getPassword());
    }

    @Test
    public void builderTest(){
        User user = User.builder()
                .username("wwww")
                .build();
        System.out.println(user);
    }

    @Test
    public void testDiscussPosts(){
        //List<DiscussPost> posts =  discussPostMapper.selectDiscussPosts(149, 0, 2);
        //for(DiscussPost discussPost : posts){
        //    System.out.println(discussPost);
        //}
        //
        //int rows = discussPostMapper.selectDiscussPostRows(0);
        //System.out.println(rows);

        //DiscussPost post = DiscussPost.builder()
        //        .userId(110)
        //        .title("一拳超人")
        //        .content("我是路飞")
        //        .commentCount(11)
        //        .createTime(new Date())
        //        .score(23.4)
        //        .status(1)
        //        .type(0)
        //        .build();
        //int row = discussPostMapper.insertDiscussPost(post);
        //System.out.println("insert : " + row);
        discussPostMapper.updateScore(295, 1677.2);
    }

    @Test
    public void testMessage(){
        List<Message> messages = messageMapper.selectConversion(111, 0, 100);
        for(Message message : messages){
            System.out.println(message);
        }

        int count = messageMapper.selectConversationCount(111);
        System.out.println(count);

        List<Message> letters = messageMapper.selectLetters("111_112", 0, 10);
        for(Message letter : letters){
            System.out.println(letter);
        }

        count = messageMapper.selectLetterCount("111_145");
        System.out.println("selectLetterCount : " + count);

        count = messageMapper.selectLetterUnReadCount(111, "111_145");
        System.out.println("selectLetterUnReadCount : " + count);
    }

    @Test
    public void testInsertMessage(){
        Message message = Message.builder()
                .fromId(111)
                .toId(112)
                .conversationId("111_112")
                .content("测试")
                .status(0)
                .createTime(new Date())
                .build();
        messageMapper.insertMessage(message);
    }
}
