package com.coatardbul.stock.controller.trade;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.stock.model.entity.StockTradeConfig;
import com.coatardbul.stock.model.entity.StockTradeDateSwitch;
import com.coatardbul.stock.service.statistic.trade.StockTradeConfigService;
import com.coatardbul.stock.service.statistic.trade.StockTradeDateSwitchService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/6/3
 *
 * @author Su Xiaolei
 */
@Slf4j
@RestController
@Api(tags = "x")
@RequestMapping("/tradeConfig")
public class StockTradeConfigController {

    @Autowired
    StockTradeConfigService stockTradeConfigService;
@Autowired
StockTradeDateSwitchService stockTradeDateSwitchService;
    /**
     * 同步买入信息
     *
     * @param dto
     * @return
     */
    @RequestMapping(path = "/switchEnvironment", method = RequestMethod.POST)
    public CommonResult switchEnvironment(@RequestBody StockTradeConfig dto) {
        stockTradeConfigService.switchEnvironment(dto);
        return CommonResult.success(null);
    }

    @RequestMapping(path = "/getDefaultConfig", method = RequestMethod.POST)
    public CommonResult getDefaultConfig() {
        StockTradeConfig defaultStockTradeConfig = stockTradeConfigService.getDefaultStockTradeConfig();
        return CommonResult.success(defaultStockTradeConfig);
    }


    @RequestMapping(path = "/getSwitchDate", method = RequestMethod.POST)
    public CommonResult getSwitchDate() {
        String date = stockTradeDateSwitchService.getCurrentDateSwitch();
        return CommonResult.success(date);
    }
    @RequestMapping(path = "/switchDate", method = RequestMethod.POST)
    public CommonResult switchDate(@RequestBody StockTradeDateSwitch dto) {
        stockTradeDateSwitchService.switchDate(dto);
        return CommonResult.success(null);
    }

}
