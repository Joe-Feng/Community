package com.xcf.community.service.impl;

import com.xcf.community.dao.DiscussPostMapper;
import com.xcf.community.dao.UserMapper;
import com.xcf.community.pojo.DiscussPost;
import com.xcf.community.pojo.User;
import com.xcf.community.service.IDemoService;
import com.xcf.community.service.IDiscussPostService;
import com.xcf.community.service.IUserService;
import com.xcf.community.utils.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author Joe
 * @ClassName DemoService.java
 * @Description
 * @createTime 2022年05月17日 14:20:00
 */
@Service
public class DemoServiceImpl implements IDemoService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save1(){
        //add new user
        User user = User.builder()
                .username("ww")
                .salt(CommunityUtil.generateUUID().substring(0, 5))
                .password(CommunityUtil.md5("123" + "salt"))
                .email("ww@qq.com")
                .createTime(new Date())
                .headerUrl("http://www.ww.com")
                .build();
       userMapper.insertUser(user);

        //add post
        DiscussPost post = DiscussPost.builder()
                .userId(user.getId())
                .title("新人报道")
                .content("新人报道贴")
                .createTime(new Date())
                .build();
        discussPostMapper.insertDiscussPost(post);

//        Integer.valueOf("abc");

        return "0k";
    }

    public Object save2(){
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                //add new user
                User user = User.builder()
                        .username("uu")
                        .salt(CommunityUtil.generateUUID().substring(0, 5))
                        .password(CommunityUtil.md5("123" + "salt"))
                        .email("uu@qq.com")
                        .createTime(new Date())
                        .headerUrl("http://www.uu.com")
                        .build();
                userMapper.insertUser(user);

                //add post
                DiscussPost post = DiscussPost.builder()
                        .userId(user.getId())
                        .title("新人uu报道")
                        .content("新人uu报道贴")
                        .createTime(new Date())
                        .build();
                discussPostMapper.insertDiscussPost(post);

                Integer.valueOf("abc");
                return "ok";
            }
        });
    }

}
