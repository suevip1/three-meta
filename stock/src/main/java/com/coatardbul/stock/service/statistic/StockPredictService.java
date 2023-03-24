package com.coatardbul.stock.service.statistic;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coatardbul.baseCommon.constants.AiStrategyEnum;
import com.coatardbul.baseCommon.constants.StockTemplateEnum;
import com.coatardbul.baseCommon.exception.BusinessException;
import com.coatardbul.baseCommon.model.bo.StrategyBO;
import com.coatardbul.baseCommon.model.bo.trade.StockDetail;
import com.coatardbul.baseCommon.model.dto.StockStrategyQueryDTO;
import com.coatardbul.baseCommon.util.JsonUtil;
import com.coatardbul.baseService.entity.bo.AiStrategyUplimitAmbushBo;
import com.coatardbul.baseService.entity.bo.StockTemplatePredict;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
        if (StringUtils.isNotBlank(dto.getAiStrategySign()) && dto.getAiStrategySign().contains(",")) {
            String[] split = dto.getAiStrategySign().split(",");
            for (String aiStrategySign : split) {
                StockPredictDto temp = new StockPredictDto();
                BeanUtils.copyProperties(dto, temp);
                temp.setAiStrategySign(aiStrategySign);
                executeSingle(temp);
            }
        } else if (StringUtils.isNotBlank(dto.getId()) && dto.getId().contains(",")) {
            String[] split = dto.getId().split(",");
            for (String id : split) {
                StockPredictDto temp = new StockPredictDto();
                BeanUtils.copyProperties(dto, temp);
                temp.setId(id);
                executeSingle(temp);
            }
        } else {
            executeSingle(dto);
        }
    }


    public void executeSingle(StockPredictDto dto) {
        Assert.notNull(dto.getHoleDay(), "天数不不能为空");
        Assert.notNull(dto.getSaleTime(), "卖出时间不不能为空");

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
                throw new BusinessException("sign不能为空或未识别");
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
            dto.setRiverStockTemplateSign(StockTemplateEnum.FIRST_UP_LIMIT_EQUAL_ABOVE.getSign());
        }

        if (AiStrategyEnum.TWO_ABOVE_UPLIMIT_AMBUSH.getCode().equals(stockPredictDto.getAiStrategySign())) {
            dto.setRiverStockTemplateSign(StockTemplateEnum.TWO_UP_LIMIT_ABOVE.getSign());
        }
        if (AiStrategyEnum.HAVE_UPLIMIT_AMBUSH.getCode().equals(stockPredictDto.getAiStrategySign())) {
            dto.setRiverStockTemplateSign(StockTemplateEnum.SIMILAR_HAVE_UP_LIMIT.getSign());
        }
//        if (AiStrategyEnum.INCREASE_GREATE_NO_UPLIMIT_AMBUSH.getCode().equals(stockPredictDto.getAiStrategySign())) {
//            dto.setRiverStockTemplateSign(StockTemplateEnum.INCREASE_GREATE_NO_UPLIMIT.getSign());
//        }

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
        //大价格补充
        if (AiStrategyEnum.HAVE_UPLIMIT_AMBUSH.getCode().equals(stockPredictDto.getAiStrategySign())) {
            dto.setRiverStockTemplateSign(StockTemplateEnum.SIMILAR_HAVE_UP_LIMIT_SUPPLEMENT.getSign());
            StrategyBO strategyTemp = null;
            try {
                strategyTemp = stockStrategyCommonService.strategy(dto);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            if (strategyTemp != null && strategyTemp.getTotalNum() > 0) {
                strategy.setTotalNum(strategy.getTotalNum()+strategyTemp.getTotalNum());
                JSONArray data = strategy.getData();
                data.addAll(strategyTemp.getData());
                strategy.setData(data);
            }
            dto.setRiverStockTemplateSign(StockTemplateEnum.SIMILAR_HAVE_UP_LIMIT.getSign());
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
            if (new BigDecimal(currInfo.get("tradeAmount").toString()).compareTo(aiStrategyParamBo.getTradeMinAmount()) < 0) {
                throw new BusinessException("不符合条件");
            }
            if (new BigDecimal(currInfo.get("auctionIncreaseRate").toString()).compareTo(aiStrategyParamBo.getAuctionIncreaseMaxRate()) > 0) {
                throw new BusinessException("不符合条件");
            }
            if (new BigDecimal(currInfo.get("lastIncreaseRate").toString()).compareTo(aiStrategyParamBo.getLastCloseIncreaseMaxRate()) > 0) {
                throw new BusinessException("不符合条件");
            }
        }
        //往后一天到三天
        int allNum = 4;
//        if (AiStrategyEnum.UPLIMIT_AMBUSH_BUY_ONE.getCode().equals(stockPredictDto.getAiStrategySign())) {
//            allNum=2;
//        }
//        if (AiStrategyEnum.HAVE_UPLIMIT_AMBUSH_BUY_ONE.getCode().equals(stockPredictDto.getAiStrategySign())) {
//            allNum=2;
//        }
        //昨曾模式，当天复合也可以买入
        if (AiStrategyEnum.HAVE_UPLIMIT_AMBUSH.getCode().equals(stockPredictDto.getAiStrategySign())) {
            StockPredictDto dto = new StockPredictDto();
            String dateStr = currInfo.get("dateStr").toString();
            StockTemplatePredict addInfo = getStockTemplatePredict(stockPredictDto, dateStr, currInfo.get("code").toString());
            addInfo.setTemplatedSign(stockPredictDto.getAiStrategySign());
            addInfo.setTemplatedName(AiStrategyEnum.getDescByCode(addInfo.getTemplatedSign()));
//            String key = addInfo.getDate() + "_" + addInfo.getTemplatedId() + "_" + addInfo.getCode();
//            redisTemplate.opsForValue().set(key, JsonUtil.toJson(addInfo), 10, TimeUnit.MINUTES);
            List<StockTemplatePredict> templatePredicts = stockTemplatePredictMapper.selectAllByCodeAndTemplatedSignAndDate(addInfo.getCode(), addInfo.getTemplatedSign(), addInfo.getDate());
            if (templatePredicts==null ||templatePredicts.size() == 0) {
                //重构信息，市值和换手率应改为涨停时的
                rebuildInsertInfo(addInfo,currInfo);
                stockTemplatePredictMapper.insertSelective(addInfo);
                //计算卖出信息
                calcSaleInfo(dto, dateStr, currInfo.get("code").toString(), addInfo);
                stockTemplatePredictMapper.updateByPrimaryKeySelective(addInfo);
            }

        }
        for (int i = 1; i < allNum; i++) {
            Map nextInfo = getNextInfo(dateFormat, i, (String) jsonObject.get("code"));

            Boolean yesFlag=false;
            //最大价格涨幅
            BigDecimal maxIncrease = new BigDecimal(nextInfo.get("maxPrice").toString()).subtract(new BigDecimal(currInfo.get("newPrice").toString())).divide(new BigDecimal(currInfo.get("newPrice").toString()), 4, BigDecimal.ROUND_HALF_UP);
            BigDecimal minIncrease = new BigDecimal(nextInfo.get("minPrice").toString()).subtract(new BigDecimal(currInfo.get("newPrice").toString())).divide(new BigDecimal(currInfo.get("newPrice").toString()), 4, BigDecimal.ROUND_HALF_UP);
            BigDecimal subtract = maxIncrease.subtract(minIncrease);
            if(aiStrategyParamBo.getFirstRateSub()==null){
                aiStrategyParamBo.setFirstRateSub(new BigDecimal("0.05"));
            }
            //首次振幅小于5
            if(subtract.compareTo(aiStrategyParamBo.getFirstRateSub())<=0&&i==1){
                if(maxIncrease.compareTo(new BigDecimal(0.06))<0){
                    yesFlag=true;
                }
            }
            //最低价小于买入价即可
            if (new BigDecimal(nextInfo.get("minPrice").toString()).compareTo(buyPrice) < 0&&
                    new BigDecimal(nextInfo.get("maxPrice").toString()).compareTo(lastClosePrice) > 0) {
                yesFlag=true;
            }
            if (yesFlag) {
                StockPredictDto dto = new StockPredictDto();
                String dateStr = nextInfo.get("dateStr").toString();
                StockTemplatePredict addInfo = getStockTemplatePredict(stockPredictDto, dateStr, nextInfo.get("code").toString());
                addInfo.setTemplatedSign(stockPredictDto.getAiStrategySign());
                addInfo.setTemplatedName(AiStrategyEnum.getDescByCode(addInfo.getTemplatedSign()));
//            String key = addInfo.getDate() + "_" + addInfo.getTemplatedId() + "_" + addInfo.getCode();
//            redisTemplate.opsForValue().set(key, JsonUtil.toJson(addInfo), 10, TimeUnit.MINUTES);
                List<StockTemplatePredict> templatePredicts = stockTemplatePredictMapper.selectAllByCodeAndTemplatedSignAndDate(addInfo.getCode(), addInfo.getTemplatedSign(), addInfo.getDate());
                if (templatePredicts.size() > 0) {
                    continue;
                }
                rebuildInsertInfo(addInfo,currInfo);
                stockTemplatePredictMapper.insertSelective(addInfo);
                //计算卖出信息
                calcSaleInfo(dto, dateStr, nextInfo.get("code").toString(), addInfo);
                stockTemplatePredictMapper.updateByPrimaryKeySelective(addInfo);

            }
        }
    }

    private Map getNextInfo(String dateFormat, String timeStr, Integer num, String code) {
        String specialDay = null;
        if (num == 0) {
            specialDay = dateFormat;
        } else {
            specialDay = riverRemoteService.getSpecialDay(dateFormat, num);
        }

        StockStrategyQueryDTO dto = new StockStrategyQueryDTO();
        dto.setRiverStockTemplateSign(StockTemplateEnum.STOCK_DETAIL.getSign());
        if (StringUtils.isNotBlank(timeStr)) {
            dto.setTimeStr(timeStr);
        }
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


    private Map getNextInfo(String dateFormat, Integer num, String code) {
        return getNextInfo(dateFormat, "", num, code);
    }

    private StockDetail convert(Map nextInfo) {
        StockDetail stockDetail = new StockDetail();
        stockDetail.setDateStr(nextInfo.get("dateStr").toString());
        stockDetail.setCode(nextInfo.get("code").toString());
        stockDetail.setName(nextInfo.get("name").toString());
        stockDetail.setTradeAmount(new BigDecimal(nextInfo.get("tradeAmount").toString()));
        stockDetail.setMarketValue(new BigDecimal(nextInfo.get("circulationMarketValue").toString()));
        stockDetail.setNewPrice(new BigDecimal(nextInfo.get("newPrice").toString()));
        stockDetail.setAuctionIncreaseRate(new BigDecimal(nextInfo.get("auctionIncreaseRate").toString()));
        stockDetail.setTurnOverRate(new BigDecimal(nextInfo.get("turnOverRate").toString()).setScale(2, BigDecimal.ROUND_HALF_UP));
        stockDetail.setNewIncreaseRate(
                new BigDecimal(nextInfo.get("newPrice").toString()).subtract(new BigDecimal(nextInfo.get("lastClosePrice").toString())).multiply(new BigDecimal(100)).
                        divide(new BigDecimal(nextInfo.get("lastClosePrice").toString()), 4, BigDecimal.ROUND_HALF_UP));
        stockDetail.setAuctionIncreaseRate(new BigDecimal(nextInfo.get("auctionIncreaseRate").toString()));
        return stockDetail;
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


    private void parseStrategyResult(StockPredictDto dto, StrategyBO strategy, String dateStr) {
        JSONArray data = strategy.getData();
        for (Object jo : data) {
            String code = (String) ((JSONObject) jo).get("code");
            StockTemplatePredict addInfo = getStockTemplatePredict(dto, dateStr, code);
//            String key = addInfo.getDate() + "_" + addInfo.getTemplatedId() + "_" + addInfo.getCode();
//            redisTemplate.opsForValue().set(key, JsonUtil.toJson(addInfo), 10, TimeUnit.MINUTES);
            List<StockTemplatePredict> templatePredicts = stockTemplatePredictMapper.selectAllByDateAndTemplatedIdAndCode(dateStr, addInfo.getTemplatedId(), addInfo.getCode());
            if (templatePredicts == null || templatePredicts.size() == 0) {
                stockTemplatePredictMapper.insertSelective(addInfo);
                //计算卖出信息
                calcSaleInfo(dto, dateStr, code, addInfo);
                stockTemplatePredictMapper.updateByPrimaryKeySelective(addInfo);
            }
        }

    }


    private void rebuildInsertInfo(StockTemplatePredict addInfo, Map currInfo){
        StockDetail dateBuyInfoDetail = convert(currInfo);
        addInfo.setBuyMarketValue(dateBuyInfoDetail.getMarketValue());
        addInfo.setBuyTurnoverRate(dateBuyInfoDetail.getTurnOverRate());
        addInfo.setBuyTradeAmount(dateBuyInfoDetail.getTradeAmount());
    }

    private StockTemplatePredict getStockTemplatePredict(StockPredictDto dto, String dateStr, String code) {
        //有买入时间的
        Map timeBuyInfoMap = getNextInfo(dateStr, dto.getBuyTime(), 0, code);
        StockDetail timeBuyInfoDetail = convert(timeBuyInfoMap);
        //无买入时间
        Map dateBuyInfoMap = getNextInfo(dateStr, 0, code);
        StockDetail dateBuyInfoDetail = convert(dateBuyInfoMap);
        StockTemplatePredict addInfo = new StockTemplatePredict();
        addInfo.setId(baseServerFeign.getSnowflakeId());
        addInfo.setDate(dateStr);
        addInfo.setTemplatedId(dto.getId());
        addInfo.setHoldDay(dto.getHoleDay());
        addInfo.setTemplatedSign(dto.getStrategySign());
        addInfo.setSaleTime(dto.getSaleTime());
        addInfo.setBuyTime(dto.getBuyTime());
        addInfo.setBuyPrice(timeBuyInfoDetail.getNewPrice());
        addInfo.setCode(timeBuyInfoDetail.getCode());
        addInfo.setName(timeBuyInfoDetail.getName());
        addInfo.setBuyMarketValue(dateBuyInfoDetail.getMarketValue());
        addInfo.setBuyTurnoverRate(dateBuyInfoDetail.getTurnOverRate());
        addInfo.setBuyTradeAmount(dateBuyInfoDetail.getTradeAmount());
        addInfo.setBuyAuctionIncreaseRate(timeBuyInfoDetail.getAuctionIncreaseRate());
        addInfo.setBuyIncreaseRate(timeBuyInfoDetail.getNewIncreaseRate());
        addInfo.setBuyCloseIncreaseRate(dateBuyInfoDetail.getNewIncreaseRate());

        addInfo.setIndustry(timeBuyInfoMap.get("thsIndustry").toString());
        addInfo.setConcept(timeBuyInfoMap.get("theirConcept").toString());


        return addInfo;
    }

    public void calcSaleInfo(StockPredictDto dto, String dateStr, String code, StockTemplatePredict addInfo) {
        try {
            //有卖出时间的
            if (!StringUtils.isNotBlank(dto.getSaleTime())) {
                dto.setSaleTime("11:29");
            }
            if (dto.getHoleDay()==null) {
                dto.setHoleDay(addInfo.getHoldDay());
            }
            Map timeSaleInfoMap = getNextInfo(dateStr, dto.getSaleTime(), dto.getHoleDay(), code);
            StockDetail timeSaleInfoDetail = convert(timeSaleInfoMap);
            //无卖出时间
            Map dateSaleInfoMap = getNextInfo(dateStr, dto.getHoleDay(), code);
            StockDetail dateSaleInfoDetail = convert(dateSaleInfoMap);

            addInfo.setSaleTime(dto.getSaleTime());
            addInfo.setSalePrice(timeSaleInfoDetail.getNewPrice());
            addInfo.setSaleIncreaseRate(timeSaleInfoDetail.getNewIncreaseRate());
            addInfo.setSaleAuctionIncreaseRate(timeSaleInfoDetail.getAuctionIncreaseRate());
            addInfo.setSaleCloseIncreaseRate(dateSaleInfoDetail.getNewIncreaseRate());
            addInfo.setIndustry(timeSaleInfoMap.get("thsIndustry").toString());
            addInfo.setConcept(timeSaleInfoMap.get("theirConcept").toString());
        } catch (Exception e) {
            log.error("获取当前" + dto.getSaleTime() + "数据出错" + e.getMessage());
        }
    }


    public List<StockTemplatePredict> getAll(StockPredictDto dto) {
        List<StockTemplatePredict> stockTemplatePredicts =null;
        if (StringUtils.isNotBlank(dto.getStrategySign()) && dto.getStrategySign().contains(",")) {
            stockTemplatePredicts = new ArrayList<>();
            String[] split = dto.getStrategySign().split(",");
            for (String aiStrategySign : split) {
                List<StockTemplatePredict> templatePredicts = stockTemplatePredictMapper.selectAllByDateBetweenEqualAndTemplatedIdAndHoldDay(dto.getBeginDate(), dto.getEndDate(), dto.getId(), aiStrategySign, dto.getHoleDay());
                stockTemplatePredicts.addAll(templatePredicts);
            }
            if (stockTemplatePredicts.size() > 0) {
                stockTemplatePredicts = stockTemplatePredicts.stream().sorted(Comparator.comparing(StockTemplatePredict::getDate)).collect(Collectors.toList());
            }
        }
        else if (StringUtils.isNotBlank(dto.getId()) && dto.getId().contains(",")) {
            stockTemplatePredicts = new ArrayList<>();
            String[] split = dto.getId().split(",");
            for (String id : split) {
                List<StockTemplatePredict> templatePredicts = stockTemplatePredictMapper.selectAllByDateBetweenEqualAndTemplatedIdAndHoldDay(dto.getBeginDate(), dto.getEndDate(), id, null, dto.getHoleDay());
                stockTemplatePredicts.addAll(templatePredicts);
            }
            if (stockTemplatePredicts.size() > 0) {
                stockTemplatePredicts = stockTemplatePredicts.stream().sorted(Comparator.comparing(StockTemplatePredict::getDate)).collect(Collectors.toList());
            }
        }else {
           stockTemplatePredicts = stockTemplatePredictMapper.selectAllByDateBetweenEqualAndTemplatedIdAndHoldDay(dto.getBeginDate(), dto.getEndDate(), dto.getId(), dto.getStrategySign(), dto.getHoleDay());

        }


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

    public void updateStatus(StockPredictDto dto) {
        StockTemplatePredict stockTemplatePredict = stockTemplatePredictMapper.selectByPrimaryKey(dto.getId());
        stockTemplatePredict.setStatus(1);
        stockTemplatePredictMapper.updateByPrimaryKey(stockTemplatePredict);
    }

    public void cancelStatus(StockPredictDto dto) {
        StockTemplatePredict stockTemplatePredict = stockTemplatePredictMapper.selectByPrimaryKey(dto.getId());
        stockTemplatePredict.setStatus(null);
        stockTemplatePredictMapper.updateByPrimaryKey(stockTemplatePredict);
    }

    public void updateByQuery(StockPredictDto dto) {

        List<StockTemplatePredict> all = getAll(dto);
        for (StockTemplatePredict stockTemplatePredict : all) {
            StockPredictDto stockPredictDto = new StockPredictDto();
            stockPredictDto.setId(stockTemplatePredict.getId());
            try {
                updateById(stockPredictDto);
            } catch (Exception e) {

            }
        }
    }
}
