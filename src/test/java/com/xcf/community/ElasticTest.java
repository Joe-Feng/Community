package com.xcf.community;

import com.alibaba.fastjson.JSONObject;
import com.xcf.community.dao.DiscussPostMapper;
import com.xcf.community.dao.elasticsearch.DiscussPostRepository;
import com.xcf.community.pojo.DiscussPost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * @author Joe
 * @ClassName ElasticTest.java
 * @Description
 * @createTime 2022年05月27日 16:15:00
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
@SpringBootTest
public class ElasticTest {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private DiscussPostRepository discussPostRepository;


    /**
     * 有些情况 DiscussPostRepository 处理不了
     * 所以需要额外的 ElasticsearchRestTemplate
     */
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 创建/更新索引
     */
    @Test
    public void testCreateIndex() {

        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(DiscussPost.class);
        Document document = indexOperations.createMapping(DiscussPost.class);
        //boolean bool = indexOperations.create(document);
        boolean bool = indexOperations.putMapping(document);
        System.out.println(bool?"成功":"失败");
    }


    @Test
    public void testInsert(){
        DiscussPost discussPost = discussPostMapper.selectDiscussPostById(288);
        System.out.println(discussPost.getCreateTime());
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(291));
    }

    @Test
    public void testInsertAll(){
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(101, 0, 100, 0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(102, 0, 100, 0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(103, 0, 100, 0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(111, 0, 100, 0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(112, 0, 100, 0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(113, 0, 100, 0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(114, 0, 100, 0));
    }

    @Test
    public void testUpadate(){
        DiscussPost post = discussPostMapper.selectDiscussPostById(231);
        post.setContent("就是喜欢灌水，哈哈哈！");
        discussPostRepository.save(post);
    }

    @Test
    public void testDelete(){
        //discussPostRepository.deleteById(231);
        discussPostRepository.deleteAll();
    }



    //不带高亮的查询
    @Test
    public void noHighlightQuery() throws IOException {
        SearchRequest searchRequest = new SearchRequest("discusspost");//discusspost是索引名，就是表名

        //构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                //在discusspost索引的title和content字段中都查询“互联网寒冬”
                .query(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                // matchQuery是模糊查询，会对key进行分词：searchSourceBuilder.query(QueryBuilders.matchQuery(key,value));
                // termQuery是精准查询：searchSourceBuilder.query(QueryBuilders.termQuery(key,value));
                .sort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                //一个可选项，用于控制允许搜索的时间：searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
                .from(0)// 指定从哪条开始查询
                .size(10);// 需要查出的总记录条数

        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        System.out.println(JSONObject.toJSON(searchResponse));

        List<DiscussPost> list = new ArrayList<>();
        for (org.elasticsearch.search.SearchHit hit : searchResponse.getHits().getHits()) {
            DiscussPost discussPost = JSONObject.parseObject(hit.getSourceAsString(), DiscussPost.class);
            System.out.println(discussPost);
            list.add(discussPost);
        }
    }

    //带高亮的查询
    @Test
    public void highlightQuery() throws Exception{
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
                .query(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .sort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .from(0)// 指定从哪条开始查询
                .size(10)// 需要查出的总记录条数
                .highlighter(highlightBuilder);//高亮

        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        //要访问返回的文档，首先要在响应中获取其中的 SearchHits
        System.out.println("------------------");
        System.out.println(searchResponse.getHits().getTotalHits()); //查询命中数量：long searchResponse.getHits().getTotalHits()
        System.out.println(searchResponse.getHits().getHits().length); //查询每页信息：SearchHit[] searchResponse.getHits().getHits()

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
            System.out.println(discussPost);
            list.add(discussPost);
        }
        System.out.println(list.size());
    }

    /**
     * TODO 出错：org.springframework.data.elasticsearch.core.convert.ConversionException:
     * Unable to convert value '2019-04-24' to java.util.Date for property 'createTime'
     */
    @Test
    public void testSearchByTemplate() {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("路飞", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))   // 默认按匹配度排序
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();
        //elasticsearchTemplate.queryForPage()
        //elasticsearchRestTemplate.queryForPage(searchQuery, DiscussPost.class, new SearchRe)

        SearchHits<DiscussPost> search = elasticsearchRestTemplate.search(searchQuery, DiscussPost.class);
        // 得到查询结果返回的内容
        List<SearchHit<DiscussPost>> searchHits = search.getSearchHits();
        // 设置一个需要返回的实体类集合
        List<DiscussPost> discussPosts = new ArrayList<>();
        // 遍历返回的内容进行处理
        for(SearchHit<DiscussPost> searchHit : searchHits){
            // 高亮的内容
            Map<String, List<String>> highLightFields = searchHit.getHighlightFields();
            // 将高亮的内容填充到content中
            searchHit.getContent().setTitle(highLightFields.get("title") == null ? searchHit.getContent().getTitle() : highLightFields.get("title").get(0));
            searchHit.getContent().setContent(highLightFields.get("content") == null ? searchHit.getContent().getContent() : highLightFields.get("content").get(0));
            // 放到实体类中
            discussPosts.add(searchHit.getContent());
        }
        System.out.println(discussPosts.size());
        for(DiscussPost discussPost : discussPosts){
            System.out.println(discussPost);
        }
        //List<Object> res = new ArrayList<>();
        //res.add((int)search.getTotalHits());
        //res.add(discussPosts);
        //return res;
    }
}
