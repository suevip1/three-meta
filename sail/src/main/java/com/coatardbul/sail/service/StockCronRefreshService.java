package com.coatardbul.sail.service;

import com.coatardbul.sail.service.stockData.DataFactory;
import com.coatardbul.baseService.service.DataServiceBridge;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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
                dataServiceBridge.refreshStockTickInfo(code);
            }catch (Exception e) {
                log.error(e.getMessage(), e);
            }

        }
    }


    public void refreshStockTickInfo(List<String> codeArr) {
        DataServiceBridge dataServiceBridge = dataFactory.build();

        for (String code : codeArr) {
            try {
                dataServiceBridge.refreshStockTickInfo(code);
            }catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public void refreshStockMinuterInfo(List<String> codeArr) {
        DataServiceBridge dataServiceBridge = dataFactory.build();

        for (String code : codeArr) {
            try {
                dataServiceBridge.refreshStockMinuterInfo(code);

            }catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
