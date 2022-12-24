package com.coatardbul.stock.service.statistic;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coatardbul.baseCommon.constants.StockTemplateEnum;
import com.coatardbul.baseCommon.model.bo.StrategyBO;
import com.coatardbul.baseCommon.model.dto.StockStrategyQueryDTO;
import com.coatardbul.baseCommon.util.JsonUtil;
import com.coatardbul.baseCommon.util.ReflexUtil;
import com.coatardbul.baseService.feign.BaseServerFeign;
import com.coatardbul.baseService.service.StockStrategyCommonService;
import com.coatardbul.baseService.service.StockUpLimitAnalyzeCommonService;
import com.coatardbul.baseService.service.romote.RiverRemoteService;
import com.coatardbul.stock.common.constants.Constant;
import com.coatardbul.stock.mapper.StockUplimitAnalyzeMapper;
import com.coatardbul.stock.model.dto.StockDefineStaticDTO;
import com.coatardbul.stock.model.dto.StockUplimitAnalyzeDTO;
import com.coatardbul.stock.model.entity.StockUplimitAnalyze;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/7/3
 *
 * @author Su Xiaolei
 */
@Slf4j
@Service
public class StockUpLimitAnalyzeService  extends StockUpLimitAnalyzeCommonService {


    @Autowired
    BaseServerFeign baseServerFeign;
    @Autowired
    StockUplimitAnalyzeMapper stockUplimitAnalyzeMapper;

    @Autowired
    RiverRemoteService riverRemoteService;

    @Autowired
    StockStrategyCommonService  stockStrategyCommonService;

    public void add(Map dto) throws IllegalAccessException {

        stockUplimitAnalyzeMapper.deleteByDateAndCode(
                (String) dto.get("dateStr"),
                (String) dto.get("code"));
        StockUplimitAnalyze stockUplimitAnalyze = new StockUplimitAnalyze();
        stockUplimitAnalyze.setId(baseServerFeign.getSnowflakeId());
        rebuild(stockUplimitAnalyze, dto);
        stockUplimitAnalyzeMapper.insert(stockUplimitAnalyze);
    }

    private void rebuild(StockUplimitAnalyze stockUplimitAnalyze, Map dto) throws IllegalAccessException {
        stockUplimitAnalyze.setCode(String.valueOf(dto.get("code")));
        stockUplimitAnalyze.setDate(String.valueOf(dto.get("dateStr")));
        stockUplimitAnalyze.setObjectSign(String.valueOf(dto.get("objectEnumSign")));
        ReflexUtil.singleSetMaptoObject(dto, stockUplimitAnalyze, stockUplimitAnalyze.getClass());
        stockUplimitAnalyze.setLastTurnOver(String.valueOf(dto.get("lastTurnOverRate")));
        stockUplimitAnalyze.setLastVol(String.valueOf(dto.get("lastVolRate")));
        stockUplimitAnalyze.setAuctionIncrease(String.valueOf(dto.get("auctionIncreaseRate")));
        stockUplimitAnalyze.setNewIncrease(String.valueOf(dto.get("newIncreaseRate")));
        stockUplimitAnalyze.setAuctionTurnOver(String.valueOf(dto.get("auctionTurnOverRate")));
        stockUplimitAnalyze.setMarketValue(String.valueOf(dto.get("marketValue")));
        stockUplimitAnalyze.setCirculationMarketValue(String.valueOf(dto.get("circulationMarketValue")));
        stockUplimitAnalyze.setCurrentPrice(String.valueOf(dto.get("newPrice")));

        stockUplimitAnalyze.setJsonDetail(JsonUtil.toJson(dto));

    }

    public List<Map> getAll(StockUplimitAnalyzeDTO dto) {
        PageHelper.startPage(1, 600);
        List<StockUplimitAnalyze> stockUplimitAnalyzes = stockUplimitAnalyzeMapper.selectByCondition(dto);
        if (stockUplimitAnalyzes.size() > 0) {
            return stockUplimitAnalyzes.stream().map(this::convert).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    private Map convert(StockUplimitAnalyze dto) {
        String jsonDetail = dto.getJsonDetail();
        if (StringUtils.isNotBlank(jsonDetail)) {
            return JsonUtil.readToValue(jsonDetail, Map.class);
        } else {
            return new HashMap();
        }
    }

    public void onlyAdd(Map dto) throws IllegalAccessException {
        StockUplimitAnalyze stockUplimitAnalyze = new StockUplimitAnalyze();
        stockUplimitAnalyze.setId(baseServerFeign.getSnowflakeId());
        rebuild(stockUplimitAnalyze, dto);
        stockUplimitAnalyzeMapper.insert(stockUplimitAnalyze);
    }

    /**
     * 模拟添加二板数据的数据
     *
     * @param dto
     */
    public void simulateAdd(StockDefineStaticDTO dto) {
        //查看开始，结束时间之间二板的信息
        List<String> dateIntervalList = riverRemoteService.getDateIntervalList(dto.getBeginDateStr(), dto.getEndDateStr());
        if (dateIntervalList != null && dateIntervalList.size() > 0) {

            for (String dateStr : dateIntervalList) {
                Constant.onceUpLimitThreadPool.submit(new Runnable() {
                    @Override
                    public void run() {
                        stockUplimitAnalyzeMapper.deleteByDate(dateStr);
                        StockStrategyQueryDTO stockStrategyQueryDTO = new StockStrategyQueryDTO();
                        stockStrategyQueryDTO.setRiverStockTemplateSign(StockTemplateEnum.FIRST_UP_LIMIT_WATCH_TWO.getSign());
                        stockStrategyQueryDTO.setDateStr(dateStr);
                        try {
                            StrategyBO strategy = stockStrategyCommonService.strategy(stockStrategyQueryDTO);
                            Integer totalNum = strategy.getTotalNum();
                            if (totalNum > 0) {
                                JSONArray data = strategy.getData();
                                for (int i = 0; i < data.size(); i++) {
                                    JSONObject jsonObject = data.getJSONObject(i);
                                    Map convert = convert(jsonObject, dateStr);
                                    if(convert.size()>0){
                                        onlyAdd(convert);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


            }

        }
    }



}
