package com.xcf.community.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.xcf.community.dao.elasticsearch.DiscussPostRepository;
import com.xcf.community.pojo.DiscussPost;
import com.xcf.community.service.IElasticSearchService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Joe
 * @ClassName ElasticSearchServiceImpl.java
 * @Description
 * @createTime 2022年05月28日 20:45:00
 */
@Service
@Slf4j
public class ElasticSearchServiceImpl implements IElasticSearchService {

    @Autowired
    private DiscussPostRepository discussPostRepository;


    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public void saveDiscussPost(DiscussPost post) {
        discussPostRepository.save(post);
    }

    @Override
    public void deleteDiscussPost(int id) {
        discussPostRepository.deleteById(id);
    }

    /**
     * 搜索
     * @param keyword 搜索关键词
     * @param current 分页起始条数
     * @param limit 每页数量
     * @return 分页数据+搜索到得到的的总数量
     */
    @Override
    public List<Object> searchDiscussPost(String keyword, int current, int limit) {
        SearchRequest searchRequest = new SearchRequest("discusspost");//discusspost是索引名，就是表名

        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.field("content");
        highlightBuilder.requireFieldMatch(false);
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");

        //构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.multiMatchQuery(keyword, "title", "content"))
                .sort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .from(current)// 指定从哪条开始查询
                .size(limit)// 需要查出的总记录条数
                .highlighter(highlightBuilder);//高亮

        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("elasticsearch 出错：" + e.getMessage());
        }

        List<DiscussPost> list = new ArrayList<>();
        for (org.elasticsearch.search.SearchHit hit : searchResponse.getHits().getHits()) {
            DiscussPost discussPost = JSONObject.parseObject(hit.getSourceAsString(), DiscussPost.class);

            // 处理高亮显示的结果
            HighlightField titleField = hit.getHighlightFields().get("title");
            if (titleField != null) {
                discussPost.setTitle(titleField.getFragments()[0].toString());
            }
            HighlightField contentField = hit.getHighlightFields().get("content");
            if (contentField != null) {
                discussPost.setContent(contentField.getFragments()[0].toString());
            }
            list.add(discussPost);
        }
        List<Object> res = new ArrayList<>();
        //插入查到的匹配项条数
        res.add(searchResponse.getHits().getTotalHits().value);
        //插入一页的数据
        res.add(list);
        return res;
    }
}
