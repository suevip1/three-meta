package com.coatardbul.stock.controller.trade;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.stock.model.entity.StockTradeSellJob;
import com.coatardbul.stock.service.statistic.trade.StockTradeBuyTaskService;
import com.coatardbul.stock.service.statistic.trade.StockTradeService;
import com.coatardbul.stock.model.entity.StockTradeBuyTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
@RequestMapping("/trade")
public class StockTradeController {

    @Autowired
    StockTradeService stockTradeService;
@Autowired
StockTradeBuyTaskService stockTradeBuyTaskService;
    /**
     * 查询持仓
     *
     * @param
     * @return
     */
    @RequestMapping(path = "/queryAssetAndPosition", method = RequestMethod.POST)
    public CommonResult queryAssetAndPosition() {
        String result = stockTradeService.queryAssetAndPosition();
        return CommonResult.success(result);
    }

    /**
     * 添加卖出信息
     *
     * @param
     * @return
     */
    @RequestMapping(path = "/addSellInfo", method = RequestMethod.POST)
    public CommonResult addSellInfo(@Validated @RequestBody StockTradeSellJob dto) {
        stockTradeService.addSellInfo(dto);
        return CommonResult.success(null);
    }

    @RequestMapping(path = "/modifySellInfo", method = RequestMethod.POST)
    public CommonResult modifySellInfo(@Validated @RequestBody StockTradeSellJob dto) {
        stockTradeService.modifySellInfo(dto);
        return CommonResult.success(null);
    }

    @RequestMapping(path = "/deleteSellInfo", method = RequestMethod.POST)
    public CommonResult deleteSellInfo(@Validated @RequestBody StockTradeSellJob dto) {
        stockTradeService.deleteSellInfo(dto);
        return CommonResult.success(null);
    }

    @RequestMapping(path = "/querySellInfo", method = RequestMethod.POST)
    public CommonResult querySellInfo(@Validated @RequestBody StockTradeSellJob dto) {
        List<StockTradeSellJob> stockTradeSellJobs = stockTradeService.querySellInfo(dto);
        return CommonResult.success(stockTradeSellJobs);
    }


    /**
     * 同步买入信息
     *
     * @param dto
     * @return
     */
    @RequestMapping(path = "/syncBuyInfo", method = RequestMethod.POST)
    public CommonResult syncBuyInfo() {
        stockTradeService.syncBuyInfo();
        return CommonResult.success(null);
    }

    @RequestMapping(path = "/initBuyInfo", method = RequestMethod.POST)
    public CommonResult initBuyInfo() {
        stockTradeService.initBuyInfo();
        return CommonResult.success(null);
    }

    @RequestMapping(path = "/quickBuy", method = RequestMethod.POST)
    public CommonResult quickBuy(@RequestBody Map map) {
        stockTradeService.directBuy(
                new BigDecimal((String)map.get("buyAmount")),
                null,
                (String)map.get("code"),
                (String) map.get("name"));
        return CommonResult.success(null);
    }

    @ApiOperation("涨停条件买入")
    @RequestMapping(path = "/uplimitBuy", method = RequestMethod.POST)
    public CommonResult uplimitBuy(@RequestBody Map map) throws Exception {
        StockTradeBuyTask stockTrade=new StockTradeBuyTask();
        stockTrade.setStrategySign("UPLIMIT_BUY_FIVE");
        stockTrade.setCron("* * * * * ? *");
        stockTrade.setTradeAmount((String)map.get("buyAmount"));
        stockTrade.setStockCode((String)map.get("code"));
        stockTrade.setStockName((String)map.get("name"));
        stockTradeBuyTaskService.add(stockTrade);
        return CommonResult.success(null);
    }


}
