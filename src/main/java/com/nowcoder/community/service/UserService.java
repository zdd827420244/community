package com.nowcoder.community.service;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;

import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import com.nowcoder.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements CommunityConstant {//处理激活逻辑时需要用到激活状态常量，所以我们让这个类实现CommunityConstant接口
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

   /* @Autowired
    private LoginTicketMapper loginTicketMapper;*/

    @Autowired
    private RedisTemplate redisTemplate;

    //发送的邮件里的激活码需要包含域名和项目名，需要注入进来
    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;  //项目名 或 应用路径

    public User findUserById(int id) {
        //return userMapper.selectById(id);
        User user = getCache(id);
        if (user == null) {
            user = initCache(id);
        }
        return user;
    }

    public Map<String,Object> register(User user){  //希望返回多个错误信息，比如账号重复了 账号不能为空  密码能为空之类的
        Map<String,Object> map=new HashMap<>();
        //对空值判断处理
        if(user==null) {
            throw new IllegalArgumentException("参数不能为空！");//非法参数
        }
        if(StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg","账号不能为空!");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg","密码不能为空!");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg","邮箱不能为空!");
            return map;
        }
        //验证账号
        User u = userMapper.selectByName(user.getUsername());
        if(u!=null) {
            map.put("usernameMsg","该账号已存在！");
            return map;
        }
        //验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if(u!=null) {
            map.put("emailMsg","该邮箱已被注册！");
            return map;
        }

        //注册用户 其实就是把用户信息存到库里
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user); //这个语句之前用户还没有id  这句话之后，mybatis自动获取到了id，对他进行了回填

        //给用户发一个激活邮件
        Context context=new Context();
        context.setVariable("email",user.getEmail());
        //http://localhost:8080/community/activation/101(用户id)/code(激活码)
        String url=domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url",url);
        String content=templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);

        return map;
    }
    public int activation(int userId,String code){
        User user=userMapper.selectById(userId);
        if(user.getStatus()==1) {
            return ACTIVATION_REPEAT;
        } else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId,1);
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    public Map<String,Object> login(String username, String password, long expiredSeconds) {
        Map<String,Object> map=new HashMap<>();
        //空值处理
        if(StringUtils.isBlank(username)) {
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)) {
            map.put("passwordMsg","密码不能为空");
            return map;
        }

        //验证账号
        User user=userMapper.selectByName(username);
        if(user==null) {
            map.put("usernameMsg","该账号不存在");
            return map;
        }
        //验证状态
        if(user.getStatus()==0) {
            map.put("usernameMsg","该账号未激活");
            return map;
        }

        //验证密码
        password=CommunityUtil.md5(password+user.getSalt());
        if(!user.getPassword().equals(password)) {
            map.put("passwordMsg","密码不正确");
            return map;
        }

        //生成登录凭证
        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSeconds *1000));
//        loginTicketMapper.insertLoginTicket(loginTicket);
        String redisKey= RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey,loginTicket);  //redis会把loginTicket这个对象自动序列化成一个json字符串

        map.put("ticket",loginTicket.getTicket());
        return map;
    }
    public void logout(String ticket){
        //loginTicketMapper.updateStatus(ticket,1);
        String redisKey= RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket=(LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey,loginTicket);
    }

    public LoginTicket findLoginTicket(String ticket) {
//        return loginTicketMapper.selectByTicket(ticket);
        String redisKey= RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    public int updateHeader(int userId,String headerUrl){
 //       return userMapper.updateHeader(userId,headerUrl);
        //访问MYSQL和访问Redis没有办法把他们放到一个事务内，他们之间的事务是分开的
        // 删除redis数据如果放在更新MySQL之前，万一更新MySQL失败，缓存白白删除掉了  还是换一种方式，先更新SQL，更新失败，缓存也不用去删除了
        int rows = userMapper.updateHeader(userId,headerUrl);
        clearCache(userId);
        return rows;
    }
    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }
//用redis缓存用户数据，也就是缓存User，其实最主要的是重构findUserById方法
    //1.查询时优先从缓存中取值
    private User getCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }
    //2.取不到时初始化缓存数据
    private User initCache(int userId) {
        User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }
    //3.数据变更时清除缓存数据，这样查的时候直接从数据库查新数据，避免并发一致性问题
    private void clearCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }

    /*public Collection<? extends GrantedAuthority> getAuthorities(int userId) {
        User user = this.findUserById(userId);

        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {
                switch (user.getType()) {
                    case 1:
                        return AUTHORITY_ADMIN;
                    case 2:
                        return AUTHORITY_MODERATOR;
                    default:
                        return AUTHORITY_USER;
                }
            }
        });
        return list;
    }*/

}
