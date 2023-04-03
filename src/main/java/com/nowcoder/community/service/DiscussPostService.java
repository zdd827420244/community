package com.nowcoder.community.service;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
        return discussPostMapper.selectDiscussPosts(userId,offset,limit);
    }

    public int findDiscussPostRows(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }
    //显示查询结果时，在页面显示外键userId,但这个肯定不是我们想要的，我们想看到用户名
    //解决方法 1.在SQL语句中就关联user表，然后查到用户的数据username  方法2.得到数据后，单独针对每一个DiscussPost，单独查一下user，然后把查到的user和DiscussPost组合在一起
    //法2更好一点  和Redis缓存更适配，性能更高，使用也更方便
    //那你本质实际上是通过userId来查对应user，这个肯定不应该写在DiscussPostService下，应该写在UserService下



}
