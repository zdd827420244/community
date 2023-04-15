package com.nowcoder.community.dao.elasticsearch;

import com.nowcoder.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

//@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> { //泛型，声明我们要处理的实体类是DiscussPost，里面的主键类型是Integer
    //不需要实现任何东西，只需要继承spring提供的ElasticsearchRepository接口就行

}
