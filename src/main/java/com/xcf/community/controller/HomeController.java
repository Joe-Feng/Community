package com.xcf.community.controller;

import com.xcf.community.pojo.DiscussPost;
import com.xcf.community.pojo.Page;
import com.xcf.community.pojo.User;
import com.xcf.community.service.IDiscussPostService;
import com.xcf.community.service.ILikeService;
import com.xcf.community.service.IUserService;
import com.xcf.community.utils.CommunityConstant;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Joe
 * @version 1.0.0
 * @ClassName HomeController.java
 * @Description
 * @createTime 2022年05月10日 20:35:00
 */
@Controller
public class HomeController implements CommunityConstant {
    @Autowired
    private IDiscussPostService discussPostService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ILikeService likeService;

    @GetMapping("/")
    public String root(){
        return "forward:/index";
    }

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page,
                               @RequestParam(name = "orderMode", defaultValue = "0") int orderMode){
        //方法调用前。SpringMVC会自动实例化Model和Page，并将Page注入到Model
        //所以，在thymeleaf中可以直接访问Page对象中的数据
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index?orderMode=" + orderMode);

        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit(), orderMode);
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                User user = userService.findUserById(post.getUserId());
                map.put("user", user);

                //显示点赞
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);

                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("orderMode", orderMode);


        return "/index";
    }

    @GetMapping(path = "/error")
    public String getErrorPage(){
        return "/error/500";
    }

    //没有访问权限而被拒绝时的提示页面
    @GetMapping("/denied")
    public String getDeniedPage(){
        return "/error/404";
    }
}
