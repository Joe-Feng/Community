package com.xcf.community.mapperTest;

import com.xcf.community.CommunityApplication;
import com.xcf.community.dao.CommentMapper;
import com.xcf.community.pojo.Comment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author Joe
 * @ClassName CommentMapperTest.java
 * @Description
 * @createTime 2022年05月17日 17:07:00
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
@SpringBootTest
public class CommentMapperTest {
    @Autowired
    private CommentMapper commentMapper;

    @Test
    public void test(){
        List<Comment> comments = commentMapper.selectCommentsByEntity(1, 228, 0, 5);
        for (Comment comment : comments){
            System.out.println(comment);
        }

        int count = commentMapper.selectCountByEntity(1, 228);
        System.out.println(count);
    }
}
