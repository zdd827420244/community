package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

//一张表配一个MAPPER
@Mapper
public interface DiscussPostMapper {
    //分页查询帖子 所以一定是多条数据
    List<DiscussPost> selectDiscussPosts(int userId, int offset,int limit);//首页查询时，不需要传入userId，而某些功能，比如要查询我的帖子时，要把我的userId传入，所以这个SQL是个动态的SQL
    //有时候要userId拼到SQL里，有时不要 可以设定，userId为0，就代表首页查询，查所有帖子  为其他数，就是查那个userId发言的帖子  offset每一页起始行号  limit每一页最多显示多少条数据

    int selectDiscussPostRows(@Param("userId") int userId);  //查询帖子数量   在写任何方法时，可以在参数前加入一个注解,意思是给这个参数起一个别名
    //如果在SQL里需要用到动态参数/条件，而恰巧这个方法有且只有一个参数，那么这个参数就必须取别名（否则会报错）  动态SQL是用<if>标签来拼的，所以动态参数就是在<if>里使用了这个参数

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(int id);

    int updateCommentCount(int id,int commentCount);


}
