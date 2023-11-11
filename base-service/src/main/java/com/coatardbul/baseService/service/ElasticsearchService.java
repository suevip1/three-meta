package com.coatardbul.baseService.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coatardbul.baseCommon.util.JsonUtil;
import com.coatardbul.baseCommon.util.ReflexUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhuyq
 */
@Slf4j
@Service
public class ElasticsearchService<T> {

    private static final String JSON_CONVERT_ERR_DESC = "json转换异常";


    private static final long ALIVE_TIME = 60 * 1000;

    private static final int DEFAULT_BATCH_NUM = 100;

    public static final String DESC = "desc";
    public static final String ASC = "asc";

    @Autowired
    RestHighLevelClient restHighLevelClient;

    private BoolQueryBuilder boolQueryBuilder;

    public BoolQueryBuilder initBoolQueryBuilder() {
        boolQueryBuilder = new BoolQueryBuilder();
        return boolQueryBuilder;
    }

    /**
     * 判断索引是否存在
     */
    public boolean checkIndexExist(String index) {
        try {
            return restHighLevelClient.indices().exists(new GetIndexRequest(index), RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Boolean.FALSE;
    }

    public void indexCreate(String indexName) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(indexName);
        request.settings(Settings.builder()
                .put("index.number_of_shards", 5)
                .put("index.number_of_replicas", 0)
                .put("index.lifecycle.name", "ilm-history-ilm-policy")

        );
        restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
    }

    /**
     * 删除索引库
     */
    public boolean indexDelete(String indexName) throws Exception {
        IndicesClient indicesClient = restHighLevelClient.indices();
        // 创建delete请求方式
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(indexName);
        // 发送delete请求
        AcknowledgedResponse response = indicesClient.delete(deleteIndexRequest, RequestOptions.DEFAULT);

        return response.isAcknowledged();
    }

    /**
     * 插入数据
     *
     * @param indexName
     * @param t
     * @param id
     * @throws IOException
     */
    public void insertData(String indexName, T t, String id) throws IOException {
        IndexRequest request = new IndexRequest(indexName);
        request.id(id);
        request.source(JSONObject.toJSONString(t), XContentType.JSON);
        IndexResponse indexResponse = restHighLevelClient.index(request, RequestOptions.DEFAULT);
        System.out.println(indexResponse.getResult());
    }

    /**
     * 集合数据全部插入
     *
     * @param indexName
     * @param list
     * @param idName
     * @throws IOException
     */
    public boolean singleBatchInsertData(String indexName, List<T> list, String idName) throws IOException {
        if (list == null || list.size() == 0) {
            return true;
        }
        BulkRequest bulk = new BulkRequest(indexName);

        for (T t : list) {
            IndexRequest indexRequest = new IndexRequest();
            try {
                Object o = ReflexUtil.readValueByName(idName, t);
                indexRequest.id(o.toString());
            } catch (IllegalAccessException e) {
                continue;
            }
            indexRequest.source(JSON.toJSONString(t), XContentType.JSON);
            bulk.add(indexRequest);
        }
        BulkResponse bulkResponse = restHighLevelClient.bulk(bulk, RequestOptions.DEFAULT);
        return bulkResponse.hasFailures();
    }

    public void singleAsyncBatchInsertData(String indexName, List<T> list, String idName) throws IOException {
        if (list == null || list.size() == 0) {
            return;
        }
        BulkRequest bulk = new BulkRequest(indexName);

        for (T t : list) {
            IndexRequest indexRequest = new IndexRequest();
            try {
                Object o = ReflexUtil.readValueByName(idName, t);
                indexRequest.id(o.toString());
            } catch (IllegalAccessException e) {
                continue;
            }
            indexRequest.source(JSON.toJSONString(t), XContentType.JSON);
            bulk.add(indexRequest);
        }
        ActionListener<BulkResponse> listener = new ActionListener<BulkResponse>() {
            @Override
            public void onResponse(BulkResponse bulkResponse) {
            }

            @Override
            public void onFailure(Exception e) {
                log.error(e.getMessage(), e);
            }
        };
        restHighLevelClient.bulkAsync(bulk, RequestOptions.DEFAULT, listener);
    }

    /**
     * 按照默认批次数插入集合数据
     *
     * @param indexName
     * @param list
     * @param idName
     * @throws IOException
     */
    public void defaultBatchInsertData(String indexName, List<T> list, String idName) throws IOException {

        batchNumInsertData(indexName, list, idName, DEFAULT_BATCH_NUM);
    }

    public void defaultAsyncBatchInsertData(String indexName, List<T> list, String idName) throws IOException {

        batchNumAsyncInsertData(indexName, list, idName, DEFAULT_BATCH_NUM);
    }

    /**
     * 按照批次数插入数据
     *
     * @param indexName
     * @param list
     * @param idName
     * @param batchNum  批次数
     * @throws IOException
     */
    public void batchNumInsertData(String indexName, List<T> list, String idName, int batchNum) throws IOException {
        //次数
        int time = list.size() / batchNum ;
        //余数
        int count = list.size() % batchNum;
        for (int i = 0; i < time; i++) {
            List<T> ts = list.subList(i * batchNum, (i + 1) * batchNum);
            singleBatchInsertData(indexName, ts, idName);
        }
        List<T> ts = list.subList(time  * batchNum, list.size());
        singleBatchInsertData(indexName, ts, idName);
    }

    public void batchNumAsyncInsertData(String indexName, List<T> list, String idName, int batchNum) throws IOException {
        //次数
        int time = list.size() / batchNum;

        for (int i = 0; i < time; i++) {
            List<T> ts = list.subList(i * batchNum, (i + 1) * batchNum);
            singleAsyncBatchInsertData(indexName, ts, idName);
        }
        List<T> ts = list.subList(time * batchNum, list.size());
        singleAsyncBatchInsertData(indexName, ts, idName);
    }


    /**
     * 根据索引和id进行删除
     *
     * @param indexName 索引名称
     * @param id        id号
     */
    public void deleteSingleData(String indexName, String id) {
        //创建删除文档请求
        DeleteRequest request = new DeleteRequest();

        //设置属性：指定要删除的索引及id值
        DeleteRequest delete = request.index(indexName).id(id);
        try {
            //执行删除请求
            DeleteResponse deleteResponse = restHighLevelClient.delete(delete, RequestOptions.DEFAULT);
            if (deleteResponse.getResult().toString().equals("DELETED")) {
                log.info("deleteData,删除成功,索引名称:" + indexName + "; id:" + id);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

    }


    /**
     * 根据条件进行删除,
     * * must:   AND
     * * mustNot: NOT
     * * should:: OR
     *
     * @param indexName 索引名称
     */

    public void deleteDataByQuery(String indexName, QueryBuilder queryBuilder) {
        //2 通过DeleteByQueryRequest来构建删除请求，setQuery来装载条件，indices来指定索引
        DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest();
        deleteByQueryRequest.setQuery(queryBuilder);
        deleteByQueryRequest.indices(indexName);  //指定删除索引
        try {
            //3 通过deleteByQuery来发起删除请求
            BulkByScrollResponse deleteResponse = restHighLevelClient.deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void deleteDataAsyncByQuery(String indexName, BoolQueryBuilder queryBuilder) {
        //2 通过DeleteByQueryRequest来构建删除请求，setQuery来装载条件，indices来指定索引
        DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest();
        deleteByQueryRequest.setQuery(queryBuilder);
        deleteByQueryRequest.indices(indexName);  //指定删除索引
        ActionListener<BulkByScrollResponse> listener = new ActionListener<BulkByScrollResponse>() {

            @Override
            public void onResponse(BulkByScrollResponse bulkByScrollResponse) {

            }

            @Override
            public void onFailure(Exception e) {

            }
        };
        try {
            //3 通过deleteByQuery来发起删除请求
            restHighLevelClient.deleteByQueryAsync(deleteByQueryRequest, RequestOptions.DEFAULT, listener);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public Long queryCountSyn(String indexName, QueryBuilder queryBuilder, Class<T> clazz) throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexName);
        searchRequest.source(new SearchSourceBuilder().query(queryBuilder));
        SearchResponse searchRes = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        return searchRes.getHits().getTotalHits().value;
    }

    /**
     * 查询默认前10页
     *
     * @param indexName
     * @param queryBuilder
     * @param clazz
     * @return
     * @throws IOException
     */
    public List<T> queryDefaultBeginSyn(String indexName, QueryBuilder queryBuilder, Class<T> clazz) throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexName);
        searchRequest.source(new SearchSourceBuilder().query(queryBuilder));
        SearchResponse searchRes = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        List<T> data = new ArrayList<>();
        if (searchRes != null) {
            SearchHit[] hits = searchRes.getHits().getHits();
            if (hits.length > 0) {
                data =  convert(hits,clazz);;

            }
        }
        return data;
    }

    public List<T> queryAllSyn(String indexName, QueryBuilder queryBuilder, Class<T> clazz) throws IOException {

        Long aLong = queryCountSyn(indexName, queryBuilder, clazz);
        long pageCount = aLong / DEFAULT_BATCH_NUM;
        List<T> data = new ArrayList<>();

        for (int i = 0; i <= pageCount; i++) {
            List<T> ts = queryPageSyn(indexName, i, DEFAULT_BATCH_NUM, queryBuilder, clazz);
            data.addAll(ts);
        }
        return data;

    }


    /**
     * 不推荐使用 from + size 做深度分页，原因如下：
     * <p>
     * 1、搜索请求通常需要跨多个分片，每个分片必须将其请求的命中文档以及任何先前页面的命中文档加载到内存中，分页越深，占用内存越多，很可能导致OOM。
     * <p>
     * 2、对于翻页较深的页面或大量结果，这些操作会显著增加内存和 CPU 使用率，从而导致性能下降或节点故障。
     *
     * @param indexName
     * @param pageNum
     * @param pageSize
     * @param queryBuilder
     * @param clazz
     * @return
     * @throws IOException
     */
    public List<T> queryPageSyn(String indexName, int pageNum, int pageSize, QueryBuilder queryBuilder, Class<T> clazz) throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexName);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 查询索引下所有的文档
        sourceBuilder.query(queryBuilder);
        //计算出记录起始下标
        int from = pageNum * pageSize;
        // 起始记录下标，从0开始
        sourceBuilder.from(from);
        //每页显示的记录数
        sourceBuilder.size(pageSize);
        searchRequest.source(sourceBuilder);

        SearchResponse searchRes = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        List<T> data = new ArrayList<>();
        if (searchRes != null) {
            SearchHit[] hits = searchRes.getHits().getHits();
            if (hits.length > 0) {
                data = convert(hits,clazz);
            }
        }
        return data;
    }

    private List<T> convert(SearchHit[] hits, Class<T> clazz) {
        List<T> data = new ArrayList<>();
        for (int i = 0; i < hits.length; i++) {
            try {
                T convert = convert(hits[i].getSourceAsString(), clazz);
                data.add(convert);
            }catch (Exception e) {
                log.error("json转换异常"+e.getMessage(), e);
            }
        }
        return data;
    }

    private <T> T convert(String jsonStr, Class<T> clazz) {
        T t = JsonUtil.readToValue(jsonStr, clazz);
        return t;
    }

    /**
     * todo
     * scroll滚动搜索是先搜索一批数据，然后下次再搜索下一批数据，以此类推，直到搜索出全部的数据来。
     * <p>
     * scroll搜索会在第一次搜索的时候，保存一个当时的视图快照，之后只会基于该视图快照搜索数据，如果在搜索期间数据发生了变更，用户是看不到变更的数据的。
     * <p>
     * 因此，滚动查询不适合实时性要求高的搜索场景。
     * <p>
     * 另外，每次发送scroll请求，我们还需要指定一个scroll_id参数和一个时间窗口，每次搜索请求只要在这个时间窗口内完成即可。
     *
     * @param index
     * @param queryBuilder
     * @param clazz
     * @return
     */
    public List<T> scrollQuery(String index, QueryBuilder queryBuilder, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        try {
            Scroll scroll = new Scroll(TimeValue.timeValueMillis(ALIVE_TIME));
            SearchRequest searchRequest = new SearchRequest(index);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(queryBuilder);
            sourceBuilder.size(DEFAULT_BATCH_NUM);
            searchRequest.source(sourceBuilder);
            searchRequest.scroll(scroll);

            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //首次检索返回scrollId，用于下一次的滚动查询
            String scrollId = response.getScrollId();
            //获取首次检索命中结果
            SearchHit[] searchHits = response.getHits().getHits();
            //计数
            int count = 0;
            // 处理第一批结果
            if (searchHits.length > 0) {
                List<T> data =   convert(searchHits,clazz);
                ;
                result.addAll(data);
            }
            // 处理滚动结果
            while (searchHits != null && searchHits.length > 0) {
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(TimeValue.timeValueMinutes(ALIVE_TIME));
                response = restHighLevelClient.scroll(scrollRequest, RequestOptions.DEFAULT);
                scrollId = response.getScrollId();
                searchHits = response.getHits().getHits();
                List<T> data = convert(searchHits,clazz);;
                result.addAll(data);
            }
            // 清理滚动上下文
            ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
            clearScrollRequest.addScrollId(scrollId);
            ClearScrollResponse clearScrollResponse = restHighLevelClient.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
            if (!clearScrollResponse.isSucceeded()) {
                log.error("清空滚动失败");
            }
            return result;

        } catch (Exception e) {
            log.error("查询索引[{}]的文档失败", index, e);
        }

        return result;

    }


    /**
     * todo
     * 注意，ES 5.0以上版本才有这个功能。
     * scroll API适用于高效的深度滚动，但滚动上下文成本高昂，不建议将其用于实时用户请求。而search_after参数通过提供一个活动光标来规避这个问题。
     * <p>
     * 这样可以使用上一页的结果来帮助检索下一页。
     * <p>
     * 在下面的例子中，我们以id字段排序（id是自增的），每个返回结果中都有一个sort字段，我们每次取最后一条记录的sort值，作为search_after的参数值，进行下一次的查询，以此类推，直至查询完所有的数据。
     * <p>
     * search_after是个数组，可以指定多个值。
     *
     * @param index
     * @param searchAfters
     * @return
     */
    public Map<String, Object> searchAfter(String index, Object[] searchAfters) {
        try {
            Map<String, Object> map = new HashMap<>();
            SearchRequest searchRequest = new SearchRequest(index);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(QueryBuilders.matchAllQuery());
            sourceBuilder.from(0);
            sourceBuilder.size(DEFAULT_BATCH_NUM);
            sourceBuilder.sort("id", SortOrder.ASC);
            if (searchAfters == null || searchAfters.length <= 0) {
                sourceBuilder.searchAfter(new Object[]{0L});
            } else {
                sourceBuilder.searchAfter(searchAfters);
            }

            searchRequest.source(sourceBuilder);

            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] searchHits = response.getHits().getHits();
            List<String> result = new ArrayList<>();
            int length = searchHits.length;
            for (int i = 0; i < length; i++) {
                result.add(searchHits[i].getSourceAsString());
                if (i == length - 1) {
                    map.put("searchAfters", searchHits[i].getSortValues());
                }
            }
            map.put("data", result);
            return map;
        } catch (Exception e) {
            log.error("查询索引[{}]的文档失败", index, e);
        }
        return null;
    }
}
