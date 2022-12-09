package com.coatardbul.stock.service.statistic.trade;

import com.coatardbul.stock.model.entity.StockTradeSellTask;
import com.coatardbul.stock.feign.BaseServerFeign;
import com.coatardbul.stock.mapper.StockTradeSellTaskMapper;
import com.coatardbul.stock.mapper.StockTradeStrategyMapper;
import com.coatardbul.stock.model.bo.QuartzBean;
import com.coatardbul.stock.service.romote.RiverRemoteService;
import com.coatardbul.stock.service.statistic.StockQuartzService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class StockTradeSellTaskService {
    @Autowired
    StockTradeSellTaskMapper stockTradeSellTaskMapper;

    @Autowired
    BaseServerFeign baseServerFeign;
    @Autowired
    RiverRemoteService riverRemoteService;
    @Autowired
    StockQuartzService stockQuartzService;
    @Autowired
    StockTradeStrategyMapper stockTradeStrategyMapper;
    @Autowired
    StockTradeService stockTradeService;

    public void add(StockTradeSellTask dto) throws Exception {
        dto.setId(baseServerFeign.getSnowflakeId());
        dto.setJobName(stockTradeService.getJobName(dto));
        stockTradeSellTaskMapper.insert(dto);

        QuartzBean quartzBean=stockTradeService.getQuartzBean(dto.getStrategySign(),dto.getJobName(),dto.getCron());
        stockQuartzService.createScheduleJobCron(quartzBean);
    }

    public void modify(StockTradeSellTask dto) throws Exception {
        stockTradeSellTaskMapper.updateByPrimaryKeySelective(dto);
        StockTradeSellTask stockTradeSellTask = stockTradeSellTaskMapper.selectByPrimaryKey(dto.getId());

        QuartzBean quartzBean=stockTradeService.getQuartzBean(stockTradeSellTask.getStrategySign(),stockTradeSellTask.getJobName(),stockTradeSellTask.getCron());
        stockQuartzService.updateScheduleJobCron(quartzBean);
    }

    public void delete(StockTradeSellTask dto) throws Exception {
        StockTradeSellTask stockTradeSellTask = stockTradeSellTaskMapper.selectByPrimaryKey(dto.getId());

        stockTradeSellTaskMapper.deleteByPrimaryKey(dto.getId());
        stockQuartzService.deleteScheduleJob(stockTradeSellTask.getJobName(),null);

    }


    public Object findAll() {
        return stockTradeSellTaskMapper.selectByAll(null);
    }
}
