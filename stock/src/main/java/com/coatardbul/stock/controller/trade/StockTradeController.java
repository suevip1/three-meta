package com.coatardbul.stock.controller.trade;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.baseService.entity.bo.StockTradeBuyTask;
import com.coatardbul.stock.model.entity.StockTradeSellJob;
import com.coatardbul.stock.service.StockUserBaseService;
import com.coatardbul.stock.service.statistic.trade.StockTradeBuyTaskService;
import com.coatardbul.stock.service.statistic.trade.StockTradeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
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
    @Autowired
    StockUserBaseService stockUserBaseService;
    /**
     * 获取历史数据
     *
     * @param
     * @return
     */
    @RequestMapping(path = "/getHisDealData", method = RequestMethod.POST)
    public CommonResult getHisDealData(@RequestBody Map map) {
        try {
            String beginDate = map.get("beginDate").toString();
            String endDate = map.get("endDate").toString();
            int pageSize =Integer.valueOf( map.get("pageSize").toString());
            Object result = stockTradeService.getHisDealData(beginDate,endDate,pageSize);
            return CommonResult.success(result);
        }catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }
    }
    @RequestMapping(path = "/getDealData", method = RequestMethod.POST)
    public CommonResult getDealData(@RequestBody Map map) {
        try {
            int pageSize =Integer.valueOf( map.get("pageSize").toString());
            Object result = stockTradeService.getDealData(pageSize);
            return CommonResult.success(result);
        }catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }
    }
    /**
     * 查询持仓
     *
     * @param
     * @return
     */
    @RequestMapping(path = "/queryAssetAndPosition", method = RequestMethod.POST)
    public CommonResult queryAssetAndPosition() {
        try {

            String result = stockTradeService.queryAssetAndPosition();
            return CommonResult.success(result);
        }catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }
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
     * @param
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
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();

        String userName = stockUserBaseService.getCurrUserName(request);
        stockTradeService.directBuy(
                new BigDecimal(map.get("buyAmount").toString()),
                null,
                (String)map.get("code"),
                (String) map.get("name"),userName);
        return CommonResult.success("买入成功");
    }
    @RequestMapping(path = "/quickSell", method = RequestMethod.POST)
    public CommonResult quickSell(@RequestBody Map map) {
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();

        stockTradeService.directSell(
                map.get("sellPrice")!=null? new BigDecimal(map.get("sellPrice").toString()):null,
                 new BigDecimal(map.get("sellNum").toString()),
                (String)map.get("code"),
                (String) map.get("name"),request);
        return CommonResult.success("卖出成功");
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
