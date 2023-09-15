package com.coatardbul.stock.service.statistic.dayStatic.scatter;

import com.alibaba.fastjson.JSONObject;
import com.coatardbul.baseCommon.model.bo.StrategyBO;
import com.coatardbul.baseCommon.model.dto.StockStrategyQueryDTO;
import com.coatardbul.baseCommon.util.JsonUtil;
import com.coatardbul.baseService.feign.RiverServerFeign;
import com.coatardbul.baseService.service.SnowFlakeService;
import com.coatardbul.baseService.service.romote.RiverRemoteService;
import com.coatardbul.stock.mapper.StockDayEmotionMapper;
import com.coatardbul.stock.mapper.StockMinuterEmotionMapper;
import com.coatardbul.stock.mapper.StockScatterStaticMapper;
import com.coatardbul.stock.mapper.StockStaticTemplateMapper;
import com.coatardbul.stock.model.bo.DayAxiosMiddleBaseBO;
import com.coatardbul.stock.model.bo.StockCallAuctionBo;
import com.coatardbul.stock.model.bo.StockLineInfoBo;
import com.coatardbul.stock.model.dto.StockEmotionDayDTO;
import com.coatardbul.stock.model.entity.StockScatterStatic;
import com.coatardbul.stock.model.entity.StockStaticTemplate;
import com.coatardbul.stock.service.base.StockStrategyService;
import com.coatardbul.stock.service.statistic.business.StockVerifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/3/1
 *
 * @author Su Xiaolei
 */
@Service
@Slf4j
public class ScatterDayUpLimitCallAuctionService extends ScatterDayAbstractService {
    @Autowired
    SnowFlakeService snowFlakeService;
    @Autowired
    RiverRemoteService riverRemoteService;
    @Autowired
    StockStrategyService stockStrategyService;
    @Autowired
    RiverServerFeign riverServerFeign;
    @Autowired
    StockStaticTemplateMapper stockStaticTemplateMapper;
    @Autowired
    StockMinuterEmotionMapper stockMinuterEmotionMapper;
    @Autowired
    StockDayEmotionMapper stockDayEmotionMapper;
    @Autowired
    StockScatterStaticMapper stockScatterStaticMapper;
    @Autowired
    StockVerifyService stockVerifyService;

    @Override
    public void refreshDayProcess(StockEmotionDayDTO dto, StockStaticTemplate stockStaticTemplate) throws IllegalAccessException, ParseException {
        //获取模型对象中的模板id集合,便于根据模板id查询对应的数据结果
        List<String> templateIdList = stockStrategyService.getTemplateIdList(stockStaticTemplate);

        if (templateIdList != null && templateIdList.size() > 0) {
            StockScatterStatic stockScatterStatic = new StockScatterStatic();
            stockScatterStatic.setId(snowFlakeService.getSnowId());
            stockScatterStatic.setDate(dto.getDateStr());
//            stockScatterStatic.setObjectStaticArray();
            stockScatterStatic.setObjectSign(dto.getObjectEnumSign());


            //获取数组里面的对象
            List<DayAxiosMiddleBaseBO> list = new ArrayList<>();
            String templateId = templateIdList.get(0);
            StockStrategyQueryDTO stockStrategyQueryDTO = new StockStrategyQueryDTO();
            stockStrategyQueryDTO.setRiverStockTemplateId(templateId);
            stockStrategyQueryDTO.setDateStr(dto.getDateStr());
            StrategyBO strategy = null;
            try {
                strategy = stockStrategyService.strategy(stockStrategyQueryDTO);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            List<StockLineInfoBo> objectArray = new ArrayList<>();
            if (strategy != null && strategy.getTotalNum() > 0) {

                String queryDateStr = dto.getDateStr().replace("-", "");
                strategy.getData().forEach(item -> {
                    Set<Map.Entry<String, Object>> entries = ((JSONObject) item).entrySet();
                    StockCallAuctionBo stockCallAuctionBo = new StockCallAuctionBo();
                    entries.forEach(stockLineInfo -> {
                        if (stockLineInfo.getKey().equals("code")) {
                            stockCallAuctionBo.setCode((String) stockLineInfo.getValue());
                        }
                        if (stockLineInfo.getKey().contains("股票简称")) {
                            stockCallAuctionBo.setName((String) stockLineInfo.getValue());
                        }
                        if (stockLineInfo.getKey().contains("总市值")) {
                            stockCallAuctionBo.setMarketValue(new BigDecimal(String.valueOf(stockLineInfo.getValue())));
                        }
                        if (stockLineInfo.getKey().contains("竞价金额")) {
                            if (stockLineInfo.getKey().contains(queryDateStr)) {
                                stockCallAuctionBo.setCompareTradeMoney(new BigDecimal(String.valueOf(stockLineInfo.getValue())));
                            } else {
                                stockCallAuctionBo.setTradeMoney(new BigDecimal(String.valueOf(stockLineInfo.getValue())));
                            }
                        }
                        if (stockLineInfo.getKey().contains("竞价涨幅")) {
                            if (stockLineInfo.getKey().contains(queryDateStr)) {
                                stockCallAuctionBo.setCompareIncreaseRange(new BigDecimal(String.valueOf(stockLineInfo.getValue())));
                            } else {
                                stockCallAuctionBo.setIncreaseRange(new BigDecimal(String.valueOf(stockLineInfo.getValue())));
                            }
                        }
                        if (stockLineInfo.getKey().contains("换手率")) {
                            if (stockLineInfo.getKey().contains(queryDateStr)) {
                                stockCallAuctionBo.setCompareTurnoverRate(new BigDecimal(String.valueOf(stockLineInfo.getValue())));
                            } else {
                                stockCallAuctionBo.setTurnoverRate(new BigDecimal(String.valueOf(stockLineInfo.getValue())));
                            }
                        }
                        if (stockLineInfo.getKey().contains("{/}竞价金额")) {
                            stockCallAuctionBo.setCallAuctionRatio(new BigDecimal(String.valueOf(stockLineInfo.getValue())));
                        }

                    });
                    objectArray.add(stockCallAuctionBo);
                });

            }
            stockScatterStatic.setObjectStaticArray(JsonUtil.toJson(objectArray));
            stockScatterStaticMapper.deleteByDateAndObjectSign(dto.getDateStr(), dto.getObjectEnumSign());
            stockScatterStaticMapper.insertSelective(stockScatterStatic);
        }
    }






}
