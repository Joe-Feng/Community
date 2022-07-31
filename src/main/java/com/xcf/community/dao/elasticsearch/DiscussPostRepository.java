package com.xcf.community.dao.elasticsearch;

import com.xcf.community.pojo.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Joe
 * @ClassName DiscussPostRepository.java
 * @Description
 * ElasticsearchRepository<DiscussPost, Integer>
 * DiscussPost：接口要处理的实体类
 * Integer：实体类中的主键是什么类型
 * ElasticsearchRepository：父接口，其中已经事先定义好了对es服务器访问的增删改查各种方法。Spring会给它自动做一个实现，我们直接去调就可以了。
 * @createTime 2022年05月27日 16:14:00
 */
@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {

}
