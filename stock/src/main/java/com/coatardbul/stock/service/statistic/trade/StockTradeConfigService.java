package com.coatardbul.stock.service.statistic.trade;

import com.coatardbul.stock.model.entity.StockTradeConfig;
import com.coatardbul.stock.mapper.StockTradeConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/6/5
 *
 * @author Su Xiaolei
 */
@Slf4j
@Service
public class StockTradeConfigService {


    private final static String DEFAULT_SIGN = "default";


    @Autowired
    StockTradeConfigMapper stockTradeConfigMapper;


    /**
     * 获取默认的数据
     *
     * @return
     */
    public StockTradeConfig getDefaultStockTradeConfig() {
        StockTradeConfig stockTradeConfig = stockTradeConfigMapper.selectAllBySign(DEFAULT_SIGN);
        return stockTradeConfig;
    }

    public void switchEnvironment(StockTradeConfig dto) {
        stockTradeConfigMapper.updateSimulateTypeBySign(dto.getSimulateType(), DEFAULT_SIGN);
    }

}
