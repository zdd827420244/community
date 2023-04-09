package com.nowcoder.community.service;

import com.nowcoder.community.dao.ADao;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;

@Service
@Scope("prototype")//如果不想单例，想每次getBean都新建一个实例  加个注解@Scope，也就是作用范围，整个容器有一个还是有多个  默认参数singleton，单例
//多例 prototype，这时每次访问Bean，都会生成一个新的实例
public class AService {

    @Autowired
    private ADao aDao;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    public AService(){
        System.out.println("实例化AService");

    }
    @PostConstruct  //要想让容器帮你管理这个方法（实际上就是让容器在适当的时候调用这个方法），要加上这个注解.注解表明这个方法会在构造器之后调用 而初始化方法就是在构造后调用进行赋值，所以这就是初始化
    public void init() {
        System.out.println("初始化AService");
    }


    @PreDestroy //注解表明在销毁对象之前去调用这个方法
    public void destory(){
        System.out.println("销毁AService");
    }

    public String find(){
        return aDao.select();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)    //事务注解  isolation指定隔离级别  Propagation传播机制
    //业务A可能会调用业务B，A和B可能都加上事务注解，那此时B的事务应该以谁为准  事务传播机制就是解决这种问题
    //REQUIRED：支持当前事务（外部事物），A调B，对于B来说，A就是当前事务，也就是调用它的那个事务  如果外部事务不存在，则创建新事务
    //REQUIRES_NEW：创建新事务，暂停当前事务（外部事务），B无视A的事务
    //NESTED：如果当前存在外部事务，则嵌套在该事务中执行（B在A事务中执行，但是B有独立的提交和回滚）  否则就和REQUIRED一样
    public Object save1() {
        //新增用户
        User user =new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5("123"+user.getSalt()));
        user.setEmail("alpha@qq.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //新增帖子
        DiscussPost post=new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("hello");
        post.setContent("新人报道");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);

        Integer.valueOf("abc"); //故意写一个错误，看是否会回滚

        return "ok";
    }
    public Object save2(){
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        return transactionTemplate.execute(new TransactionCallback<Object>() { //方法内部要传入一个接口实例，我们可以做匿名实现
            @Override
            public Object doInTransaction(TransactionStatus status) {
                User user =new User();
                user.setUsername("beta");
                user.setSalt(CommunityUtil.generateUUID().substring(0,5));
                user.setPassword(CommunityUtil.md5("123"+user.getSalt()));
                user.setEmail("beta@qq.com");
                user.setHeaderUrl("http://image.nowcoder.com/head/999t.png");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);

                //新增帖子
                DiscussPost post=new DiscussPost();
                post.setUserId(user.getId());
                post.setTitle("你好");
                post.setContent("woshi xinren");
                post.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(post);
                Integer.valueOf("abc"); //故意写一个错误，看是否会回滚
                return "ok";
            }
        });

    }







}
