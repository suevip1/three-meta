package com.coatardbul.stock.service.es;

import com.coatardbul.baseCommon.util.DateTimeUtil;
import com.coatardbul.baseService.entity.bo.es.EsIndustryDataBo;
import com.coatardbul.baseService.service.ElasticsearchService;
import com.coatardbul.stock.service.statistic.TongHuaShunIndustryService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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


    private static final String INDUSTRY_DATA_INDEX_NAME="industry_data";
    @Autowired
    ElasticsearchService elasticsearchService;


    @Autowired
    TongHuaShunIndustryService tongHuaShunIndustryService;

    private BoolQueryBuilder getQueryBuilder(EsIndustryDataBo dto) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("yearStr", dto.getYearStr()))
                .must(QueryBuilders.termQuery("bkCode", dto.getBkCode()));
        return queryBuilder;
    }
    public long getCount(EsIndustryDataBo dto) throws IOException {
        QueryBuilder queryBuilder = getQueryBuilder(dto);
        Long aLong= elasticsearchService.queryCountSyn(INDUSTRY_DATA_INDEX_NAME, queryBuilder, EsIndustryDataBo.class);
        return aLong;
    }

    public void syncData(EsIndustryDataBo dto) throws IOException {
        boolean indexExist = elasticsearchService.checkIndexExist(INDUSTRY_DATA_INDEX_NAME);
        if (!indexExist) {
            elasticsearchService.indexCreate(INDUSTRY_DATA_INDEX_NAME);
        }
        //count总数
        List<Map<String, String>> yearIncreaseRate = tongHuaShunIndustryService.getYearIncreaseRate(dto.getBkCode(), dto.getYearStr());

        List<EsIndustryDataBo> convert = convert(yearIncreaseRate, dto);
        deleteEsSync(dto);
        elasticsearchService.defaultBatchInsertData(INDUSTRY_DATA_INDEX_NAME, convert, "id");
    }
    private List<EsIndustryDataBo> convert( List<Map<String, String>> yearIncreaseRate,EsIndustryDataBo dto){
        List<EsIndustryDataBo>result=new ArrayList<>();
        if(yearIncreaseRate!=null &&yearIncreaseRate.size()>0){
            for(Map<String, String> map:yearIncreaseRate){
                result.add(convert(dto,map));
            }
        }
        return result;
    }
    private EsIndustryDataBo convert(EsIndustryDataBo dto,Map<String, String> map ){
        EsIndustryDataBo result=new EsIndustryDataBo();
        BeanUtils.copyProperties(dto, result);
        result.setDateStr(map.get("dateStr"));
        result.setIncreaseRate(map.get("increaseRate"));
        result.setMaxIncreaseRate(map.get("maxIncreaseRate"));
        result.setId(result.getBkCode()+"_"+result.getDateStr());
        return result;
    }

    public void deleteEsSync(EsIndustryDataBo dto) {
        QueryBuilder queryBuilder = getQueryBuilder(dto);
        elasticsearchService.deleteDataByQuery(INDUSTRY_DATA_INDEX_NAME, queryBuilder);
    }

    public List getList(EsIndustryDataBo dto) throws IOException {
        List<EsIndustryDataBo> list=new ArrayList<>();
        String dateFormat = DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD);
//        if(dateFormat.equals(dto.getDateStr())){
//            //todo
//        }else {
//            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
//                    .must(QueryBuilders.termQuery("dateStr", dto.getDateStr().replaceAll("-","")));
//             list = elasticsearchService.queryAllSyn(INDUSTRY_DATA_INDEX_NAME, queryBuilder, EsIndustryDataBo.class);
//        }
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("dateStr", dto.getDateStr().replaceAll("-","")));
        list = elasticsearchService.queryAllSyn(INDUSTRY_DATA_INDEX_NAME, queryBuilder, EsIndustryDataBo.class);
        return list;

    }
}
