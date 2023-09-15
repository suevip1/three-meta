package com.coatardbul.stock.service.statistic.trade;

import com.coatardbul.baseService.service.SnowFlakeService;
import com.coatardbul.baseService.service.romote.RiverRemoteService;
import com.coatardbul.baseService.utils.RedisKeyUtils;
import com.coatardbul.stock.mapper.StockTradeAiStrategyMapper;
import com.coatardbul.stock.model.entity.StockTradeAiStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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
    SnowFlakeService snowFlakeService;
    @Autowired
    RiverRemoteService riverRemoteService;
    @Autowired
    RedisTemplate redisTemplate;

    public void add(StockTradeAiStrategy dto) {
        dto.setId(snowFlakeService.getSnowId());
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
