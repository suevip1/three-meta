package com.coatardbul.baseService.service;

import com.coatardbul.baseCommon.model.entity.StockBase;
import com.coatardbul.baseService.entity.bo.es.EsIndustryDataBo;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2023/11/19
 *
 * @author Su Xiaolei
 */
@Component
@Slf4j
public class EsStockBaseService {
    private static final String INDUSTRY_DATA_INDEX_NAME="stock_base";
    @Autowired
    ElasticsearchService elasticsearchService;


    public void  insert(StockBase stockBase) throws IOException {
        elasticsearchService.insertData(INDUSTRY_DATA_INDEX_NAME,stockBase,stockBase.getCode());
    }

    /**
     * 名称为前缀名称 得润电子   得润电 得润 得
     * @return
     */
    public Long queryCount(String stockName) throws IOException {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.matchPhraseQuery("name", stockName));
        Long aLong= elasticsearchService.queryCountSyn(INDUSTRY_DATA_INDEX_NAME, boolQuery, EsIndustryDataBo.class);
        return aLong;
    }
}
