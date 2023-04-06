package com.nowcoder.community.service;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    private SensitiveFilter sensitiveFilter;

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

    public int addDiscussPost(DiscussPost discussPost) {
        if(discussPost==null){
            throw new IllegalArgumentException("参数不能为空");
        }
        //用户写入数据是，他可能写入了一些标签，比如<script>xxx</script>，那它显示在网页中，会显示出标签的特点  那如果里面注入了一些对页面有损害的东西，就会对页面有影响
        //我们只希望浏览器认为这<script>xxx</script>只是一段文字，而不认为这是一个标签
        //转义HTML标记
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));//HtmlUtils.htmlEscape能把里面的小于号大于号转义成转义字符
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));
        //过滤敏感词
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));

        return discussPostMapper.insertDiscussPost(discussPost);
    }

    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    public int updateCommentCount(int id,int commentCount){
        return  discussPostMapper.updateCommentCount(id,commentCount);
    }

}
