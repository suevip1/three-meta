package com.coatardbul.stock.task.powerJob;

import com.coatardbul.baseCommon.util.DateTimeUtil;
import com.coatardbul.baseCommon.util.JsonUtil;
import com.coatardbul.baseService.service.CronRefreshService;
import com.coatardbul.stock.model.dto.StockEmotionDayDTO;
import com.coatardbul.stock.service.statistic.StockCronRefreshService;
import com.coatardbul.stock.service.statistic.business.StockStrategyWatchService;
import com.coatardbul.stock.service.statistic.trade.StockTradeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.annotation.PowerJobHandler;
import tech.powerjob.worker.core.processor.TaskContext;

import javax.annotation.Resource;
import java.util.Date;

/**
 * <p>
 * Note:策略扫描，扫描表中的固定类型的数据
 * <p>
 * Date: 2022/3/5
 *
 * @author Su Xiaolei
 */
@Slf4j
@Component
public class PowerJobStrategyWatchJob {

    @Autowired
    StockStrategyWatchService stockStrategyWatchService;
    @Autowired
    StockCronRefreshService stockCronRefreshService;
    @Resource
     CronRefreshService cronRefreshService;
    @Autowired
    StockTradeService stockTradeService;

     @PowerJobHandler(name ="strategyNowWatchJobHandler")
    public void strategyWatchJobHandler(TaskContext context) throws Exception {
        String param = context.getJobParams();
        log.info("策略监控定时任务开始,传递参数为：" + param);
        StockEmotionDayDTO stockEmotionDayDTO = new StockEmotionDayDTO();
        stockEmotionDayDTO.setDateStr(DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD));
        stockEmotionDayDTO.setTimeStr(DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.HH_MM));
        log.info("策略监控定时任务开始,请求参数为：" + stockEmotionDayDTO);
        stockStrategyWatchService.strategyNowWatch(stockEmotionDayDTO);
        log.info("策略监控定时任务结束");
    }


     @PowerJobHandler(name ="historyStrategyWatchJobHandler")
    public void historyStrategyWatchJobHandler(TaskContext context) throws Exception {
        String param = context.getJobParams();
        log.info("历史策略监控定时任务开始,传递参数为：" + param);
        if (StringUtils.isNotBlank(param)) {
            StockEmotionDayDTO dto = JsonUtil.readToValue(param, StockEmotionDayDTO.class);
            stockStrategyWatchService.simulateHistoryStrategyWatch(dto);
        }
        log.info("历史策略监控定时任务结束");
    }


     @PowerJobHandler(name ="emailStrategyWatchJobHandler")
    public void emailStrategyWatchJobHandler(TaskContext context) throws Exception {
        String param = context.getJobParams();
        log.info("邮件策略监控定时任务开始,传递参数为：" + param);
        if (StringUtils.isNotBlank(param)) {
            StockEmotionDayDTO stockEmotionDayDTO = new StockEmotionDayDTO();
            stockEmotionDayDTO.setDateStr(DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD));
            stockEmotionDayDTO.setTimeStr(DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.HH_MM));
            stockStrategyWatchService.emailStrategyWatch(stockEmotionDayDTO);
        }
        log.info("邮件策略监控定时任务结束");
    }





     @PowerJobHandler(name ="cronRefreshJobHandler")
    public void cronRefreshJobHandler(TaskContext context) throws Exception {
        log.info("定时刷新股票信息定时任务开始,传递参数为：" );
        Boolean isOpenCronRefreshFlag = cronRefreshService.getIsOpenCronRefreshFlag();
        if(isOpenCronRefreshFlag){
            stockCronRefreshService.cronRefresh();
        }
        log.info("定时刷新股票信息定时任务结束");
    }
}
