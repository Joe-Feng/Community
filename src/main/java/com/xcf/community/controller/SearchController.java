package com.xcf.community.controller;

import com.xcf.community.pojo.DiscussPost;
import com.xcf.community.pojo.Page;
import com.xcf.community.service.IElasticSearchService;
import com.xcf.community.service.ILikeService;
import com.xcf.community.service.IUserService;
import com.xcf.community.utils.CommunityConstant;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Joe
 * @ClassName ElasticSearchController.java
 * @Description
 * @createTime 2022年05月28日 22:31:00
 */
@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    private IElasticSearchService elasticSearchService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ILikeService likeService;

    @GetMapping("/search")
    public String search(String keyword, Model model, Page page){
        //查找帖子
        List<Object> list = elasticSearchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());

        //聚合数据
        List<DiscussPost> discussPostList = (List<DiscussPost>) list.get(1);
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if(discussPostList != null){
            for (DiscussPost post: discussPostList) {
                Map<String, Object> map = new HashMap<>();
                //帖子
                map.put("post", post);
                System.out.println(post);
                //作者
                map.put("user", userService.findUserById(post.getUserId()));
                //点赞数量
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));

                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("keyword", keyword);

        //设置分页
        page.setPath("/search?keyword=" + keyword);
        long rows = (long) list.get(0);
        page.setRows(list.get(0) == null ? 0 : (int)rows);

        return "/site/search";
    }
}
