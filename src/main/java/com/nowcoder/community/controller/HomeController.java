package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    //controller可以不加访问路径，这样这一级就是空的，直接访问方法就行
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page){
        //SpringMVC中方法的参数，都是由DS帮我们自动初始化，DS除了帮我们初始化model，page，它还会自动帮我们把page注入到model里
        //所以，thymeleaf中可以直接访问Page对象中的数据  不用再Model.addAttribute(Page)了
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");


        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());//不完整，因为DiscussPost里面只有userId,但是我们显示需要username
        //遍历DiscussPost集合，查询userId对应user，把数据组装一下，放到一个新的集合，返回给页面 新集合里面的对象，得能封装DiscussPost对象和User对象  Map可以，新创一个类也行
        List<Map<String,Object>> discussPosts=new ArrayList<>();
        if(list!=null) {
            for(DiscussPost post:list) {
                Map<String,Object> map=new HashMap<>();
                map.put("post",post);
                User user = userService.findUserById(post.getUserId());
                map.put("user",user);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);//传入model页面才能收到
        return "/index";//返回模板路径  是templates路径下的

    }
    @RequestMapping(path = "/error", method = RequestMethod.GET)
    public String getErrorPage() {
        return "/error/500";
    }
}
