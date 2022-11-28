package com.coatardbul.stock.service.statistic;

import com.alibaba.fastjson.JSONObject;
import com.coatardbul.stock.model.entity.StockTradeSellTask;
import com.coatardbul.stock.service.statistic.business.StockVerifyService;
import com.coatardbul.stock.service.statistic.trade.StockTradeService;
import com.coatardbul.stock.common.util.DateTimeUtil;
import com.coatardbul.stock.mapper.StockTradeSellTaskMapper;
import com.coatardbul.stock.service.statistic.tradeQuartz.LowRateSellTradeService;
import com.coatardbul.stock.service.statistic.tradeQuartz.TimeSellTradeService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

@Component
@Slf4j
public class SellTask extends QuartzJobBean {
    @Autowired
    StockTradeService stockTradeService;
    @Autowired
    StockTradeSellTaskMapper stockTradeSellTaskMapper;
    @Autowired
    StockVerifyService stockVerifyService;
    @Autowired
    StockQuartzService stockQuartzService;
    @Autowired
    LowRateSellTradeService lowRateSellTradeService;
    @Autowired
    TimeSellTradeService timeSellTradeService;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        JobKey jobKey = context.getJobDetail().getKey();
        String jobName = jobKey.getName();
        String jobGroupName = jobKey.getGroup();
        String[] strings = stockTradeService.splitJobName(jobName);
        Boolean flag = false;
        //买入配置id
        String buyId = strings[0];
        StockTradeSellTask stockTradeSellTask = stockTradeSellTaskMapper.selectByPrimaryKey(buyId);
        if ("TIME_SELL".equals(stockTradeSellTask.getStrategySign())) {

//            String strategyParam = stockTradeSellTask.getStrategyParam();
//            JSONObject jsonObject = JSONObject.parseObject(strategyParam);
//            String buyDate = jsonObject.getString("buyDate");
//            String buyTime = jsonObject.getString("buyTime");

            flag = timeSellTradeService.tradeProcess(convertAmount(stockTradeSellTask.getTradeAmount()),convertAmount(stockTradeSellTask.getTradeNum()), stockTradeSellTask.getStockCode());
        }
        //低于多少卖出
        if ("LOW_RATE_SELL".equals(stockTradeSellTask.getStrategySign())) {
            String dateStr = DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD);
            String timeStr = DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.HH_MM);

            Boolean illegalDateTimeStr = null;
            try {
                illegalDateTimeStr = stockVerifyService.isIllegalDateTimeStr(dateStr, timeStr);
            } catch (ParseException e) {
                log.error(e.getMessage(), e);
            }
            if (illegalDateTimeStr) {
                return;
            }
            String strategyParam = stockTradeSellTask.getStrategyParam();
            JSONObject jsonObject = JSONObject.parseObject(strategyParam);
            String lessRateStr = jsonObject.getString("lessRate");
            String minRateStr = jsonObject.getString("minRate");

            BigDecimal lessRate = new BigDecimal(lessRateStr).divide(new BigDecimal(100),2, BigDecimal.ROUND_HALF_UP);
            BigDecimal minRate = new BigDecimal(minRateStr).divide(new BigDecimal(100),2, BigDecimal.ROUND_HALF_UP);
            flag = lowRateSellTradeService.tradeProcess(lessRate, minRate, convertAmount(stockTradeSellTask.getTradeAmount()),convertAmount(stockTradeSellTask.getTradeNum()), stockTradeSellTask.getStockCode());
        }

        if (flag) {
            //暂停任务
            try {
                stockQuartzService.pauseScheduleJob(jobName, jobGroupName);
            } catch (Exception e) {

                log.error(e.getMessage(), e);
            }
        }
    }





    private BigDecimal convertAmount(Object ojb){
        if(ojb==null){
            return null;
        }
        if(ojb instanceof String){
            return  new BigDecimal(((String)ojb));
        }
        if(ojb instanceof Integer){
            return  new BigDecimal(((Integer)ojb));
        }
        return new BigDecimal(ojb.toString());
    }
}