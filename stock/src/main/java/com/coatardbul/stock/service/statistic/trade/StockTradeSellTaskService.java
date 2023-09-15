package com.coatardbul.stock.service.statistic.trade;

import com.coatardbul.baseService.service.SnowFlakeService;
import com.coatardbul.baseService.service.romote.RiverRemoteService;
import com.coatardbul.stock.mapper.StockTradeSellTaskMapper;
import com.coatardbul.stock.mapper.StockTradeStrategyMapper;
import com.coatardbul.stock.model.bo.QuartzBean;
import com.coatardbul.stock.model.entity.StockTradeSellTask;
import com.coatardbul.stock.service.StockUserBaseService;
import com.coatardbul.stock.service.statistic.StockQuartzService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

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
    SnowFlakeService snowFlakeService;
    @Autowired
    RiverRemoteService riverRemoteService;
    @Autowired
    StockQuartzService stockQuartzService;
    @Autowired
    StockTradeStrategyMapper stockTradeStrategyMapper;
    @Autowired
    StockTradeService stockTradeService;
    @Autowired
    StockUserBaseService stockUserBaseService;
    public void add(StockTradeSellTask dto) throws Exception {
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();

        String userName = stockUserBaseService.getCurrUserName(request);
        dto.setId(snowFlakeService.getSnowId());
        dto.setJobName(stockTradeService.getJobName(dto));
        dto.setTradeUserId(userName);

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
