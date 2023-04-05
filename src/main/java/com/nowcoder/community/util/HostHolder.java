package com.nowcoder.community.util;


import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;

//起到一个容器的作用，持有用户信息，用于代替session对象   session可以直接持有数据，并且是线程隔离的
@Component
public class HostHolder {
    //ThreadLocal的方法是以线程为key存值
    private ThreadLocal<User> users=new ThreadLocal<User>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }





}
