package com.nowcoder.community.dao.elasticsearch;

import com.nowcoder.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> { //泛型，声明我们要处理的实体类是DiscussPost，里面的主键类型是Integer
    //不需要实现任何东西，只需要继承spring提供的ElasticsearchRepository接口就行
    //父接口ElasticsearchRepository里面已经事先定义了对ES服务器的增删改查各种功能，声明@Repository注解后，spring会自动给他实现

}
