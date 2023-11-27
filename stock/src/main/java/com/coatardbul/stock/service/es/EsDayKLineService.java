package com.coatardbul.stock.service.es;

import com.coatardbul.baseCommon.model.entity.StockBase;
import com.coatardbul.baseCommon.model.entity.StockPrice;
import com.coatardbul.baseService.service.DongFangCommonService;
import com.coatardbul.baseService.service.ElasticsearchService;
import com.coatardbul.stock.mapper.StockBaseMapper;
import com.coatardbul.stock.model.dto.EsDayKlineDto;
import com.coatardbul.stock.service.statistic.DongFangSortService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2023/11/13
 *
 * @author Su Xiaolei
 */
@Component
@Slf4j
public class EsDayKLineService {


    private static final String DAY_K_LINE_INDEX_NAME = "day_k_line";
    @Autowired
    ElasticsearchService elasticsearchService;

@Autowired
    DongFangSortService dongFangSortService;
    @Autowired
    DongFangCommonService dongFangCommonService;

    @Autowired
    StockBaseMapper stockBaseMapper;
    private BoolQueryBuilder getQueryBuilder(EsDayKlineDto dto) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(dto.getBeginDateStr())) {
            queryBuilder.must(QueryBuilders.rangeQuery("dateStr").gte(dto.getBeginDateStr()));
        }
        if (StringUtils.isNotBlank(dto.getEndDateStr())) {
            queryBuilder.must(QueryBuilders.rangeQuery("dateStr").lte(dto.getEndDateStr()));
        }
        if (StringUtils.isNotBlank(dto.getCode())) {
            queryBuilder.must(QueryBuilders.termQuery("code", dto.getCode()));
        }
        return queryBuilder;
    }



    /**
     * 计数方式是按照年计算
     *
     * @param dto
     * @return
     * @throws IOException
     */
    public Long getCount(EsDayKlineDto dto) throws IOException {
        QueryBuilder queryBuilder = getQueryBuilder(dto);
        Long aLong = elasticsearchService.queryCountSyn(DAY_K_LINE_INDEX_NAME, queryBuilder, StockPrice.class);
        return aLong;
    }

    /**
     * 按照年同步
     *
     * @param dto
     * @throws IOException
     */
    public void syncData(EsDayKlineDto dto) throws IOException, InterruptedException {
        boolean indexExist = elasticsearchService.checkIndexExist(DAY_K_LINE_INDEX_NAME);
        if (!indexExist) {
            elasticsearchService.indexCreate(DAY_K_LINE_INDEX_NAME);
        }
        if(StringUtils.isNotBlank(dto.getCode())){
            defaultBatchInsertData(dto);
        }else {
            List<StockBase> stockBases = stockBaseMapper.selectByAll(new StockBase());
            for(StockBase stockBase:stockBases){
                EsDayKlineDto esDayKlineDto=new EsDayKlineDto();
                BeanUtils.copyProperties(dto, esDayKlineDto);
                esDayKlineDto.setCode(stockBase.getCode());
                defaultBatchInsertData(esDayKlineDto);
                Thread.sleep(new Random().nextInt(5*1000));
            }
        }
    }

    private void defaultBatchInsertData(EsDayKlineDto dto) throws IOException {
        List<StockPrice> allDayKline = dongFangCommonService.getAllDayKline(dto.getCode());
        deleteEsSync(dto);
        List<StockPrice> collect = allDayKline.stream().filter(o1 -> o1.getDateStr().compareTo(dto.getBeginDateStr()) >= 0
                && o1.getDateStr().compareTo(dto.getEndDateStr()) <= 0
        ).collect(Collectors.toList());
        elasticsearchService.defaultBatchInsertData(DAY_K_LINE_INDEX_NAME, collect, "id");

    }

    /**
     * 同步当日数据
     *
     * @param dto
     * @throws IOException
     */
    public void syncTodayData() throws IOException, ParseException, InterruptedException {
        boolean indexExist = elasticsearchService.checkIndexExist(DAY_K_LINE_INDEX_NAME);
        if (!indexExist) {
            elasticsearchService.indexCreate(DAY_K_LINE_INDEX_NAME);
        }
        List<StockPrice> increaseObjAll = dongFangSortService.getIncreaseObjAll();
        elasticsearchService.defaultBatchInsertData(DAY_K_LINE_INDEX_NAME, increaseObjAll, "id");

    }



    /**
     * 数据纬度是年，删除去除条件
     *
     * @param dto
     */
    public void deleteEsSync(EsDayKlineDto dto) {
        QueryBuilder queryBuilder = getQueryBuilder(dto);
        elasticsearchService.deleteDataByQuery(DAY_K_LINE_INDEX_NAME, queryBuilder);
    }

    /**
     * 获取日期数据
     *
     * @param dto
     * @return
     * @throws IOException
     */
    public List getDateList(EsDayKlineDto dto) throws IOException {
        List<StockPrice> list = new ArrayList<>();
        BoolQueryBuilder queryBuilder = getQueryBuilder(dto);
        list = elasticsearchService.queryAllSyn(DAY_K_LINE_INDEX_NAME, queryBuilder, StockPrice.class);
        return list;
    }



    public List getRangeList(EsDayKlineDto dto) throws IOException {
        return getDateList(dto);
    }
}
