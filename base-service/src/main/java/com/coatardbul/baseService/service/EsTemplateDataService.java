package com.coatardbul.baseService.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coatardbul.baseCommon.constants.EsTemplateConfigEnum;
import com.coatardbul.baseCommon.model.bo.StrategyBO;
import com.coatardbul.baseCommon.model.dto.EsTemplateConfigDTO;
import com.coatardbul.baseCommon.model.entity.EsTemplateConfig;
import com.coatardbul.baseCommon.util.JsonUtil;
import com.coatardbul.baseService.entity.bo.es.EsTemplateDataBo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.BeanUtils;
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

    private static final String ID_SPLIT = "_";

    public Long getCount(EsTemplateConfigDTO dto) {
        QueryBuilder queryBuilder = getQueryBuilder(dto);
        try {
            Long aLong=0L;
            if (EsTemplateConfigEnum.TYPE_DAY.getSign().equals(dto.getEsDataType())
                    || EsTemplateConfigEnum.TYPE_AUCTION.getSign().equals(dto.getEsDataType())
                    || EsTemplateConfigEnum.TYPE_MINUTER.getSign().equals(dto.getEsDataType())
            ) {
                //每日数据
                 aLong = elasticsearchService.queryCountSyn(dto.getEsIndexName(), queryBuilder, EsTemplateDataBo.class);
            }
            if (EsTemplateConfigEnum.TYPE_DAY_COUNT.getSign().equals(dto.getEsDataType())
                    || EsTemplateConfigEnum.TYPE_MINUTER_COUNT.getSign().equals(dto.getEsDataType())
            ) {
                //count总数
                List<EsTemplateDataBo> list = elasticsearchService.queryAllSyn(dto.getEsIndexName(), queryBuilder, EsTemplateDataBo.class);
                if(list!=null &&list.size()>0){
                    return list.get(0).getCount();
                }
            }
            return aLong;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return 0L;
    }

    /**
     * 获取es上策略结果
     *
     * @param dto
     * @return
     * @throws IOException
     */
    public StrategyBO getEsStrategyResult(EsTemplateConfigDTO dto) throws IOException {
        BoolQueryBuilder queryBuilder = getQueryBuilder(dto);

        List<EsTemplateDataBo> list = elasticsearchService.queryAllSyn(dto.getEsIndexName(), queryBuilder, EsTemplateDataBo.class);
        StrategyBO ret = new StrategyBO();
        ret.setTotalNum(list.size());
        JSONArray jsonArray = new JSONArray();
        for (EsTemplateDataBo es : list) {
            JSONObject jsonObject = JSONObject.parseObject(es.getJsonStr());
            jsonArray.add(jsonObject);
        }
        ret.setData(jsonArray);
        return ret;
    }

    private BoolQueryBuilder getQueryBuilder(EsTemplateConfigDTO dto) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("templateId", dto.getRiverStockTemplateId()))
                .must(QueryBuilders.termQuery("dateStr", dto.getDateStr()));
        if (StringUtils.isNotBlank(dto.getThemeStr())) {
            queryBuilder.must(QueryBuilders.termQuery("industryPrefix.keyword", dto.getThemeStr()));
        }
        if (StringUtils.isNotBlank(dto.getTimeStr())) {
            queryBuilder.must(QueryBuilders.termQuery("timeStr.keyword", dto.getTimeStr()));
        }
        return queryBuilder;
    }

    /**
     * 查询问财数据，并且同步上去
     *
     * @param dto
     * @throws ScriptException
     * @throws IOException
     * @throws NoSuchMethodException
     */
    public void syncData(EsTemplateConfigDTO dto) throws ScriptException, IOException, NoSuchMethodException {
        boolean indexExist = elasticsearchService.checkIndexExist(dto.getEsIndexName());
        if (!indexExist) {
            elasticsearchService.indexCreate(dto.getEsIndexName());
        }
        if (EsTemplateConfigEnum.TYPE_DAY.getSign().equals(dto.getEsDataType())
                || EsTemplateConfigEnum.TYPE_AUCTION.getSign().equals(dto.getEsDataType())
                || EsTemplateConfigEnum.TYPE_MINUTER.getSign().equals(dto.getEsDataType())
        ) {
            //每日数据
            StrategyBO strategyBO = new StrategyBO();
            if (EsTemplateConfigEnum.MODE_ALL.getSign().equals(dto.getEsDataMode())) {
                //全量数据
                strategyBO = stockStrategyCommonService.wenCaiStrategy(dto);
            }
            if (EsTemplateConfigEnum.MODE_FIRST.getSign().equals(dto.getEsDataMode())) {
                //首页第一页数据
                strategyBO = stockStrategyCommonService.strategyFirstProcess(dto);
            }
            if (strategyBO.getData().size() > 0) {
                List<EsTemplateDataBo> convert = convert(strategyBO.getData(), dto);
                //先删除，后插入
                deleteEsSync(dto);
                elasticsearchService.defaultBatchInsertData(dto.getEsIndexName(), convert, "id");
            }
        }
        if (EsTemplateConfigEnum.TYPE_DAY_COUNT.getSign().equals(dto.getEsDataType())
                ||EsTemplateConfigEnum.TYPE_MINUTER_COUNT.getSign().equals(dto.getEsDataType())
        ) {
            //count总数
            StrategyBO strategyBO = stockStrategyCommonService.strategyFirstProcess(dto);
            EsTemplateDataBo convert = convertCount(strategyBO.getTotalNum(), dto);
            deleteEsSync(dto);
            elasticsearchService.insertData(dto.getEsIndexName(), convert, convert.getId());
        }
    }


    /**
     * 不支持分钟级别的，分钟级别的最好当天请求，
     * 支持分钟误操作容易服务器阻塞
     * @param dateArrInfo
     * @param filterEsTemplateConfigList
     */
    public void syncRangeData(List dateArrInfo, List filterEsTemplateConfigList) {
        if(dateArrInfo.size()>0){
            for(Object dateStrObj: dateArrInfo){
                String dateStr = dateStrObj.toString();
                for(Object obj:filterEsTemplateConfigList){
                    String jsonStr = JsonUtil.toJson(obj);
                    EsTemplateConfig esTemplateConfig = JsonUtil.readToValue(jsonStr, EsTemplateConfig.class);
                    EsTemplateConfigDTO esTemplateConfigDto=new EsTemplateConfigDTO();
                    BeanUtils.copyProperties(esTemplateConfig,esTemplateConfigDto);
                    esTemplateConfigDto.setRiverStockTemplateId(esTemplateConfig.getTemplateId());
                    esTemplateConfigDto.setDateStr(dateStr);
                    //分钟级别不操作
                    if(EsTemplateConfigEnum.TYPE_MINUTER.getSign().equals(esTemplateConfigDto.getEsDataType())
                    ||EsTemplateConfigEnum.TYPE_MINUTER_COUNT.getSign().equals(esTemplateConfigDto.getEsDataType())){
                    }else {
                        try {
                            syncData(esTemplateConfigDto);
                            Thread.sleep(EsTemplateConfigEnum.getTimeInterval(esTemplateConfigDto.getEsDataLevel()));
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }

                }
            }
        }
    }

    private EsTemplateDataBo convertCount(Integer count, EsTemplateConfigDTO dto) {

        EsTemplateDataBo convert = convertCount(count, dto.getRiverStockTemplateId(), dto.getDateStr(),dto.getTimeStr());

        return convert;
    }

    /**
     * 转换数据，支持 日，竞价，分钟
     * @param data
     * @param dto
     * @return
     */
    private List<EsTemplateDataBo> convert(JSONArray data, EsTemplateConfigDTO dto) {
        List<EsTemplateDataBo> result = new ArrayList<EsTemplateDataBo>();
        for (int i = 0; i < data.size(); i++) {
            JSONObject jsonObject = data.getJSONObject(i);
            EsTemplateDataBo convert = convertSingle(jsonObject, dto);
            result.add(convert);
        }
        return result;
    }
    private EsTemplateDataBo convertCount(Integer count, String templateId, String dateStr,String timeStr) {
        EsTemplateDataBo esTemplateDataBo = new EsTemplateDataBo();
        esTemplateDataBo.setTemplateId(templateId);
        esTemplateDataBo.setDateStr(dateStr);
        esTemplateDataBo.setTimeStr(timeStr);
        esTemplateDataBo.setCount(Long.valueOf(count));
        esTemplateDataBo.setParams("");
        esTemplateDataBo.setId(getEsCountId(esTemplateDataBo));
        return esTemplateDataBo;
    }

    private EsTemplateDataBo convertSingle(JSONObject jsonObject,EsTemplateConfigDTO dto) {
        EsTemplateDataBo esTemplateDataBo = new EsTemplateDataBo();
        esTemplateDataBo.setTemplateId(dto.getRiverStockTemplateId());
        esTemplateDataBo.setDateStr(dto.getDateStr());
        if(StringUtils.isNotBlank(dto.getTimeStr())){
            esTemplateDataBo.setTimeStr(dto.getTimeStr());
        }
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
        esTemplateDataBo.setId(getEsId(esTemplateDataBo));
        return esTemplateDataBo;
    }

    private String getEsId(EsTemplateDataBo esTemplateDataBo) {
        String baseStr= esTemplateDataBo.getDateStr() + ID_SPLIT + esTemplateDataBo.getTemplateId() + ID_SPLIT + esTemplateDataBo.getStockCode();
        if(StringUtils.isNotBlank(esTemplateDataBo.getTimeStr())){
            return baseStr+ID_SPLIT+esTemplateDataBo.getTimeStr();
        }else {
            return baseStr;
        }
    }
    private String getEsCountId(EsTemplateDataBo esTemplateDataBo) {
        String baseStr= esTemplateDataBo.getDateStr() + ID_SPLIT + esTemplateDataBo.getTemplateId() ;
        if(StringUtils.isNotBlank(esTemplateDataBo.getTimeStr())){
            return baseStr+ID_SPLIT+esTemplateDataBo.getTimeStr();
        }else {
            return baseStr;
        }
    }


    public List<EsTemplateDataBo> getEsTemplateDataList(EsTemplateConfigDTO dto) throws IOException {
        QueryBuilder queryBuilder = getQueryBuilder(dto);
        List<EsTemplateDataBo> list = elasticsearchService.queryAllSyn(dto.getEsIndexName(), queryBuilder, EsTemplateDataBo.class);
        return list;
    }

    public void deleteEsSync(EsTemplateConfigDTO dto) {
        QueryBuilder queryBuilder = getQueryBuilder(dto);
        elasticsearchService.deleteDataByQuery(dto.getEsIndexName(), queryBuilder);
    }



}
