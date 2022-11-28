package com.coatardbul.stock.controller.trade;

import com.coatardbul.stock.service.statistic.trade.StockTradeService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * Note://todo 交易任务，包括定时策略策略买入，单个股票策略。
 * <p>
 * Date: 2022/6/3
 *
 * @author Su Xiaolei
 */
@Slf4j
@RestController
@Api(tags = "x")
@RequestMapping("/task")
public class StockTradeTaskController {

    @Autowired
    StockTradeService stockTradeService;


}
