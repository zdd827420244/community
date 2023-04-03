package com.nowcoder.community.dao;

import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper//要表明这个Bean用@Repository当然行，但是MyBatis有一个专门的注解
public interface UserMapper {
    //在Mapper声明一些增删改查的方法，再写上对应SQL配置文件
    User selectById(int id);
    User selectByName(String username);
    User selectByEmail(String email);

    int insertUser(User user);//返回插入数据的行数  插入就1行
    int updateStatus(int id,int status);//修改状态，返回修改的行数  条件是id，status是要修改成的新status
    int updateHeader(int id,String headerUrl);
    int updatePassword(int id,String password);
    //要想实现这些方法，需要提供一个配置文件，配置文件里需要给每一个方法提供它所需要的SQL，这样Mybatis底层会自动帮我们生成实现类

}
