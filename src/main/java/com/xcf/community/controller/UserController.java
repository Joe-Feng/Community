package com.xcf.community.controller;

import com.xcf.community.annotation.LoginRequired;
import com.xcf.community.pojo.User;
import com.xcf.community.service.IFileUploadService;
import com.xcf.community.service.IFollowService;
import com.xcf.community.service.ILikeService;
import com.xcf.community.service.IUserService;
import com.xcf.community.utils.CommunityConstant;
import com.xcf.community.utils.CommunityUtil;
import com.xcf.community.utils.HostHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Paths;

/**
 * @author Joe
 * @ClassName UserController.java
 * @Description
 * @createTime 2022年05月14日 16:23:00
 */
@Controller
@RequestMapping("/user")
@Slf4j
public class UserController implements CommunityConstant {
    @Value("${community.path.domain}")
    private String domain;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private IUserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private ILikeService likeService;

    @Autowired
    private IFollowService followService;

    @Autowired
    private IFileUploadService fileUploadService;

    @LoginRequired
    @GetMapping("/setting")
    public String getSetting(){
        return "/site/setting";
    }

    @LoginRequired
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage == null){
            model.addAttribute("error", "您还没有选择图片!");
            return "/site/setting";
        }

        //获取文件名
        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        if(suffix == null){
            model.addAttribute("error", "文件格式不正确!");
            return "/site/setting";
        }
        //生成随机文件名
        //filename = CommunityUtil.generateUUID() + suffix;
        //确定文件存放路径
        //File dest = new File(uploadPath + "/" + filename);
        //try {
        //    //存储文件
        //    headerImage.transferTo(dest);
        //} catch (IOException e) {
        //    log.error("文件上传失败，服务器发生错误！" + e.getMessage());
        //    throw new RuntimeException("上传文件失败，，服务器发生错误", e);
        //}
        //上传文件到 aliyun-oos
        String headerUrl = fileUploadService.upload(headerImage);

        //更新当前用户头像路径（web访问路径）
        //http://localhost:8088/community/user/header/xxx.png
        User user = hostHolder.getUser();
        //String headerUrl = domain + contextPath + "/user/header/" + filename;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }

    //@GetMapping("/header/{filename}")
    //public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response){
    //    filename = uploadPath + "/" + filename;
    //    String suffix = filename.substring(filename.lastIndexOf(".") + 1);
    //    response.setContentType("/image" + suffix);
    //    try(
    //            FileInputStream fis = new FileInputStream(filename);
    //            OutputStream os = response.getOutputStream();
    //    ) {
    //        byte[] buffer = new byte[1024];
    //        int b = 0;
    //        while ((b = fis.read(buffer)) != -1){
    //            os.write(buffer, 0, b);
    //        }
    //
    //    } catch (IOException e) {
    //        log.error("读取头像失败" + e.getMessage());
    //    }
    //}

    @GetMapping("/header/{filename}")
    public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response){
            fileUploadService.getImage(filename, response);
    }



    @LoginRequired
    @PostMapping("/changePassword")
    public String changePassword(String oldPassword, String newPassword, Model model){
        User user = hostHolder.getUser();
        if(user.getPassword().equals(CommunityUtil.md5(oldPassword + user.getSalt()))){
            //新密码和原始密码不能相同
            if(oldPassword.equals(newPassword)){
                model.addAttribute("newMsg", "新密码和原始密码相同，请重新输入！");
                return "/site/setting";
            }
            //更新密码
            userService.updatePassword(user, newPassword);
        }else{
            model.addAttribute("oldMsg", "输入的原始密码不正确!");
            return "/site/setting";
        }
        return "redirect:/logout";
    }

    @GetMapping(path = "/profile/{userId}")
    public String getProfilePage(@PathVariable("userId") int userId, Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在！");
        }

        //用户
        model.addAttribute("user", user);
        //用户点赞数
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);

        //关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        //粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
        //是否已关注该用户
        boolean hasFollowed = false;
        if(hostHolder.getUser() != null){
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);

        return "/site/profile";
    }
}
