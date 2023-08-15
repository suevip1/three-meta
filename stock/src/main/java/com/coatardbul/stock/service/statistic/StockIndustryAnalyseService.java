package com.coatardbul.stock.service.statistic;

import com.alibaba.fastjson.JSONArray;
import com.coatardbul.baseCommon.constants.StockTemplateEnum;
import com.coatardbul.baseCommon.model.bo.StrategyBO;
import com.coatardbul.baseCommon.model.dto.StockStrategyQueryDTO;
import com.coatardbul.baseCommon.util.JsonUtil;
import com.coatardbul.baseService.service.StockStrategyCommonService;
import com.coatardbul.baseService.service.romote.RiverRemoteService;
import com.coatardbul.stock.mapper.StockIndustryAnalyseMapper;
import com.coatardbul.stock.model.dto.StockEmotionDayRangeDTO;
import com.coatardbul.stock.model.dto.StockIndustryAnalyseDTO;
import com.coatardbul.stock.model.entity.StockIndustryAnalyse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2023/6/14
 *
 * @author Su Xiaolei
 */
@Service
@Slf4j
public class StockIndustryAnalyseService {

    @Autowired
    StockIndustryAnalyseMapper stockIndustryAnalyseMapper;

    @Autowired
    RiverRemoteService riverRemoteService;
    @Autowired
    StockStrategyCommonService stockStrategyCommonService;

    public void add(StockIndustryAnalyseDTO dto) {

        StockIndustryAnalyse convert = convert(dto);
        StockIndustryAnalyse stockIndustryAnalyse = stockIndustryAnalyseMapper.selectByPrimaryKey(dto.getDate());
        if (stockIndustryAnalyse == null) {
            stockIndustryAnalyseMapper.insertSelective(convert);
        }else {
            stockIndustryAnalyseMapper.updateByPrimaryKeySelective(stockIndustryAnalyse);
        }
    }

    public List<StockIndustryAnalyseDTO> getAll() {
        List<StockIndustryAnalyse> stockIndustryAnalyses = stockIndustryAnalyseMapper.selectByAll();
        List<StockIndustryAnalyseDTO> stockIndustryAnalyseDTOS = new ArrayList<>();
        for (StockIndustryAnalyse stockIndustryAnalyse : stockIndustryAnalyses) {
            StockIndustryAnalyseDTO stockIndustryAnalyseDTO = convert(stockIndustryAnalyse);
            stockIndustryAnalyseDTOS.add(stockIndustryAnalyseDTO);
        }
        return stockIndustryAnalyseDTOS;
    }


    public StockIndustryAnalyse convert(StockIndustryAnalyseDTO dto) {
        StockIndustryAnalyse stockIndustryAnalyse = new StockIndustryAnalyse();
        stockIndustryAnalyse.setDate(dto.getDate());
        stockIndustryAnalyse.setCallAuctionGreate5Info(getStringJson(dto.getCallAuctionGreate5Info()));
        stockIndustryAnalyse.setGreateThanLine(getStringJson(dto.getGreateThanLine()));
        stockIndustryAnalyse.setBigIncrease(getStringJson(dto.getBigIncrease()));
        stockIndustryAnalyse.setNextCallAuctionBuy(getStringJson(dto.getNextCallAuctionBuy()));
        stockIndustryAnalyse.setSmallIncrease(getStringJson(dto.getSmallIncrease()));
        stockIndustryAnalyse.setUplimitInfo(getStringJson(dto.getUplimitInfo()));
        stockIndustryAnalyse.setRemark(dto.getRemark());
        return stockIndustryAnalyse;
    }

    public StockIndustryAnalyseDTO convert(StockIndustryAnalyse stockIndustryAnalyse) {
        StockIndustryAnalyseDTO dto = new StockIndustryAnalyseDTO();
        dto.setDate(stockIndustryAnalyse.getDate());
        dto.setCallAuctionGreate5Info(getArrByStr(stockIndustryAnalyse.getCallAuctionGreate5Info()));
        dto.setCallAuctionGreate2Info(getArrByStr(stockIndustryAnalyse.getCallAuctionGreate2Info()));
        dto.setGreateThanLine(getArrByStr(stockIndustryAnalyse.getGreateThanLine()));
        dto.setBigIncrease(getArrByStr(stockIndustryAnalyse.getBigIncrease()));
        dto.setNextCallAuctionBuy(getArrByStr(stockIndustryAnalyse.getNextCallAuctionBuy()));
        dto.setSmallIncrease(getArrByStr(stockIndustryAnalyse.getSmallIncrease()));
        dto.setUplimitInfo(getArrByStr(stockIndustryAnalyse.getUplimitInfo()));
        dto.setIncreaseGreate5Info(getArrByStr(stockIndustryAnalyse.getIncreaseGreate5Info()));
        dto.setRemark(stockIndustryAnalyse.getRemark());
        return dto;
    }

    private List<String> getArrByStr(String str) {
        if (StringUtils.isNotBlank(str)) {
            List<String> arr = JsonUtil.readToValue(str, new TypeReference<List<String>>() {
            });
            return arr;
        } else {
            return new ArrayList<>();
        }
    }

    private String getStringJson(List<String> strArr) {
        if (strArr == null) {
            strArr = new ArrayList<>();
        }
        return JsonUtil.toJson(strArr);
    }

    public List<StockIndustryAnalyseDTO> findDayRange(StockEmotionDayRangeDTO dto) {
        List<StockIndustryAnalyse> stockIndustryAnalyses = stockIndustryAnalyseMapper.selectAllByDateBetweenEqual(dto.getBeginDate(), dto.getEndDate());
        List<StockIndustryAnalyseDTO> stockIndustryAnalyseDTOS = new ArrayList<>();
        for (StockIndustryAnalyse stockIndustryAnalyse : stockIndustryAnalyses) {
            StockIndustryAnalyseDTO stockIndustryAnalyseDTO = convert(stockIndustryAnalyse);
            stockIndustryAnalyseDTOS.add(stockIndustryAnalyseDTO);
        }
        return stockIndustryAnalyseDTOS;
    }

    public void modify(StockIndustryAnalyseDTO dto) {
        StockIndustryAnalyse convert = convert(dto);
        stockIndustryAnalyseMapper.updateByPrimaryKey(convert);

    }

    public void delete(StockIndustryAnalyseDTO dto) {
        stockIndustryAnalyseMapper.deleteByPrimaryKey(dto.getDate());
    }

    public StockIndustryAnalyseDTO get(StockIndustryAnalyseDTO dto) {
        StockIndustryAnalyse stockIndustryAnalyse = stockIndustryAnalyseMapper.selectByPrimaryKey(dto.getDate());
        return convert(stockIndustryAnalyse);
    }

    public void updateDayRange(StockEmotionDayRangeDTO dto) {
        List<String> dateIntervalList = riverRemoteService.getDateIntervalList(dto.getBeginDate(), dto.getEndDate());
        for (String dateStr : dateIntervalList) {
            updateDay(dateStr);
        }
    }

    private void updateDay(String dateStr) {
        StockIndustryAnalyse stockIndustryAnalyse = new StockIndustryAnalyse();
        stockIndustryAnalyse.setDate(dateStr);
        stockIndustryAnalyse.setCallAuctionGreate5Info(getStrategyInfo(dateStr, StockTemplateEnum.AUCTION_GREATE5.getSign()));
        stockIndustryAnalyse.setCallAuctionGreate2Info(getStrategyInfo(dateStr, StockTemplateEnum.AUCTION_GREATE2.getSign()));
        stockIndustryAnalyse.setUplimitInfo(getStrategyInfo(dateStr, StockTemplateEnum.ZCK_UPLIMIT.getSign()));
        stockIndustryAnalyse.setIncreaseGreate5Info(getStrategyInfo(dateStr, StockTemplateEnum.INCREASE_GREATE5.getSign()));
        StockIndustryAnalyse temp = stockIndustryAnalyseMapper.selectByPrimaryKey(dateStr);
        if(temp==null){
            stockIndustryAnalyseMapper.insert(stockIndustryAnalyse);
        }else {
            stockIndustryAnalyseMapper.updateByPrimaryKeySelective(stockIndustryAnalyse);
        }
    }

    private String getStrategyInfo(String dateStr, String objectSign) {
        List<String> strategyInfoList = getStrategyInfoList(dateStr, objectSign);
        return getStringJson(strategyInfoList);
    }

    private List<String> getStrategyInfoList(String dateStr, String objectSign) {
        List<String> result = new ArrayList<String>();
        StockStrategyQueryDTO dto = new StockStrategyQueryDTO();
        dto.setRiverStockTemplateSign(StockTemplateEnum.MACD_FILTER.getSign());
        dto.setDateStr(dateStr);
        dto.setRiverStockTemplateSign(objectSign);
        StrategyBO strategy = null;
        try {
            strategy = stockStrategyCommonService.strategy(dto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        if (strategy == null || strategy.getData().size() == 0) {
            return result;
        }
        JSONArray data = strategy.getData();
        Map<String, Integer> map = new TreeMap<>();
        for (int i = 0; i < data.size(); i++) {
            String themeAllStr = data.getJSONObject(i).getString("所属同花顺行业");
            if (StringUtils.isNotBlank(themeAllStr)) {
                String themeStr = themeAllStr.split("-")[0];
                if (map.containsKey(themeStr)) {
                    map.put(themeStr, map.get(themeStr) + 1);
                } else {
                    map.put(themeStr, 1);
                }
            }
        }
        List<Map.Entry<String, Integer>> list = new LinkedList<>(map.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {

            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }

        });
        for (Map.Entry<String, Integer> entry : list) {
            if(entry.getValue()>1){
                result.add(entry.getKey() + "-" + entry.getValue());
            }
        }
        return result;
    }
}
