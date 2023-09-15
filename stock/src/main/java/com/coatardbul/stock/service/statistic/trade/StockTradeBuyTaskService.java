package com.coatardbul.stock.service.statistic.trade;

import com.coatardbul.baseService.entity.bo.StockTradeBuyTask;
import com.coatardbul.baseService.service.SnowFlakeService;
import com.coatardbul.baseService.service.romote.RiverRemoteService;
import com.coatardbul.stock.mapper.StockTradeBuyTaskMapper;
import com.coatardbul.stock.mapper.StockTradeStrategyMapper;
import com.coatardbul.stock.model.bo.QuartzBean;
import com.coatardbul.stock.model.entity.StockTradeStrategy;
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
public class StockTradeBuyTaskService {
    @Autowired
    StockTradeBuyTaskMapper stockTradeBuyTaskMapper;

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

    public void add(StockTradeBuyTask dto) throws Exception {
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();

        String userName = stockUserBaseService.getCurrUserName(request);
        dto.setId(snowFlakeService.getSnowId());
        dto.setJobName(stockTradeService.getJobName(dto));
        dto.setTradeUserId(userName);
        stockTradeBuyTaskMapper.insert(dto);

        StockTradeStrategy stockTradeStrategies = stockTradeStrategyMapper.selectAllBySign(dto.getStrategySign());

        QuartzBean quartzBean=stockTradeService.getQuartzBean(stockTradeStrategies,dto.getJobName(),dto.getCron());
        stockQuartzService.createScheduleJobCron(quartzBean);

    }

    public void modify(StockTradeBuyTask dto) throws Exception {
        stockTradeBuyTaskMapper.updateByPrimaryKeySelective(dto);
        StockTradeBuyTask stockTradeBuyTask = stockTradeBuyTaskMapper.selectByPrimaryKey(dto.getId());
        QuartzBean quartzBean=stockTradeService.getQuartzBean(dto.getStrategySign(),stockTradeBuyTask.getJobName(),stockTradeBuyTask.getCron());
        stockQuartzService.updateScheduleJobCron(quartzBean);
    }

    public void delete(StockTradeBuyTask dto) throws Exception {
        StockTradeBuyTask stockTradeBuyTask = stockTradeBuyTaskMapper.selectByPrimaryKey(dto.getId());

        stockTradeBuyTaskMapper.deleteByPrimaryKey(dto.getId());

        stockQuartzService.deleteScheduleJob(stockTradeBuyTask.getJobName(),null);
    }


    public Object findAll() {
        return stockTradeBuyTaskMapper.selectByAll(null);
    }
}
