package com.coatardbul.stock.service.statistic;

import com.coatardbul.baseService.service.DataServiceBridge;
import com.coatardbul.baseService.service.XinlangCommonService;
import com.coatardbul.stock.service.base.StockStrategyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/11/11
 *
 * @author Su Xiaolei
 */
@Service
@Slf4j
public class XinlangService extends XinlangCommonService
        implements DataServiceBridge {
    @Autowired
    StockStrategyService stockStrategyService;

}
