package com.coatardbul.stock.service.statistic;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.baseCommon.constants.AiStrategyEnum;
import com.coatardbul.baseCommon.constants.StockTemplateEnum;
import com.coatardbul.baseCommon.exception.BusinessException;
import com.coatardbul.baseCommon.model.bo.StrategyBO;
import com.coatardbul.baseCommon.model.dto.StockStrategyQueryDTO;
import com.coatardbul.baseCommon.util.JsonUtil;
import com.coatardbul.baseService.entity.bo.AiStrategyUplimitAmbushBo;
import com.coatardbul.baseService.entity.bo.StockTemplatePredict;
import com.coatardbul.baseService.entity.feign.StockTemplateQueryDTO;
import com.coatardbul.baseService.feign.BaseServerFeign;
import com.coatardbul.baseService.feign.RiverServerFeign;
import com.coatardbul.baseService.service.DongFangCommonService;
import com.coatardbul.baseService.service.SnowFlakeService;
import com.coatardbul.baseService.service.StockParseAndConvertService;
import com.coatardbul.baseService.service.StockStrategyCommonService;
import com.coatardbul.baseService.service.StockUpLimitAnalyzeCommonService;
import com.coatardbul.baseService.service.romote.RiverRemoteService;
import com.coatardbul.baseService.utils.RedisKeyUtils;
import com.coatardbul.stock.common.constants.Constant;
import com.coatardbul.stock.mapper.StockDayEmotionMapper;
import com.coatardbul.stock.mapper.StockMinuterEmotionMapper;
import com.coatardbul.stock.mapper.StockStaticTemplateMapper;
import com.coatardbul.stock.mapper.StockStrategyWatchMapper;
import com.coatardbul.stock.mapper.StockTemplatePredictMapper;
import com.coatardbul.stock.model.dto.StockPredictDto;
import com.coatardbul.stock.service.base.StockStrategyService;
import com.coatardbul.stock.service.statistic.business.StockVerifyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/4/4
 *
 * @author Su Xiaolei
 */
@Service
@Slf4j
public class StockPredictService {
    @Autowired
    BaseServerFeign baseServerFeign;
    @Autowired
    StockStrategyCommonService stockStrategyCommonService;
    @Autowired
    RiverRemoteService riverRemoteService;
    @Autowired
    StockStrategyService stockStrategyService;
    @Autowired
    StockCronRefreshService stockCronRefreshService;
    @Autowired
    RiverServerFeign riverServerFeign;
    @Autowired
    StockStaticTemplateMapper stockStaticTemplateMapper;
    @Autowired
    StockMinuterEmotionMapper stockMinuterEmotionMapper;
    @Autowired
    StockVerifyService stockVerifyService;
    @Autowired
    SnowFlakeService snowFlakeService;
    @Autowired
    DongFangCommonService dongFangCommonService;

    @Autowired
    StockUpLimitAnalyzeCommonService stockUpLimitAnalyzeCommonService;
    @Autowired
    StockTemplatePredictMapper stockTemplatePredictMapper;
    @Autowired
    StockParseAndConvertService stockParseAndConvertService;
    @Autowired
    StockStrategyWatchMapper stockStrategyWatchMapper;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    StockDayEmotionMapper stockDayEmotionMapper;

    public void execute(StockPredictDto dto) {
        Assert.notNull(dto.getHoleDay(), "天数不不能为空");
        if (!StringUtils.isNotBlank(dto.getId())) {
            //ai策略，目前只支持涨停伏击
            if (AiStrategyEnum.UPLIMIT_AMBUSH.getCode().equals(dto.getAiStrategySign())
                    || AiStrategyEnum.TWO_ABOVE_UPLIMIT_AMBUSH.getCode().equals(dto.getAiStrategySign())
                    || AiStrategyEnum.HAVE_UPLIMIT_AMBUSH.getCode().equals(dto.getAiStrategySign())
            ) {
                //ai策略
                String strategyParam = (String) redisTemplate.opsForValue().get(RedisKeyUtils.getAiStrategyParam(dto.getAiStrategySign()));
                AiStrategyUplimitAmbushBo aiStrategyParamBo = JsonUtil.readToValue(strategyParam, AiStrategyUplimitAmbushBo.class);
                List<String> dateIntervalList = riverRemoteService.getDateIntervalList(dto.getBeginDate(), dto.getEndDate());
                for (String dateStr : dateIntervalList) {
                    try {
                        jointAiStrategyQueryAndParse(dto, aiStrategyParamBo, dateStr);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            } else {
                throw new BusinessException("id不能为空");
            }
        } else {
            //获取时间区间
            List<String> dateIntervalList = riverRemoteService.getDateIntervalList(dto.getBeginDate(), dto.getEndDate());

            for (String dateStr : dateIntervalList) {
                Constant.emotionByDateRangeThreadPool.execute(() -> {
                    jointStrategyQueryAndParse(dto, dateStr);
                });
            }
        }
    }

    private void jointAiStrategyQueryAndParse(StockPredictDto stockPredictDto, AiStrategyUplimitAmbushBo aiStrategyParamBo, String dateFormat) {
        StockStrategyQueryDTO dto = new StockStrategyQueryDTO();
        if (AiStrategyEnum.UPLIMIT_AMBUSH.getCode().equals(stockPredictDto.getAiStrategySign())) {
            dto.setRiverStockTemplateSign(StockTemplateEnum.FIRST_UP_LIMIT.getSign());
        }
        if (AiStrategyEnum.TWO_ABOVE_UPLIMIT_AMBUSH.getCode().equals(stockPredictDto.getAiStrategySign())) {
            dto.setRiverStockTemplateSign(StockTemplateEnum.TWO_UP_LIMIT_ABOVE.getSign());
        }
        if (AiStrategyEnum.HAVE_UPLIMIT_AMBUSH.getCode().equals(stockPredictDto.getAiStrategySign())) {
            dto.setRiverStockTemplateSign(StockTemplateEnum.HAVE_UP_LIMIT.getSign());
        }
        dto.setDateStr(dateFormat);
        StrategyBO strategy = null;
        try {
            strategy = stockStrategyCommonService.strategy(dto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        if (strategy == null || strategy.getTotalNum() == 0) {
            return;
        }
        //当日涨停
        for (int jsonLen = 0; jsonLen < strategy.getTotalNum(); jsonLen++) {
            StrategyBO finalStrategy = strategy;
            int finalJsonLen = jsonLen;
            Constant.emotionByDateRangeThreadPool.execute(() -> {
                try {
                    //根据日期，股票，插入股票预测数据
                    insertStockPredict(stockPredictDto, aiStrategyParamBo, dateFormat, finalStrategy, finalJsonLen);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            });
        }
    }

    /**
     * @param stockPredictDto
     * @param aiStrategyParamBo
     * @param dateFormat
     * @param finalStrategy
     * @param finalJsonLen
     */
    private void insertStockPredict(StockPredictDto stockPredictDto, AiStrategyUplimitAmbushBo aiStrategyParamBo, String dateFormat, StrategyBO finalStrategy, int finalJsonLen) {
        JSONObject jsonObject = finalStrategy.getData().getJSONObject(finalJsonLen);
//            Map convert = stockUpLimitAnalyzeCommonService.convertFirstUpLimit(jsonObject, dateFormat);
//            BigDecimal lastClosePrice = new BigDecimal(convert.get("lastClosePrice").toString());
//            BigDecimal multiply = lastClosePrice.multiply(new BigDecimal(1.02));
        //昨日信息
//        Map lastInfo = getNextInfo(dateFormat, -1, (String) jsonObject.get("code"));
//        BigDecimal lastLastClosePriceTemp = new BigDecimal(lastInfo.get("lastClosePrice").toString());
//        BigDecimal maxIncrease = new BigDecimal(lastInfo.get("maxPrice").toString()).subtract(lastLastClosePriceTemp).divide(lastLastClosePriceTemp, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
//        if (maxIncrease.compareTo(aiStrategyParamBo.getLastMaxIncreaseMaxRate()) > 0) {
//            throw new BusinessException("不符合条件");
//        }
        //验证当日参数是否复合规定
        Map currInfo = getNextInfo(dateFormat, 0, (String) jsonObject.get("code"));
        BigDecimal lastClosePrice = new BigDecimal(currInfo.get("lastClosePrice").toString());
        //买入的价格
        BigDecimal buyPrice = lastClosePrice.multiply(aiStrategyParamBo.getBuyIncreaseMinRate());

        if (!AiStrategyEnum.TWO_ABOVE_UPLIMIT_AMBUSH.getCode().equals(stockPredictDto.getAiStrategySign())) {
            if (new BigDecimal(currInfo.get("auctionIncreaseRate").toString()).compareTo(aiStrategyParamBo.getAuctionIncreaseMaxRate()) > 0) {
                throw new BusinessException("不符合条件");
            }
            if (new BigDecimal(currInfo.get("lastIncreaseRate").toString()).compareTo(aiStrategyParamBo.getLastCloseIncreaseMaxRate()) > 0) {
                throw new BusinessException("不符合条件");
            }
        }
        for (int i = 1; i < 4; i++) {
            Map nextInfo = getNextInfo(dateFormat, i, (String) jsonObject.get("code"));
//            try {
//                if (new BigDecimal(nextInfo.get("minPrice").toString()).compareTo(buyPrice) < 0 && new BigDecimal(nextInfo.get("maxPrice").toString()).compareTo(buyPrice) > 0) {
//                    int num = 1232;
//                    int num2 = 1232;
//
//                }
//            }catch (Exception e){
//                log.error(e.getMessage(),e);
//            }

//            if(i==1){
//                BigDecimal lastClosePriceTemp = new BigDecimal(nextInfo.get("lastClosePrice").toString());
//                BigDecimal maxIncreaseTemp = new BigDecimal(nextInfo.get("maxPrice").toString()).subtract(lastClosePriceTemp).divide(lastClosePrice, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
//                if (maxIncreaseTemp.compareTo(aiStrategyParamBo.getNextMaxIncreaseMaxRate()) > 0) {
//                    throw new BusinessException("不符合条件");
//                }
//            }

            if (new BigDecimal(nextInfo.get("minPrice").toString()).compareTo(buyPrice) < 0 && new BigDecimal(nextInfo.get("maxPrice").toString()).compareTo(buyPrice) > 0) {
                //todo
                StockTemplatePredict stockTemplatePredict = new StockTemplatePredict();
                stockTemplatePredict.setId(snowFlakeService.getSnowId());
                stockTemplatePredict.setDate(nextInfo.get("dateStr").toString());
                stockTemplatePredict.setTemplatedSign(stockPredictDto.getAiStrategySign());
                stockTemplatePredict.setTemplatedName(AiStrategyEnum.getDescByCode(stockTemplatePredict.getTemplatedSign()));
                stockTemplatePredict.setHoldDay(2);
                stockTemplatePredict.setCode(nextInfo.get("code").toString());
                stockTemplatePredict.setName(nextInfo.get("name").toString());
                stockTemplatePredict.setBuyPrice(buyPrice);
//                stockTemplatePredict.setBuyTime(preQuartzTradeDetail.getTime());
//                stockTemplatePredict.setBuyIncreaseRate(preQuartzTradeDetail.getIncreaseRate());
//                stockTemplatePredict.setCloseIncreaseRate(preQuartzTradeDetail.getCloseIncreaseRate());
                stockTemplatePredictMapper.insert(stockTemplatePredict);
                String specialDay = riverRemoteService.getSpecialDay(dateFormat, i + stockPredictDto.getHoleDay());
                try {
                    Map stockDetailMap = dongFangCommonService.getStockDetailMap(stockTemplatePredict.getCode(), specialDay, "11:29");
                    BigDecimal newPrice = new BigDecimal(stockDetailMap.get("newPrice").toString());
                    stockTemplatePredict.setSalePrice(newPrice);
                    stockTemplatePredict.setSaleTime("11:29");
                    stockTemplatePredict.setDetail(stockDetailMap.get("thsIndustry").toString() + "\\n" + stockDetailMap.get("theirConcept").toString());
                } catch (Exception e) {
                    log.error("获取当前11.29分数据出错" + e.getMessage());
                }
                stockTemplatePredictMapper.updateByPrimaryKey(stockTemplatePredict);
                break;
            }
        }
    }


    private Map getNextInfo(String dateFormat, Integer num, String code) {
        String specialDay = null;
        if (num == 0) {
            specialDay = dateFormat;
        } else {
            specialDay = riverRemoteService.getSpecialDay(dateFormat, num);
        }

        StockStrategyQueryDTO dto = new StockStrategyQueryDTO();
        dto.setRiverStockTemplateSign(StockTemplateEnum.STOCK_DETAIL.getSign());
        dto.setDateStr(specialDay);
        dto.setStockCode(code);
        StrategyBO strategy = null;
        try {
            strategy = stockStrategyCommonService.strategy(dto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        if (strategy == null || strategy.getTotalNum() == 0) {
            return null;
        }
        JSONObject jsonObject = strategy.getData().getJSONObject(0);
        Map convert = stockUpLimitAnalyzeCommonService.convert(jsonObject, specialDay);
        return convert;
    }


    /**
     * 传入的
     *
     * @param dto     传入的日期
     * @param dateStr 卖出日期
     */
    private void jointStrategyQueryAndParse(StockPredictDto dto, String dateStr) {
        //买入的查询语句
        StockPredictDto result = new StockPredictDto();
        BeanUtils.copyProperties(dto, result);
        result.setBuyTime(dto.getBuyTime());

        //策略查询
        StockStrategyQueryDTO stockStrategyQueryDTO = new StockStrategyQueryDTO();
        stockStrategyQueryDTO.setRiverStockTemplateId(dto.getId());
        stockStrategyQueryDTO.setDateStr(dateStr);
        try {
            StrategyBO strategy = stockStrategyService.strategy(stockStrategyQueryDTO);
            //动态结果解析
            if (strategy != null && strategy.getTotalNum() > 0) {
                parseStrategyResult(result, strategy, dateStr);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    /**
     * 需要拼接 2022年03月04日13点30分价格
     * 则结果为：{{afterDay4}}{{time}}截个
     *
     * @param holeDay 持有天数
     * @return
     */
    private String getSaleQueryScript(Integer holeDay) {
        StringBuffer sb = new StringBuffer();
        sb.append("{{afterDay").append(holeDay).append("}}").append("{{time}}").append("价格");
        return sb.toString();
    }

    private String getSaleQueryInfo(StockPredictDto dto, String saleQueryScript, String dateStr) {
        StockTemplateQueryDTO stockTemplateQueryDto = new StockTemplateQueryDTO();
        stockTemplateQueryDto.setDateStr(dateStr);
        stockTemplateQueryDto.setTimeStr(dto.getSaleTime());
        stockTemplateQueryDto.setStockScript(saleQueryScript);
        CommonResult<String> riverServerFeignResult = riverServerFeign.getQuery(stockTemplateQueryDto);
        if (riverServerFeignResult != null) {
            return riverServerFeignResult.getData();
        } else {
            return null;
        }
    }

    private String getBuyQueryInfo(StockPredictDto dto, String dateStr) {
        StockTemplateQueryDTO stockTemplateQueryDto = new StockTemplateQueryDTO();
        stockTemplateQueryDto.setId(dto.getId());
        stockTemplateQueryDto.setDateStr(dateStr);
        stockTemplateQueryDto.setTimeStr(dto.getBuyTime());
        CommonResult<String> riverServerFeignResult = riverServerFeign.getQuery(stockTemplateQueryDto);
        if (riverServerFeignResult != null) {
            return riverServerFeignResult.getData();
        } else {
            return null;
        }
    }


    private void parseStrategyResult(StockPredictDto dto, StrategyBO strategy, String dateStr) {
        JSONArray data = strategy.getData();
        //卖出日期
        String saleDateFormat = riverRemoteService.getSpecialDay(dateStr, dto.getHoleDay());
        for (Object jo : data) {
            StockTemplatePredict addInfo = getStockTemplatePredict(dto, dateStr, saleDateFormat, jo);
            String key = addInfo.getDate() + "_" + addInfo.getTemplatedId() + "_" + addInfo.getCode();
            redisTemplate.opsForValue().set(key, JsonUtil.toJson(addInfo), 10, TimeUnit.MINUTES);
            stockTemplatePredictMapper.insertSelective(addInfo);
        }

    }

    private StockTemplatePredict getStockTemplatePredict(StockPredictDto dto, String dateStr, String saleDateFormat, Object jo) {
        Map buyInfo = getNextInfo(dateStr, 0, (String) ((JSONObject) jo).get("code"));
        Map saleInfo = getNextInfo(saleDateFormat, 0, (String) ((JSONObject) jo).get("code"));
        StockTemplatePredict addInfo = new StockTemplatePredict();
        addInfo.setId(baseServerFeign.getSnowflakeId());
        addInfo.setDate(dateStr);
        addInfo.setTemplatedId(dto.getId());
        addInfo.setHoldDay(dto.getHoleDay());
        addInfo.setTemplatedSign(dto.getStrategySign());
        addInfo.setSaleTime(dto.getSaleTime());
        addInfo.setBuyTime(dto.getBuyTime());
        addInfo.setCode((String) ((JSONObject) jo).get("code"));
        addInfo.setName((String) ((JSONObject) jo).get("股票简称"));

        addInfo.setBuyPrice(new BigDecimal(buyInfo.get("newPrice").toString()));
        addInfo.setSalePrice(new BigDecimal(saleInfo.get("newPrice").toString()));

        return addInfo;
    }


    public List<StockTemplatePredict> getAll(StockPredictDto dto) {

        List<StockTemplatePredict> stockTemplatePredicts = stockTemplatePredictMapper.selectAllByDateBetweenEqualAndTemplatedIdAndHoldDay(dto.getBeginDate(), dto.getEndDate(), dto.getId(), dto.getStrategySign(), dto.getHoleDay());

        if (stockTemplatePredicts != null && stockTemplatePredicts.size() > 0) {
            Map<String, String> templateIdMap = stockTemplatePredicts.stream().filter(item -> StringUtils.isNotBlank(item.getTemplatedId())).collect(Collectors.toMap(StockTemplatePredict::getTemplatedId, StockTemplatePredict::getTemplatedId, (o1, o2) -> o1));
            for (Map.Entry<String, String> entry : templateIdMap.entrySet()) {
                String templateName = riverRemoteService.getTemplateNameById(entry.getKey());
                entry.setValue(templateName);
            }
            stockTemplatePredicts = stockTemplatePredicts.stream().map(o1 -> convert(o1, templateIdMap)).collect(Collectors.toList());
        }
        return stockTemplatePredicts;
    }

    private StockTemplatePredict convert(StockTemplatePredict stockTemplatePredict, Map<String, String> templateIdMap) {
        if (StringUtils.isNotBlank(stockTemplatePredict.getTemplatedId())) {
            stockTemplatePredict.setTemplatedName(templateIdMap.get(stockTemplatePredict.getTemplatedId()));
        } else {
            stockTemplatePredict.setTemplatedId(stockTemplatePredict.getTemplatedSign());
            stockTemplatePredict.setTemplatedName(AiStrategyEnum.getDescByCode(stockTemplatePredict.getTemplatedSign()));

        }
        return stockTemplatePredict;
    }

    public void deleteById(StockPredictDto dto) {
        stockTemplatePredictMapper.deleteByPrimaryKey(dto.getId());
    }

    public void deleteByQuery(StockPredictDto dto) {
        stockTemplatePredictMapper.deleteByTemplatedIdAndHoldDayAndDateBetweenEqual(dto.getId(), dto.getHoleDay(), dto.getBeginDate(), dto.getEndDate());
    }

    public void updateById(StockPredictDto dto) {
        StockTemplatePredict stockTemplatePredict = stockTemplatePredictMapper.selectByPrimaryKey(dto.getId());
        stockCronRefreshService.calcSaleInfo(stockTemplatePredict);
    }
}
