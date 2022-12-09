package com.coatardbul.sail.service;

import com.coatardbul.baseCommon.constants.Constant;
import com.coatardbul.baseCommon.util.JsonUtil;
import com.coatardbul.baseService.entity.bo.PreQuartzTradeDetail;
import com.coatardbul.baseService.entity.bo.StockTradeBuyTask;
import com.coatardbul.baseService.service.AiStrategyService;
import com.coatardbul.baseService.utils.RedisKeyUtils;
import com.coatardbul.sail.feign.StockServerFeign;
import com.coatardbul.sail.model.dto.StockCronRefreshDTO;
import com.coatardbul.sail.service.stockData.DataFactory;
import com.coatardbul.baseService.service.DataServiceBridge;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.bcel.Const;
import org.aspectj.apache.bcel.Constants;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Note:  采用新浪财经数据，将股票信息同步到redis上，以redis为数据库，进行信息同步
 * <p>
 * Date: 2022/10/23
 *
 * @author Su Xiaolei
 */
@Service
@Slf4j
public class StockCronRefreshService {


    @Resource
    DataFactory dataFactory;
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    AiStrategyService aiStrategyService;
    @Resource
    StockServerFeign stockServerFeign;

    /**
     * 获取刷新redis上股票信息
     *
     * @param codes
     * @return
     */
    public void refreshStockInfo(List<String> codes) {
        DataServiceBridge dataServiceBridge = dataFactory.build();
        for (String code : codes) {
            try {
                dataServiceBridge.getAndRefreshStockInfo(code);
                //嵌入交易模块
                Constant.strategyThreadPool.submit(() -> {
                    try {
                        preQuartzTradeDetailProcess(code);
                    } catch (Exception e) {
                       aiStrategyService.removeCodeFromPreTrade(code);
                        log.error("交易模块异常" + e.getMessage(), e);
                    }
                });

                dataServiceBridge.refreshStockTickInfo(code);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

        }
    }

    private void preQuartzTradeDetailProcess(String code) {
        PreQuartzTradeDetail preQuartzTradeDetail = aiStrategyService.getPreQuartzTradeDetail(code);
        if (preQuartzTradeDetail.getTradeFlag()) {
            StockTradeBuyTask stockTradeBuyTask = new StockTradeBuyTask();
            stockTradeBuyTask.setStockCode(preQuartzTradeDetail.getCode());
            stockTradeBuyTask.setStockName(preQuartzTradeDetail.getName());
            stockTradeBuyTask.setStrategySign(preQuartzTradeDetail.getQuartzSign());
            stockTradeBuyTask.setCron("0/1 * * * * ? ");
            stockTradeBuyTask.setTradeAmount(preQuartzTradeDetail.getUserMoney().toString());
            stockTradeBuyTask.setTradeRateType(0);
            stockTradeBuyTask.setStrategyParam("{\"greateRate\":9}");
            if (preQuartzTradeDetail.getTradeNum() != null) {
                stockTradeBuyTask.setTradeNum(preQuartzTradeDetail.getTradeNum().toString());
            }
            stockServerFeign.add(stockTradeBuyTask);
        }
    }


    public void refreshStockTickInfo(List<String> codeArr) {
        DataServiceBridge dataServiceBridge = dataFactory.build();

        for (String code : codeArr) {
            try {
                dataServiceBridge.refreshStockTickInfo(code);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public void refreshStockMinuterInfo(List<String> codeArr) {
        DataServiceBridge dataServiceBridge = dataFactory.build();

        for (String code : codeArr) {
            try {
                dataServiceBridge.refreshStockMinuterInfo(code);

            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }


    public void refreshHisStockInfo(StockCronRefreshDTO dto) {
        DataServiceBridge dataServiceBridge = dataFactory.build();
        for (String code : dto.getCodeArr()) {
            try {
                dataServiceBridge.getAndRefreshStockInfo(code, dto.getDateStr());

            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

        }
    }
}
