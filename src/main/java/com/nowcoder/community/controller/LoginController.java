package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.RedisKeyUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController implements CommunityConstant { //为了让激活返回的值我们能识别
    private static final Logger logger= LoggerFactory.getLogger(LoginController.class);


    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @RequestMapping(path = "/register",method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }

    @RequestMapping(path = "/login",method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }  //会给浏览器返回一个html页面，这个页面里有一个图片路径，浏览器又会给服务器再次发一个请求，获得这张图片  所以我们要单独写一个请求，向浏览器返回图片


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

    @RequestMapping(path = "/kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response/*, HttpSession session*/) {//向浏览器输出的是一个图片，不是字符串也不是html网页  我们需要自己用response对象手动向浏览器输出  生成验证码后，服务端要记住，不能存到浏览器端  并且这次请求要生成，下次登录请求要访问，所以跨请求了  用session
        //生成验证码
        String text=kaptchaProducer.createText();//生成字符串
        BufferedImage image = kaptchaProducer.createImage(text);

        //验证码存入session
        //session.setAttribute("kaptcha",text);

        //验证码的归属
        String kaptchaOwner= CommunityUtil.generateUUID();
        Cookie cookie=new Cookie("kaptchaOwner",kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);
        //将验证码存入reids
        String redisKey= RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey,text,60, TimeUnit.SECONDS);//超过60秒就失效了



        //图片输出给浏览器
        response.setContentType("image/png"); //声明给浏览器返回什么类型的数据
        try {
            OutputStream os = response.getOutputStream();//字节流
            ImageIO.write(image,"png",os); //向浏览器输出图片的工具
            //response是由springmvc维护的，自动会关，所以我们这里可以不关
        } catch (IOException e) {
            logger.error("响应验证码失败："+e.getMessage());
        }
        //原来把验证码存到了session中，现在我们要重构，存到redis中  不同浏览器，对应验证码是不同的，但我们目前不能用userid来识别，因为目前还是未登录状态，所以我们就通过cookie给浏览器发放一个随机码owner，通过这个归属来判断该浏览器对应的验证码是哪一个
    }

    @RequestMapping(path = "/login",method = RequestMethod.POST) //请求方式和上面那个不一样，所以path可以重复
    public String login(String username,String password, String code, boolean rememberme,Model model,
                        /*HttpSession session,*/HttpServletResponse response,@CookieValue("kaptchaOwner") String kaptchaOwner) { //如果不是普通的参数，而是实体，比如User，springmvc会自动把user装配到model，我们就可以在页面直接得到user里的数据
        //但是如果是普通的参数，基本类型，int String boolean等，springmvc不会把这些参数装进model，那怎么在页面中获取呢：法1，手动装入model里 法2，这些值都是存在于在request对象中的，而我们执行到页面时，这个request实际上还没有结束，所以依然能用request访问
        //检查验证码
        //String kaptcha=(String)session.getAttribute("kaptcha");  //返回Object  需要强制转换
        String kaptcha=null;
        if(StringUtils.isNotBlank(kaptchaOwner)) {
            String redisKey=RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha=(String)redisTemplate.opsForValue().get(redisKey);
        }
        if(StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)) { //equalsIgnoreCase和equals相等的逻辑是一样的，不过它忽略大小写
            model.addAttribute("codeMsg", "验证码不正确");
            return "/site/login";
        }

        //检查账号,密码
        int expiredSeconds=rememberme?REMEMBER_EXPIRED_SECONDS: DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if(map.containsKey("ticket")) {
            Cookie cookie=new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index"; //重定向到首页
        }else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));


            return "/site/login";
        }
    }

    @RequestMapping(path = "/logout",method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket){  //浏览器已经存了cookie，可以通过CookieValue注解来让springmvc自动把cookie中的值传入进来
        userService.logout(ticket);
        return "redirect:/login"; //默认是get请求
    }

}
