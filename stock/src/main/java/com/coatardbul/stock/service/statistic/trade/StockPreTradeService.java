package com.coatardbul.stock.service.statistic.trade;

import com.coatardbul.baseCommon.util.DateTimeUtil;
import com.coatardbul.baseCommon.util.JsonUtil;
import com.coatardbul.baseService.service.StockStrategyCommonService;
import com.coatardbul.baseService.service.StockUpLimitAnalyzeCommonService;
import com.coatardbul.baseService.service.romote.RiverRemoteService;
import com.coatardbul.baseService.utils.RedisKeyUtils;
import com.coatardbul.stock.model.entity.StockPreTrade;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/12/27
 *
 * @author Su Xiaolei
 */
@Slf4j
@Service
public class StockPreTradeService {
    @Autowired
    StockUpLimitAnalyzeCommonService stockUpLimitAnalyzeCommonService;
    @Autowired
    RiverRemoteService riverRemoteService;
    @Autowired
    StockStrategyCommonService stockStrategyCommonService;

    @Autowired
    RedisTemplate redisTemplate;

    public void add(StockPreTrade dto) {
        String nowPreTradeStockInfo = RedisKeyUtils.getNowPreTradeStockInfo(dto.getCode());
        redisTemplate.opsForValue().set(nowPreTradeStockInfo, JsonUtil.toJson(dto), 12, TimeUnit.HOURS);

    }

    public void modify(StockPreTrade dto) {
        String nowPreTradeStockInfo = RedisKeyUtils.getNowPreTradeStockInfo(dto.getCode());
        redisTemplate.opsForValue().set(nowPreTradeStockInfo, JsonUtil.toJson(dto), 12, TimeUnit.HOURS);

    }

    public void delete(StockPreTrade dto) {
        String nowPreTradeStockInfo = RedisKeyUtils.getNowPreTradeStockInfo(dto.getCode());
        redisTemplate.delete(nowPreTradeStockInfo);
    }

    public List getAll(StockPreTrade dto) {
        List<StockPreTrade> result = new ArrayList<>();
        String key="";
        if(StringUtils.isNotBlank(dto.getTradeDate())){
            key= RedisKeyUtils.getPreTradeStockInfoPattern(dto.getTradeDate());
        }else {
            String dateFormat = DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD);
            key= RedisKeyUtils.getPreTradeStockInfoPattern(dateFormat);
        }
        Set keys = redisTemplate.keys(key);
        for(Object keyUrl:keys){
            if(keyUrl instanceof String){
                String jsonStr = (String)redisTemplate.opsForValue().get(keyUrl);
                StockPreTrade stockPreTrade = JsonUtil.readToValue(jsonStr, StockPreTrade.class);
                result.add(stockPreTrade);
            }
        }
        return result;
    }






}
