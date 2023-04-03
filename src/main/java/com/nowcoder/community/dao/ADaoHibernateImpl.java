package com.nowcoder.community.dao;

import org.springframework.stereotype.Repository;

@Repository("AHibernate") //默认名字是类的名字，首字母小写，如果觉得这个类名太长，也可以自定义bean的名字  就是在注释后面加个括号，里面写上字符串，就是类的名字
public class ADaoHibernateImpl implements ADao{
    @Override
    public String select() {
        return "H";
    }
}
