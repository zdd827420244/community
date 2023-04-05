package com.nowcoder.community.dao;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketMapper {
    //实现方法，之前都是在mapper目录下新建一个xml文件写SQL
    //其实还有一种方式，就是Mapper接口当中写注解，通过注解声明方法对应的SQ


    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"        //values里面是表达式，引用的是loginTicket对象中的属性
    })//把里面多个字符串拼成一个SQL  SQL可以拆成多部分去写,它会帮你拼
    //希望id是自动生成的，所以需要声明一下,同时还要声明自动生成的值回填给对象的哪个属性  xml中也声明了  声明SQL的一些机制需要用到@Options注解
    @Options(useGeneratedKeys = true,keyProperty = "id")  //application.properties中声明的自动生成id，对注解形式的SQL是无效的，所以这里要用@optiton的方式声明
    int insertLoginTicket(LoginTicket loginTicket);

    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    @Update({  //注解里写SQL其实也支持动态SQL，比如用到了if  //script表示脚本 srcipt标签里面就可以加if标签
            "<script>",
            "update login_ticket set status=#{status} where ticket=#{ticket} ",
            "<if test=\"ticket!=null\">",  //这里双引号要加上转义  这个if和xml语法是一模一样的，要想写if，前面必须套上script标签
            "and 1=1",
            "</if>",
            "</script>"
    })
    int updateStatus(String ticket,int status);


}
