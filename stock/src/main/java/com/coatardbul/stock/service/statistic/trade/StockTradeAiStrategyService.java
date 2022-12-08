package com.coatardbul.stock.service.statistic.trade;

import com.coatardbul.baseCommon.util.JsonUtil;
import com.coatardbul.baseService.utils.RedisKeyUtils;
import com.coatardbul.stock.mapper.StockTradeAiStrategyMapper;
import com.coatardbul.stock.model.entity.StockTradeAiStrategy;
import com.coatardbul.stock.feign.BaseServerFeign;
import com.coatardbul.stock.service.romote.RiverRemoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/7/19
 *
 * @author Su Xiaolei
 */
@Slf4j
@Service
public class StockTradeAiStrategyService {
    @Autowired
    StockTradeAiStrategyMapper stockTradeAiStrategyMapper;

    @Autowired
    BaseServerFeign baseServerFeign;
    @Autowired
    RiverRemoteService riverRemoteService;
    @Autowired
    RedisTemplate redisTemplate;

    public void add(StockTradeAiStrategy dto) {
        dto.setId(baseServerFeign.getSnowflakeId());
        stockTradeAiStrategyMapper.insert(dto);
        redisTemplate.opsForValue().set(RedisKeyUtils.getAiStrategyParam(dto.getStrategySign()), dto.getParamObject());
    }

    public void modify(StockTradeAiStrategy dto) {
        stockTradeAiStrategyMapper.updateByPrimaryKeySelective(dto);
        redisTemplate.opsForValue().set(RedisKeyUtils.getAiStrategyParam(dto.getStrategySign()), dto.getParamObject());
    }

    public void delete(StockTradeAiStrategy dto) {
        stockTradeAiStrategyMapper.deleteByPrimaryKey(dto.getId());
        redisTemplate.delete(RedisKeyUtils.getAiStrategyParam(dto.getStrategySign()));
    }


    public Object findAll() {
        return stockTradeAiStrategyMapper.selectAll();
    }
}
