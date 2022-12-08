package com.coatardbul.stock.service.statistic;

import com.alibaba.fastjson.JSONObject;
import com.coatardbul.baseCommon.util.DateTimeUtil;
import com.coatardbul.stock.mapper.StockTradeBuyTaskMapper;
import com.coatardbul.baseService.entity.bo.StockTradeBuyTask;
import com.coatardbul.stock.service.statistic.business.StockVerifyService;
import com.coatardbul.stock.service.statistic.trade.StockTradeService;
import com.coatardbul.stock.service.statistic.tradeQuartz.RateGreateBuyTradeService;
import com.coatardbul.stock.service.statistic.tradeQuartz.ReboundGreateBuyTradeService;
import com.coatardbul.stock.service.statistic.tradeQuartz.TimeBuyTradeService;
import com.coatardbul.stock.service.statistic.tradeQuartz.UpLimitTfiveBuyTradeService;
import com.coatardbul.stock.service.statistic.tradeQuartz.UplimitBuyTradeService;
import com.coatardbul.stock.service.statistic.tradeQuartz.UplimitFiveBuyTradeService;
import com.coatardbul.stock.service.statistic.tradeQuartz.VBuyTradeService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

@DisallowConcurrentExecution
@Component
@Slf4j
public class BuyTask extends QuartzJobBean {
    @Autowired
    StockTradeService stockTradeService;
    @Autowired
    StockTradeBuyTaskMapper stockTradeBuyTaskMapper;
    @Autowired
    StockVerifyService stockVerifyService;
    @Autowired
    StockQuartzService stockQuartzService;

    @Autowired
    VBuyTradeService vBuyTradeService;

    @Autowired
    UplimitBuyTradeService uplimitBuyTradeService;

    @Autowired
    ReboundGreateBuyTradeService reboundGreateBuyTradeService;

    @Autowired
    TimeBuyTradeService timeBuyTradeService;

    @Autowired
    UplimitFiveBuyTradeService uplimitFiveBuyTradeService;

    @Autowired
    RateGreateBuyTradeService rateGreateBuyTradeService;

    @Autowired
    UpLimitTfiveBuyTradeService upLimitTfiveBuyTradeService;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        JobKey jobKey = context.getJobDetail().getKey();
        String jobName = jobKey.getName();
        String jobGroupName = jobKey.getGroup();

        String[] strings = stockTradeService.splitJobName(jobName);
        //买入配置id
        String buyId = strings[0];
        StockTradeBuyTask stockTradeBuyTask = stockTradeBuyTaskMapper.selectByPrimaryKey(buyId);

        Boolean flag = false;

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

        //定时买入
        if ("TIME_BUY".equals(stockTradeBuyTask.getStrategySign())) {
            flag = stockTradeService.directBuy(convertAmount(stockTradeBuyTask.getTradeAmount()), convertAmount(stockTradeBuyTask.getTradeNum()), stockTradeBuyTask.getStockCode(), stockTradeBuyTask.getStockName());
        }


        // V 起来买，
        if ("V_BUY".equals(stockTradeBuyTask.getStrategySign())) {
            flag = vBuyTradeService.tradeProcess(convertAmount(stockTradeBuyTask.getTradeAmount()), convertAmount(stockTradeBuyTask.getTradeNum()), stockTradeBuyTask.getStockCode());
        }

        //  涨停买入 还需优化，不太智能
        if ("UPLIMIT_BUY".equals(stockTradeBuyTask.getStrategySign())) {
            flag = uplimitBuyTradeService.tradeProcess(convertAmount(stockTradeBuyTask.getTradeAmount()), convertAmount(stockTradeBuyTask.getTradeNum()), stockTradeBuyTask.getStockCode());
        }
        //  涨停前五档买入
        if ("UPLIMIT_BUY_FIVE".equals(stockTradeBuyTask.getStrategySign())) {
            flag = uplimitFiveBuyTradeService.tradeProcess(convertAmount(stockTradeBuyTask.getTradeAmount()), convertAmount(stockTradeBuyTask.getTradeNum()), stockTradeBuyTask.getStockCode());
        }
        //涨幅大于多少买入
        if ("RATE_GREATE_BUY".equals(stockTradeBuyTask.getStrategySign())) {
            String strategyParam = stockTradeBuyTask.getStrategyParam();
            JSONObject jsonObject = JSONObject.parseObject(strategyParam);
            String greateRateStr = jsonObject.getString("greateRate");
            BigDecimal greateRate = new BigDecimal(greateRateStr).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
            flag = rateGreateBuyTradeService.tradeProcess(greateRate, convertAmount(stockTradeBuyTask.getTradeAmount()), convertAmount(stockTradeBuyTask.getTradeNum()), stockTradeBuyTask.getStockCode());
        }

        //做T 高于最低点几个点买入，即回弹比例，需要主观性
        if ("REBOUND_GREATE_BUY".equals(stockTradeBuyTask.getStrategySign())) {
            String strategyParam = stockTradeBuyTask.getStrategyParam();
            JSONObject jsonObject = JSONObject.parseObject(strategyParam);
            String reboundRateStr = jsonObject.getString("reboundRate");
            BigDecimal reboundRate = new BigDecimal(reboundRateStr).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
            flag = reboundGreateBuyTradeService.tradeProcess(reboundRate, convertAmount(stockTradeBuyTask.getTradeAmount()), convertAmount(stockTradeBuyTask.getTradeNum()), stockTradeBuyTask.getStockCode());
        }

        //T板前五回封
        if ("UPLIMIT_T_FIVE_BUY".equals(stockTradeBuyTask.getStrategySign())) {
            flag = upLimitTfiveBuyTradeService.tradeProcess(convertAmount(stockTradeBuyTask.getTradeAmount()), convertAmount(stockTradeBuyTask.getTradeNum()), stockTradeBuyTask.getStockCode());
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


    private BigDecimal convertAmount(Object ojb) {
        if (ojb == null) {
            return null;
        }
        if (ojb instanceof String) {
            return new BigDecimal(((String) ojb));
        }
        if (ojb instanceof Integer) {
            return new BigDecimal(((Integer) ojb));
        }
        return new BigDecimal(ojb.toString());
    }
}