package com.coatardbul.baseService.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coatardbul.baseCommon.model.bo.StrategyBO;
import com.coatardbul.baseCommon.model.dto.StockStrategyQueryDTO;
import com.coatardbul.baseService.entity.bo.es.EsTemplateDataBo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.script.ScriptException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Note:一般条件为模板+日期
 * <p>
 * Date: 2023/11/11
 *
 * @author Su Xiaolei
 */
@Service
@Slf4j
public class EsTemplateDataService {

    @Autowired
    ElasticsearchService elasticsearchService;


    @Autowired
    StockStrategyCommonService stockStrategyCommonService;
    private static final String ES_TEMPLATE_INDEX_NAME = "template_data";

    private static final String ID_SPLIT = "_";

    public Long getCount(StockStrategyQueryDTO dto) {
        QueryBuilder queryBuilder = getQueryBuilder(dto);
        try {
            Long aLong = elasticsearchService.queryCountSyn(ES_TEMPLATE_INDEX_NAME, queryBuilder, EsTemplateDataBo.class);
            return aLong;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return 0L;
    }

    /**
     * 获取es上策略结果
     * @param dto
     * @return
     * @throws IOException
     */
    public StrategyBO getEsStrategyResult(StockStrategyQueryDTO dto) throws IOException {
        BoolQueryBuilder queryBuilder = getQueryBuilder(dto);
        if (StringUtils.isNotBlank(dto.getThemeStr())) {
            queryBuilder.must(QueryBuilders.termQuery("industryPrefix", dto.getThemeStr()));
        }
        List<EsTemplateDataBo> list = elasticsearchService.queryAllSyn(ES_TEMPLATE_INDEX_NAME, queryBuilder, EsTemplateDataBo.class);
        StrategyBO ret = new StrategyBO();
        ret.setTotalNum(list.size());
        JSONArray jsonArray=new JSONArray();
        for(EsTemplateDataBo es:list){
            JSONObject jsonObject = JSONObject.parseObject(es.getJsonStr());
            jsonArray.add(jsonObject);
        }
        ret.setData(jsonArray);
        return ret;
    }

    private BoolQueryBuilder getQueryBuilder(StockStrategyQueryDTO dto) {
        BoolQueryBuilder queryBuilder1 = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("templateId", dto.getRiverStockTemplateId()))
                .must(QueryBuilders.termQuery("dateStr", dto.getDateStr()));
        return queryBuilder1;
    }

    /**
     * 查询问财数据，并且同步上去
     *
     * @param dto
     * @throws ScriptException
     * @throws IOException
     * @throws NoSuchMethodException
     */
    public void syncData(StockStrategyQueryDTO dto) throws ScriptException, IOException, NoSuchMethodException {
        StrategyBO strategyBO = stockStrategyCommonService.wenCaiStrategy(dto);
        boolean indexExist = elasticsearchService.checkIndexExist(ES_TEMPLATE_INDEX_NAME);
        if (!indexExist) {
            elasticsearchService.indexCreate(ES_TEMPLATE_INDEX_NAME);
        }
        if (strategyBO.getData().size() > 0) {
            List<EsTemplateDataBo> convert = convert(strategyBO.getData(), dto);
            elasticsearchService.defaultBatchInsertData(ES_TEMPLATE_INDEX_NAME, convert, "id");
        }
    }

    private List<EsTemplateDataBo> convert(JSONArray data, StockStrategyQueryDTO dto) {
        List<EsTemplateDataBo> result = new ArrayList<EsTemplateDataBo>();
        for (int i = 0; i < data.size(); i++) {
            JSONObject jsonObject = data.getJSONObject(i);
            EsTemplateDataBo convert = convert(jsonObject, dto.getRiverStockTemplateId(), dto.getDateStr());
            result.add(convert);
        }
        return result;
    }

    private EsTemplateDataBo convert(JSONObject jsonObject, String templateId, String dateStr) {
        EsTemplateDataBo esTemplateDataBo = new EsTemplateDataBo();
        esTemplateDataBo.setTemplateId(templateId);
        esTemplateDataBo.setDateStr(dateStr);
        esTemplateDataBo.setStockCode(jsonObject.getString("code"));
        esTemplateDataBo.setStockName(jsonObject.getString("股票简称"));

        esTemplateDataBo.setTheme(jsonObject.getString("所属概念"));
        esTemplateDataBo.setParams("");
        esTemplateDataBo.setJsonStr(jsonObject.toJSONString());

        String industry = jsonObject.getString("所属同花顺行业");
        esTemplateDataBo.setIndustry(industry);
        if (StringUtils.isNotBlank(industry)) {
            esTemplateDataBo.setIndustryPrefix(industry.split("-")[0]);
        }
        esTemplateDataBo.setId(getId(esTemplateDataBo));
        return esTemplateDataBo;
    }

    private String getId(EsTemplateDataBo esTemplateDataBo) {
        return esTemplateDataBo.getDateStr() +ID_SPLIT+ esTemplateDataBo.getTemplateId() +ID_SPLIT+ esTemplateDataBo.getStockCode();
    }


    public void delete(StockStrategyQueryDTO dto) {
        QueryBuilder queryBuilder = getQueryBuilder(dto);
        elasticsearchService.deleteDataByQuery(ES_TEMPLATE_INDEX_NAME, queryBuilder);
    }
}
