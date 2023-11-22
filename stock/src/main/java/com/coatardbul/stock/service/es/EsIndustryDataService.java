package com.coatardbul.stock.service.es;

import com.coatardbul.baseCommon.util.DateTimeUtil;
import com.coatardbul.baseService.entity.bo.es.EsIndustryDataBo;
import com.coatardbul.baseService.service.ElasticsearchService;
import com.coatardbul.stock.service.statistic.TongHuaShunIndustryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
public class EsIndustryDataService {


    private static final String INDUSTRY_DATA_INDEX_NAME = "industry_data";
    @Autowired
    ElasticsearchService elasticsearchService;


    @Autowired
    TongHuaShunIndustryService tongHuaShunIndustryService;

    private BoolQueryBuilder getQueryBuilder(EsIndustryDataBo dto) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(dto.getYearStr())) {
            queryBuilder.must(QueryBuilders.termQuery("yearStr", dto.getYearStr()));
        }
        if (StringUtils.isNotBlank(dto.getBkCode())) {
            queryBuilder.must(QueryBuilders.termQuery("bkCode", dto.getBkCode()));
        }

        if (StringUtils.isNotBlank(dto.getDateStr())) {
            queryBuilder.must(QueryBuilders.termQuery("dateStr", dto.getDateStr()));
        }
        return queryBuilder;
    }

    private BoolQueryBuilder getRangeQueryBuilder(EsIndustryDataBo dto) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(dto.getBkCode())) {
            queryBuilder.must(QueryBuilders.termQuery("bkCode", dto.getBkCode()));
        }
        if (StringUtils.isNotBlank(dto.getBeginDateStr())) {
            queryBuilder.must(QueryBuilders.rangeQuery("dateStr").gte(dto.getBeginDateStr()));
            queryBuilder.must(QueryBuilders.rangeQuery("dateStr").lte(dto.getEndDateStr()));
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
    public long getCount(EsIndustryDataBo dto) throws IOException {
        dto.setDateStr(null);
        QueryBuilder queryBuilder = getQueryBuilder(dto);
        Long aLong = elasticsearchService.queryCountSyn(INDUSTRY_DATA_INDEX_NAME, queryBuilder, EsIndustryDataBo.class);
        return aLong;
    }

    /**
     * 按照年同步
     *
     * @param dto
     * @throws IOException
     */
    public void syncData(EsIndustryDataBo dto) throws IOException {
        dto.setDateStr(null);
        boolean indexExist = elasticsearchService.checkIndexExist(INDUSTRY_DATA_INDEX_NAME);
        if (!indexExist) {
            elasticsearchService.indexCreate(INDUSTRY_DATA_INDEX_NAME);
        }
        //count总数
        List<EsIndustryDataBo> yearIncreaseRate = tongHuaShunIndustryService.getYearIncreaseRate(dto.getBkCode(), dto.getYearStr());

        List<EsIndustryDataBo> convert = convert(yearIncreaseRate, dto);
        deleteEsSync(dto);
        elasticsearchService.defaultBatchInsertData(INDUSTRY_DATA_INDEX_NAME, convert, "id");
    }

    /**
     * 同步当日数据
     *
     * @param dto
     * @throws IOException
     */
    public void syncTodayData(EsIndustryDataBo dto) throws IOException {
        boolean indexExist = elasticsearchService.checkIndexExist(INDUSTRY_DATA_INDEX_NAME);
        if (!indexExist) {
            elasticsearchService.indexCreate(INDUSTRY_DATA_INDEX_NAME);
        }
        //count总数
        elasticsearchService.insertData(INDUSTRY_DATA_INDEX_NAME, dto, dto.getId());
    }

    private List<EsIndustryDataBo> convert(List<EsIndustryDataBo> yearIncreaseRate, EsIndustryDataBo dto) {
        List<EsIndustryDataBo> result = new ArrayList<>();
        if (yearIncreaseRate != null && yearIncreaseRate.size() > 0) {
            for (EsIndustryDataBo map : yearIncreaseRate) {
                map.setBkName(dto.getBkName());
                map.setId(map.getDateStr() + "_" + map.getBkCode());
                result.add(map);
            }
        }
        return result;
    }

    /**
     * 数据纬度是年，删除去除条件
     *
     * @param dto
     */
    public void deleteEsSync(EsIndustryDataBo dto) {
        dto.setDateStr(null);
        QueryBuilder queryBuilder = getQueryBuilder(dto);
        elasticsearchService.deleteDataByQuery(INDUSTRY_DATA_INDEX_NAME, queryBuilder);
    }

    /**
     * 获取日期数据
     *
     * @param dto
     * @return
     * @throws IOException
     */
    public List getDateList(EsIndustryDataBo dto) throws IOException {
        List<EsIndustryDataBo> list = new ArrayList<>();
        String dateFormat = DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD);
//        if(dateFormat.equals(dto.getDateStr())){
//            //todo
//        }else {
//            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
//                    .must(QueryBuilders.termQuery("dateStr", dto.getDateStr().replaceAll("-","")));
//             list = elasticsearchService.queryAllSyn(INDUSTRY_DATA_INDEX_NAME, queryBuilder, EsIndustryDataBo.class);
//        }
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("dateStr", dto.getDateStr().replaceAll("-", "")));
        list = elasticsearchService.queryAllSyn(INDUSTRY_DATA_INDEX_NAME, queryBuilder, EsIndustryDataBo.class);
        return list;
    }

    /**
     * 获取单个数据
     *
     * @param dto
     * @return
     * @throws IOException
     */
    public EsIndustryDataBo getSingData(EsIndustryDataBo dto) throws IOException {
        QueryBuilder queryBuilder = getQueryBuilder(dto);
        List<EsIndustryDataBo> list = elasticsearchService.queryAllSyn(INDUSTRY_DATA_INDEX_NAME, queryBuilder, EsIndustryDataBo.class);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return new EsIndustryDataBo();
        }
    }

    public List getRangeList(EsIndustryDataBo dto) throws IOException {
        QueryBuilder queryBuilder = getRangeQueryBuilder(dto);
        List<EsIndustryDataBo> list = elasticsearchService.queryAllSyn(INDUSTRY_DATA_INDEX_NAME, queryBuilder, EsIndustryDataBo.class);
        return list;
    }
}
