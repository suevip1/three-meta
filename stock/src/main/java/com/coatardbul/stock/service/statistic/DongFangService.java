package com.coatardbul.stock.service.statistic;

import com.coatardbul.baseService.service.DataServiceBridge;
import com.coatardbul.baseService.service.DongFangCommonService;
import com.coatardbul.stock.service.base.StockStrategyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
public class DongFangService extends DongFangCommonService implements DataServiceBridge {



    @Resource
    XinlangService xinlangService;


    @Autowired
    StockStrategyService stockStrategyService;



}
