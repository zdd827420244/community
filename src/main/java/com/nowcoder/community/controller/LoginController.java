package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@Controller
public class LoginController implements CommunityConstant { //为了让激活返回的值我们能识别

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/register",method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }

    @RequestMapping(path = "/login",method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }


    @RequestMapping(path = "/register",method = RequestMethod.POST)
    public String register(Model model,User user) { //页面传入值的时候，值与user属性相匹配，springmvc就会自动把值注入给user对象里的属性  User已经被自动租入岛Model里了，所以在页面里可以直接通过user.来访问user里的属性
        Map<String, Object> map = userService.register(user);
        if(map==null || map.isEmpty()) {
            model.addAttribute("msg","注册成功,我们已将向您的邮箱发送一封激活邮件");
            model.addAttribute("target","/login");
            return "/site/operate-result";  //中间页面
        } else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));

            return "/site/register";
        }
    }

    //http://localhost:8080/community/activation/101(用户id)/code(激活码)
    @RequestMapping(path = "/activation/{userId}/{code}",method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId")int userId,@PathVariable("code")String code){ //从路径中取值
        int result = userService.activation(userId, code);
        if(result==ACTIVATION_SUCCESS) {
            model.addAttribute("msg","激活成功,您的账号已经可以正常使用");
            model.addAttribute("target","/login");

        }else if(result==ACTIVATION_REPEAT) {
            model.addAttribute("msg","无效操作，该账号已经激活过");
            model.addAttribute("target","/index");

        } else {
            model.addAttribute("msg","激活失败,您提供的激活码不正确");
            model.addAttribute("target","/index");

        }
        return "/site/operate-result";
    }

}
